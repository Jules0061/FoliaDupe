package com.Jules.foliaDupe.dupe;

import com.Jules.foliaDupe.blacklist.BlacklistManager;
import com.Jules.foliaDupe.blacklist.BlockReason;
import com.Jules.foliaDupe.config.ConfigManager;
import com.Jules.foliaDupe.hook.WorldGuardHook;
import com.Jules.foliaDupe.message.MessageManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;

public final class DuplicationManager {

    private final ConfigManager config;
    private final BlacklistManager blacklist;
    private final MessageManager messages;
    private final WorldGuardHook worldGuard;

    public DuplicationManager(ConfigManager config, BlacklistManager blacklist, MessageManager messages,
                              WorldGuardHook worldGuard) {
        this.config = config;
        this.blacklist = blacklist;
        this.messages = messages;
        this.worldGuard = worldGuard;
    }

    public boolean duplicate(Player player, ItemStack expected, int times) {
        final PlayerInventory inventory = player.getInventory();

        final ItemStack live = inventory.getItemInMainHand();
        if (live.getType().isAir() || !live.isSimilar(expected)) {
            messages.air(player);
            return false;
        }

        final BlockReason reason = blacklist.evaluate(live);
        if (reason.isBlocked()) {
            if (reason == BlockReason.CONTAINER_CONTENTS) {
                messages.containerBlacklisted(player);
            } else {
                messages.blacklisted(player);
            }
            return false;
        }

        final ItemStack[] batch = new ItemStack[times];
        for (int i = 0; i < times; i++) {
            batch[i] = live.clone();
        }

        final Map<Integer, ItemStack> overflow = inventory.addItem(batch);

        if (overflow.isEmpty() || !config.settings().dropOverflow()) {
            return true;
        }
        if (config.settings().disableOverflowInRegions()
                && worldGuard.isInsideRegion(player, config.settings().overflowRegions())) {
            return true;
        }
        for (ItemStack leftover : overflow.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
        }
        return true;
    }
}
