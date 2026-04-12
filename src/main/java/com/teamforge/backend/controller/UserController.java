package com.teamforge.backend.controller;

import com.teamforge.backend.dto.user.UserResponse;
import com.teamforge.backend.dto.user.UserUpdateRequest;
import com.teamforge.backend.security.SecurityUser;
import com.teamforge.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * PATCH /api/users/me
     * Partially updates the authenticated user's profile (nickname only for now).
     * Uses PATCH semantics — only non-null fields in the request body are applied.
     */
    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateMe(
            @AuthenticationPrincipal SecurityUser securityUser,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        return ResponseEntity.ok(userService.updateMe(securityUser.getId(), request));
    }

    /**
     * GET /api/users/me
     * Returns the currently authenticated user's public data.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(
            @AuthenticationPrincipal SecurityUser securityUser) {

        return ResponseEntity.ok(userService.getMe(securityUser.getId()));
    }
}
