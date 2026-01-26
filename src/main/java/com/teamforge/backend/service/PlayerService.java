package com.teamforge.backend.service;

import com.teamforge.backend.dto.CreatePlayerRequest;
import com.teamforge.backend.dto.PlayerSearchRequest;
import com.teamforge.backend.exception.PlayerAlreadyExistsException;
import com.teamforge.backend.model.Player;
import com.teamforge.backend.repository.PlayerRepository;
import com.teamforge.backend.specification.PlayerSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Transactional
    public Player createPlayer(CreatePlayerRequest request) {

        if(playerRepository.existsBySteamId(request.steamId())) {
            throw new PlayerAlreadyExistsException("Player with Steam ID " + request.steamId() + " already exists");
        }

        if(playerRepository.existsByUsername(request.username())) {
            throw new PlayerAlreadyExistsException("Username " + request.username() + " is already taken");
        }

        if(playerRepository.existsByDiscordId(request.discordId())) {
            throw new PlayerAlreadyExistsException("Discord account is already linked to another player");
        }

        Player player = Player.builder()
                .username(request.username())
                .steamId(request.steamId())
                .rank(request.rank())
                .stars(request.stars())
                .positions(request.positions())
                .discordId(request.discordId())
                .build();

        return playerRepository.save(player);
    }

    public List<Player> searchPlayers(PlayerSearchRequest request) {
        var spec = PlayerSpecification.getSpec(request);
        return playerRepository.findAll(spec);
    }

    public Player getPlayerById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new PlayerAlreadyExistsException("Player with ID " + id + " not found"));
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }
}
