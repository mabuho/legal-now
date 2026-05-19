package com.legalnow.api.auth.dto;

public record AuthResponse(
    UserResponse user,
    TokenResponse tokens
) {}
