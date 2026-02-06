package com.teamforge.backend.controller;

import com.teamforge.backend.dto.DotaProfileSearchRequest;
import com.teamforge.backend.dto.DotaProfileUpdateRequest;
import com.teamforge.backend.model.DotaProfile;
import com.teamforge.backend.service.DotaProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dota")
@RequiredArgsConstructor
public class DotaProfileController {

    private final DotaProfileService dotaProfileService;

    // TODO: после подключения JWT заменить на @AuthenticationPrincipal User user
    @PutMapping("/profile/{userId}")
    public DotaProfile updateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody DotaProfileUpdateRequest request) {
        return dotaProfileService.updateProfile(userId, request);
    }

    @PostMapping("/search")
    public List<DotaProfile> searchPlayers(@RequestBody DotaProfileSearchRequest request) {
        return dotaProfileService.searchProfiles(request);
    }

    @GetMapping("/{id}")
    public DotaProfile getPlayerById(@PathVariable Long id) {
        return dotaProfileService.getProfileById(id);
    }

    @GetMapping
    public List<DotaProfile> getAllPlayers() {
        return dotaProfileService.getAllProfiles();
    }
}
