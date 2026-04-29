package com.teamforge.backend.dto.dota;

import com.teamforge.backend.model.enums.DotaPosition;

import java.math.BigDecimal;
import java.util.List;

public record DotaProfileSearchRequest(
        String nickname,
        List<Integer> rankTiers,
        List<DotaPosition> positions,
        boolean includeUnranked,
        BigDecimal minWinRate,
        Integer minMatches,
        Boolean requireSteam,
        Boolean lookingForTeam
) {
}
