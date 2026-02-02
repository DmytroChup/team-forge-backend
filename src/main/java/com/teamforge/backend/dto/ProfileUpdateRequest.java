package com.teamforge.backend.dto;

import com.teamforge.backend.model.enums.DotaPosition;
import com.teamforge.backend.model.enums.DotaRank;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.Set;

public record ProfileUpdateRequest(
        String username,
        DotaRank rank,

        @Min(1)
        @Max(5)
        Integer stars,

        Set<DotaPosition> positions
) {
}
