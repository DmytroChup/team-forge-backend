package com.teamforge.backend.dto.opendota;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenDotaWinLossResponse(
    int win,
    int lose
) {
}