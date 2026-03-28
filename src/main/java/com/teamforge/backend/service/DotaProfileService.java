package com.teamforge.backend.service;

import com.teamforge.backend.config.dota.DotaMmrTable;
import com.teamforge.backend.dto.DotaProfileResponse;
import com.teamforge.backend.dto.DotaProfileSearchRequest;
import com.teamforge.backend.dto.DotaProfileUpdateRequest;
import com.teamforge.backend.exception.DotaProfileNotFoundException;
import com.teamforge.backend.exception.UserNotFoundException;
import com.teamforge.backend.model.DotaProfile;
import com.teamforge.backend.model.User;
import com.teamforge.backend.repository.DotaProfileRepository;
import com.teamforge.backend.repository.UserRepository;
import com.teamforge.backend.specification.PlayerSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DotaProfileService {

    private final DotaProfileRepository dotaProfileRepository;
    private final UserRepository userRepository;

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

        calculateAndSetRank(profile, request.mmr());

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
}
