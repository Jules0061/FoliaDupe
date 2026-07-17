package com.Jules.foliaDupe.command;

import com.Jules.foliaDupe.FoliaDupe;
import com.Jules.foliaDupe.config.Settings;
import com.Jules.foliaDupe.message.MessageManager;
import org.bukkit.command.CommandSender;

final class AdminActions {

    private AdminActions() {
    }

    static void reload(FoliaDupe plugin, Settings settings, MessageManager messages, CommandSender sender) {
        if (!sender.hasPermission(settings.permReload())) {
            messages.noPermission(sender);
            return;
        }
        try {
            plugin.loadAll();
            messages.reloadSuccess(sender,
                    plugin.blacklist().materialCount(),
                    plugin.blacklist().keywordCount());
        } catch (Exception ex) {
            plugin.getSLF4JLogger().error("Failed to reload FoliaDupe configuration", ex);
            messages.reloadFailed(sender);
        }
    }

    static void version(FoliaDupe plugin, Settings settings, MessageManager messages, CommandSender sender) {
        if (!sender.hasPermission(settings.permVersion())) {
            messages.noPermission(sender);
            return;
        }
        messages.version(sender, plugin.getPluginMeta().getVersion());
    }
}
