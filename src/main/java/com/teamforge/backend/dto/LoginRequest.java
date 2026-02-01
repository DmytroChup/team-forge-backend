package com.teamforge.backend.dto;

public record LoginRequest(
        String email,
        String password
) {
}
