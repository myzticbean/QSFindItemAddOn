# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Bukkit/Paper plugin that adds a `/finditem` command for searching items across all QuickShop-Hikari shops on a Minecraft server. Also exposes `/finditemadmin` (alias `/fiadmin`) for reload/debug. Java 21, built with Maven, shaded into a single jar. Paper 1.20+ API, with Folia support via FoliaLib.

## Build & Run

- Build: `mvn clean package` — produces the shaded jar in `target/`.
- There are **no unit tests** in this repository; `mvn test` is a no-op. Verification is done by dropping the jar into a Paper/Folia server with QuickShop-Hikari installed.
- `maven-shade-plugin` relocates `com.tcoded.folialib` → `io.myzticbean.finditemaddon.shaded.folialib` and `org.bstats` → `...shaded.metrics`. Never reference unshaded packages in new code.
- `pom.xml` has commented-out `<outputDirectory>` entries in `maven-jar-plugin` intended to auto-copy the jar into a local test server's `plugins/` folder — uncomment the relevant one for your OS when iterating locally.
- One system-scope dependency lives in `lib/Residence5.1.5.1.jar` (not on any Maven repo) — keep that file in place.
- Version in `pom.xml` and the snapshot build date are edited manually; `plugin.yml` picks up `${project.version}` via resource filtering.

## Architecture

### Entry point
`io.myzticbean.finditemaddon.FindItemAddOn` (extends `JavaPlugin`) orchestrates the lifecycle:
1. `onLoad` — initializes `FoliaLib` (all scheduling must go through `FindItemAddOn.getScheduler()`, never `Bukkit.getScheduler()` directly, or Folia will crash).
2. `onEnable` — registers Bukkit listeners, loads config (`ConfigSetup` + `ConfigProvider`), registers commands via SimpAPI's `CommandManager`, then defers the rest to `runPluginStartupTasks()` on the next tick so dependencies have finished enabling.
3. `runPluginStartupTasks` — hard-requires QuickShop-Hikari, instantiates the `QSApi` impl, loads `shops.json`, sets up optional integrations, registers the 15-min async task, bStats, and the update checker.

### QuickShop abstraction
`quickshop/QSApi.java` is a generic interface (`QSApi<SHOP, PLAYER>`) over the shop plugin. Only `QSHikariAPIHandler` is active; `QSReremakeAPIHandler` is dead code retained for reference (Reremake support was dropped). All shop queries (search-to-buy, search-to-sell, material filters, stock checks, enchantment/potion metadata) go through this interface. When touching shop logic, add methods on `QSApi` and implement in `QSHikariAPIHandler` — do **not** call QuickShop APIs from command/GUI code directly.

### Commands
Built on `me.kodysimpson.simpapi.command.CommandManager` (SimpAPI). Subcommand classes in `commands/simpapi/` (`BuySubCmd`, `SellSubCmd`, `HideShopSubCmd`, `RevealShopSubCmd`, `ReloadSubCmd`, `DebugSubCmd`) are registered by reflection via `CommandManager.createCoreCommand`. The `hide`/`reveal` subcommands are conditionally included based on `FIND_ITEM_CMD_REMOVE_HIDE_REVEAL_SUBCMDS`. There is also a `commands/quickshop/` package that registers a subcommand directly under `/qs` via `QSApi.registerSubCommand()`.

### GUI
`handlers/gui/Menu.java` + `PaginatedMenu.java` are the inventory-based GUI base classes; concrete menus live in `handlers/gui/menus/`. Per-player state is stored in a `PlayerMenuUtility` retrieved via `FindItemAddOn.getPlayerMenuUtility(Player)` — this is the channel for passing search results between the command handler, menus, and click handlers. `MenuListener` dispatches `InventoryClickEvent` to the active menu.

### Persistence
Hidden/visited-shop state is serialized to JSON under the plugin data folder by `utils/json/ShopSearchActivityStorageUtil` (Jackson with `jsr310`). On startup, `migrateHiddenShopsToShopsJson()` migrates the pre-2.0 `hiddenShops.json` format — keep this migration intact. `onDisable` saves the file.

### Optional integrations
Each lives in `dependencies/` as a static setup-and-check class: `PlayerWarpsPlugin`, `EssentialsXPlugin`, `WGPlugin` (WorldGuard), `ResidencePlugin`, `BentoBoxPlugin`, plus GriefPrevention support in utils. The pattern is: `setup()` checks `Bukkit.getPluginManager()`, caches the API handle, and sets an `isEnabled` flag. Always gate integration code behind that flag. EssentialsX warps are refreshed on a 15-minute schedule (`scheduledtasks/Task15MinInterval`) because per-query lookups are too expensive.

### Concurrency
- Scheduling: always via `FindItemAddOn.getScheduler()` (FoliaLib `PlatformScheduler`) — `runNextTick`, `runTimerAsync`, region-aware variants. Never use raw Bukkit scheduler.
- Heavy, non-Bukkit work (search aggregation, HTTP) goes through `utils/async/VirtualThreadScheduler` (shut down in `onDisable`).
- Any call that touches a shop's block / chunk / location on Folia must be wrapped in the scheduler's region task.

### Config
`config/ConfigSetup` handles file creation, missing-key backfill, and writing out `sample-config.yml`. `config/ConfigProvider` is the typed accessor used throughout the code (`FindItemAddOn.getConfigProvider().SOME_KEY`). When adding a config option: add the field + parse logic in `ConfigProvider`, add the default to `resources/config.yml`, update `ConfigSetup.checkForMissingProperties()` so existing installs get the new key, and **increment `config-version`** (currently 21) in the default config.

### Permissions
All permission nodes are declared in `models/enums/PlayerPermsEnum`. Use `PlayerPermsEnum.PERMISSION_NAME.getPermission()` for checks — never hardcode permission strings. The nodes are: `finditem.use`, `finditem.hideshop`, `finditem.reload`, `finditem.admin`, `finditem.shoptp`, `finditem.shoptp.own`, `finditem.shoptp-delay.bypass`, `finditem.shoptp.bypass-safetycheck`.

### Shop cache sync
`handlers/events/ShopCreateEventListener` and `ShopDeleteEventListener` keep the in-memory shop list in sync when shops are created or removed at runtime. Any feature that modifies the cached shop list must go through these listeners, not inline in command handlers.

### Enums
`models/enums/` holds cross-cutting enums: `PlayerPermsEnum` (permissions), `ShopLorePlaceholdersEnum` (GUI lore placeholders like `{ITEM_PRICE}`, `{SHOP_OWNER}`, `{NEAREST_WARP}`), `CustomCmdPlaceholdersEnum` (placeholders for custom TP commands), and `NearestWarpModeEnum`.

### GriefPrevention
GriefPrevention is a Maven dependency but is **not** listed in `plugin.yml`'s `softdepend`. Its integration is handled in utils (not in `dependencies/`), making it the odd one out — do not follow the `dependencies/` pattern for it.

### MenuListener threading
As of v2.0.8.0, `MenuListener` handles `InventoryClickEvent` asynchronously. Any GUI click logic that touches Bukkit APIs must be dispatched back to the main/region thread via `FindItemAddOn.getScheduler()`.

## Conventions

- PascalCase classes, camelCase methods/variables, ALL_CAPS constants.
- Lombok is available and used (`@Getter`, `@Slf4j`) — prefer it over hand-written boilerplate.
- Logging goes through `utils/log/Logger` (`logInfo`, `logWarning`, `logError`), not `System.out` or raw `getLogger()`.
- Permissions checks always use `PlayerPermsEnum`, never hardcoded strings.
