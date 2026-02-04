package com.teamforge.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Nickname is required")
        @Size(min = 3, max = 20, message = "Nickname must be between 3 and 20 characters")
        @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Nickname can only contain letters, numbers, - and _")
        String nickname,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
) {
}
