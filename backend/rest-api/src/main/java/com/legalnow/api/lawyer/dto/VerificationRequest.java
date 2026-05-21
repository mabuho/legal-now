package com.legalnow.api.lawyer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Size;

public record VerificationRequest(
    @JsonProperty("notes") @Size(max = 2000) String notes
) {}
