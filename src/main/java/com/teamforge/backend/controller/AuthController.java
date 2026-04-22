package com.teamforge.backend.controller;

import com.teamforge.backend.dto.auth.AuthResponse;
import com.teamforge.backend.dto.auth.AuthResult;
import com.teamforge.backend.dto.auth.LoginRequest;
import com.teamforge.backend.dto.auth.RegisterRequest;
import com.teamforge.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpirationMs;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResult result = authService.register(request);
        return buildResponseWithCookie(result);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResult result = authService.authenticate(request);
        return buildResponseWithCookie(result);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@CookieValue(name = "refresh_token") String refreshToken) {
        AuthResult result = authService.refreshToken(refreshToken);
        return buildResponseWithCookie(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
        if (refreshToken != null) {
            authService.logout(refreshToken);
        }

        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false) // todo: Change to 'true' in production (requires HTTPS)
                .path("/api/auth")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

    /**
     * Builds an HTTP response containing the Access Token in the JSON body
     * and the Refresh Token in a secure, HttpOnly cookie.
     * * @param result AuthResult containing both access and refresh tokens
     * @return ResponseEntity with the configured cookie and AuthResponse body
     */
    private ResponseEntity<AuthResponse> buildResponseWithCookie(AuthResult result) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", result.refreshToken())
                .httpOnly(true)       // Hide from JavaScript on the frontend (XSS protection)
                .secure(false)        // todo: Change to 'true' in production (requires HTTPS)
                .path("/api/auth") // Cookie will be sent ONLY to this specific endpoint
                .maxAge(refreshTokenExpirationMs / 1000) // Expiration time in seconds
                .sameSite("Strict")   // Protection against Cross-Site Request Forgery (CSRF)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(AuthResponse.builder().accessToken(result.accessToken()).build());
    }
}
