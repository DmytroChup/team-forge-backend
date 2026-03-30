package com.teamforge.backend.dto;

import com.teamforge.backend.model.DotaProfile;
import com.teamforge.backend.model.User;
import com.teamforge.backend.model.enums.DotaPosition;
import com.teamforge.backend.model.enums.DotaRank;

import java.util.Set;

public record DotaProfileResponse(
        Long profileId,
        Long userId,
        String nickname,
        String avatarUrl,
        Integer mmr,
        DotaRank rank,
        Integer stars,
        Set<DotaPosition> positions,
        boolean lookingForTeam,
        String steamId,
        String aboutMe
) {
    public static DotaProfileResponse fromEntity(User user) {
        DotaProfile profile = user.getDotaProfile();
        return new DotaProfileResponse(
                profile.getId(),
                user.getId(),
                user.getNickname(),
                user.getAvatarUrl(),
                profile.getMmr(),
                profile.getRank(),
                profile.getStars(),
                profile.getPositions(),
                profile.isLookingForTeam(),
                user.getSteamId(),
                profile.getAboutMe()
        );
    }

    public static DotaProfileResponse fromDotaProfile(DotaProfile profile) {
        return new DotaProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getUser().getNickname(),
                profile.getUser().getAvatarUrl(),
                profile.getMmr(),
                profile.getRank(),
                profile.getStars(),
                profile.getPositions(),
                profile.isLookingForTeam(),
                profile.getUser().getSteamId(),
                profile.getAboutMe()
        );
    }
}
