package com.teamforge.backend.service;

import com.teamforge.backend.dto.dota.DotaProfileResponse;
import com.teamforge.backend.dto.dota.DotaProfileUpdateRequest;
import com.teamforge.backend.model.User;
import com.teamforge.backend.model.enums.DotaRank;
import com.teamforge.backend.repository.DotaProfileRepository;
import com.teamforge.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DotaProfileServiceTest {

    @Mock
    private DotaProfileRepository dotaProfileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DotaProfileService dotaProfileService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .nickname("Player1")
                .email("test@test.com")
                .build();
    }

    @Test
    @DisplayName("Should calculate null rank and null stars for 0 or null MMR")
    void shouldCalculateUncalibratedForZeroMmr() {
        DotaProfileUpdateRequest request = new DotaProfileUpdateRequest(0, Set.of(), true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        DotaProfileResponse response = dotaProfileService.updateMyProfile(1L, request);

        assertNull(response.rank());
        assertNull(response.stars());
    }

    @ParameterizedTest
    @CsvSource({
            "0, , ",
            "609, HERALD, 4",
            "610, HERALD, 5",
            "770, GUARDIAN, 1",
            "3230, LEGEND, 2",
            "3999, ANCIENT, 1",
            "4620, DIVINE, 1",
            "5420, DIVINE, 5",
            "5620, IMMORTAL, 0",
            "15300, IMMORTAL, 0"
    })
    void testRankCalculation(int mmr, DotaRank expectedRank, Integer expectedStars) {

        DotaProfileUpdateRequest request = new DotaProfileUpdateRequest(mmr, Set.of(), true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        DotaProfileResponse response = dotaProfileService.updateMyProfile(1L, request);

        assertEquals(expectedRank, response.rank(), "Failed for MMR: " + mmr);
        assertEquals(expectedStars, response.stars(), "Stars failed for MMR: " + mmr);
    }

    @Test
    @DisplayName("Should set IMMORTAL rank if MMR is above 5620")
    void shouldSetImmortalForHighMmr() {
        DotaProfileUpdateRequest request = new DotaProfileUpdateRequest(6000, Set.of(), true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        DotaProfileResponse response = dotaProfileService.updateMyProfile(1L, request);

        assertEquals(DotaRank.IMMORTAL, response.rank());
        assertEquals(0, response.stars());
    }
}
