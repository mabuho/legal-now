package com.legalnow.api.lawyer.onboarding;

import com.legalnow.api.lawyer.VerificationService;
import com.legalnow.api.lawyer.domain.LawyerProfile;
import com.legalnow.api.lawyer.domain.LawyerProfileRepository;
import com.legalnow.api.sep.SepValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LawyerOnboardingService {

    private final LawyerProfileRepository lawyerProfileRepository;
    private final SepValidationService sepValidationService;
    private final VerificationService verificationService;

    @Transactional
    public OnboardingCompleteResponse completeOnboarding(UUID lawyerId) {
        LawyerProfile profile = lawyerProfileRepository.findById(lawyerId)
            .orElseThrow(() -> new RuntimeException("Lawyer profile not found: " + lawyerId));

        profile.setOnboardingCompletedAt(OffsetDateTime.now());

        var result = sepValidationService.validateLawyer(lawyerId);
        if (result.found()) {
            profile.setVerifiedAt(OffsetDateTime.now());
            lawyerProfileRepository.save(profile);
            verificationService.recordSystemAttempt(lawyerId, "approved", "SEP match found");
            return new OnboardingCompleteResponse("verified", "Cédula verificada via SEP", true);
        } else {
            lawyerProfileRepository.save(profile);
            verificationService.recordSystemAttempt(lawyerId, "rejected", "No SEP match — admin review pending");
            return new OnboardingCompleteResponse("pending", "No se encontró cédula. Revisión manual pendiente.", false);
        }
    }
}
