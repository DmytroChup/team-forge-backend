package com.teamforge.backend.dto;

import com.teamforge.backend.model.enums.DotaPosition;
import com.teamforge.backend.model.enums.DotaRank;

import java.util.Set;

public record DotaProfileUpdateRequest(
        DotaRank rank,
        Integer stars,
        Set<DotaPosition> positions,
        boolean lookingForTeam
) {
}
