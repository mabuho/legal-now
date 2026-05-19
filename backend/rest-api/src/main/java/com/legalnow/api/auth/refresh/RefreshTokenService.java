package com.legalnow.api.auth.refresh;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.legalnow.api.auth.exception.InvalidTokenException;
import com.legalnow.api.user.User;
import com.legalnow.api.user.UserRepository;

@Service
public class RefreshTokenService {

    private static final SecureRandom RNG = new SecureRandom();
    private static final int TOKEN_BYTES = 64;

    private final RefreshTokenRepository repository;
    private final UserRepository userRepository;
    private final long refreshTtlDays;

    public RefreshTokenService(
        RefreshTokenRepository repository,
        UserRepository userRepository,
        @Value("${app.jwt.refresh-token-ttl-days}") long refreshTtlDays
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.refreshTtlDays = refreshTtlDays;
    }

    public record IssuedRefreshToken(RefreshToken entity, String rawToken) {}

    public record RotationResult(User user, IssuedRefreshToken issued) {}

    @Transactional
    public IssuedRefreshToken issue(User user) {
        String raw = randomToken();
        String hash = sha256(raw);

        RefreshToken token = new RefreshToken();
        token.setUserId(user.getId());
        token.setTokenHash(hash);
        token.setExpiresAt(OffsetDateTime.now().plusDays(refreshTtlDays));
        RefreshToken saved = repository.save(token);
        return new IssuedRefreshToken(saved, raw);
    }

    @Transactional
    public RotationResult validateAndRotate(String rawToken) {
        String hash = sha256(rawToken);
        RefreshToken token = repository.findByTokenHash(hash)
            .orElseThrow(() -> new InvalidTokenException("Refresh token not recognized"));

        OffsetDateTime now = OffsetDateTime.now();
        if (token.getRevokedAt() != null) {
            throw new InvalidTokenException("Refresh token revoked");
        }
        if (!token.getExpiresAt().isAfter(now)) {
            throw new InvalidTokenException("Refresh token expired");
        }

        User user = userRepository.findById(token.getUserId())
            .orElseThrow(() -> new InvalidTokenException("Token owner no longer exists"));

        token.setRevokedAt(now);
        repository.save(token);

        IssuedRefreshToken issued = issue(user);
        return new RotationResult(user, issued);
    }

    @Transactional
    public void revoke(String rawToken) {
        String hash = sha256(rawToken);
        repository.findByTokenHash(hash).ifPresent(t -> {
            if (t.getRevokedAt() == null) {
                t.setRevokedAt(OffsetDateTime.now());
                repository.save(t);
            }
        });
    }

    @Transactional
    public void revokeAllForUser(UUID userId) {
        repository.revokeAllForUser(userId, OffsetDateTime.now());
    }

    private static String randomToken() {
        byte[] buf = new byte[TOKEN_BYTES];
        RNG.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }

    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
