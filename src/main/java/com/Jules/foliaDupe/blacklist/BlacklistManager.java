package com.Jules.foliaDupe.blacklist;

import com.Jules.foliaDupe.FoliaDupe;
import com.Jules.foliaDupe.util.Keys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class BlacklistManager {

    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    private static final Set<Material> SPAWN_EGGS = buildSpawnEggSet();

    private final FoliaDupe plugin;
    private final Keys keys;
    private volatile Snapshot snapshot = Snapshot.empty();

    public BlacklistManager(FoliaDupe plugin, Keys keys) {
        this.plugin = plugin;
        this.keys = keys;
    }

    public void load(FileConfiguration config) {
        final EnumSet<Material> materials = EnumSet.noneOf(Material.class);
        int invalid = 0;
        for (String raw : config.getStringList("blacklisted-materials")) {
            if (raw == null || raw.isBlank()) {
                continue;
            }
            final Material material = Material.matchMaterial(raw.trim());
            if (material == null) {
                plugin.getSLF4JLogger().warn("Ignoring invalid blacklisted material: '{}'", raw);
                invalid++;
                continue;
            }
            materials.add(material);
        }

        final Set<String> nbtKeywords = toLowerSet(config.getStringList("blacklisted-nbt-keywords"));

        final boolean lifestealEnabled = config.getBoolean("lifesteal-protection.enabled", true);
        final Set<String> lifestealKeywords = new HashSet<>();
        if (lifestealEnabled) {
            lifestealKeywords.addAll(toLowerSet(config.getStringList("lifesteal-protection.keywords")));
            lifestealKeywords.addAll(toLowerSet(config.getStringList("lifesteal-protection.block-namespaces")));
        }

        final boolean blockIllegalEnchants = config.getBoolean("block-illegal-enchants", true);

        final boolean blockSpawnEggs = config.getBoolean("block-spawn-eggs", true);

        this.snapshot = new Snapshot(materials, nbtKeywords, lifestealEnabled, Set.copyOf(lifestealKeywords),
                blockIllegalEnchants, blockSpawnEggs);

        plugin.getSLF4JLogger().info(
                "Loaded blacklist: {} materials ({} invalid), {} NBT keywords, lifesteal-protection {}, "
                        + "illegal-enchant-protection {}, spawn-egg-protection {}.",
                materials.size(), invalid, nbtKeywords.size(), lifestealEnabled ? "ON" : "OFF",
                blockIllegalEnchants ? "ON" : "OFF", blockSpawnEggs ? "ON" : "OFF");
    }

    public BlockReason evaluate(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return BlockReason.AIR;
        }

        final Snapshot snap = this.snapshot;

        if (snap.materials.contains(item.getType())) {
            return BlockReason.MATERIAL;
        }

        if (snap.blockSpawnEggs && SPAWN_EGGS.contains(item.getType())) {
            return BlockReason.SPAWN_EGG;
        }

        if (!item.hasItemMeta()) {
            return BlockReason.ALLOWED;
        }
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return BlockReason.ALLOWED;
        }

        final PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (Boolean.TRUE.equals(pdc.get(keys.blacklisted(), PersistentDataType.BOOLEAN))) {
            return BlockReason.BLACKLIST_TAG;
        }

        if (snap.blockIllegalEnchants && hasIllegalEnchant(meta)) {
            return BlockReason.ILLEGAL_ENCHANT;
        }

        final boolean scanNbt = !snap.nbtKeywords.isEmpty();
        final boolean scanLifesteal = snap.lifestealEnabled && !snap.lifestealKeywords.isEmpty();
        if (!scanNbt && !scanLifesteal) {
            return BlockReason.ALLOWED;
        }

        final String haystack = buildHaystack(item, meta, pdc);

        if (scanNbt && containsAny(haystack, snap.nbtKeywords)) {
            return BlockReason.NBT_KEYWORD;
        }
        if (scanLifesteal && containsAny(haystack, snap.lifestealKeywords)) {
            return BlockReason.LIFESTEAL;
        }

        if (hasBlockedContent(meta)) {
            return BlockReason.CONTAINER_CONTENTS;
        }

        return BlockReason.ALLOWED;
    }

    private static Set<Material> buildSpawnEggSet() {
        final EnumSet<Material> eggs = EnumSet.noneOf(Material.class);
        for (Material material : Material.values()) {
            if (material.name().endsWith("_SPAWN_EGG")) {
                eggs.add(material);
            }
        }
        return eggs;
    }

    private static boolean hasIllegalEnchant(ItemMeta meta) {
        if (containsIllegalEnchant(meta.getEnchants())) {
            return true;
        }
        return meta instanceof EnchantmentStorageMeta storage
                && containsIllegalEnchant(storage.getStoredEnchants());
    }

    private static boolean containsIllegalEnchant(Map<Enchantment, Integer> enchants) {
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            final Enchantment enchantment = entry.getKey();
            if (!NamespacedKey.MINECRAFT.equals(enchantment.getKey().getNamespace())) {
                return true;
            }
            final int level = entry.getValue();
            if (level > enchantment.getMaxLevel() || level < enchantment.getStartLevel()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasBlockedContent(ItemMeta meta) {
        if (meta instanceof BlockStateMeta bsm && bsm.hasBlockState()) {
            final BlockState state = bsm.getBlockState();
            if (state instanceof Container container) {
                for (ItemStack content : container.getInventory().getContents()) {
                    if (content != null && !content.getType().isAir()
                            && evaluate(content).isBlocked()) {
                        return true;
                    }
                }
            }
        }
        if (meta instanceof BundleMeta bundle) {
            for (ItemStack content : bundle.getItems()) {
                if (content != null && !content.getType().isAir()
                        && evaluate(content).isBlocked()) {
                    return true;
                }
            }
        }
        return false;
    }

    public int materialCount() {
        return snapshot.materials.size();
    }

    public int keywordCount() {
        return snapshot.nbtKeywords.size() + snapshot.lifestealKeywords.size();
    }

    private static String buildHaystack(ItemStack item, ItemMeta meta, PersistentDataContainer pdc) {
        final StringBuilder builder = new StringBuilder(64);
        builder.append(item.getType().name()).append(' ');

        if (meta.hasDisplayName()) {
            appendComponent(builder, meta.displayName());
        }
        if (meta.hasLore()) {
            final List<Component> lore = meta.lore();
            if (lore != null) {
                for (Component line : lore) {
                    appendComponent(builder, line);
                }
            }
        }
        for (Enchantment enchantment : meta.getEnchants().keySet()) {
            builder.append(enchantment.getKey().asString()).append(' ');
        }
        if (meta instanceof EnchantmentStorageMeta storage) {
            for (Enchantment enchantment : storage.getStoredEnchants().keySet()) {
                builder.append(enchantment.getKey().asString()).append(' ');
            }
        }
        for (NamespacedKey key : pdc.getKeys()) {
            builder.append(key.asString()).append(' ');
        }

        return builder.toString().toLowerCase(Locale.ROOT);
    }

    private static void appendComponent(StringBuilder builder, Component component) {
        if (component != null) {
            builder.append(PLAIN.serialize(component)).append(' ');
        }
    }

    private static boolean containsAny(String haystack, Set<String> needles) {
        for (String needle : needles) {
            if (haystack.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private static Set<String> toLowerSet(List<String> values) {
        if (values == null || values.isEmpty()) {
            return Set.of();
        }
        final Set<String> set = new HashSet<>(Math.max(8, values.size() * 2));
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                set.add(value.trim().toLowerCase(Locale.ROOT));
            }
        }
        return Set.copyOf(set);
    }

    private record Snapshot(Set<Material> materials, Set<String> nbtKeywords,
                            boolean lifestealEnabled, Set<String> lifestealKeywords,
                            boolean blockIllegalEnchants, boolean blockSpawnEggs) {

        static Snapshot empty() {
            return new Snapshot(EnumSet.noneOf(Material.class), Set.of(), false, Set.of(), false, false);
        }
    }
}
