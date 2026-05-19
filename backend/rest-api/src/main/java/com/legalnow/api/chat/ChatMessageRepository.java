package com.legalnow.api.chat;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    Page<ChatMessage> findBySessionIdOrderBySentAtAsc(UUID sessionId, Pageable pageable);

    Slice<ChatMessage> findBySessionIdAndSentAtBeforeOrderBySentAtDesc(
        UUID sessionId,
        OffsetDateTime before,
        Pageable pageable
    );
}
