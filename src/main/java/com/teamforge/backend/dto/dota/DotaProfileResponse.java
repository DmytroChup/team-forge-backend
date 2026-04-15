package com.teamforge.backend.dto.dota;

import com.teamforge.backend.model.DotaProfile;
import com.teamforge.backend.model.User;
import com.teamforge.backend.model.enums.DotaPosition;

import java.math.BigDecimal;
import java.util.Set;

public record DotaProfileResponse(
        Long profileId,
        Long userId,
        String nickname,
        String avatarUrl,
        Integer mmr,
        Integer rankTier,
        Set<DotaPosition> positions,
        BigDecimal winRate,
        Integer totalMatches,
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
                profile.getRankTier(),
                profile.getPositions(),
                profile.getWinRate(),
                profile.getTotalMatches(),
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
                profile.getRankTier(),
                profile.getPositions(),
                profile.getWinRate(),
                profile.getTotalMatches(),
                profile.isLookingForTeam(),
                profile.getUser().getSteamId(),
                profile.getAboutMe()
        );
    }
}
