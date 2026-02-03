package com.teamforge.backend.dto;

import com.teamforge.backend.model.enums.DotaPosition;
import com.teamforge.backend.model.enums.DotaRank;

import java.util.List;

public record DotaProfileSearchRequest(
        List<DotaRank> ranks,
        List<DotaPosition> positions
) {
}
