package com.legalnow.api.chat;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {

    Optional<ChatSession> findByConsultationId(UUID consultationId);

    boolean existsByConsultationId(UUID consultationId);

    Page<ChatSession> findByConsultationIdIn(Collection<UUID> consultationIds, Pageable pageable);

    @Query("""
        SELECT s FROM ChatSession s
        WHERE s.consultationId IN (
            SELECT c.id FROM Consultation c
            WHERE c.clientId = :userId OR c.lawyerId = :userId
        )
        """)
    Page<ChatSession> findForParticipant(@Param("userId") UUID userId, Pageable pageable);
}
