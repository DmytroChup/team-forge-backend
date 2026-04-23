package com.teamforge.backend.controller;

import com.teamforge.backend.dto.dota.DotaProfileResponse;
import com.teamforge.backend.dto.dota.DotaProfileSearchRequest;
import com.teamforge.backend.dto.dota.DotaProfileUpdateRequest;
import com.teamforge.backend.security.SecurityUser;
import com.teamforge.backend.service.DotaProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles/dota")
@RequiredArgsConstructor
public class DotaProfileController {

    private final DotaProfileService dotaProfileService;

    @PutMapping("/me")
    public ResponseEntity<DotaProfileResponse> updateProfile(
            @AuthenticationPrincipal SecurityUser securityUser,
            @Valid @RequestBody DotaProfileUpdateRequest request) {
        DotaProfileResponse response = dotaProfileService.updateMyProfile(securityUser.getId(), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyProfile(@AuthenticationPrincipal SecurityUser securityUser) {
        dotaProfileService.deleteMyProfile(securityUser.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/refresh-stats")
    public ResponseEntity<DotaProfileResponse> refreshStats(@AuthenticationPrincipal SecurityUser securityUser) {
        DotaProfileResponse response = dotaProfileService.refreshMyProfileStats(securityUser.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<DotaProfileResponse>> searchPlayers(
            @RequestBody DotaProfileSearchRequest request,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(dotaProfileService.searchProfiles(request, pageable));
    }

    @GetMapping("/by-nickname/{nickname}")
    public ResponseEntity<DotaProfileResponse> getByNickname(@PathVariable String nickname) {
        return ResponseEntity.ok(dotaProfileService.getProfileByNickname(nickname));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DotaProfileResponse> getPlayerById(@PathVariable Long id) {
        return ResponseEntity.ok(dotaProfileService.getProfileById(id));
    }

    @GetMapping
    public ResponseEntity<Page<DotaProfileResponse>> getAllPlayers(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(dotaProfileService.getAllProfiles(pageable));
    }
}
