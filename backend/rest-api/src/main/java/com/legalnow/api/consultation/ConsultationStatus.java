package com.legalnow.api.consultation;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ConsultationStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    SCHEDULED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    private static final Map<ConsultationStatus, Set<ConsultationStatus>> TRANSITIONS = Map.of(
        PENDING, EnumSet.of(ACCEPTED, REJECTED, CANCELLED),
        ACCEPTED, EnumSet.of(SCHEDULED, CANCELLED),
        SCHEDULED, EnumSet.of(IN_PROGRESS, CANCELLED),
        IN_PROGRESS, EnumSet.of(COMPLETED, CANCELLED),
        REJECTED, EnumSet.noneOf(ConsultationStatus.class),
        COMPLETED, EnumSet.noneOf(ConsultationStatus.class),
        CANCELLED, EnumSet.noneOf(ConsultationStatus.class)
    );

    @JsonCreator
    public static ConsultationStatus fromDb(String value) {
        if (value == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        return switch (value.trim().toLowerCase()) {
            case "pending" -> PENDING;
            case "accepted" -> ACCEPTED;
            case "rejected" -> REJECTED;
            case "scheduled" -> SCHEDULED;
            case "in_progress" -> IN_PROGRESS;
            case "completed" -> COMPLETED;
            case "cancelled" -> CANCELLED;
            default -> throw new IllegalArgumentException("Unknown consultation status: " + value);
        };
    }

    @JsonValue
    public String toDb() {
        return name().toLowerCase();
    }

    public boolean canTransitionTo(ConsultationStatus next) {
        if (next == null) {
            return false;
        }
        return TRANSITIONS.getOrDefault(this, Set.of()).contains(next);
    }

    public boolean isTerminal() {
        return TRANSITIONS.getOrDefault(this, Set.of()).isEmpty();
    }
}
