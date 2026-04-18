package com.teamforge.backend.controller;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;
import com.teamforge.backend.dto.auth.LoginRequest;
import com.teamforge.backend.dto.auth.RegisterRequest;
import com.teamforge.backend.model.RefreshToken;
import com.teamforge.backend.repository.RefreshTokenRepository;
import com.teamforge.backend.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@Testcontainers
class AuthControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18.0-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void setUp() {
        // Clear the database before each test to ensure a clean state
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testRegisterAndReceiveTokens() throws Exception {
        // 1. Prepare data
        RegisterRequest request = new RegisterRequest("testuser", "test@mail.com", "password123");

        // 2. Execute a POST request and verify the result
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                // Verify that accessToken exists in the JSON response
                .andExpect(jsonPath("$.accessToken").exists())
                // Verify that the refresh_token cookie is present with correct flags
                .andExpect(cookie().exists("refresh_token"))
                .andExpect(cookie().httpOnly("refresh_token", true))
                .andExpect(cookie().path("refresh_token", "/api/auth/refresh-token"));

        // 3. Verify the database state
        assertEquals(1, userRepository.count());
        assertEquals(1, refreshTokenRepository.count());
    }

    @Test
    void testLoginAndReceiveTokens() throws Exception {
        // 1. First, register a user
        RegisterRequest registerRequest = new RegisterRequest("logintester", "login@mail.com", "password123");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        // 2. Attempt to login with the registered credentials
        LoginRequest loginRequest = new LoginRequest("login@mail.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(cookie().exists("refresh_token"));
    }

    @Test
    void testRefreshTokenRotation() throws Exception {
        // 1. Register a user to obtain the initial refresh cookie
        RegisterRequest registerRequest = new RegisterRequest("refreshtester", "refresh@mail.com", "password123");
        var mvcResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andReturn();

        // Extract the issued cookie from the response
        Cookie refreshTokenCookie = mvcResult.getResponse().getCookie("refresh_token");
        assertNotNull(refreshTokenCookie);
        String oldTokenValue = refreshTokenCookie.getValue();

        // 2. Make a refresh request, passing the extracted cookie
        mockMvc.perform(post("/api/auth/refresh-token")
                        .cookie(refreshTokenCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(cookie().exists("refresh_token"));

        // 3. Verify token rotation in the database
        RefreshToken oldTokenInDb = refreshTokenRepository.findByToken(oldTokenValue).orElseThrow();

        // The old refresh token must be revoked after rotation
        assertTrue(oldTokenInDb.isRevoked(), "Old refresh token should be revoked after rotation");
    }

    @Test
    void testLogout() throws Exception {
        // 1. Register a user to get an active refresh token cookie
        RegisterRequest registerRequest = new RegisterRequest("logouttester", "logout@mail.com", "password123");
        var mvcResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andReturn();

        // Extract the issued cookie
        Cookie refreshTokenCookie = mvcResult.getResponse().getCookie("refresh_token");
        assertNotNull(refreshTokenCookie);
        String tokenValue = refreshTokenCookie.getValue();

        // Ensure that the token is active in the database before logout (revoked = false)
        RefreshToken tokenInDbBefore = refreshTokenRepository.findByToken(tokenValue).orElseThrow();
        assertFalse(tokenInDbBefore.isRevoked(), "Token should be active before logout");

        // 2. Perform a logout request, passing this cookie
        mockMvc.perform(post("/api/auth/logout")
                        .cookie(refreshTokenCookie))
                .andExpect(status().isOk())
                // Main frontend check: the backend should send a cookie with Max-Age = 0 (command to delete)
                .andExpect(cookie().maxAge("refresh_token", 0))
                .andExpect(cookie().value("refresh_token", ""));

        // 3. Verify that the token is now revoked in the database (revoked = true)
        RefreshToken tokenInDbAfter = refreshTokenRepository.findByToken(tokenValue).orElseThrow();
        assertTrue(tokenInDbAfter.isRevoked(), "Token should be revoked after logout");
    }
}
