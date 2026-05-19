package com.legalnow.api.consultation.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.legalnow.api.consultation.ConsultationStatus;

import jakarta.validation.constraints.NotNull;

public record StatusTransitionRequest(
    @NotNull ConsultationStatus status,
    String reason
) {
    @JsonCreator
    public static StatusTransitionRequest of(
        @JsonProperty("status") String status,
        @JsonProperty("reason") String reason
    ) {
        return new StatusTransitionRequest(
            status == null ? null : ConsultationStatus.fromDb(status),
            reason
        );
    }
}
