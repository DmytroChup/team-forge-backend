package com.teamforge.backend.repository;

import com.teamforge.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    boolean existsBySteamId(String steamId);

    boolean existsByDiscordId(String discordId);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
