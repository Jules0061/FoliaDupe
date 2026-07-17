package com.Jules.foliaDupe.command;

import com.Jules.foliaDupe.config.ConfigManager;
import com.Jules.foliaDupe.config.Settings;
import com.Jules.foliaDupe.message.MessageManager;
import com.Jules.foliaDupe.util.Keys;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NonNull;

public final class BlacklistCommand implements CommandExecutor {

    private final Keys keys;
    private final ConfigManager config;
    private final MessageManager messages;

    public BlacklistCommand(Keys keys, ConfigManager config, MessageManager messages) {
        this.keys = keys;
        this.config = config;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        final Settings settings = config.settings();

        if (!(sender instanceof Player player)) {
            messages.playerOnly(sender);
            return true;
        }
        if (!player.hasPermission(settings.permBlacklist())) {
            messages.noPermission(player);
            return true;
        }

        final ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand.getType().isAir()) {
            messages.air(player);
            return true;
        }

        final ItemMeta meta = hand.getItemMeta();
        if (meta == null) {
            messages.blacklisted(player);
            return true;
        }

        final PersistentDataContainer pdc = meta.getPersistentDataContainer();
        final boolean nowBlacklisted;
        if (Boolean.TRUE.equals(pdc.get(keys.blacklisted(), PersistentDataType.BOOLEAN))) {
            pdc.remove(keys.blacklisted());
            nowBlacklisted = false;
        } else {
            pdc.set(keys.blacklisted(), PersistentDataType.BOOLEAN, true);
            nowBlacklisted = true;
        }
        hand.setItemMeta(meta);

        if (nowBlacklisted) {
            messages.itemBlacklisted(player);
        } else {
            messages.itemUnblacklisted(player);
        }
        return true;
    }
}
