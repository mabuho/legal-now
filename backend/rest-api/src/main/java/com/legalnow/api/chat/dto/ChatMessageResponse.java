package com.legalnow.api.chat.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legalnow.api.chat.ChatMessage;

public record ChatMessageResponse(
    UUID id,
    @JsonProperty("session_id") UUID sessionId,
    @JsonProperty("sender_id") UUID senderId,
    String body,
    @JsonProperty("sent_at") OffsetDateTime sentAt
) {
    public static ChatMessageResponse from(ChatMessage m) {
        return new ChatMessageResponse(
            m.getId(),
            m.getSessionId(),
            m.getSenderId(),
            m.getBody(),
            m.getSentAt()
        );
    }
}
