package com.teamforge.backend.service;

import com.teamforge.backend.config.dota.DotaRankTierConverter;
import com.teamforge.backend.dto.dota.DotaProfileResponse;
import com.teamforge.backend.dto.dota.DotaProfileSearchRequest;
import com.teamforge.backend.dto.dota.DotaProfileUpdateRequest;
import com.teamforge.backend.dto.opendota.OpenDotaPlayerResponse;
import com.teamforge.backend.dto.opendota.OpenDotaWinLossResponse;
import com.teamforge.backend.exception.*;
import com.teamforge.backend.model.DotaProfile;
import com.teamforge.backend.model.User;
import com.teamforge.backend.repository.DotaProfileRepository;
import com.teamforge.backend.repository.UserRepository;
import com.teamforge.backend.specification.PlayerSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DotaProfileService {

    private final DotaProfileRepository dotaProfileRepository;
    private final UserRepository userRepository;
    private final OpenDotaApiService openDotaApiService;

    private static final Duration REFRESH_COOLDOWN = Duration.ofSeconds(1);

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

        profile.setPositions(request.positions());
        profile.setLookingForTeam(request.lookingForTeam());
        profile.setAboutMe(request.aboutMe());

        dotaProfileRepository.save(profile);

        return DotaProfileResponse.fromEntity(user);
    }

    @Transactional
    public DotaProfileResponse refreshMyProfileStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        DotaProfile profile = user.getDotaProfile();

        // Ensure profile exists before attempting to update stats
        if (profile == null) {
            throw new DotaProfileNotFoundException("Profile not found. Please create a profile first.");
        }

        if (profile.getLastStatsRefreshedAt() != null) {
            Duration timeSinceLastRefresh = Duration.between(profile.getLastStatsRefreshedAt(), LocalDateTime.now());
            if (timeSinceLastRefresh.compareTo(REFRESH_COOLDOWN) < 0) {
                throw new ValidationException("Stats can only be refreshed once per hour.");
            }
        }

        fetchAndApplyStats(profile);

        profile.setLastStatsRefreshedAt(LocalDateTime.now());
        dotaProfileRepository.save(profile);

        return DotaProfileResponse.fromEntity(user);
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void scheduledStatsRefresh() {
        log.info("Starting scheduled Dota stats refresh...");

        List<DotaProfile> profiles = dotaProfileRepository.findAllWithSteamId();
        int success = 0;
        int failed  = 0;

        for (DotaProfile profile : profiles) {
            try {
                fetchAndApplyStats(profile);
                profile.setLastStatsRefreshedAt(LocalDateTime.now());
                dotaProfileRepository.save(profile);
                success++;

                Thread.sleep(1100);

            } catch (Exception e) {
                failed++;
                log.warn("Failed to refresh stats for profile {}: {}", profile.getId(), e.getMessage());
            }
        }

        log.info("Scheduled refresh complete. Success: {}, Failed: {}", success, failed);
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

    public Page<DotaProfileResponse> searchProfiles(DotaProfileSearchRequest request, Pageable pageable) {
        var spec = PlayerSpecification.getSpec(request);
        return dotaProfileRepository.findAll(spec, pageable)
                .map(DotaProfileResponse::fromDotaProfile);
    }

    public DotaProfileResponse getProfileByNickname(String nickname) {
        DotaProfile profile = dotaProfileRepository.findByUser_Nickname(nickname)
                .orElseThrow(() -> new DotaProfileNotFoundException("Profile not found: " + nickname));
        return DotaProfileResponse.fromDotaProfile(profile);
    }

    public DotaProfileResponse getProfileById(Long id) {
        DotaProfile profile = dotaProfileRepository.findById(id)
                .orElseThrow(() -> new DotaProfileNotFoundException("Dota profile not found"));
        return DotaProfileResponse.fromDotaProfile(profile);
    }

    public Page<DotaProfileResponse> getAllProfiles(Pageable pageable) {
        return dotaProfileRepository.findAll(pageable)
                .map(DotaProfileResponse::fromDotaProfile);
    }

    /**
     * Converts a 64-bit SteamID to a 32-bit account ID.
     * The constant 76561197960265728 is the base value for the conversion.
     */
    private String convertSteamId64ToAccountId32(String steamId64) {
        long steamId64Long = Long.parseLong(steamId64);
        return String.valueOf(steamId64Long - STEAM_ID_64_TO_ACCOUNT_ID_32_CONVERSION_CONSTANT);
    }

    /**
     * Calls OpenDota API and applies the result to the profile entity.
     * Does NOT save — caller is responsible for persisting.
     */
    private void fetchAndApplyStats(DotaProfile profile) {
        String steamId = profile.getUser().getSteamId();

        if (steamId == null || steamId.isBlank()) {
            throw new ValidationException("Steam ID is not linked to the profile. Cannot refresh stats.");
        }

        String accountId = convertSteamId64ToAccountId32(steamId);

        Optional<OpenDotaPlayerResponse> playerData = openDotaApiService.fetchPlayerProfile(accountId);
        if (playerData.isEmpty()) {
            throw new ExternalApiException("Could not fetch player profile from OpenDota API.");
        }

        OpenDotaPlayerResponse pStats = playerData.get();
        profile.setRankTier(pStats.rankTier());

        profile.setEstimatedMmr(
                DotaRankTierConverter.getEstimatedMmr(
                        pStats.rankTier()
                )
        );

        Optional<OpenDotaWinLossResponse> winLossData = openDotaApiService.fetchPlayerWinLoss(accountId);
        if (winLossData.isEmpty()) {
            throw new ExternalApiException("Could not fetch win/loss stats from OpenDota API.");
        }

        OpenDotaWinLossResponse wlStats = winLossData.get();
        int totalMatches = wlStats.win() + wlStats.lose();
        BigDecimal winRate = BigDecimal.ZERO;

        if (totalMatches > 0) {
            winRate = BigDecimal.valueOf(wlStats.win())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalMatches), 2, RoundingMode.HALF_UP);
        }

        profile.setTotalMatches(totalMatches);
        profile.setWinRate(winRate);
    }
}
