package com.teamforge.backend.service;

import com.teamforge.backend.config.dota.DotaMmrTable;
import com.teamforge.backend.dto.DotaProfileResponse;
import com.teamforge.backend.dto.DotaProfileSearchRequest;
import com.teamforge.backend.dto.DotaProfileUpdateRequest;
import com.teamforge.backend.dto.opendota.OpenDotaWinLossResponse;
import com.teamforge.backend.exception.ExternalApiException;
import com.teamforge.backend.exception.DotaProfileNotFoundException;
import com.teamforge.backend.exception.UserNotFoundException;
import com.teamforge.backend.exception.ValidationException;
import com.teamforge.backend.model.DotaProfile;
import com.teamforge.backend.model.User;
import com.teamforge.backend.repository.DotaProfileRepository;
import com.teamforge.backend.repository.UserRepository;
import com.teamforge.backend.specification.PlayerSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DotaProfileService {

    private final DotaProfileRepository dotaProfileRepository;
    private final UserRepository userRepository;
    private final OpenDotaApiService openDotaApiService;

    /**
     * The constant used to convert a 64-bit SteamID to a 32-bit account ID.
     */
    private static final long STEAM_ID_64_TO_ACCOUNT_ID_32_CONVERSION_CONSTANT = 76561197960265728L;

    /**
     * Updates or creates a Dota profile for the current authenticated user.
     * Uses the userId to ensure we work with a managed entity in the current persistence context.
     */
    @Transactional
    public DotaProfileResponse updateMyProfile(Long userId, DotaProfileUpdateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        DotaProfile profile = user.getDotaProfile();

        if (profile == null) {
            profile = new DotaProfile();
            profile.assignToUser(user);
        }

        profile.setMmr(request.mmr());
        profile.setPositions(request.positions());
        profile.setLookingForTeam(request.lookingForTeam());
        profile.setAboutMe(request.aboutMe());

        calculateAndSetRank(profile, request.mmr());

        dotaProfileRepository.save(profile);

        return DotaProfileResponse.fromEntity(user);
    }

    @Transactional
    public DotaProfileResponse refreshMyProfileStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getSteamId() == null || user.getSteamId().isBlank()) {
            throw new ValidationException("Steam ID is not linked to the profile. Cannot refresh stats.");
        }

        // OpenDota uses the 32-bit account ID, which is derived from the 64-bit SteamID.
        String accountId = convertSteamId64ToAccountId32(user.getSteamId());

        Optional<OpenDotaWinLossResponse> winLossData = openDotaApiService.fetchPlayerWinLoss(accountId);

        if (winLossData.isEmpty()) {
            throw new ExternalApiException("Could not fetch stats from OpenDota API. The service may be temporarily unavailable.");
        }

        OpenDotaWinLossResponse stats = winLossData.get();
        int totalMatches = stats.win() + stats.lose();
        BigDecimal winRate = BigDecimal.ZERO;
        if (totalMatches > 0) {
            winRate = BigDecimal.valueOf(stats.win())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalMatches), 2, RoundingMode.HALF_UP);
        }

        DotaProfile profile = user.getDotaProfile();

        // Ensure profile exists before attempting to update stats
        if (profile == null) {
            profile = new DotaProfile();
            profile.assignToUser(user);
        }

        profile.setTotalMatches(totalMatches);
        profile.setWinRate(winRate);

        dotaProfileRepository.save(profile);

        return DotaProfileResponse.fromEntity(user);
    }

    private void calculateAndSetRank(DotaProfile profile, Integer mmr) {
        if (mmr == null || mmr <= 0) {
            profile.setRank(null);
            profile.setStars(null);
            return;
        }

        var entry = DotaMmrTable.getMmrThresholds().floorEntry(mmr);

        // If for some reason the map is empty or changed, still use a safe check
        if (entry != null) {
            DotaMmrTable.RankInfo info = entry.getValue();
            profile.setRank(info.rank());
            profile.setStars(info.stars());
        } else {
            profile.setRank(null);
            profile.setStars(null);
        }
    }

    @Transactional
    public void deleteMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        DotaProfile profile = user.getDotaProfile();
        if (profile != null) {
            user.setDotaProfile(null);
            dotaProfileRepository.delete(profile);
        }
    }

    public List<DotaProfileResponse> searchProfiles(DotaProfileSearchRequest request) {
        var spec = PlayerSpecification.getSpec(request);
        return dotaProfileRepository.findAll(spec).stream()
                .map(DotaProfileResponse::fromDotaProfile)
                .toList();
    }

    public DotaProfileResponse getProfileById(Long id) {
        DotaProfile profile = dotaProfileRepository.findById(id)
                .orElseThrow(() -> new DotaProfileNotFoundException("Dota profile not found"));
        return DotaProfileResponse.fromDotaProfile(profile);
    }

    public List<DotaProfileResponse> getAllProfiles() {
        return dotaProfileRepository.findAll().stream()
                .map(DotaProfileResponse::fromDotaProfile)
                .toList();
    }

    /**
     * Converts a 64-bit SteamID to a 32-bit account ID.
     * The constant 76561197960265728 is the base value for the conversion.
     */
    private String convertSteamId64ToAccountId32(String steamId64) {
        long steamId64Long = Long.parseLong(steamId64);
        return String.valueOf(steamId64Long - STEAM_ID_64_TO_ACCOUNT_ID_32_CONVERSION_CONSTANT);
    }
}
