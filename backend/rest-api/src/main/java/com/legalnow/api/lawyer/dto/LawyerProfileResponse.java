package com.legalnow.api.lawyer.dto;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legalnow.api.lawyer.domain.LawyerProfile;
import com.legalnow.api.lawyer.domain.Specialization;

public record LawyerProfileResponse(
    @JsonProperty("user_id") UUID userId,
    @JsonProperty("name") String name,
    @JsonProperty("avatar_url") String avatarUrl,
    @JsonProperty("bar_id") String barId,
    @JsonProperty("bio") String bio,
    @JsonProperty("languages") List<String> languages,
    @JsonProperty("specializations") List<SpecializationResponse> specializations,
    @JsonProperty("verified_at") OffsetDateTime verifiedAt,
    @JsonProperty("created_at") OffsetDateTime createdAt,
    @JsonProperty("updated_at") OffsetDateTime updatedAt
) {
    public static LawyerProfileResponse from(LawyerProfile lp) {
        List<SpecializationResponse> specs = lp.getSpecializations().stream()
            .sorted(Comparator.comparing(Specialization::getCode))
            .map(SpecializationResponse::from)
            .toList();
        String name = lp.getUser() != null ? lp.getUser().getName() : null;
        String avatar = lp.getUser() != null ? lp.getUser().getAvatarUrl() : null;
        return new LawyerProfileResponse(
            lp.getUserId(),
            name,
            avatar,
            lp.getBarId(),
            lp.getBio(),
            List.copyOf(lp.getLanguages()),
            specs,
            lp.getVerifiedAt(),
            lp.getCreatedAt(),
            lp.getUpdatedAt()
        );
    }
}
