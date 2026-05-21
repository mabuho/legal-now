package com.legalnow.api.lawyer.domain;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationAttemptRepository extends JpaRepository<VerificationAttempt, UUID> {

    List<VerificationAttempt> findByLawyerId(UUID lawyerId);
}
