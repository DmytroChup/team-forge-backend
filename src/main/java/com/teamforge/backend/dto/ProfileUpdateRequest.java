package com.teamforge.backend.dto;

import com.teamforge.backend.model.Position;
import com.teamforge.backend.model.Rank;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.Set;

public record ProfileUpdateRequest(
        String username,
        Rank rank,

        @Min(1)
        @Max(5)
        Integer stars,

        Set<Position> positions
) {
}
