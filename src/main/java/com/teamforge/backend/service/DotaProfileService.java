package com.teamforge.backend.service;

import com.teamforge.backend.dto.DotaProfileSearchRequest;
import com.teamforge.backend.dto.DotaProfileUpdateRequest;
import com.teamforge.backend.exception.DotaProfileNotFoundException;
import com.teamforge.backend.model.DotaProfile;
import com.teamforge.backend.repository.DotaProfileRepository;
import com.teamforge.backend.specification.PlayerSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DotaProfileService {

    private final DotaProfileRepository dotaProfileRepository;

    @Transactional
    public DotaProfile updateProfile(Long userId, DotaProfileUpdateRequest request) {
        DotaProfile profile = dotaProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new DotaProfileNotFoundException(
                        "Dota profile not found"));

        profile.setRank(request.rank());
        profile.setPositions(request.positions());
        profile.setStars(request.stars());
        profile.setLookingForTeam(request.lookingForTeam());

        return dotaProfileRepository.save(profile);
    }

    public List<DotaProfile> searchProfiles(DotaProfileSearchRequest request) {
        var spec = PlayerSpecification.getSpec(request);
        return dotaProfileRepository.findAll(spec);
    }

    public DotaProfile getProfileById(Long id) {
        return dotaProfileRepository.findById(id)
                .orElseThrow(() -> new DotaProfileNotFoundException("Dota profile not found"));
    }

    public List<DotaProfile> getAllProfiles() {
        return dotaProfileRepository.findAll();
    }
}
