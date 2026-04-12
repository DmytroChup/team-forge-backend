package com.teamforge.backend.dto.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(min = 3, max = 20, message = "Nickname must be between 3 and 20 characters")
        @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Nickname can only contain letters, numbers, underscores and hyphens")
        String nickname
) {
}
