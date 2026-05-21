package com.legalnow.api.lawyer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.legalnow.api.common.SecurityUtils;
import com.legalnow.api.common.exception.BadRequestException;
import com.legalnow.api.common.exception.ConflictException;
import com.legalnow.api.common.exception.ForbiddenException;
import com.legalnow.api.common.exception.NotFoundException;
import com.legalnow.api.lawyer.domain.LawyerProfile;
import com.legalnow.api.lawyer.domain.LawyerProfileRepository;
import com.legalnow.api.lawyer.domain.Specialization;
import com.legalnow.api.lawyer.domain.SpecializationRepository;
import com.legalnow.api.lawyer.dto.CreateLawyerProfileRequest;
import com.legalnow.api.lawyer.dto.LawyerProfileResponse;
import com.legalnow.api.lawyer.dto.SpecializationResponse;
import com.legalnow.api.lawyer.dto.UpdateLawyerProfileRequest;
import com.legalnow.api.user.Role;
import com.legalnow.api.user.User;
import com.legalnow.api.user.UserRepository;

@Service
public class LawyerService {

    private final LawyerProfileRepository lawyerRepository;
    private final SpecializationRepository specializationRepository;
    private final UserRepository userRepository;

    public LawyerService(
        LawyerProfileRepository lawyerRepository,
        SpecializationRepository specializationRepository,
        UserRepository userRepository
    ) {
        this.lawyerRepository = lawyerRepository;
        this.specializationRepository = specializationRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<LawyerProfileResponse> listVerified(String specializationCode, Pageable pageable) {
        Page<LawyerProfile> page = (specializationCode == null || specializationCode.isBlank())
            ? lawyerRepository.findAllVerified(pageable)
            : lawyerRepository.findAllVerifiedBySpecializationCode(specializationCode, pageable);
        return page.map(LawyerProfileResponse::from);
    }

    @Transactional(readOnly = true)
    public LawyerProfileResponse getById(UUID id) {
        LawyerProfile lp = lawyerRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new NotFoundException("Lawyer profile not found"));
        return LawyerProfileResponse.from(lp);
    }

    @Transactional(readOnly = true)
    public LawyerProfileResponse getMe() {
        UUID callerId = SecurityUtils.currentUserId();
        LawyerProfile lp = lawyerRepository.findByIdWithDetails(callerId)
            .orElseThrow(() -> new NotFoundException("Lawyer profile not found"));
        return LawyerProfileResponse.from(lp);
    }

    @Transactional
    public LawyerProfileResponse createMe(CreateLawyerProfileRequest req) {
        UUID callerId = SecurityUtils.currentUserId();
        User user = userRepository.findById(callerId)
            .orElseThrow(() -> new NotFoundException("User not found"));
        if (user.getRole() != Role.LAWYER) {
            throw new ForbiddenException("Only lawyers may create a lawyer profile");
        }
        if (lawyerRepository.existsById(callerId)) {
            throw new ConflictException("Lawyer profile already exists");
        }

        LawyerProfile lp = new LawyerProfile();
        lp.setUser(user);
        lp.setBarId(req.barId());
        lp.setBio(req.bio());
        lp.setLanguages(normalizeLanguages(req.languages()));
        lp.setSpecializations(resolveSpecializations(req.specializationCodes()));

        LawyerProfile saved = lawyerRepository.save(lp);
        return LawyerProfileResponse.from(reload(saved.getUserId()));
    }

    @Transactional
    public LawyerProfileResponse updateMe(UpdateLawyerProfileRequest req) {
        UUID callerId = SecurityUtils.currentUserId();
        LawyerProfile lp = lawyerRepository.findByIdWithDetails(callerId)
            .orElseThrow(() -> new NotFoundException("Lawyer profile not found"));

        if (req.barId() != null) {
            lp.setBarId(req.barId());
        }
        if (req.bio() != null) {
            lp.setBio(req.bio());
        }
        if (req.languages() != null) {
            lp.setLanguages(normalizeLanguages(req.languages()));
        }
        if (req.specializationCodes() != null) {
            lp.setSpecializations(resolveSpecializations(req.specializationCodes()));
        }

        lawyerRepository.save(lp);
        return LawyerProfileResponse.from(reload(lp.getUserId()));
    }

    @Transactional(readOnly = true)
    public List<SpecializationResponse> listSpecializations() {
        return specializationRepository.findAll().stream()
            .sorted((a, b) -> a.getCode().compareTo(b.getCode()))
            .map(SpecializationResponse::from)
            .toList();
    }

    private Set<Specialization> resolveSpecializations(Set<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return new HashSet<>();
        }
        Set<Specialization> resolved = new HashSet<>();
        for (String code : codes) {
            Specialization s = specializationRepository.findByCode(code)
                .orElseThrow(() -> new BadRequestException("Unknown specialization code: " + code));
            resolved.add(s);
        }
        return resolved;
    }

    private List<String> normalizeLanguages(List<String> langs) {
        if (langs == null) {
            return List.of();
        }
        List<String> out = new ArrayList<>(langs.size());
        for (String l : langs) {
            if (l == null) continue;
            String t = l.trim().toLowerCase();
            if (!t.isEmpty() && !out.contains(t)) {
                out.add(t);
            }
        }
        return out;
    }

    private LawyerProfile reload(UUID id) {
        return lawyerRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new NotFoundException("Lawyer profile not found"));
    }
}
