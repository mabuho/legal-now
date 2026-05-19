package com.legalnow.api.consultation;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, UUID> {

    Page<Consultation> findByClientId(UUID clientId, Pageable pageable);

    Page<Consultation> findByLawyerId(UUID lawyerId, Pageable pageable);

    Page<Consultation> findByClientIdAndStatus(UUID clientId, ConsultationStatus status, Pageable pageable);

    Page<Consultation> findByLawyerIdAndStatus(UUID lawyerId, ConsultationStatus status, Pageable pageable);
}
