package com.teamforge.backend.dto.auth;

import lombok.Builder;

@Builder
public record AuthResult(
        String accessToken,
        String refreshToken
) {
}
