package com.teamforge.backend.dto.auth;

import lombok.Builder;

@Builder
public record AuthResponse(
        String token
) {
}
