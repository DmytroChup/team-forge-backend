package com.teamforge.backend.dto.dota;

import com.teamforge.backend.model.enums.DotaPosition;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record DotaProfileUpdateRequest(
        Set<DotaPosition> positions,

        boolean lookingForTeam,

        @Size(max = 1000, message = "About me text cannot exceed 1000 characters")
        String aboutMe
) {
}
