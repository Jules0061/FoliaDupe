package com.Jules.foliaDupe.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public final class DupeBansListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof DupeBansMenu menu)) {
            return;
        }
        event.setCancelled(true);

        final Inventory clicked = event.getClickedInventory();
        if (clicked == null || !clicked.equals(event.getView().getTopInventory())) {
            return;
        }
        if (event.getWhoClicked() instanceof Player player) {
            menu.handleClick(player, event.getSlot());
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof DupeBansMenu) {
            event.setCancelled(true);
        }
    }
}
