package com.teamforge.backend.dto;

import com.teamforge.backend.model.enums.DotaPosition;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record DotaProfileUpdateRequest(
        @Min(value = 0, message = "MMR cannot be negative")
        @Max(value = 25000, message = "MMR cannot exceed 25000")
        Integer mmr,

        Set<DotaPosition> positions,

        boolean lookingForTeam,

        @Size(max = 1000, message = "About me text cannot exceed 1000 characters")
        String aboutMe
) {
}
