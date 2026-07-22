package com.Jules.foliaDupe.command;

import com.Jules.foliaDupe.FoliaDupe;
import com.Jules.foliaDupe.blacklist.BlacklistManager;
import com.Jules.foliaDupe.config.ConfigManager;
import com.Jules.foliaDupe.config.Settings;
import com.Jules.foliaDupe.gui.DupeBansMenu;
import com.Jules.foliaDupe.message.MessageManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;

public final class DupeBansCommand implements CommandExecutor, TabCompleter {

    private final FoliaDupe plugin;
    private final ConfigManager config;
    private final MessageManager messages;
    private final BlacklistManager blacklist;

    public DupeBansCommand(FoliaDupe plugin, ConfigManager config, MessageManager messages,
                           BlacklistManager blacklist) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
        this.blacklist = blacklist;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command,
                             @NonNull String label, String @NonNull [] args) {
        final Settings settings = config.settings();

        if (!(sender instanceof Player player)) {
            messages.playerOnly(sender);
            return true;
        }
        if (!player.hasPermission(settings.permDupeBans())) {
            messages.noPermission(player);
            return true;
        }

        final List<Material> entries = blacklist.displayableMaterials();
        if (entries.isEmpty()) {
            messages.bansEmpty(player);
            return true;
        }

        final int hidden = blacklist.materialCount() - entries.size();
        new DupeBansMenu(plugin, messages, entries, hidden, blacklist.keywordCount()).open(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command,
                                      @NonNull String label, String @NonNull [] args) {
        return List.of();
    }
}
