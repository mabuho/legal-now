package com.legalnow.api.janus;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import static org.junit.jupiter.api.Assertions.assertThrows;

@RestClientTest(JanusClient.class)
class JanusClientTest {
    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private JanusClient janusClient;

    @Autowired
    private ObjectMapper objectMapper;

    private JanusProperties properties;

    @BeforeEach
    void setUp() {
        properties = new JanusProperties("http://janus:8088", "janusoverlord");
    }

    @Test
    void createTextRoom_success() throws Exception {
        long roomId = 42L;
        String sessionId = "123456";
        String handleId = "789012";

        server.expect(MockRestRequestMatchers.requestTo("http://janus:8088/"))
            .andRespond(MockRestResponseCreators.withSuccess(
                """
                {"janus":"success","data":{"id":%s}}
                """.formatted(sessionId),
                org.springframework.http.MediaType.APPLICATION_JSON
            ));

        server.expect(MockRestRequestMatchers.requestTo("http://janus:8088/" + sessionId))
            .andRespond(MockRestResponseCreators.withSuccess(
                """
                {"janus":"ack","transaction":""}
                """,
                org.springframework.http.MediaType.APPLICATION_JSON
            ));

        server.expect(MockRestRequestMatchers.requestTo("http://janus:8088/" + sessionId))
            .andRespond(MockRestResponseCreators.withSuccess(
                """
                {"janus":"success","data":{"id":%s}}
                """.formatted(handleId),
                org.springframework.http.MediaType.APPLICATION_JSON
            ));

        server.expect(MockRestRequestMatchers.requestTo("http://janus:8088/" + sessionId + "/" + handleId))
            .andRespond(MockRestResponseCreators.withSuccess(
                """
                {"janus":"ack"}
                """,
                org.springframework.http.MediaType.APPLICATION_JSON
            ));

        server.expect(MockRestRequestMatchers.requestTo("http://janus:8088/" + sessionId))
            .andRespond(MockRestResponseCreators.withSuccess(
                """
                {"janus":"ack"}
                """,
                org.springframework.http.MediaType.APPLICATION_JSON
            ));

        janusClient.createTextRoom(roomId);
        server.verify();
    }

    @Test
    void createTextRoom_sessionCreationFails() {
        server.expect(MockRestRequestMatchers.requestTo("http://janus:8088/"))
            .andRespond(MockRestResponseCreators.withSuccess(
                """
                {"janus":"error","error":{"code":400,"reason":"Invalid request"}}
                """,
                org.springframework.http.MediaType.APPLICATION_JSON
            ));

        assertThrows(JanusRoomCreationException.class, () -> janusClient.createTextRoom(42L));
    }
}
