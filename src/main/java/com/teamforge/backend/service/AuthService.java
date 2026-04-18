package com.teamforge.backend.service;

import com.teamforge.backend.dto.auth.AuthResult;
import com.teamforge.backend.dto.auth.LoginRequest;
import com.teamforge.backend.dto.auth.RegisterRequest;
import com.teamforge.backend.model.RefreshToken;
import com.teamforge.backend.model.User;
import com.teamforge.backend.model.enums.Role;
import com.teamforge.backend.repository.RefreshTokenRepository;
import com.teamforge.backend.repository.UserRepository;
import com.teamforge.backend.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository  refreshTokenRepository;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpirationMs;

    @Transactional
    public AuthResult register(RegisterRequest request) {
        var user = User.builder()
                .nickname(request.nickname())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .enabled(true)
                .build();

        userRepository.save(user);

        var securityUser = new SecurityUser(user);
        var accessToken = jwtService.generateToken(securityUser);
        var refreshToken = createAndSaveRefreshToken(user);

        return AuthResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Transactional
    public AuthResult authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        var user = userRepository.findByEmail(request.email())
                .orElseThrow();

        var securityUser = new SecurityUser(user);
        var accessToken = jwtService.generateToken(securityUser);
        var refreshToken = createAndSaveRefreshToken(user);

        return AuthResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Transactional
    public AuthResult refreshToken(String token) {

        RefreshToken storedToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (storedToken.isInvalid()) {
            throw new IllegalArgumentException("Refresh token is expired or revoked");
        }

        var user = storedToken.getUser();
        var securityUser = new SecurityUser(user);

        var newAccessToken = jwtService.generateToken(securityUser);

        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);
        var newRefreshToken = createAndSaveRefreshToken(user);

        return AuthResult.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .build();
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    private RefreshToken createAndSaveRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenExpirationMs))
                .revoked(false)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }
}
