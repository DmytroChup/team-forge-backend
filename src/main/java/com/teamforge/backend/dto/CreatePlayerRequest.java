package com.teamforge.backend.dto;

import com.teamforge.backend.model.Position;
import com.teamforge.backend.model.Rank;

import java.util.Set;

public record CreatePlayerRequest(
        String username,
        String steamId,
        Rank rank,
        Integer stars,
        Set<Position> positions,
        String discordId
) {
}
