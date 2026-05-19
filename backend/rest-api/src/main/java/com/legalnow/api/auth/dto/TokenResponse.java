package com.legalnow.api.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("refresh_token") String refreshToken,
    @JsonProperty("expires_in") long expiresIn,
    @JsonProperty("token_type") String tokenType
) {
    public static TokenResponse bearer(String accessToken, String refreshToken, long expiresInSeconds) {
        return new TokenResponse(accessToken, refreshToken, expiresInSeconds, "Bearer");
    }
}
