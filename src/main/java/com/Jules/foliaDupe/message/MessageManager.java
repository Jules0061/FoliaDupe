package com.Jules.foliaDupe.message;

import com.Jules.foliaDupe.FoliaDupe;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    public void bansEmpty(Audience audience) {
        final Messages m = messages;
        send(audience, m, m.bansEmpty());
    }

    public Component bansTitle(int page, int pages) {
        return render(messages.bansTitle(), pageResolvers(page, pages));
    }

    public Component bansPrevious(int page, int pages) {
        return icon(messages.bansPrevious(), pageResolvers(page, pages));
    }

    public Component bansNext(int page, int pages) {
        return icon(messages.bansNext(), pageResolvers(page, pages));
    }

    public Component bansInfoName() {
        return icon(messages.bansInfoName());
    }

    public List<Component> bansInfoLore(int shown, int hidden, int keywords) {
        return iconLore(messages.bansInfoLore(),
                Placeholder.unparsed("shown", Integer.toString(shown)),
                Placeholder.unparsed("hidden", Integer.toString(hidden)),
                Placeholder.unparsed("keywords", Integer.toString(keywords)));
    }

    public List<Component> bansEntryLore(String material, boolean creativeOnly) {
        final Messages m = messages;
        final List<String> lines = creativeOnly ? m.bansEntryLoreHidden() : m.bansEntryLore();
        return iconLore(lines, Placeholder.unparsed("material", material));
    }

    public Component bansToggleName() {
        return icon(messages.bansToggleName());
    }

    public List<Component> bansToggleLore(boolean showingHidden, int hidden) {
        final Messages m = messages;
        return iconLore(m.bansToggleLore(),
                Placeholder.parsed("state", showingHidden ? m.bansStateShown() : m.bansStateHidden()),
                Placeholder.unparsed("hidden", Integer.toString(hidden)));
    }

    private static TagResolver[] pageResolvers(int page, int pages) {
        return new TagResolver[]{
                Placeholder.unparsed("page", Integer.toString(page)),
                Placeholder.unparsed("pages", Integer.toString(pages))
        };
    }

    private static Component render(String body, TagResolver... resolvers) {
        return MINI_MESSAGE.deserialize(body == null ? "" : body, resolvers);
    }

    private static Component icon(String body, TagResolver... resolvers) {
        return render(body, resolvers).decoration(TextDecoration.ITALIC, false);
    }

    private static List<Component> iconLore(List<String> lines, TagResolver... resolvers) {
        if (lines == null || lines.isEmpty()) {
            return List.of();
        }
        final List<Component> out = new ArrayList<>(lines.size());
        for (String line : lines) {
            out.add(icon(line, resolvers));
        }
        return out;
    }

    private void send(Audience audience, Messages snapshot, String body, TagResolver... resolvers) {
        if (body == null || body.isBlank() || body.equalsIgnoreCase("none")) {
            return;
        }
        audience.sendMessage(MINI_MESSAGE.deserialize(snapshot.prefix() + body, resolvers));
    }
}
