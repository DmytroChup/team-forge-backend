package com.teamforge.backend.repository;

import com.teamforge.backend.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long>, JpaSpecificationExecutor<Player> {

    boolean existsBySteamId(String steamId);

    boolean existsByUsername(String username);

    boolean existsByDiscordId(String discordId);

    Optional<Player> findByUsername(String username);

    Optional<Player> findByEmail(String email);
}
