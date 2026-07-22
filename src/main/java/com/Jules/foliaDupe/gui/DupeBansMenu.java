package com.Jules.foliaDupe.gui;

import com.Jules.foliaDupe.FoliaDupe;
import com.Jules.foliaDupe.message.MessageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Locale;

public final class DupeBansMenu implements InventoryHolder {

    public static final int PAGE_SIZE = 45;

    private static final int SIZE = 54;
    private static final int SLOT_PREVIOUS = 45;
    private static final int SLOT_INFO = 49;
    private static final int SLOT_NEXT = 53;
    private static final Material FILLER = Material.GRAY_STAINED_GLASS_PANE;

    private final FoliaDupe plugin;
    private final MessageManager messages;
    private final List<Material> entries;
    private final int hidden;
    private final int keywords;
    private final int pages;

    private int page;
    private Inventory inventory;

    public DupeBansMenu(FoliaDupe plugin, MessageManager messages, List<Material> entries,
                        int hidden, int keywords) {
        this.plugin = plugin;
        this.messages = messages;
        this.entries = entries;
        this.hidden = hidden;
        this.keywords = keywords;
        this.pages = Math.max(1, (entries.size() + PAGE_SIZE - 1) / PAGE_SIZE);
    }

    public void open(Player player) {
        this.inventory = Bukkit.createInventory(this, SIZE, messages.bansTitle(page + 1, pages));
        render();
        player.openInventory(inventory);
    }

    @Override
    public @NonNull Inventory getInventory() {
        return inventory;
    }

    public void handleClick(Player player, int slot) {
        final int target;
        if (slot == SLOT_PREVIOUS) {
            target = page - 1;
        } else if (slot == SLOT_NEXT) {
            target = page + 1;
        } else {
            return;
        }
        if (target < 0 || target >= pages) {
            return;
        }
        this.page = target;
        player.getScheduler().run(plugin, task -> open(player), null);
    }

    private void render() {
        final ItemStack filler = build(FILLER, Component.empty(), List.of());
        for (int slot = PAGE_SIZE; slot < SIZE; slot++) {
            inventory.setItem(slot, filler);
        }

        final int start = page * PAGE_SIZE;
        final int end = Math.min(start + PAGE_SIZE, entries.size());
        for (int index = start; index < end; index++) {
            final Material material = entries.get(index);
            inventory.setItem(index - start,
                    build(material, Component.translatable(material.translationKey()),
                            messages.bansEntryLore(material.name().toLowerCase(Locale.ROOT))));
        }

        inventory.setItem(SLOT_INFO, build(Material.BOOK, messages.bansInfoName(),
                messages.bansInfoLore(entries.size(), hidden, keywords)));

        if (page > 0) {
            inventory.setItem(SLOT_PREVIOUS,
                    build(Material.ARROW, messages.bansPrevious(page, pages), List.of()));
        }
        if (page < pages - 1) {
            inventory.setItem(SLOT_NEXT,
                    build(Material.ARROW, messages.bansNext(page + 2, pages), List.of()));
        }
    }

    private static ItemStack build(Material material, Component name, List<Component> lore) {
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(name.decoration(TextDecoration.ITALIC, false));
            if (!lore.isEmpty()) {
                meta.lore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
