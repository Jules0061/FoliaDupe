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
        String permDupe,
        String permBlacklist,
        String permReload,
        String permVersion) {

    public static Settings defaults() {
        return new Settings(
                10, 1, true, true, Set.of(), false, false,
                "foliadupe.dupe",
                "foliadupe.blacklist",
                "foliadupe.reload",
                "foliadupe.version");
    }
}
