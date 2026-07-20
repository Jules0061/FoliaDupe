package com.Jules.foliaDupe.command;

import com.Jules.foliaDupe.FoliaDupe;
import com.Jules.foliaDupe.blacklist.BlacklistManager;
import com.Jules.foliaDupe.blacklist.BlockReason;
import com.Jules.foliaDupe.config.ConfigManager;
import com.Jules.foliaDupe.config.Settings;
import com.Jules.foliaDupe.dupe.DuplicationManager;
import com.Jules.foliaDupe.message.MessageManager;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class DupeCommand implements CommandExecutor, TabCompleter {

    private final FoliaDupe plugin;
    private final ConfigManager config;
    private final MessageManager messages;
    private final BlacklistManager blacklist;
    private final DuplicationManager duplication;

    public DupeCommand(FoliaDupe plugin, ConfigManager config, MessageManager messages,
                       BlacklistManager blacklist, DuplicationManager duplication) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
        this.blacklist = blacklist;
        this.duplication = duplication;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        final Settings settings = config.settings();

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                AdminActions.reload(plugin, settings, messages, sender);
                return true;
            }
            if (args[0].equalsIgnoreCase("version")) {
                AdminActions.version(plugin, settings, messages, sender);
                return true;
            }
        }

        if (!(sender instanceof Player player)) {
            messages.playerOnly(sender);
            return true;
        }
        if (!player.hasPermission(settings.permDupe())) {
            messages.noPermission(player);
            return true;
        }

        final ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand.getType().isAir()) {
            messages.air(player);
            return true;
        }

        final BlockReason reason = blacklist.evaluate(hand);
        if (reason.isBlocked()) {
            if (reason == BlockReason.CONTAINER_CONTENTS) {
                messages.containerBlacklisted(player);
            } else {
                messages.blacklisted(player);
            }
            return true;
        }

        int amount = settings.defaultAmount();
        if (args.length >= 1) {
            final Integer parsed = parsePositive(args[0]);
            if (parsed == null) {
                messages.invalidAmount(player);
                return true;
            }
            amount = Math.min(parsed, settings.maxDupeAmount());
        }

        if (duplication.duplicate(player, hand, amount)) {
            messages.dupeSuccess(player, hand, amount);
            playDupeSound(player, settings);
        }
        return true;
    }

    private static void playDupeSound(Player player, Settings settings) {
        if (!settings.soundEnabled()) {
            return;
        }
        final NamespacedKey key = NamespacedKey.minecraft(settings.soundName().toLowerCase(Locale.ROOT));
        final Sound sound = Registry.SOUNDS.get(key);
        if (sound == null) {
            return;
        }
        player.playSound(player.getLocation(), sound, settings.soundVolume(), settings.soundPitch());
    }

    private static Integer parsePositive(String input) {
        try {
            final int value = Integer.parseInt(input);
            return value >= 1 ? value : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        if (args.length != 1) {
            return List.of();
        }
        final Settings settings = config.settings();
        final String partial = args[0].toLowerCase(Locale.ROOT);
        final List<String> out = new ArrayList<>(4);

        addIfMatch(out, "1", partial);
        addIfMatch(out, Integer.toString(settings.maxDupeAmount()), partial);
        if (sender.hasPermission(settings.permReload())) {
            addIfMatch(out, "reload", partial);
        }
        if (sender.hasPermission(settings.permVersion())) {
            addIfMatch(out, "version", partial);
        }
        return out;
    }

    private static void addIfMatch(List<String> out, String value, String partial) {
        if (value.toLowerCase(Locale.ROOT).startsWith(partial)) {
            out.add(value);
        }
    }
}
