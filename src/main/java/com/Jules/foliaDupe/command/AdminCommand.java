package com.Jules.foliaDupe.command;

import com.Jules.foliaDupe.FoliaDupe;
import com.Jules.foliaDupe.config.ConfigManager;
import com.Jules.foliaDupe.config.Settings;
import com.Jules.foliaDupe.message.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class AdminCommand implements CommandExecutor, TabCompleter {

    private final FoliaDupe plugin;
    private final ConfigManager config;
    private final MessageManager messages;

    public AdminCommand(FoliaDupe plugin, ConfigManager config, MessageManager messages) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        final Settings settings = config.settings();
        final String sub = args.length >= 1 ? args[0].toLowerCase(Locale.ROOT) : "version";

        if (sub.equals("reload")) {
            AdminActions.reload(plugin, settings, messages, sender);
        } else {
            AdminActions.version(plugin, settings, messages, sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        if (args.length != 1) {
            return List.of();
        }
        final Settings settings = config.settings();
        final String partial = args[0].toLowerCase(Locale.ROOT);
        final List<String> out = new ArrayList<>(2);

        if (sender.hasPermission(settings.permReload()) && "reload".startsWith(partial)) {
            out.add("reload");
        }
        if (sender.hasPermission(settings.permVersion()) && "version".startsWith(partial)) {
            out.add("version");
        }
        return out;
    }
}
