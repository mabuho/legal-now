package com.legalnow.api.consultation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.legalnow.api.auth.refresh.RefreshTokenRepository;
import com.legalnow.api.common.exception.BadRequestException;
import com.legalnow.api.common.exception.ConflictException;
import com.legalnow.api.common.exception.ForbiddenException;
import com.legalnow.api.consultation.dto.ConsultationResponse;
import com.legalnow.api.consultation.dto.CreateConsultationRequest;
import com.legalnow.api.consultation.dto.StatusTransitionRequest;
import com.legalnow.api.user.Role;
import com.legalnow.api.user.User;
import com.legalnow.api.user.UserRepository;

@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = ConsultationServiceTest.PostgresInitializer.class)
class ConsultationServiceTest {

    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("legalnow")
        .withUsername("legalnow")
        .withPassword("legalnow");

    static {
        if (isDockerAvailable()) {
            POSTGRES.start();
        }
    }

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User client;
    private User lawyer;
    private User otherClient;

    @BeforeEach
    void setUp() {
        assumeTrue(isDockerAvailable(), "Docker is not available; skipping.");
        refreshTokenRepository.deleteAll();
        consultationRepository.deleteAll();
        userRepository.deleteAll();

        client = persistUser("c@example.com", "Client", Role.CLIENT);
        lawyer = persistUser("l@example.com", "Lawyer", Role.LAWYER);
        otherClient = persistUser("c2@example.com", "Client2", Role.CLIENT);
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void clientCreatesConsultation_defaultsToPending() {
        authenticate(client.getId(), Role.CLIENT);
        ConsultationResponse res = consultationService.create(new CreateConsultationRequest(
            lawyer.getId(), "Need help", "Description", null
        ));
        assertNotNull(res.id());
        assertEquals("pending", res.status());
        assertEquals(client.getId(), res.clientId());
        assertEquals(lawyer.getId(), res.lawyerId());
        // Embedded participant objects (Option A enrichment).
        assertNotNull(res.client());
        assertNotNull(res.lawyer());
        assertEquals(client.getId(), res.client().id());
        assertEquals("Client", res.client().name());
        assertEquals("client", res.client().role());
        assertEquals(lawyer.getId(), res.lawyer().id());
        assertEquals("Lawyer", res.lawyer().name());
        assertEquals("lawyer", res.lawyer().role());
    }

    @Test
    void create_targetIsNotLawyer_throwsBadRequest() {
        authenticate(client.getId(), Role.CLIENT);
        assertThrows(BadRequestException.class, () -> consultationService.create(new CreateConsultationRequest(
            otherClient.getId(), "Wrong target", null, null
        )));
    }

    @Test
    void create_lawyerIdEqualsClientId_throwsBadRequest() {
        authenticate(client.getId(), Role.CLIENT);
        assertThrows(BadRequestException.class, () -> consultationService.create(new CreateConsultationRequest(
            client.getId(), "Self", null, null
        )));
    }

    @Test
    void lawyer_transitionsPendingToAccepted_succeeds_clientCannot() {
        authenticate(client.getId(), Role.CLIENT);
        ConsultationResponse created = consultationService.create(new CreateConsultationRequest(
            lawyer.getId(), "Help me", null, null
        ));

        // Client trying to accept their own pending consultation must fail.
        assertThrows(ForbiddenException.class, () -> consultationService.transition(
            created.id(),
            new StatusTransitionRequest(ConsultationStatus.ACCEPTED, null)
        ));

        authenticate(lawyer.getId(), Role.LAWYER);
        ConsultationResponse accepted = consultationService.transition(
            created.id(),
            new StatusTransitionRequest(ConsultationStatus.ACCEPTED, null)
        );
        assertEquals("accepted", accepted.status());
        // Transition response also carries embedded participants.
        assertNotNull(accepted.client());
        assertNotNull(accepted.lawyer());
        assertEquals(client.getId(), accepted.client().id());
        assertEquals(lawyer.getId(), accepted.lawyer().id());
    }

    @Test
    void invalidTransition_pendingToInProgress_throwsConflict() {
        authenticate(client.getId(), Role.CLIENT);
        ConsultationResponse created = consultationService.create(new CreateConsultationRequest(
            lawyer.getId(), "Help me", null, null
        ));

        authenticate(lawyer.getId(), Role.LAWYER);
        assertThrows(ConflictException.class, () -> consultationService.transition(
            created.id(),
            new StatusTransitionRequest(ConsultationStatus.IN_PROGRESS, null)
        ));
    }

    private void authenticate(UUID userId, Role role) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            userId,
            null,
            List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private User persistUser(String email, String name, Role role) {
        User u = new User();
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode("password123"));
        u.setName(name);
        u.setRole(role);
        return userRepository.save(u);
    }

    private static boolean isDockerAvailable() {
        try {
            DockerClientFactory.instance().client();
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    static class PostgresInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            if (!isDockerAvailable()) {
                return;
            }
            TestPropertyValues.of(
                "spring.datasource.url=" + POSTGRES.getJdbcUrl(),
                "spring.datasource.username=" + POSTGRES.getUsername(),
                "spring.datasource.password=" + POSTGRES.getPassword(),
                "spring.jpa.hibernate.ddl-auto=validate",
                "spring.flyway.enabled=true",
                "app.jwt.secret=test-jwt-secret-that-is-definitely-at-least-32-bytes-long-for-hs256"
            ).applyTo(applicationContext.getEnvironment());
        }
    }
}
