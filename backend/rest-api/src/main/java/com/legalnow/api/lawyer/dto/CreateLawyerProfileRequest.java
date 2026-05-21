package com.legalnow.api.lawyer.dto;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Size;

public record CreateLawyerProfileRequest(
    @JsonProperty("bar_id") @Size(max = 64) String barId,
    @JsonProperty("bio") @Size(max = 4000) String bio,
    @JsonProperty("languages") List<@Size(min = 1, max = 32) String> languages,
    @JsonProperty("specialization_codes") Set<String> specializationCodes
) {}
