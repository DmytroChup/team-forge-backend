package com.teamforge.backend.dto;

import com.teamforge.backend.model.enums.DotaPosition;
import jakarta.validation.constraints.Min;

import java.util.Set;

public record DotaProfileUpdateRequest(
        @Min(value = 0, message = "MMR cannot be negative")
        Integer mmr,
        Set<DotaPosition> positions,
        boolean lookingForTeam
) {
}
