package com.legalnow.api.chat.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

public record CreateChatSessionRequest(
    @NotNull @JsonProperty("consultation_id") UUID consultationId
) {
}
