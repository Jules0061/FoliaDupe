package com.Jules.foliaDupe.message;

import org.bukkit.configuration.file.FileConfiguration;

public record Messages(
        String prefix,
        String dupeSuccess,
        String air,
        String blacklisted,
        String containerBlacklisted,
        String itemBlacklisted,
        String itemUnblacklisted,
        String invalidAmount,
        String noPermission,
        String playerOnly,
        String reloadSuccess,
        String reloadFailed,
        String version) {

    static Messages empty() {
        return new Messages("", "", "", "", "", "", "", "", "", "", "", "", "");
    }

    static Messages from(FileConfiguration config) {
        return new Messages(
                config.getString("prefix", ""),
                config.getString("dupe-success", "none"),
                config.getString("air", "none"),
                config.getString("blacklisted", "none"),
                config.getString("container-blacklisted", "none"),
                config.getString("item-blacklisted", "none"),
                config.getString("item-unblacklisted", "none"),
                config.getString("invalid-amount", "none"),
                config.getString("no-permission", "none"),
                config.getString("player-only", "none"),
                config.getString("reload-success", "none"),
                config.getString("reload-failed", "none"),
                config.getString("version", "none"));
    }
}
