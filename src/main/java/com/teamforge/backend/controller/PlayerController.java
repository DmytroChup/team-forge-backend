package com.teamforge.backend.controller;

import com.teamforge.backend.dto.CreatePlayerRequest;
import com.teamforge.backend.dto.PlayerSearchRequest;
import com.teamforge.backend.dto.ProfileUpdateRequest;
import com.teamforge.backend.model.Player;
import com.teamforge.backend.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping
    public Player createPlayer(@RequestBody CreatePlayerRequest request) {
        return playerService.createPlayer(request);
    }

    @PutMapping("/profile")
    public Player updatePlayer(@Valid @RequestBody ProfileUpdateRequest request) {
        return playerService.updatePlayer(request);
    }

    @PostMapping("/search")
    public List<Player> searchPlayers(@RequestBody PlayerSearchRequest request) {
        return playerService.searchPlayers(request);
    }

    @GetMapping("/{id}")
    public Player getPlayerById(@PathVariable Long id) {
        return playerService.getPlayerById(id);
    }

    @GetMapping
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }
}
