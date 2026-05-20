package com.legalnow.api.consultation.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legalnow.api.consultation.Consultation;
import com.legalnow.api.user.User;
import com.legalnow.api.user.dto.PublicUserResponse;

public record ConsultationResponse(
    @JsonProperty("id") UUID id,
    @JsonProperty("status") String status,
    @JsonProperty("title") String title,
    @JsonProperty("description") String description,
    @JsonProperty("client_id") UUID clientId,
    @JsonProperty("lawyer_id") UUID lawyerId,
    @JsonProperty("client") PublicUserResponse client,
    @JsonProperty("lawyer") PublicUserResponse lawyer,
    @JsonProperty("scheduled_at") OffsetDateTime scheduledAt,
    @JsonProperty("janus_room_id") Long janusRoomId,
    @JsonProperty("janus_pin") String janusPin,
    @JsonProperty("created_at") OffsetDateTime createdAt,
    @JsonProperty("updated_at") OffsetDateTime updatedAt
) {
    public static ConsultationResponse from(Consultation c, User client, User lawyer) {
        return new ConsultationResponse(
            c.getId(),
            c.getStatus() == null ? null : c.getStatus().toDb(),
            c.getTitle(),
            c.getDescription(),
            c.getClientId(),
            c.getLawyerId(),
            client == null ? null : PublicUserResponse.from(client),
            lawyer == null ? null : PublicUserResponse.from(lawyer),
            c.getScheduledAt(),
            c.getJanusRoomId(),
            c.getJanusPin(),
            c.getCreatedAt(),
            c.getUpdatedAt()
        );
    }
}
