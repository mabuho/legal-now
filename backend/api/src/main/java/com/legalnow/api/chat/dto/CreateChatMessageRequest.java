package com.legalnow.api.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateChatMessageRequest(
    @NotBlank @Size(max = 8000) String body
) {
}
