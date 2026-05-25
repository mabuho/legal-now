package com.legalnow.api.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.legalnow.api.auth.dto.AuthResponse;
import com.legalnow.api.auth.dto.ConfirmEmailResponse;
import com.legalnow.api.auth.dto.LoginRequest;
import com.legalnow.api.auth.dto.RegisterRequest;
import com.legalnow.api.auth.dto.TokenResponse;
import com.legalnow.api.auth.dto.UserResponse;
import com.legalnow.api.auth.exception.EmailAlreadyExistsException;
import com.legalnow.api.auth.exception.InvalidCredentialsException;
import com.legalnow.api.auth.exception.TokenExpiredException;
import com.legalnow.api.auth.exception.TokenInvalidException;
import com.legalnow.api.auth.refresh.RefreshTokenService;
import com.legalnow.api.auth.refresh.RefreshTokenService.IssuedRefreshToken;
import com.legalnow.api.auth.refresh.RefreshTokenService.RotationResult;
import com.legalnow.api.email.EmailService;
import com.legalnow.api.lawyer.domain.LawyerProfile;
import com.legalnow.api.lawyer.domain.LawyerProfileRepository;
import com.legalnow.api.user.Role;
import com.legalnow.api.user.User;
import com.legalnow.api.user.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final LawyerProfileRepository lawyerProfileRepository;
    private final EmailService emailService;

    public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        RefreshTokenService refreshTokenService,
        LawyerProfileRepository lawyerProfileRepository,
        EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.lawyerProfileRepository = lawyerProfileRepository;
        this.emailService = emailService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new EmailAlreadyExistsException("Email already registered");
        }
        User user = new User();
        user.setEmail(req.email());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setName(req.name());
        user.setRole(req.role());
        User saved = userRepository.save(user);

        String confirmToken = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
        confirmToken = confirmToken.substring(0, 64);
        saved.setEmailConfirmToken(confirmToken);
        saved.setEmailConfirmExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));
        userRepository.save(saved);
        emailService.sendConfirmationEmail(saved.getEmail(), confirmToken);

        if (saved.getRole() == Role.LAWYER) {
            LawyerProfile lp = new LawyerProfile();
            lp.setUser(saved);
            lp.setBarId(req.barId());
            lp.setLanguages(List.of());
            lawyerProfileRepository.save(lp);
        }

        return buildAuthResponse(saved);
    }

    @Transactional
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
            .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse refresh(String rawRefreshToken) {
        RotationResult result = refreshTokenService.validateAndRotate(rawRefreshToken);
        String accessToken = jwtService.generateAccessToken(result.user());
        TokenResponse tokens = TokenResponse.bearer(
            accessToken,
            result.issued().rawToken(),
            jwtService.getAccessTtlSeconds()
        );
        return new AuthResponse(UserResponse.from(result.user()), tokens);
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        // No-op if not found — don't leak token existence.
        refreshTokenService.revoke(rawRefreshToken);
    }

    @Transactional
    public ConfirmEmailResponse confirmEmail(String token) {
        User user = userRepository.findByEmailConfirmToken(token)
            .orElseThrow(TokenInvalidException::new);
        if (user.getEmailConfirmExpiresAt().isBefore(Instant.now())) {
            throw new TokenExpiredException();
        }
        user.setEmailConfirmedAt(Instant.now());
        user.setEmailConfirmToken(null);
        user.setEmailConfirmExpiresAt(null);
        userRepository.save(user);
        return new ConfirmEmailResponse("Email confirmed", true);
    }

    @Transactional
    public void resendConfirmation(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getEmailConfirmedAt() != null) return;
        String token = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
        token = token.substring(0, 64);
        user.setEmailConfirmToken(token);
        user.setEmailConfirmExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));
        userRepository.save(user);
        emailService.sendConfirmationEmail(user.getEmail(), token);
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        IssuedRefreshToken issued = refreshTokenService.issue(user);
        TokenResponse tokens = TokenResponse.bearer(
            accessToken,
            issued.rawToken(),
            jwtService.getAccessTtlSeconds()
        );
        return new AuthResponse(UserResponse.from(user), tokens);
    }
}
