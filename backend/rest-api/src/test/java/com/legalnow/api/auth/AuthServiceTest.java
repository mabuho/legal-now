package com.legalnow.api.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.legalnow.api.auth.dto.AuthResponse;
import com.legalnow.api.auth.dto.LoginRequest;
import com.legalnow.api.auth.dto.RegisterRequest;
import com.legalnow.api.auth.exception.EmailAlreadyExistsException;
import com.legalnow.api.auth.exception.InvalidCredentialsException;
import com.legalnow.api.auth.exception.InvalidTokenException;
import com.legalnow.api.auth.refresh.RefreshToken;
import com.legalnow.api.auth.refresh.RefreshTokenRepository;
import com.legalnow.api.lawyer.domain.LawyerProfileRepository;
import com.legalnow.api.user.Role;
import com.legalnow.api.user.UserRepository;

@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = AuthServiceTest.PostgresInitializer.class)
class AuthServiceTest {

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
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private LawyerProfileRepository lawyerProfileRepository;

    @BeforeEach
    void cleanDb() {
        assumeTrue(isDockerAvailable(), "Docker is not available; skipping.");
        refreshTokenRepository.deleteAll();
        lawyerProfileRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void register_returnsTokensAndPersistsUser() {
        AuthResponse res = authService.register(new RegisterRequest(
            "alice@example.com", "password123", "Alice", Role.CLIENT, "12345678"
        ));

        assertNotNull(res);
        assertNotNull(res.tokens().accessToken());
        assertNotNull(res.tokens().refreshToken());
        assertEquals("Bearer", res.tokens().tokenType());
        assertEquals("alice@example.com", res.user().email());
        assertEquals("client", res.user().role());
        assertTrue(userRepository.existsByEmail("alice@example.com"));
    }

    @Test
    void register_duplicateEmail_throws() {
        authService.register(new RegisterRequest(
            "dup@example.com", "password123", "Dup", Role.CLIENT, "12345678"
        ));
        assertThrows(EmailAlreadyExistsException.class, () ->
            authService.register(new RegisterRequest(
                "dup@example.com", "password123", "Dup2", Role.LAWYER, "12345678"
            ))
        );
    }

    @Test
    void login_wrongPassword_throws() {
        authService.register(new RegisterRequest(
            "bob@example.com", "correct-password", "Bob", Role.LAWYER, "12345678"
        ));
        assertThrows(InvalidCredentialsException.class, () ->
            authService.login(new LoginRequest("bob@example.com", "wrong-password"))
        );
    }

    @Test
    void refresh_rotation_revokesOldTokenAndIssuesNew() {
        AuthResponse initial = authService.register(new RegisterRequest(
            "carol@example.com", "password123", "Carol", Role.CLIENT, "12345678"
        ));
        String originalRefresh = initial.tokens().refreshToken();

        AuthResponse rotated = authService.refresh(originalRefresh);

        assertNotNull(rotated.tokens().refreshToken());
        assertNotEquals(originalRefresh, rotated.tokens().refreshToken());

        // Old token must be revoked.
        String oldHash = com.legalnow.api.auth.refresh.RefreshTokenService.sha256(originalRefresh);
        RefreshToken old = refreshTokenRepository.findByTokenHash(oldHash).orElseThrow();
        assertNotNull(old.getRevokedAt());

        // Reusing the original refresh token must fail.
        assertThrows(InvalidTokenException.class, () -> authService.refresh(originalRefresh));
    }

    @Test
    void register_lawyer_withBarId_createsLawyerProfile() {
        authService.register(new RegisterRequest(
            "lawyer1@example.com", "password123", "Lawyer One", Role.LAWYER, "12345678"
        ));

        assertTrue(lawyerProfileRepository.existsById(
            userRepository.findByEmail("lawyer1@example.com").orElseThrow().getId()
        ));
        assertEquals("12345678",
            lawyerProfileRepository.findById(
                userRepository.findByEmail("lawyer1@example.com").orElseThrow().getId()
            ).orElseThrow().getBarId()
        );
    }

    @Test
    void register_lawyer_withoutBarId_createsLawyerProfileWithNullBarId() {
        authService.register(new RegisterRequest(
            "lawyer2@example.com", "password123", "Lawyer Two", Role.LAWYER, null
        ));

        var profile = lawyerProfileRepository.findById(
            userRepository.findByEmail("lawyer2@example.com").orElseThrow().getId()
        ).orElseThrow();
        assertNotNull(profile);
        org.junit.jupiter.api.Assertions.assertNull(profile.getBarId());
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
