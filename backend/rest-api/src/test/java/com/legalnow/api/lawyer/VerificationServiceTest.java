package com.legalnow.api.lawyer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.legalnow.api.common.exception.NotFoundException;
import com.legalnow.api.lawyer.domain.LawyerProfile;
import com.legalnow.api.lawyer.domain.LawyerProfileRepository;
import com.legalnow.api.lawyer.domain.VerificationAttempt;
import com.legalnow.api.lawyer.domain.VerificationAttemptRepository;

@ExtendWith(MockitoExtension.class)
class VerificationServiceTest {

    @Mock
    private VerificationAttemptRepository attemptRepository;

    @Mock
    private LawyerProfileRepository lawyerProfileRepository;

    @InjectMocks
    private VerificationService verificationService;

    @Test
    void requestVerification_lawyerNotFound_throwsNotFound() {
        UUID lawyerId = UUID.randomUUID();
        when(lawyerProfileRepository.existsById(lawyerId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> verificationService.requestVerification(lawyerId));
    }

    @Test
    void requestVerification_happyPath_createsPendingAttempt() {
        UUID lawyerId = UUID.randomUUID();
        when(lawyerProfileRepository.existsById(lawyerId)).thenReturn(true);
        when(attemptRepository.save(any(VerificationAttempt.class))).thenAnswer(i -> i.getArgument(0));

        verificationService.requestVerification(lawyerId);

        ArgumentCaptor<VerificationAttempt> captor = ArgumentCaptor.forClass(VerificationAttempt.class);
        verify(attemptRepository).save(captor.capture());
        assert "pending".equals(captor.getValue().getStatus());
        assert lawyerId.equals(captor.getValue().getLawyerId());
    }

    @Test
    void approve_happyPath_setsVerifiedAtAndApproves() {
        UUID lawyerId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();

        LawyerProfile profile = new LawyerProfile();
        profile.setUserId(lawyerId);

        VerificationAttempt pending = new VerificationAttempt();
        pending.setId(UUID.randomUUID());
        pending.setLawyerId(lawyerId);
        pending.setStatus("pending");

        when(lawyerProfileRepository.findById(lawyerId)).thenReturn(Optional.of(profile));
        when(attemptRepository.findByLawyerId(lawyerId)).thenReturn(List.of(pending));
        when(attemptRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(lawyerProfileRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        verificationService.approve(adminId, lawyerId, "Looks good");

        assert "approved".equals(pending.getStatus());
        assert adminId.equals(pending.getReviewedBy());
        assertNotNull(pending.getReviewedAt());
        assertNotNull(profile.getVerifiedAt());
        verify(lawyerProfileRepository).save(profile);
    }

    @Test
    void reject_noPendingAttempt_throwsNotFound() {
        UUID lawyerId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();

        when(lawyerProfileRepository.existsById(lawyerId)).thenReturn(true);
        when(attemptRepository.findByLawyerId(lawyerId)).thenReturn(List.of());

        assertThrows(NotFoundException.class, () -> verificationService.reject(adminId, lawyerId, "Not valid"));
    }
}
