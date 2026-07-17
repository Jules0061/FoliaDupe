package com.Jules.foliaDupe.config;

import com.Jules.foliaDupe.FoliaDupe;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class ConfigManager {

    private final FoliaDupe plugin;
    private volatile Settings settings = Settings.defaults();

    public ConfigManager(FoliaDupe plugin) {
        this.plugin = plugin;
    }

    public void load(FileConfiguration config) {
        final int max = Math.max(1, config.getInt("max-dupe-amount", 10));
        final int defaultAmount = clamp(config.getInt("settings.default-amount", 1), max);

        this.settings = new Settings(
                max,
                defaultAmount,
                config.getBoolean("settings.drop-overflow", true),
                config.getBoolean("worldguard.disable-overflow-in-regions", true),
                loadRegions(config),
                config.getBoolean("settings.protect-blacklisted-in-crafting", false),
                config.getBoolean("settings.metrics", false),
                config.getString("permissions.dupe", "foliadupe.dupe"),
                config.getString("permissions.blacklist", "foliadupe.blacklist"),
                config.getString("permissions.reload", "foliadupe.reload"),
                config.getString("permissions.version", "foliadupe.version"));

        plugin.getSLF4JLogger().info("Loaded settings: max-dupe-amount={}, default-amount={}, drop-overflow={}.",
                max, defaultAmount, settings.dropOverflow());
    }

    public Settings settings() {
        return settings;
    }

    private static Set<String> loadRegions(FileConfiguration config) {
        final Set<String> regions = new HashSet<>();
        for (String raw : config.getStringList("worldguard.regions")) {
            if (raw != null && !raw.isBlank()) {
                regions.add(raw.trim().toLowerCase(Locale.ROOT));
            }
        }
        return Set.copyOf(regions);
    }

    private static int clamp(int value, int high) {
        return Math.clamp(high, 1, value);
    }
}
