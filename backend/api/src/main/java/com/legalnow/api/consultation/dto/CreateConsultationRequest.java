package com.legalnow.api.consultation.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateConsultationRequest(
    @NotNull @JsonProperty("lawyer_id") UUID lawyerId,
    @NotBlank @Size(max = 255) String title,
    @Size(max = 4000) String description,
    @JsonProperty("scheduled_at") OffsetDateTime scheduledAt
) {
}
