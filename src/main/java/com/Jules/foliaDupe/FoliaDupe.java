package com.Jules.foliaDupe;

import com.Jules.foliaDupe.blacklist.BlacklistManager;
import com.Jules.foliaDupe.command.AdminCommand;
import com.Jules.foliaDupe.command.BlacklistCommand;
import com.Jules.foliaDupe.command.DupeCommand;
import com.Jules.foliaDupe.config.ConfigManager;
import com.Jules.foliaDupe.dupe.DuplicationManager;
import com.Jules.foliaDupe.hook.WorldGuardHook;
import com.Jules.foliaDupe.listener.CraftProtectionListener;
import com.Jules.foliaDupe.message.MessageManager;
import com.Jules.foliaDupe.util.Keys;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class FoliaDupe extends JavaPlugin {

    private ConfigManager configManager;
    private MessageManager messageManager;
    private BlacklistManager blacklistManager;

    @Override
    public void onEnable() {
        final long startNanos = System.nanoTime();

        saveDefaultConfig();

        Keys keys = new Keys(this);
        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);
        this.blacklistManager = new BlacklistManager(this, keys);
        WorldGuardHook worldGuardHook = WorldGuardHook.create(this);
        DuplicationManager duplicationManager = new DuplicationManager(configManager, blacklistManager, messageManager,
                worldGuardHook);

        loadAll();

        registerCommand("dupe", new DupeCommand(this, configManager, messageManager,
                blacklistManager, duplicationManager));
        registerCommand("blacklist", new BlacklistCommand(keys, configManager, messageManager));
        registerCommand("dupeplugin", new AdminCommand(this, configManager, messageManager));

        getServer().getPluginManager().registerEvents(
                new CraftProtectionListener(blacklistManager, configManager), this);

        getSLF4JLogger().info("FoliaDupe v{} enabled in {} ms.",
                getPluginMeta().getVersion(),
                (System.nanoTime() - startNanos) / 1_000_000L);
    }

    public void loadAll() {
        reloadConfig();
        final FileConfiguration config = getConfig();
        configManager.load(config);
        blacklistManager.load(config);
        messageManager.load();
    }

    private void registerCommand(String name, CommandExecutor handler) {
        final PluginCommand command = getCommand(name);
        if (command == null) {
            getSLF4JLogger().error("Command /{} is missing from plugin.yml - skipping registration.", name);
            return;
        }
        command.setExecutor(handler);
        if (handler instanceof TabCompleter completer) {
            command.setTabCompleter(completer);
        }
    }

    public BlacklistManager blacklist() {
        return blacklistManager;
    }
}
