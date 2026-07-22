package com.Jules.foliaDupe.message;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

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
        String version,
        String bansTitle,
        String bansEmpty,
        String bansPrevious,
        String bansNext,
        String bansInfoName,
        List<String> bansInfoLore,
        List<String> bansEntryLore,
        List<String> bansEntryLoreHidden,
        String bansToggleName,
        List<String> bansToggleLore,
        String bansStateShown,
        String bansStateHidden) {

    static Messages empty() {
        return new Messages("", "", "", "", "", "", "", "", "", "", "", "", "",
                "Blacklisted Items", "", "", "", "", List.of(), List.of(),
                List.of(), "", List.of(), "", "");
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
                config.getString("version", "none"),
                config.getString("dupebans.title", "Blacklisted Items <dark_gray>(<page>/<pages>)"),
                config.getString("dupebans.empty", "none"),
                config.getString("dupebans.previous-page", "<yellow>Previous Page"),
                config.getString("dupebans.next-page", "<yellow>Next Page"),
                config.getString("dupebans.info-name", "<white>Blacklisted Items"),
                List.copyOf(config.getStringList("dupebans.info-lore")),
                List.copyOf(config.getStringList("dupebans.entry-lore")),
                List.copyOf(config.getStringList("dupebans.entry-lore-hidden")),
                config.getString("dupebans.toggle-name", "<white>Creative-Only Items"),
                List.copyOf(config.getStringList("dupebans.toggle-lore")),
                config.getString("dupebans.state-shown", "<green>Shown"),
                config.getString("dupebans.state-hidden", "<red>Hidden"));
    }
}
