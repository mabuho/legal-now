package com.legalnow.api.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    CLIENT,
    LAWYER,
    ADMIN;

    @JsonCreator
    public static Role fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("role must not be null");
        }
        return switch (value.trim().toLowerCase()) {
            case "client" -> CLIENT;
            case "lawyer" -> LAWYER;
            case "admin" -> ADMIN;
            default -> throw new IllegalArgumentException("Unknown role: " + value);
        };
    }

    @JsonValue
    public String toDbValue() {
        return name().toLowerCase();
    }
}
