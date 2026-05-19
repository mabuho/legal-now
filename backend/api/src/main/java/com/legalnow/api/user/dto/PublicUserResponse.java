package com.legalnow.api.user.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legalnow.api.user.User;

public record PublicUserResponse(
    @JsonProperty("id") UUID id,
    @JsonProperty("name") String name,
    @JsonProperty("role") String role,
    @JsonProperty("avatar_url") String avatarUrl
) {
    public static PublicUserResponse from(User u) {
        return new PublicUserResponse(u.getId(), u.getName(), u.getRole().toDbValue(), u.getAvatarUrl());
    }
}
