package com.legalnow.api.chat;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.legalnow.api.auth.refresh.RefreshTokenRepository;
import com.legalnow.api.chat.dto.ChatMessageResponse;
import com.legalnow.api.chat.dto.ChatSessionResponse;
import com.legalnow.api.chat.dto.CreateChatMessageRequest;
import com.legalnow.api.chat.dto.CreateChatSessionRequest;
import com.legalnow.api.common.exception.ConflictException;
import com.legalnow.api.common.exception.ForbiddenException;
import com.legalnow.api.consultation.Consultation;
import com.legalnow.api.consultation.ConsultationRepository;
import com.legalnow.api.consultation.ConsultationStatus;
import com.legalnow.api.user.Role;
import com.legalnow.api.user.User;
import com.legalnow.api.user.UserRepository;

@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = ChatServiceTest.PostgresInitializer.class)
class ChatServiceTest {

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
    private ChatService chatService;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

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
    private User stranger;
    private Consultation consultation;

    @BeforeEach
    void setUp() {
        assumeTrue(isDockerAvailable(), "Docker is not available; skipping.");
        refreshTokenRepository.deleteAll();
        chatMessageRepository.deleteAll();
        chatSessionRepository.deleteAll();
        consultationRepository.deleteAll();
        userRepository.deleteAll();

        client = persistUser("client@example.com", "Client", Role.CLIENT);
        lawyer = persistUser("lawyer@example.com", "Lawyer", Role.LAWYER);
        stranger = persistUser("stranger@example.com", "Stranger", Role.CLIENT);

        Consultation c = new Consultation();
        c.setClientId(client.getId());
        c.setLawyerId(lawyer.getId());
        c.setStatus(ConsultationStatus.ACCEPTED);
        c.setTitle("Help");
        consultation = consultationRepository.save(c);
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void participantCreatesSession_strangerCannot() {
        authenticate(stranger.getId(), Role.CLIENT);
        assertThrows(ForbiddenException.class, () -> chatService.createSession(
            new CreateChatSessionRequest(consultation.getId())
        ));

        authenticate(client.getId(), Role.CLIENT);
        ChatSessionResponse session = chatService.createSession(
            new CreateChatSessionRequest(consultation.getId())
        );
        assertNotNull(session.id());
        assertEquals(consultation.getId(), session.consultationId());
    }

    @Test
    void duplicateSessionForSameConsultation_throwsConflict() {
        authenticate(client.getId(), Role.CLIENT);
        chatService.createSession(new CreateChatSessionRequest(consultation.getId()));
        assertThrows(ConflictException.class, () -> chatService.createSession(
            new CreateChatSessionRequest(consultation.getId())
        ));
    }

    @Test
    void postingAfterEndSession_throwsConflict() {
        authenticate(client.getId(), Role.CLIENT);
        ChatSessionResponse session = chatService.createSession(
            new CreateChatSessionRequest(consultation.getId())
        );
        chatService.endSession(session.id());
        assertThrows(ConflictException.class, () -> chatService.postMessage(
            session.id(),
            new CreateChatMessageRequest("late message")
        ));
    }

    @Test
    void listMessages_returnsOrderedAscendingBySentAt() throws InterruptedException {
        authenticate(client.getId(), Role.CLIENT);
        ChatSessionResponse session = chatService.createSession(
            new CreateChatSessionRequest(consultation.getId())
        );
        chatService.postMessage(session.id(), new CreateChatMessageRequest("first"));
        Thread.sleep(10);
        chatService.postMessage(session.id(), new CreateChatMessageRequest("second"));
        Thread.sleep(10);
        chatService.postMessage(session.id(), new CreateChatMessageRequest("third"));

        Page<ChatMessageResponse> page = chatService.listMessages(session.id(), PageRequest.of(0, 50));
        List<ChatMessageResponse> items = page.getContent();
        assertEquals(3, items.size());
        assertEquals("first", items.get(0).body());
        assertEquals("second", items.get(1).body());
        assertEquals("third", items.get(2).body());
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
