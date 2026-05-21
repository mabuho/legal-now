package com.legalnow.api.sep;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SepCedulaClient {

    private static final TypeReference<List<SepProfesionistaDto>> LIST_TYPE = new TypeReference<>() {};

    private final RestClient restClient;
    private final SepCedulaProperties properties;
    private final ObjectMapper objectMapper;

    public SepCedulaClient(SepCedulaProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
            .baseUrl(properties.apiBase())
            .build();
        this.objectMapper = new ObjectMapper();
    }

    private String fetchToken() {
        try {
            String raw = restClient.get()
                .uri(properties.apiAuth())
                .header(HttpHeaders.HOST, properties.host())
                .header(HttpHeaders.REFERER, properties.refer())
                .header("X-API-Key", properties.apiKey())
                .header("X-Client-Id", properties.clientId())
                .retrieve()
                .body(String.class);

            if (raw == null || raw.isBlank()) {
                throw new SepApiException("SEP auth token response is empty");
            }

            String trimmed = raw.trim();
            if (trimmed.startsWith("{")) {
                try {
                    JsonNode node = objectMapper.readTree(trimmed);
                    if (node.has("token")) {
                        return node.get("token").asText();
                    }
                    if (node.has("access_token")) {
                        return node.get("access_token").asText();
                    }
                    log.warn("SEP auth response is JSON but has no token/access_token field; using raw");
                } catch (JsonProcessingException e) {
                    log.warn("SEP auth response looked like JSON but failed to parse; using raw text");
                }
            }

            return trimmed;
        } catch (RestClientException e) {
            throw new SepApiException("Failed to fetch SEP auth token", e);
        }
    }

    public List<SepProfesionistaDto> queryByCedula(String numCedula) {
        log.debug("SEP query by cedula: {}", numCedula);
        Map<String, String> body = Map.of("numCedula", numCedula);
        return post(body);
    }

    public List<SepProfesionistaDto> queryByDetail(
        String nombre,
        String primerApellido,
        String segundoApellido,
        String curp
    ) {
        log.debug("SEP query by detail: nombre={} curp={}", nombre, curp);
        Map<String, String> body = Map.of(
            "nombre", nombre != null ? nombre : "",
            "primerApellido", primerApellido != null ? primerApellido : "",
            "segundoApellido", segundoApellido != null ? segundoApellido : "",
            "curp", curp != null ? curp : ""
        );
        return post(body);
    }

    private List<SepProfesionistaDto> post(Map<String, String> body) {
        String token = fetchToken();
        try {
            String bodyJson = objectMapper.writeValueAsString(body);
            String response = restClient.post()
                .uri(properties.apiPath())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(bodyJson)
                .retrieve()
                .body(String.class);

            if (response == null || response.isBlank()) {
                log.warn("SEP consultar returned empty body");
                return Collections.emptyList();
            }

            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.isArray() ? root : root.path("items");
            if (items.isMissingNode()) {
                items = root.path("results");
            }
            if (items.isMissingNode() || !items.isArray()) {
                log.warn("Unexpected SEP response structure: {}", response);
                return Collections.emptyList();
            }

            return objectMapper.convertValue(items, LIST_TYPE);
        } catch (RestClientException e) {
            throw new SepApiException("SEP consultar HTTP error", e);
        } catch (JsonProcessingException e) {
            throw new SepApiException("SEP consultar response parse error", e);
        }
    }
}
