package com.teamforge.backend.dto.user;

import com.teamforge.backend.model.User;

public record UserResponse(
        Long id,
        String nickname,
        String email
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getNickname(),
                user.getEmail()
        );
    }
}
