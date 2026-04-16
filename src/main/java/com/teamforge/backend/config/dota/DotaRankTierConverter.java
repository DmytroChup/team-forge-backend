package com.teamforge.backend.config.dota;

import java.util.Map;
import static java.util.Map.entry;

public final class DotaRankTierConverter {

    private DotaRankTierConverter() {}

    private static final Map<Integer, Integer> RANK_TIER_TO_MMR_MAP = Map.ofEntries(
            // Herald (11-15)
            entry(11, 1),
            entry(12, 150),
            entry(13, 300),
            entry(14, 460),
            entry(15, 610),

            // Guardian (21-25)
            entry(21, 770),
            entry(22, 920),
            entry(23, 1080),
            entry(24, 1230),
            entry(25, 1400),

            // Crusader (31-35)
            entry(31, 1540),
            entry(32, 1700),
            entry(33, 1850),
            entry(34, 2000),
            entry(35, 2150),

            // Archon (41-45)
            entry(41, 2310),
            entry(42, 2450),
            entry(43, 2610),
            entry(44, 2770),
            entry(45, 2930),

            // Legend (51-55)
            entry(51, 3080),
            entry(52, 3230),
            entry(53, 3390),
            entry(54, 3540),
            entry(55, 3700),

            // Ancient (61-65)
            entry(61, 3850),
            entry(62, 4000),
            entry(63, 4150),
            entry(64, 4300),
            entry(65, 4460),

            // Divine (71-75)
            entry(71, 4620),
            entry(72, 4820),
            entry(73, 5020),
            entry(74, 5220),
            entry(75, 5420)
    );

    /**
     * Returns an estimated MMR based on the rank_tier retrieved from the OpenDota API.
     *
     * @param rankTier a two-digit number from the API (e.g., 41 for Archon 1)
     * @return the estimated MMR, or null if the rank is uncalibrated or hidden
     */
    public static Integer getEstimatedMmr(Integer rankTier) {
        if (rankTier == null || rankTier == 0) {
            return null; // Uncalibrated or hidden profile
        }

        // Handling Immortal rank. The API returns 80 or higher (for leaderboards).
        if (rankTier >= 80) {
            return 5620;
        }

        return RANK_TIER_TO_MMR_MAP.get(rankTier);
    }
}