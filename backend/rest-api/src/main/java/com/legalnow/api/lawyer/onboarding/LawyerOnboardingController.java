package com.legalnow.api.lawyer.onboarding;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.legalnow.api.user.UserRepository;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lawyers/me/onboarding")
@RequiredArgsConstructor
public class LawyerOnboardingController {

    private final LawyerOnboardingService onboardingService;
    private final UserRepository userRepository;

    @PostMapping("/complete")
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<OnboardingCompleteResponse> complete(
            @AuthenticationPrincipal UserDetails principal) {
        var user = userRepository.findByEmail(principal.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(onboardingService.completeOnboarding(user.getId()));
    }
}
