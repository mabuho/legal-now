package com.legalnow.api.auth.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legalnow.api.user.User;

public record UserResponse(
    UUID id,
    String email,
    String name,
    String role,
    @JsonProperty("avatar_url") String avatarUrl
) {
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getRole().toDbValue(),
            user.getAvatarUrl()
        );
    }
}
