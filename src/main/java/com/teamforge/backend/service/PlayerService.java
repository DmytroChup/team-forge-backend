package com.teamforge.backend.service;

import com.teamforge.backend.dto.CreatePlayerRequest;
import com.teamforge.backend.model.Player;
import com.teamforge.backend.repository.PlayerRepository;
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

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }
}
