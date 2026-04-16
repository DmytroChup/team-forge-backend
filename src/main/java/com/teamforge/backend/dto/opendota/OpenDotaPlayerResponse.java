package com.teamforge.backend.dto.opendota;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenDotaPlayerResponse(
        @JsonProperty("rank_tier") Integer rankTier
) {
}
