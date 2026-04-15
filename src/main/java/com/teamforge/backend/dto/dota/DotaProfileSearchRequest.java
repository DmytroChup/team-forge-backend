package com.teamforge.backend.dto.dota;

import com.teamforge.backend.model.enums.DotaPosition;

import java.util.List;

public record DotaProfileSearchRequest(
        List<Integer> rankTiers,
        List<DotaPosition> positions
) {
}
