package com.legalnow.api.chat;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.legalnow.api.chat.dto.ChatMessageResponse;
import com.legalnow.api.chat.dto.ChatSessionResponse;
import com.legalnow.api.chat.dto.CreateChatMessageRequest;
import com.legalnow.api.chat.dto.CreateChatSessionRequest;
import com.legalnow.api.common.SecurityUtils;
import com.legalnow.api.common.exception.ConflictException;
import com.legalnow.api.common.exception.ForbiddenException;
import com.legalnow.api.common.exception.NotFoundException;
import com.legalnow.api.consultation.Consultation;
import com.legalnow.api.consultation.ConsultationRepository;

@Service
public class ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ConsultationRepository consultationRepository;

    public ChatService(
        ChatSessionRepository chatSessionRepository,
        ChatMessageRepository chatMessageRepository,
        ConsultationRepository consultationRepository
    ) {
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.consultationRepository = consultationRepository;
    }

    @Transactional
    public ChatSessionResponse createSession(CreateChatSessionRequest req) {
        Consultation consultation = consultationRepository.findById(req.consultationId())
            .orElseThrow(() -> new NotFoundException("Consultation not found"));
        authorizeParticipant(consultation);

        if (chatSessionRepository.existsByConsultationId(consultation.getId())) {
            throw new ConflictException("Chat session already exists for this consultation");
        }

        ChatSession session = new ChatSession();
        session.setConsultationId(consultation.getId());
        ChatSession saved = chatSessionRepository.save(session);
        return ChatSessionResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public ChatSessionResponse getSession(UUID sessionId) {
        ChatSession session = loadSessionOrThrow(sessionId);
        authorizeSessionAccess(session);
        return ChatSessionResponse.from(session);
    }

    @Transactional(readOnly = true)
    public Page<ChatSessionResponse> listMine(Pageable pageable) {
        UUID callerId = SecurityUtils.currentUserId();
        return chatSessionRepository.findForParticipant(callerId, pageable)
            .map(ChatSessionResponse::from);
    }

    @Transactional
    public ChatSessionResponse endSession(UUID sessionId) {
        ChatSession session = loadSessionOrThrow(sessionId);
        authorizeSessionAccess(session);
        if (session.getEndedAt() == null) {
            session.setEndedAt(OffsetDateTime.now());
        }
        return ChatSessionResponse.from(chatSessionRepository.save(session));
    }

    @Transactional
    public ChatMessageResponse postMessage(UUID sessionId, CreateChatMessageRequest req) {
        ChatSession session = loadSessionOrThrow(sessionId);
        authorizeSessionAccess(session);
        if (session.getEndedAt() != null) {
            throw new ConflictException("session ended");
        }
        ChatMessage msg = new ChatMessage();
        msg.setSessionId(session.getId());
        msg.setSenderId(SecurityUtils.currentUserId());
        msg.setBody(req.body());
        return ChatMessageResponse.from(chatMessageRepository.save(msg));
    }

    @Transactional(readOnly = true)
    public Page<ChatMessageResponse> listMessages(UUID sessionId, Pageable pageable) {
        ChatSession session = loadSessionOrThrow(sessionId);
        authorizeSessionAccess(session);
        return chatMessageRepository.findBySessionIdOrderBySentAtAsc(session.getId(), pageable)
            .map(ChatMessageResponse::from);
    }

    private ChatSession loadSessionOrThrow(UUID sessionId) {
        return chatSessionRepository.findById(sessionId)
            .orElseThrow(() -> new NotFoundException("Chat session not found"));
    }

    // Caller must be client or lawyer on the consultation, or admin.
    private void authorizeSessionAccess(ChatSession session) {
        if (SecurityUtils.isAdmin()) {
            return;
        }
        Consultation consultation = consultationRepository.findById(session.getConsultationId())
            .orElseThrow(() -> new NotFoundException("Consultation not found"));
        authorizeParticipant(consultation);
    }

    private void authorizeParticipant(Consultation consultation) {
        if (SecurityUtils.isAdmin()) {
            return;
        }
        UUID callerId = SecurityUtils.currentUserId();
        if (!consultation.getClientId().equals(callerId) && !consultation.getLawyerId().equals(callerId)) {
            throw new ForbiddenException("Not a participant of this consultation");
        }
    }
}
