package com.Jules.foliaDupe.hook;

import com.Jules.foliaDupe.FoliaDupe;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Set;

public record WorldGuardHook(boolean available) {

    public static WorldGuardHook create(FoliaDupe plugin) {
        final boolean available = plugin.getServer().getPluginManager().getPlugin("WorldGuard") != null;
        if (available) {
            plugin.getSLF4JLogger().info("Hooked into WorldGuard.");
        }
        return new WorldGuardHook(available);
    }

    public boolean isInsideRegion(Player player, Set<String> regions) {
        if (!available) {
            return false;
        }
        try {
            return Wg.isInsideRegion(player, regions);
        } catch (Throwable t) {
            return false;
        }
    }

    private static final class Wg {

        static boolean isInsideRegion(Player player, Set<String> regions) {
            final var applicable = com.sk89q.worldguard.WorldGuard.getInstance()
                    .getPlatform()
                    .getRegionContainer()
                    .createQuery()
                    .getApplicableRegions(
                            com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(player.getLocation()));
            if (regions.isEmpty()) {
                return applicable.size() > 0;
            }
            for (com.sk89q.worldguard.protection.regions.ProtectedRegion region : applicable) {
                if (regions.contains(region.getId().toLowerCase(Locale.ROOT))) {
                    return true;
                }
            }
            return false;
        }
    }
}
