package com.Jules.foliaDupe.util;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public record Keys(NamespacedKey blacklisted) {

    public Keys(Plugin plugin) {
        this(new NamespacedKey(plugin, "blacklisted"));
    }
}
