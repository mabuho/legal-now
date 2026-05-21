package com.legalnow.api.lawyer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

@ExtendWith(MockitoExtension.class)
class LawyerServiceTest {

    @Mock
    private LawyerProfileRepository lawyerRepository;

    @Mock
    private SpecializationRepository specializationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LawyerService lawyerService;

    private UUID callerId;
    private User lawyerUser;
    private User clientUser;

    @BeforeEach
    void setUp() {
        callerId = UUID.randomUUID();

        lawyerUser = new User();
        lawyerUser.setId(callerId);
        lawyerUser.setName("Test Lawyer");
        lawyerUser.setEmail("lawyer@test.com");
        lawyerUser.setRole(Role.LAWYER);

        clientUser = new User();
        clientUser.setId(callerId);
        clientUser.setName("Test Client");
        clientUser.setEmail("client@test.com");
        clientUser.setRole(Role.CLIENT);
    }

    // --- createMe ---

    @Test
    void createMe_nonLawyerRole_throwsForbidden() {
        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::currentUserId).thenReturn(callerId);
            when(userRepository.findById(callerId)).thenReturn(Optional.of(clientUser));

            CreateLawyerProfileRequest req = new CreateLawyerProfileRequest(
                "BAR001", "Bio text", List.of("es"), null
            );

            assertThrows(ForbiddenException.class, () -> lawyerService.createMe(req));
            verify(lawyerRepository, never()).save(any());
        }
    }

    @Test
    void createMe_profileAlreadyExists_throwsConflict() {
        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::currentUserId).thenReturn(callerId);
            when(userRepository.findById(callerId)).thenReturn(Optional.of(lawyerUser));
            when(lawyerRepository.existsById(callerId)).thenReturn(true);

            CreateLawyerProfileRequest req = new CreateLawyerProfileRequest(
                "BAR001", "Bio text", List.of("es"), null
            );

            assertThrows(ConflictException.class, () -> lawyerService.createMe(req));
            verify(lawyerRepository, never()).save(any());
        }
    }

    @Test
    void createMe_unknownSpecializationCode_throwsBadRequest() {
        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::currentUserId).thenReturn(callerId);
            when(userRepository.findById(callerId)).thenReturn(Optional.of(lawyerUser));
            when(lawyerRepository.existsById(callerId)).thenReturn(false);
            when(specializationRepository.findByCode("UNKNOWN")).thenReturn(Optional.empty());

            CreateLawyerProfileRequest req = new CreateLawyerProfileRequest(
                "BAR001", "Bio", List.of("es"), Set.of("UNKNOWN")
            );

            assertThrows(BadRequestException.class, () -> lawyerService.createMe(req));
            verify(lawyerRepository, never()).save(any());
        }
    }

    @Test
    void createMe_happyPath_savesAndReturnsProfile() {
        Specialization spec = buildSpec((short) 1, "civil", "Civil Law");

        LawyerProfile savedProfile = buildProfile(callerId, lawyerUser, "BAR001", "Bio", Set.of(spec));

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::currentUserId).thenReturn(callerId);
            when(userRepository.findById(callerId)).thenReturn(Optional.of(lawyerUser));
            when(lawyerRepository.existsById(callerId)).thenReturn(false);
            when(specializationRepository.findByCode("civil")).thenReturn(Optional.of(spec));
            when(lawyerRepository.save(any(LawyerProfile.class))).thenReturn(savedProfile);
            when(lawyerRepository.findByIdWithDetails(callerId)).thenReturn(Optional.of(savedProfile));

            CreateLawyerProfileRequest req = new CreateLawyerProfileRequest(
                "BAR001", "Bio", List.of("es"), Set.of("civil")
            );

            LawyerProfileResponse response = lawyerService.createMe(req);

            assertNotNull(response);
            assertEquals(callerId, response.userId());
            assertEquals("BAR001", response.barId());
            verify(lawyerRepository).save(any(LawyerProfile.class));
        }
    }

    // --- updateMe ---

    @Test
    void updateMe_profileNotFound_throwsNotFound() {
        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::currentUserId).thenReturn(callerId);
            when(lawyerRepository.findByIdWithDetails(callerId)).thenReturn(Optional.empty());

            UpdateLawyerProfileRequest req = new UpdateLawyerProfileRequest(
                "BAR002", null, null, null
            );

            assertThrows(NotFoundException.class, () -> lawyerService.updateMe(req));
            verify(lawyerRepository, never()).save(any());
        }
    }

    @Test
    void updateMe_partialPatch_onlyNonNullFieldsApplied() {
        LawyerProfile existing = buildProfile(callerId, lawyerUser, "OLD_BAR", "Old bio", Set.of());
        LawyerProfile reloaded = buildProfile(callerId, lawyerUser, "NEW_BAR", "Old bio", Set.of());

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::currentUserId).thenReturn(callerId);
            when(lawyerRepository.findByIdWithDetails(callerId))
                .thenReturn(Optional.of(existing))
                .thenReturn(Optional.of(reloaded));
            when(lawyerRepository.save(any(LawyerProfile.class))).thenReturn(existing);

            UpdateLawyerProfileRequest req = new UpdateLawyerProfileRequest(
                "NEW_BAR", null, null, null
            );

            LawyerProfileResponse response = lawyerService.updateMe(req);

            assertEquals("NEW_BAR", existing.getBarId());
            assertEquals("Old bio", existing.getBio());
            assertNotNull(response);
            verify(lawyerRepository).save(existing);
        }
    }

    // --- getMe ---

    @Test
    void getMe_profileNotFound_throwsNotFound() {
        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::currentUserId).thenReturn(callerId);
            when(lawyerRepository.findByIdWithDetails(callerId)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> lawyerService.getMe());
        }
    }

    @Test
    void getMe_found_returnsResponse() {
        LawyerProfile profile = buildProfile(callerId, lawyerUser, "BAR001", "Bio", Set.of());

        try (MockedStatic<SecurityUtils> su = mockStatic(SecurityUtils.class)) {
            su.when(SecurityUtils::currentUserId).thenReturn(callerId);
            when(lawyerRepository.findByIdWithDetails(callerId)).thenReturn(Optional.of(profile));

            LawyerProfileResponse response = lawyerService.getMe();

            assertNotNull(response);
            assertEquals(callerId, response.userId());
        }
    }

    // --- getById ---

    @Test
    void getById_notFound_throwsNotFound() {
        UUID unknownId = UUID.randomUUID();
        when(lawyerRepository.findByIdWithDetails(unknownId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> lawyerService.getById(unknownId));
    }

    @Test
    void getById_found_returnsResponse() {
        UUID targetId = UUID.randomUUID();
        User u = new User();
        u.setId(targetId);
        u.setName("Other Lawyer");
        u.setRole(Role.LAWYER);
        LawyerProfile profile = buildProfile(targetId, u, "BAR999", "Bio", Set.of());

        when(lawyerRepository.findByIdWithDetails(targetId)).thenReturn(Optional.of(profile));

        LawyerProfileResponse response = lawyerService.getById(targetId);

        assertNotNull(response);
        assertEquals(targetId, response.userId());
    }

    // --- listVerified ---

    @Test
    void listVerified_noFilter_delegatesToFindAllVerified() {
        Pageable pageable = PageRequest.of(0, 10);
        LawyerProfile profile = buildProfile(callerId, lawyerUser, "BAR001", "Bio", Set.of());
        Page<LawyerProfile> pageResult = new PageImpl<>(List.of(profile), pageable, 1);

        when(lawyerRepository.findAllVerified(pageable)).thenReturn(pageResult);

        Page<LawyerProfileResponse> result = lawyerService.listVerified(null, pageable);

        assertEquals(1, result.getTotalElements());
        verify(lawyerRepository).findAllVerified(pageable);
        verify(lawyerRepository, never()).findAllVerifiedBySpecializationCode(any(), any());
    }

    @Test
    void listVerified_withBlankFilter_delegatesToFindAllVerified() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<LawyerProfile> pageResult = new PageImpl<>(List.of(), pageable, 0);

        when(lawyerRepository.findAllVerified(pageable)).thenReturn(pageResult);

        lawyerService.listVerified("   ", pageable);

        verify(lawyerRepository).findAllVerified(pageable);
        verify(lawyerRepository, never()).findAllVerifiedBySpecializationCode(any(), any());
    }

    @Test
    void listVerified_withSpecializationCode_delegatesToFindAllVerifiedBySpecializationCode() {
        Pageable pageable = PageRequest.of(0, 10);
        LawyerProfile profile = buildProfile(callerId, lawyerUser, "BAR001", "Bio", Set.of());
        Page<LawyerProfile> pageResult = new PageImpl<>(List.of(profile), pageable, 1);

        when(lawyerRepository.findAllVerifiedBySpecializationCode("civil", pageable)).thenReturn(pageResult);

        Page<LawyerProfileResponse> result = lawyerService.listVerified("civil", pageable);

        assertEquals(1, result.getTotalElements());
        verify(lawyerRepository).findAllVerifiedBySpecializationCode("civil", pageable);
        verify(lawyerRepository, never()).findAllVerified(any());
    }

    // --- listSpecializations ---

    @Test
    void listSpecializations_returnsSortedByCode() {
        Specialization s1 = buildSpec((short) 3, "tax", "Tax Law");
        Specialization s2 = buildSpec((short) 1, "civil", "Civil Law");
        Specialization s3 = buildSpec((short) 2, "labor", "Labor Law");

        when(specializationRepository.findAll()).thenReturn(List.of(s1, s2, s3));

        List<SpecializationResponse> result = lawyerService.listSpecializations();

        assertEquals(3, result.size());
        assertEquals("civil", result.get(0).code());
        assertEquals("labor", result.get(1).code());
        assertEquals("tax", result.get(2).code());
    }

    // --- helpers ---

    private Specialization buildSpec(short id, String code, String name) {
        Specialization s = new Specialization();
        s.setId(id);
        s.setCode(code);
        s.setName(name);
        return s;
    }

    private LawyerProfile buildProfile(UUID userId, User user, String barId, String bio,
                                        Set<Specialization> specializations) {
        LawyerProfile lp = new LawyerProfile();
        lp.setUserId(userId);
        lp.setUser(user);
        lp.setBarId(barId);
        lp.setBio(bio);
        lp.setLanguages(List.of("es"));
        lp.setSpecializations(specializations);
        return lp;
    }
}
