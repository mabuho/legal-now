package com.legalnow.api.lawyer;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.legalnow.api.common.exception.NotFoundException;
import com.legalnow.api.lawyer.domain.LawyerProfile;
import com.legalnow.api.lawyer.domain.LawyerProfileRepository;
import com.legalnow.api.lawyer.domain.VerificationAttempt;
import com.legalnow.api.lawyer.domain.VerificationAttemptRepository;

@Service
public class VerificationService {

    private final VerificationAttemptRepository attemptRepository;
    private final LawyerProfileRepository lawyerProfileRepository;

    public VerificationService(
        VerificationAttemptRepository attemptRepository,
        LawyerProfileRepository lawyerProfileRepository
    ) {
        this.attemptRepository = attemptRepository;
        this.lawyerProfileRepository = lawyerProfileRepository;
    }

    @Transactional
    public void requestVerification(UUID lawyerId) {
        if (!lawyerProfileRepository.existsById(lawyerId)) {
            throw new NotFoundException("Lawyer profile not found");
        }
        VerificationAttempt attempt = new VerificationAttempt();
        attempt.setId(UUID.randomUUID());
        attempt.setLawyerId(lawyerId);
        attempt.setStatus("pending");
        attemptRepository.save(attempt);
    }

    @Transactional
    public void approve(UUID adminId, UUID lawyerId, String notes) {
        LawyerProfile lp = lawyerProfileRepository.findById(lawyerId)
            .orElseThrow(() -> new NotFoundException("Lawyer profile not found"));

        VerificationAttempt attempt = latestPendingAttempt(lawyerId);
        attempt.setStatus("approved");
        attempt.setNotes(notes);
        attempt.setReviewedBy(adminId);
        attempt.setReviewedAt(OffsetDateTime.now());
        attemptRepository.save(attempt);

        lp.setVerifiedAt(OffsetDateTime.now());
        lawyerProfileRepository.save(lp);
    }

    @Transactional
    public void reject(UUID adminId, UUID lawyerId, String notes) {
        if (!lawyerProfileRepository.existsById(lawyerId)) {
            throw new NotFoundException("Lawyer profile not found");
        }
        VerificationAttempt attempt = latestPendingAttempt(lawyerId);
        attempt.setStatus("rejected");
        attempt.setNotes(notes);
        attempt.setReviewedBy(adminId);
        attempt.setReviewedAt(OffsetDateTime.now());
        attemptRepository.save(attempt);
    }

    private VerificationAttempt latestPendingAttempt(UUID lawyerId) {
        return attemptRepository.findByLawyerId(lawyerId).stream()
            .filter(a -> "pending".equals(a.getStatus()))
            .reduce((first, second) -> second)
            .orElseThrow(() -> new NotFoundException("No pending verification attempt found"));
    }
}
