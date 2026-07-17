package com.Jules.foliaDupe.blacklist;

public enum BlockReason {

    ALLOWED,
    AIR,
    MATERIAL,
    BLACKLIST_TAG,
    NBT_KEYWORD,
    LIFESTEAL,
    ILLEGAL_ENCHANT,
    SPAWN_EGG,
    CONTAINER_CONTENTS;

    public boolean isBlocked() {
        return this != ALLOWED;
    }
}
