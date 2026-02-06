package com.teamforge.backend.service;

import com.teamforge.backend.dto.auth.AuthResponse;
import com.teamforge.backend.dto.auth.LoginRequest;
import com.teamforge.backend.dto.auth.RegisterRequest;
import com.teamforge.backend.model.User;
import com.teamforge.backend.model.enums.Role;
import com.teamforge.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        var user = User.builder()
                .nickname(request.nickname())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .enabled(true)
                .build();

        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        var user = userRepository.findByEmail(request.email())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}
