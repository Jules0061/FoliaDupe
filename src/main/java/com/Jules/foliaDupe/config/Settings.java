package com.Jules.foliaDupe.config;

import java.util.Set;

public record Settings(
        int maxDupeAmount,
        int defaultAmount,
        boolean dropOverflow,
        boolean disableOverflowInRegions,
        Set<String> overflowRegions,
        boolean protectCraftingBlacklisted,
        boolean metrics,
        boolean soundEnabled,
        String soundName,
        float soundVolume,
        float soundPitch,
        String permDupe,
        String permBlacklist,
        String permReload,
        String permVersion) {

    public static Settings defaults() {
        return new Settings(
                10, 1, true, true, Set.of(), false, false,
                true, "entity.item.pickup", 1.0f, 1.0f,
                "foliadupe.dupe",
                "foliadupe.blacklist",
                "foliadupe.reload",
                "foliadupe.version");
    }
}
