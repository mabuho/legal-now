package com.legalnow.api.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.legalnow.api.auth.exception.InvalidTokenException;
import com.legalnow.api.user.Role;
import com.legalnow.api.user.User;

import io.jsonwebtoken.Claims;

class JwtServiceTest {

    private static final String SECRET = "unit-test-jwt-secret-min-32-bytes-long-for-hs256-signing";

    private JwtService newService(long ttlMinutes) {
        JwtService svc = new JwtService(SECRET, ttlMinutes);
        svc.init();
        return svc;
    }

    private User newUser() {
        User u = new User();
        // Assign id since DB normally does it.
        try {
            Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(u, UUID.randomUUID());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
        u.setEmail("test@example.com");
        u.setName("Test");
        u.setRole(Role.CLIENT);
        u.setPasswordHash("x");
        return u;
    }

    @Test
    void generateAndParse_roundtrip() {
        JwtService svc = newService(15);
        User user = newUser();

        String token = svc.generateAccessToken(user);
        assertNotNull(token);

        Claims claims = svc.parseClaims(token);
        assertEquals(user.getId().toString(), claims.getSubject());
        assertEquals("test@example.com", claims.get("email", String.class));
        assertEquals("client", claims.get("role", String.class));
        assertEquals("Test", claims.get("name", String.class));

        Optional<UUID> extracted = svc.extractUserId(token);
        assertTrue(extracted.isPresent());
        assertEquals(user.getId(), extracted.get());
        assertTrue(svc.isValid(token));
    }

    @Test
    void expiredToken_returnsEmptyAndIsInvalid() throws InterruptedException {
        // TTL = 0 minutes → exp == iat, JJWT treats as expired immediately.
        JwtService svc = newService(0);
        User user = newUser();
        String token = svc.generateAccessToken(user);

        // Small wait to ensure clock moves past exp.
        Thread.sleep(50);

        assertFalse(svc.isValid(token));
        assertTrue(svc.extractUserId(token).isEmpty());
        assertThrows(InvalidTokenException.class, () -> svc.parseClaims(token));
    }

    @Test
    void malformedToken_throws() {
        JwtService svc = newService(15);
        assertThrows(InvalidTokenException.class, () -> svc.parseClaims("not.a.valid.jwt"));
        assertThrows(InvalidTokenException.class, () -> svc.parseClaims(""));
        assertFalse(svc.isValid("garbage"));
    }

    @Test
    void shortSecret_failsAtInit() {
        JwtService svc = new JwtService("too-short", 15);
        assertThrows(IllegalStateException.class, svc::init);
    }
}
