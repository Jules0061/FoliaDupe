package com.Jules.foliaDupe.blacklist;

import org.bukkit.Material;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public final class VanillaItems {

    private static final List<String> CREATIVE_ONLY_NAMES = List.of(
            "COMMAND_BLOCK",
            "CHAIN_COMMAND_BLOCK",
            "REPEATING_COMMAND_BLOCK",
            "COMMAND_BLOCK_MINECART",
            "STRUCTURE_BLOCK",
            "STRUCTURE_VOID",
            "JIGSAW",
            "BARRIER",
            "LIGHT",
            "DEBUG_STICK",
            "KNOWLEDGE_BOOK",
            "BEDROCK",
            "END_PORTAL_FRAME",
            "REINFORCED_DEEPSLATE",
            "SPAWNER",
            "TRIAL_SPAWNER",
            "VAULT",
            "BUDDING_AMETHYST",
            "PETRIFIED_OAK_SLAB",
            "FARMLAND",
            "DIRT_PATH",
            "FROGSPAWN",
            "SUSPICIOUS_SAND",
            "SUSPICIOUS_GRAVEL");

    private static final Set<Material> EXCLUDED = buildExcluded();

    private VanillaItems() {
    }

    public static boolean isSurvivalObtainable(Material material) {
        return material != null
                && !material.isLegacy()
                && material.isItem()
                && !material.isAir()
                && !EXCLUDED.contains(material);
    }

    private static Set<Material> buildExcluded() {
        final EnumSet<Material> excluded = EnumSet.noneOf(Material.class);
        for (String name : CREATIVE_ONLY_NAMES) {
            final Material material = Material.getMaterial(name);
            if (material != null) {
                excluded.add(material);
            }
        }
        for (Material material : Material.values()) {
            if (material.name().endsWith("_SPAWN_EGG")) {
                excluded.add(material);
            }
        }
        return excluded;
    }
}
