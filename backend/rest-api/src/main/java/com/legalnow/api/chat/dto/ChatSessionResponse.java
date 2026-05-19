package com.legalnow.api.chat.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legalnow.api.chat.ChatSession;

public record ChatSessionResponse(
    UUID id,
    @JsonProperty("consultation_id") UUID consultationId,
    @JsonProperty("started_at") OffsetDateTime startedAt,
    @JsonProperty("ended_at") OffsetDateTime endedAt,
    @JsonProperty("recording_path") String recordingPath,
    @JsonProperty("transcript_path") String transcriptPath
) {
    public static ChatSessionResponse from(ChatSession s) {
        return new ChatSessionResponse(
            s.getId(),
            s.getConsultationId(),
            s.getStartedAt(),
            s.getEndedAt(),
            s.getRecordingPath(),
            s.getTranscriptPath()
        );
    }
}
