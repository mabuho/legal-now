package com.legalnow.api.lawyer.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legalnow.api.lawyer.domain.LawyerDocument;

public record DocumentResponse(
    @JsonProperty("id") UUID id,
    @JsonProperty("lawyer_id") UUID lawyerId,
    @JsonProperty("doc_type") String docType,
    @JsonProperty("file_name") String fileName,
    @JsonProperty("content_type") String contentType,
    @JsonProperty("size_bytes") Long sizeBytes,
    @JsonProperty("uploaded_at") OffsetDateTime uploadedAt
) {
    public static DocumentResponse from(LawyerDocument doc) {
        return new DocumentResponse(
            doc.getId(),
            doc.getLawyerId(),
            doc.getDocType(),
            doc.getFileName(),
            doc.getContentType(),
            doc.getSizeBytes(),
            doc.getUploadedAt()
        );
    }
}
