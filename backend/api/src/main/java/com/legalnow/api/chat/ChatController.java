package com.legalnow.api.chat;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.legalnow.api.chat.dto.ChatMessageResponse;
import com.legalnow.api.chat.dto.ChatSessionResponse;
import com.legalnow.api.chat.dto.CreateChatMessageRequest;
import com.legalnow.api.chat.dto.CreateChatSessionRequest;
import com.legalnow.api.common.PageResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/chat-sessions")
public class ChatController {

    private static final int DEFAULT_SESSION_PAGE_SIZE = 20;
    private static final int MAX_SESSION_PAGE_SIZE = 100;
    private static final int DEFAULT_MESSAGE_PAGE_SIZE = 50;
    private static final int MAX_MESSAGE_PAGE_SIZE = 200;

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatSessionResponse> create(@Valid @RequestBody CreateChatSessionRequest req) {
        ChatSessionResponse created = chatService.createSession(req);
        return ResponseEntity.created(URI.create("/api/v1/chat-sessions/" + created.id())).body(created);
    }

    @GetMapping
    public PageResponse<ChatSessionResponse> list(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(
            Math.max(0, page),
            clampSize(size, DEFAULT_SESSION_PAGE_SIZE, MAX_SESSION_PAGE_SIZE),
            Sort.by(Sort.Direction.DESC, "startedAt")
        );
        Page<ChatSessionResponse> result = chatService.listMine(pageable);
        return PageResponse.from(result, c -> c);
    }

    @GetMapping("/{id}")
    public ChatSessionResponse get(@PathVariable UUID id) {
        return chatService.getSession(id);
    }

    @PostMapping("/{id}/end")
    public ChatSessionResponse end(@PathVariable UUID id) {
        return chatService.endSession(id);
    }

    @PostMapping("/{id}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatMessageResponse postMessage(
        @PathVariable UUID id,
        @Valid @RequestBody CreateChatMessageRequest req
    ) {
        return chatService.postMessage(id, req);
    }

    @GetMapping("/{id}/messages")
    public PageResponse<ChatMessageResponse> listMessages(
        @PathVariable UUID id,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "50") int size
    ) {
        Pageable pageable = PageRequest.of(
            Math.max(0, page),
            clampSize(size, DEFAULT_MESSAGE_PAGE_SIZE, MAX_MESSAGE_PAGE_SIZE)
        );
        Page<ChatMessageResponse> result = chatService.listMessages(id, pageable);
        return PageResponse.from(result, m -> m);
    }

    private static int clampSize(int size, int defaultSize, int maxSize) {
        if (size <= 0) return defaultSize;
        return Math.min(size, maxSize);
    }
}
