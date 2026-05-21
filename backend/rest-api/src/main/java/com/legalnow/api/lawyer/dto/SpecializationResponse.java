package com.legalnow.api.lawyer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legalnow.api.lawyer.domain.Specialization;

public record SpecializationResponse(
    @JsonProperty("id") Short id,
    @JsonProperty("code") String code,
    @JsonProperty("name") String name
) {
    public static SpecializationResponse from(Specialization s) {
        return new SpecializationResponse(s.getId(), s.getCode(), s.getName());
    }
}
