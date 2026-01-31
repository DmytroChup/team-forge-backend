package com.teamforge.backend;

import com.teamforge.backend.dto.ProfileUpdateRequest;
import com.teamforge.backend.model.Player;
import com.teamforge.backend.model.Position;
import com.teamforge.backend.model.Rank;
import com.teamforge.backend.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PlayerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    void checkUpdateProfile() throws Exception {
        Player existingPlayer = Player.builder()
                .username("TestUser")
                .steamId("123456789")
                .rank(Rank.HERALD)
                .stars(1)
                .build();
        playerRepository.save(existingPlayer);

        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "TestUser",
                Rank.DIVINE,
                5,
                Set.of(Position.MID)
        );

        mockMvc.perform(put("/api/players/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rank").value("DIVINE"))
                .andExpect(jsonPath("$.stars").value(5));
    }

    @Test
    void checkUpdateProfile_InvalidStars() throws Exception {
        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "TestUser",
                Rank.IMMORTAL,
                100,
                Set.of(Position.CARRY)
        );

        mockMvc.perform(put("/api/players/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
