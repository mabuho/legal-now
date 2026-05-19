package com.legalnow.api.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.legalnow.api.auth.dto.AuthResponse;
import com.legalnow.api.auth.dto.LoginRequest;
import com.legalnow.api.auth.dto.RegisterRequest;
import com.legalnow.api.auth.dto.TokenResponse;
import com.legalnow.api.auth.dto.UserResponse;
import com.legalnow.api.auth.exception.EmailAlreadyExistsException;
import com.legalnow.api.auth.exception.InvalidCredentialsException;
import com.legalnow.api.auth.refresh.RefreshTokenService;
import com.legalnow.api.auth.refresh.RefreshTokenService.IssuedRefreshToken;
import com.legalnow.api.auth.refresh.RefreshTokenService.RotationResult;
import com.legalnow.api.user.User;
import com.legalnow.api.user.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
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
