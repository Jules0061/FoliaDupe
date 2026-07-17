package com.Jules.foliaDupe.listener;

import com.Jules.foliaDupe.blacklist.BlacklistManager;
import com.Jules.foliaDupe.config.ConfigManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;


public final class CraftProtectionListener implements Listener {

    private final BlacklistManager blacklist;
    private final ConfigManager config;

    public CraftProtectionListener(BlacklistManager blacklist, ConfigManager config) {
        this.blacklist = blacklist;
        this.config = config;
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        if (!config.settings().protectCraftingBlacklisted()) {
            return;
        }
        final CraftingInventory inventory = event.getInventory();
        for (ItemStack ingredient : inventory.getMatrix()) {
            if (ingredient == null || ingredient.getType().isAir()) {
                continue;
            }
            if (blacklist.evaluate(ingredient).isBlocked()) {
                inventory.setResult(null);
                return;
            }
        }
    }
}
