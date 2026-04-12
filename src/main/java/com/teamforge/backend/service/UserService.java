package com.teamforge.backend.service;

import com.teamforge.backend.dto.user.UserResponse;
import com.teamforge.backend.dto.user.UserUpdateRequest;
import com.teamforge.backend.exception.UserNotFoundException;
import com.teamforge.backend.model.User;
import com.teamforge.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse getMe(Long userId) {
        User user  = findByIdOrThrow(userId);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateMe(Long userId, UserUpdateRequest request) {
        User user = findByIdOrThrow(userId);

        if(request.nickname() != null ) {
            if (!request.nickname().equalsIgnoreCase(user.getNickname())
                    && userRepository.existsByNicknameIgnoreCase(request.nickname())) {
                throw new IllegalArgumentException("Nickname '" + request.nickname() + "' is already taken");
            }
            user.setNickname(request.nickname());
        }

        return UserResponse.from(userRepository.save(user));
    }

    private User findByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
    }
}
