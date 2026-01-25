package com.teamforge.backend.repository;

import com.teamforge.backend.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    Optional<Player> findByUsername(String username);

    Optional<Player> findBySteamId(String steamId);

    boolean existsBySteamId(String steamId);

    boolean existsByUsername(String username);

    boolean existsByDiscordId(String discordId);

    Player getPlayerById(Long id);
}
