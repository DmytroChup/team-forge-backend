package com.teamforge.backend.dto;

import com.teamforge.backend.model.DotaProfile;
import com.teamforge.backend.model.User;
import com.teamforge.backend.model.enums.DotaPosition;
import com.teamforge.backend.model.enums.DotaRank;

import java.util.Set;

public record DotaProfileResponse(
        String nickname,
        Integer mmr,
        DotaRank rank,
        Integer stars,
        Set<DotaPosition> positions,
        boolean lookingForTeam,
        String steamId
) {
    public static DotaProfileResponse fromEntity(User user) {
        DotaProfile profile = user.getDotaProfile();
        return new DotaProfileResponse(
                user.getNickname(),
                profile.getMmr(),
                profile.getRank(),
                profile.getStars(),
                profile.getPositions(),
                profile.isLookingForTeam(),
                user.getSteamId()
        );
    }

    public static DotaProfileResponse fromDotaProfile(DotaProfile profile) {
        return new DotaProfileResponse(
                profile.getUser().getNickname(),
                profile.getMmr(),
                profile.getRank(),
                profile.getStars(),
                profile.getPositions(),
                profile.isLookingForTeam(),
                profile.getUser().getSteamId()
        );
    }
}
