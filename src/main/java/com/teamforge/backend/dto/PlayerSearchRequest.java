package com.teamforge.backend.dto;

import java.util.List;

public record PlayerSearchRequest(
        List<String> ranks,
        List<String> positions
) {
}
