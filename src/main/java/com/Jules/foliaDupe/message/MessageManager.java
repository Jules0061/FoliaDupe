package com.Jules.foliaDupe.message;

import com.Jules.foliaDupe.FoliaDupe;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public final class MessageManager {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final FoliaDupe plugin;
    private volatile Messages messages = Messages.empty();

    public MessageManager(FoliaDupe plugin) {
        this.plugin = plugin;
    }

    public void load() {
        final File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        this.messages = Messages.from(YamlConfiguration.loadConfiguration(file));
    }

    public void air(Audience audience) {
        final Messages m = messages;
        send(audience, m, m.air());
    }

    public void blacklisted(Audience audience) {
        final Messages m = messages;
        send(audience, m, m.blacklisted());
    }

    public void containerBlacklisted(Audience audience) {
        final Messages m = messages;
        send(audience, m, m.containerBlacklisted());
    }

    public void itemBlacklisted(Audience audience) {
        final Messages m = messages;
        send(audience, m, m.itemBlacklisted());
    }

    public void itemUnblacklisted(Audience audience) {
        final Messages m = messages;
        send(audience, m, m.itemUnblacklisted());
    }

    public void invalidAmount(Audience audience) {
        final Messages m = messages;
        send(audience, m, m.invalidAmount());
    }

    public void noPermission(Audience audience) {
        final Messages m = messages;
        send(audience, m, m.noPermission());
    }

    public void playerOnly(Audience audience) {
        final Messages m = messages;
        send(audience, m, m.playerOnly());
    }

    public void reloadFailed(Audience audience) {
        final Messages m = messages;
        send(audience, m, m.reloadFailed());
    }

    public void dupeSuccess(Audience audience, ItemStack item, int amount) {
        final Messages m = messages;
        send(audience, m, m.dupeSuccess(),
                Placeholder.component("item", item.effectiveName()),
                Placeholder.unparsed("amount", Integer.toString(amount)));
    }

    public void reloadSuccess(Audience audience, int materials, int keywords) {
        final Messages m = messages;
        send(audience, m, m.reloadSuccess(),
                Placeholder.unparsed("materials", Integer.toString(materials)),
                Placeholder.unparsed("keywords", Integer.toString(keywords)));
    }

    public void version(Audience audience, String version) {
        final Messages m = messages;
        send(audience, m, m.version(), Placeholder.unparsed("version", version));
    }

    private void send(Audience audience, Messages snapshot, String body, TagResolver... resolvers) {
        if (body == null || body.isBlank() || body.equalsIgnoreCase("none")) {
            return;
        }
        audience.sendMessage(MINI_MESSAGE.deserialize(snapshot.prefix() + body, resolvers));
    }
}
