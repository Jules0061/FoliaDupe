# FoliaDupe

A highly optimized, **Folia-ready** item duplication plugin for Paper **1.21.11+**
(forward-compatible with future 1.21.x). Java 21. A faithful, hardened Java port
of the original *Dupe Skript v3.0* by MrT1_.

## Highlights

- **Folia-safe by design** — no `BukkitScheduler`. Player commands are dispatched
  on the region thread that owns the player, so inventory mutation needs no extra
  scheduling. All cached config is published through `volatile` immutable
  snapshots (lock-free reads, no torn state across region threads).
- **Optimized for 500+ players** — O(1) `EnumSet` material lookups, O(1) `HashSet`
  keyword membership, a single item→text snapshot per check (no repeated NBT
  string parsing), and batched duplication (`Inventory#addItem` once, never one
  item at a time).
- **Adventure / MiniMessage** for every message.
- **No deprecated APIs** — uses the data-component API, `getPluginMeta()`,
  `getSLF4JLogger()` and PersistentDataContainer keys.

## Commands

| Command | Aliases | Permission | Description |
|---|---|---|---|
| `/dupe [amount]` | `/d` | `foliadupe.dupe` (default: all) | Duplicate the held item. |
| `/dupe reload` | — | `foliadupe.reload` (op) | Reload config & messages. |
| `/dupe version` | — | `foliadupe.version` (all) | Show the version. |
| `/blacklist` | — | `foliadupe.blacklist` (op) | Toggle the blacklist tag on the held item. |
| `/dupeplugin <reload\|version>` | `/foliadupe`, `/fdupe` | as above | Admin entry point. |
| `/dupebans` | `/dupeban`, `/dupeblacklist` | `foliadupe.dupebans` (default: all) | Paginated GUI listing every blacklisted item. |

`/dupebans` opens a 54-slot menu (45 items per page) with arrow navigation and a
summary book. Only items obtainable in vanilla survival are listed — creative-only
entries such as command blocks, spawn eggs, barriers, light blocks, the debug
stick, structure blocks and jigsaws are filtered out of the display. They remain
fully blacklisted; they are simply not rendered as icons.

Permission nodes are configurable in `config.yml`.

## Anti-dupe protections

An item is blocked from duplication when **any** of these match:

1. **Material** is in `blacklisted-materials` (command blocks, bedrock, barrier,
   player heads, dragon egg, mace, vault, …).
2. **PDC blacklist tag** set via `/blacklist`.
3. **NBT keyword** from `blacklisted-nbt-keywords` appears in the item's name,
   lore, enchantment keys (e.g. `minecraft:breach`) or PDC key namespaces
   (e.g. `excellentcrates`).
4. **LifeSteal protection** (`lifesteal-protection` section) — dedicated coverage
   for LifeStealZ / soulbound hearts / revive / custom LifeSteal items, matched
   by keyword and plugin namespace.

Optional: `settings.protect-blacklisted-in-crafting` cancels crafting recipes
that use a tagged item.

## Build

```bash
mvn clean package
```

The jar is produced at `target/FoliaDupe-1.0-ALPHA.jar`. Drop it into `plugins/`.

> Adjust `paper.api.version` in `pom.xml` if your target build differs from
> `1.21.11-R0.1-SNAPSHOT`. The `api-version: '1.21'` in `plugin.yml` keeps the jar
> loading on newer 1.21.x releases without a rebuild.

## Package layout

```
com.Jules.foliaDupe
├── FoliaDupe                 (main / wiring)
├── command   (DupeCommand, BlacklistCommand, AdminCommand, AdminActions, DupeBansCommand)
├── config    (ConfigManager, Settings)
├── message   (MessageManager, Messages)
├── blacklist (BlacklistManager, BlockReason, VanillaItems)
├── dupe      (DuplicationManager)
├── gui       (DupeBansMenu, DupeBansListener)
├── listener  (CraftProtectionListener)
└── util      (Keys)
```

> **Note on config files:** per the spec, the material and NBT blacklists live in
> `config.yml` (sections `blacklisted-materials`, `blacklisted-nbt-keywords`,
> `lifesteal-protection`) rather than a separate `Blacklisted.yml`. The
> `BlacklistManager` is already isolated, so splitting them into their own file
> is a one-line change if preferred.
