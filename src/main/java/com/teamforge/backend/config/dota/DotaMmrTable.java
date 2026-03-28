package com.teamforge.backend.config.dota;

import com.teamforge.backend.model.enums.DotaRank;

import java.util.NavigableMap;
import java.util.TreeMap;

public final class DotaMmrTable {

    private DotaMmrTable() {}

    private static final NavigableMap<Integer, RankInfo> MMR_THRESHOLDS = new TreeMap<>();

    static {
        // Herald
        MMR_THRESHOLDS.put(1, new RankInfo(DotaRank.HERALD, 1));
        MMR_THRESHOLDS.put(150, new RankInfo(DotaRank.HERALD, 2));
        MMR_THRESHOLDS.put(300, new RankInfo(DotaRank.HERALD, 3));
        MMR_THRESHOLDS.put(460, new RankInfo(DotaRank.HERALD, 4));
        MMR_THRESHOLDS.put(610, new RankInfo(DotaRank.HERALD, 5));

        // Guardian
        MMR_THRESHOLDS.put(770, new RankInfo(DotaRank.GUARDIAN, 1));
        MMR_THRESHOLDS.put(920, new RankInfo(DotaRank.GUARDIAN, 2));
        MMR_THRESHOLDS.put(1080, new RankInfo(DotaRank.GUARDIAN, 3));
        MMR_THRESHOLDS.put(1230, new RankInfo(DotaRank.GUARDIAN, 4));
        MMR_THRESHOLDS.put(1400, new RankInfo(DotaRank.GUARDIAN, 5));

        // Crusader
        MMR_THRESHOLDS.put(1540, new RankInfo(DotaRank.CRUSADER, 1));
        MMR_THRESHOLDS.put(1700, new RankInfo(DotaRank.CRUSADER, 2));
        MMR_THRESHOLDS.put(1850, new RankInfo(DotaRank.CRUSADER, 3));
        MMR_THRESHOLDS.put(2000, new RankInfo(DotaRank.CRUSADER, 4));
        MMR_THRESHOLDS.put(2150, new RankInfo(DotaRank.CRUSADER, 5));

        // Archon
        MMR_THRESHOLDS.put(2310, new RankInfo(DotaRank.ARCHON, 1));
        MMR_THRESHOLDS.put(2450, new RankInfo(DotaRank.ARCHON, 2));
        MMR_THRESHOLDS.put(2610, new RankInfo(DotaRank.ARCHON, 3));
        MMR_THRESHOLDS.put(2770, new RankInfo(DotaRank.ARCHON, 4));
        MMR_THRESHOLDS.put(2930, new RankInfo(DotaRank.ARCHON, 5));

        // Legend
        MMR_THRESHOLDS.put(3080, new RankInfo(DotaRank.LEGEND, 1));
        MMR_THRESHOLDS.put(3230, new RankInfo(DotaRank.LEGEND, 2));
        MMR_THRESHOLDS.put(3390, new RankInfo(DotaRank.LEGEND, 3));
        MMR_THRESHOLDS.put(3540, new RankInfo(DotaRank.LEGEND, 4));
        MMR_THRESHOLDS.put(3700, new RankInfo(DotaRank.LEGEND, 5));

        // Ancient
        MMR_THRESHOLDS.put(3850, new RankInfo(DotaRank.ANCIENT, 1));
        MMR_THRESHOLDS.put(4000, new RankInfo(DotaRank.ANCIENT, 2));
        MMR_THRESHOLDS.put(4150, new RankInfo(DotaRank.ANCIENT, 3));
        MMR_THRESHOLDS.put(4300, new RankInfo(DotaRank.ANCIENT, 4));
        MMR_THRESHOLDS.put(4460, new RankInfo(DotaRank.ANCIENT, 5));

        // Divine
        MMR_THRESHOLDS.put(4620, new RankInfo(DotaRank.DIVINE, 1));
        MMR_THRESHOLDS.put(4820, new RankInfo(DotaRank.DIVINE, 2));
        MMR_THRESHOLDS.put(5020, new RankInfo(DotaRank.DIVINE, 3));
        MMR_THRESHOLDS.put(5220, new RankInfo(DotaRank.DIVINE, 4));
        MMR_THRESHOLDS.put(5420, new RankInfo(DotaRank.DIVINE, 5));

        // Immortal (no stars)
        MMR_THRESHOLDS.put(5620, new RankInfo(DotaRank.IMMORTAL, 0));
    }

    public static NavigableMap<Integer, RankInfo> getMmrThresholds() {
        return MMR_THRESHOLDS;
    }

    public record RankInfo(DotaRank rank, int stars) {}
}
