package com.legalnow.api.auth.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legalnow.api.lawyer.domain.LawyerProfile;
import com.legalnow.api.user.User;

public record UserResponse(
    UUID id,
    String email,
    String name,
    String role,
    @JsonProperty("avatar_url") String avatarUrl,
    @JsonProperty("email_confirmed_at") String emailConfirmedAt,
    @JsonProperty("onboarding_completed_at") String onboardingCompletedAt,
    @JsonProperty("verified_at") String verifiedAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getRole().toDbValue(),
            user.getAvatarUrl(),
            user.getEmailConfirmedAt() == null ? null : user.getEmailConfirmedAt().toString(),
            null,
            null
        );
    }

    public static UserResponse from(User user, LawyerProfile lp) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getRole().toDbValue(),
            user.getAvatarUrl(),
            user.getEmailConfirmedAt() == null ? null : user.getEmailConfirmedAt().toString(),
            lp.getOnboardingCompletedAt() == null ? null : lp.getOnboardingCompletedAt().toString(),
            lp.getVerifiedAt() == null ? null : lp.getVerifiedAt().toString()
        );
    }
}
