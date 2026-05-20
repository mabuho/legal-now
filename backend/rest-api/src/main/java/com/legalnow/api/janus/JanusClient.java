package com.legalnow.api.janus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Slf4j
@Component
public class JanusClient {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public JanusClient(JanusProperties properties) {
        this.restClient = RestClient.builder()
            .baseUrl(properties.apiUrl())
            .build();
        this.objectMapper = new ObjectMapper();
    }

    public void createTextRoom(long roomId) {
        createRoom(roomId, "janus.plugin.textroom", "textroom");
    }

    public void createVideoRoom(long roomId) {
        createRoom(roomId, "janus.plugin.videoroom", "videoroom");
    }

    private void createRoom(long roomId, String pluginName, String roomType) {
        try {
            String transactionId = UUID.randomUUID().toString();

            long sessionId = createSession(transactionId);
            log.debug("Created Janus session: {}", sessionId);

            long handleId = attachPlugin(sessionId, pluginName, transactionId);
            log.debug("Attached {} plugin, handle: {}", pluginName, handleId);

            sendCreateRoomMessage(sessionId, handleId, roomId, roomType, transactionId);
            log.info("Created {} room {}", roomType, roomId);

            destroySession(sessionId, transactionId);
            log.debug("Destroyed session: {}", sessionId);
        } catch (RestClientException | InterruptedException | JsonProcessingException e) {
            throw new JanusRoomCreationException("Failed to create " + roomType + " room " + roomId, e);
        }
    }

    private long createSession(String transactionId) throws RestClientException, JsonProcessingException {
        String body = """
            {"janus":"create","transaction":"%s"}
            """.formatted(transactionId);

        String response = restClient.post()
            .uri("")
            .body(body)
            .retrieve()
            .body(String.class);

        JsonNode jsonNode = objectMapper.readTree(response);
        if (!"success".equals(jsonNode.get("janus").asText())) {
            throw new JanusRoomCreationException("Failed to create Janus session: " + response);
        }
        return jsonNode.get("data").get("id").asLong();
    }

    private long attachPlugin(long sessionId, String pluginName, String transactionId) throws RestClientException, InterruptedException, JsonProcessingException {
        String body = """
            {"janus":"attach","plugin":"%s","transaction":"%s"}
            """.formatted(pluginName, transactionId);

        String ackResponse = restClient.post()
            .uri("/{sessionId}", sessionId)
            .body(body)
            .retrieve()
            .body(String.class);

        JsonNode ackNode = objectMapper.readTree(ackResponse);
        if (!"ack".equals(ackNode.get("janus").asText())) {
            throw new JanusRoomCreationException("Expected ack, got: " + ackResponse);
        }

        for (int i = 0; i < 10; i++) {
            Thread.sleep(200);
            String pollResponse = restClient.get()
                .uri("/{sessionId}", sessionId)
                .retrieve()
                .body(String.class);

            JsonNode pollNode = objectMapper.readTree(pollResponse);
            if ("success".equals(pollNode.get("janus").asText())) {
                return pollNode.get("data").get("id").asLong();
            }
        }

        throw new JanusRoomCreationException("Timeout waiting for plugin attach response");
    }

    private void sendCreateRoomMessage(long sessionId, long handleId, long roomId, String roomType, String transactionId) throws RestClientException, JsonProcessingException {
        String body = roomType.equals("textroom")
            ? """
                {"janus":"message","handle_id":%d,"transaction":"%s","body":{"request":"create","room":%d,"permanent":false,"history":50}}
                """.formatted(handleId, transactionId, roomId)
            : """
                {"janus":"message","handle_id":%d,"transaction":"%s","body":{"request":"create","room":%d,"publishers":2,"bitrate":128000}}
                """.formatted(handleId, transactionId, roomId);

        String response = restClient.post()
            .uri("/{sessionId}/{handleId}", sessionId, handleId)
            .body(body)
            .retrieve()
            .body(String.class);

        JsonNode jsonNode = objectMapper.readTree(response);
        if (!"ack".equals(jsonNode.get("janus").asText()) && !"success".equals(jsonNode.get("janus").asText())) {
            throw new JanusRoomCreationException("Failed to create " + roomType + " room: " + response);
        }
    }

    private void destroySession(long sessionId, String transactionId) throws RestClientException, JsonProcessingException {
        String body = """
            {"janus":"destroy","transaction":"%s"}
            """.formatted(transactionId);

        String response = restClient.post()
            .uri("/{sessionId}", sessionId)
            .body(body)
            .retrieve()
            .body(String.class);

        JsonNode jsonNode = objectMapper.readTree(response);
        if (!"ack".equals(jsonNode.get("janus").asText())) {
            log.warn("Unexpected response when destroying session: {}", response);
        }
    }
}
