# In-GUI Sort & Filter Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add per-player, persistent sort and filter controls directly into the existing 9-slot navigation bar of `FoundShopsMenu`, replacing four idle filler slots.

**Architecture:** A new `SortMode` enum and `PlayerSortFilterPrefs` POJO hold per-player state stored in a `ConcurrentHashMap` inside `PlayerPrefsStorageUtil`. The sorted/filtered view is computed lazily via a Java stream inside `setMenuItems()` only when a page is actually rendered — the raw result list in `PlayerMenuUtility` is never modified. Preferences persist to `player_prefs.json` using Gson (same library already used by the plugin).

**Tech Stack:** Java 25, Bukkit/Paper API, Gson (already in pom.xml via QuickShop transitive dep), Lombok, FoliaLib, VirtualThreadScheduler (existing in-project util)

---

## File Map

| Action | Path | Responsibility |
|---|---|---|
| **Create** | `src/main/java/io/myzticbean/finditemaddon/models/enums/SortMode.java` | Enum of 4 sort modes with `next()` cycle method |
| **Create** | `src/main/java/io/myzticbean/finditemaddon/models/PlayerSortFilterPrefs.java` | Gson-serializable POJO holding sort + 3 filter booleans |
| **Create** | `src/main/java/io/myzticbean/finditemaddon/utils/json/PlayerPrefsStorageUtil.java` | Load/save player_prefs.json; `getPrefs(UUID)` accessor |
| **Modify** | `src/main/java/io/myzticbean/finditemaddon/FindItemAddOn.java` | Call `PlayerPrefsStorageUtil.load()` in startup tasks, `save()` in `onDisable()` |
| **Modify** | `src/main/java/io/myzticbean/finditemaddon/handlers/gui/PaginatedMenu.java` | Remove filler placement for slots 47/48/50/51 in `addMenuBottomBar()` |
| **Modify** | `src/main/java/io/myzticbean/finditemaddon/handlers/gui/menus/FoundShopsMenu.java` | Add `applyPrefs()`, `setSortFilterButtons()`, `buildSortButton()`, `buildFilterButton()`; update `setMenuItems()` and `handleNavigationClick()` |

---

## Task 1: Create `SortMode` Enum

**Files:**
- Create: `src/main/java/io/myzticbean/finditemaddon/models/enums/SortMode.java`

- [ ] **Step 1: Create the file**

```java
package io.myzticbean.finditemaddon.models.enums;

/**
 * Sort modes available in the in-GUI sort control.
 * Ordered for intuitive cycling: DEFAULT → cheapest → most expensive → nearest.
 */
public enum SortMode {
    DEFAULT,
    PRICE_ASC,
    PRICE_DESC,
    DISTANCE_ASC;

    /** Returns the next mode in the cycle, wrapping around. */
    public SortMode next() {
        SortMode[] values = values();
        return values[(ordinal() + 1) % values.length];
    }
}
```

- [ ] **Step 2: Compile check**

```
cd D:\Github_myzticbean\QSFindItemAddOn
set JAVA_HOME=C:\Data\jdk-25.0.3
C:\Data\apache-maven-3.9.15\bin\mvn.cmd clean compile -q
```
Expected: BUILD SUCCESS (Lombok warnings about sun.misc.Unsafe are acceptable)

- [ ] **Step 3: Commit**

```bash
git add src/main/java/io/myzticbean/finditemaddon/models/enums/SortMode.java
git commit -m "feat: add SortMode enum for in-GUI sort cycling"
```

---

## Task 2: Create `PlayerSortFilterPrefs` Model

**Files:**
- Create: `src/main/java/io/myzticbean/finditemaddon/models/PlayerSortFilterPrefs.java`

- [ ] **Step 1: Create the file**

```java
package io.myzticbean.finditemaddon.models;

import io.myzticbean.finditemaddon.models.enums.SortMode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Holds a player's persistent sort and filter preferences for the shop search GUI.
 * Serialized to player_prefs.json via Gson. All fields must have defaults so that
 * a fresh instance (for first-time players) behaves identically to the old plugin behaviour.
 */
@Getter
@Setter
@NoArgsConstructor
public class PlayerSortFilterPrefs {
    private SortMode sortMode = SortMode.DEFAULT;
    private boolean filterHasStock = false;
    private boolean filterSameWorld = false;
    private boolean filterExcludeOwnShops = false;
}
```

- [ ] **Step 2: Compile check**

```
set JAVA_HOME=C:\Data\jdk-25.0.3
C:\Data\apache-maven-3.9.15\bin\mvn.cmd clean compile -q
```
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add src/main/java/io/myzticbean/finditemaddon/models/PlayerSortFilterPrefs.java
git commit -m "feat: add PlayerSortFilterPrefs model for persistent GUI preferences"
```

---

## Task 3: Create `PlayerPrefsStorageUtil`

**Files:**
- Create: `src/main/java/io/myzticbean/finditemaddon/utils/json/PlayerPrefsStorageUtil.java`

This follows the exact same Gson + FileReader/FileWriter pattern as the existing `ShopSearchActivityStorageUtil`.

- [ ] **Step 1: Create the file**

```java
package io.myzticbean.finditemaddon.utils.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.models.PlayerSortFilterPrefs;
import io.myzticbean.finditemaddon.utils.async.VirtualThreadScheduler;
import io.myzticbean.finditemaddon.utils.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages per-player sort/filter preferences.
 * Backed by player_prefs.json in the plugin data folder.
 * Thread-safe: the in-memory map is a ConcurrentHashMap;
 * all file I/O is dispatched to a virtual thread via VirtualThreadScheduler.
 */
public class PlayerPrefsStorageUtil {

    private static final String FILE_NAME = "player_prefs.json";

    /** In-memory store. UUID → prefs. Never contains null values. */
    private static final ConcurrentHashMap<UUID, PlayerSortFilterPrefs> prefsMap = new ConcurrentHashMap<>();

    /**
     * Returns the preferences for the given UUID.
     * If no entry exists, inserts and returns a fresh default.
     * Never returns null. Safe to call from any thread.
     */
    @NotNull
    public static PlayerSortFilterPrefs getPrefs(@NotNull UUID uuid) {
        return prefsMap.computeIfAbsent(uuid, k -> new PlayerSortFilterPrefs());
    }

    /**
     * Persists the full prefs map to disk asynchronously.
     * Called after every in-GUI state change so preferences survive reloads.
     * The write is non-blocking; the in-memory map is the source of truth.
     */
    public static void saveAsync() {
        VirtualThreadScheduler.runTaskAsync(PlayerPrefsStorageUtil::saveToFile);
    }

    /**
     * Synchronous save — called from onDisable() to guarantee a final flush
     * before the JVM exits, in case the last async save hasn't completed yet.
     */
    public static void save() {
        saveToFile();
    }

    /**
     * Loads preferences from player_prefs.json into the in-memory map.
     * Called once during plugin startup (runPluginStartupTasks).
     * No-op if the file does not exist (first run).
     */
    public static void load() {
        File file = new File(getFilePath());
        if (!file.exists()) {
            Logger.logInfo("player_prefs.json not found — starting fresh (first run).");
            return;
        }
        Gson gson = new GsonBuilder().create();
        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<HashMap<String, PlayerSortFilterPrefs>>() {}.getType();
            Map<String, PlayerSortFilterPrefs> rawMap = gson.fromJson(reader, type);
            if (rawMap != null) {
                rawMap.forEach((uuidStr, prefs) -> {
                    if (uuidStr != null && prefs != null) {
                        try {
                            prefsMap.put(UUID.fromString(uuidStr), prefs);
                        } catch (IllegalArgumentException ignored) {
                            Logger.logWarning("Skipping malformed UUID in player_prefs.json: " + uuidStr);
                        }
                    }
                });
                Logger.logInfo("Loaded player preferences for " + prefsMap.size() + " player(s).");
            }
        } catch (IOException e) {
            Logger.logError("Failed to load player_prefs.json", e);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static void saveToFile() {
        Gson gson = new GsonBuilder().create();
        File file = new File(getFilePath());
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
            // Serialise as Map<String, PlayerSortFilterPrefs> — Gson handles UUID keys poorly,
            // so convert to String keys first.
            Map<String, PlayerSortFilterPrefs> serialisable = new HashMap<>(prefsMap.size());
            prefsMap.forEach((uuid, prefs) -> serialisable.put(uuid.toString(), prefs));
            try (Writer writer = new FileWriter(file, false)) {
                gson.toJson(serialisable, writer);
                writer.flush();
            }
            Logger.logDebugInfo("Saved player preferences (" + serialisable.size() + " entries).");
        } catch (IOException e) {
            Logger.logError("Failed to save player_prefs.json", e);
        }
    }

    private static @NotNull String getFilePath() {
        return FindItemAddOn.getInstance().getDataFolder().getAbsolutePath()
                + File.separator + FILE_NAME;
    }
}
```

- [ ] **Step 2: Compile check**

```
set JAVA_HOME=C:\Data\jdk-25.0.3
C:\Data\apache-maven-3.9.15\bin\mvn.cmd clean compile -q
```
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add src/main/java/io/myzticbean/finditemaddon/utils/json/PlayerPrefsStorageUtil.java
git commit -m "feat: add PlayerPrefsStorageUtil for persistent per-player GUI preferences"
```

---

## Task 4: Hook `PlayerPrefsStorageUtil` into Plugin Lifecycle

**Files:**
- Modify: `src/main/java/io/myzticbean/finditemaddon/FindItemAddOn.java`

Two changes:
1. `runPluginStartupTasks()` — call `PlayerPrefsStorageUtil.load()` right after `loadShopsFromFile()`.
2. `onDisable()` — call `PlayerPrefsStorageUtil.save()` alongside the existing shops save.

- [ ] **Step 1: Add load call in `runPluginStartupTasks()`**

Find this block (around line 216):
```java
        // Load all hidden shops from file
        ShopSearchActivityStorageUtil.loadShopsFromFile();

        // v2.0.0.0 - Migrating hiddenShops.json to shops.json
        ShopSearchActivityStorageUtil.migrateHiddenShopsToShopsJson();
```
Add one line immediately after `loadShopsFromFile()`:
```java
        // Load all hidden shops from file
        ShopSearchActivityStorageUtil.loadShopsFromFile();
        PlayerPrefsStorageUtil.load();

        // v2.0.0.0 - Migrating hiddenShops.json to shops.json
        ShopSearchActivityStorageUtil.migrateHiddenShopsToShopsJson();
```

- [ ] **Step 2: Add save call in `onDisable()`**

Find this block (around line 183):
```java
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if(qsApi != null) {
            ShopSearchActivityStorageUtil.saveShopsToFile();
        }
```
Add the prefs save inside the same guard:
```java
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if(qsApi != null) {
            ShopSearchActivityStorageUtil.saveShopsToFile();
            PlayerPrefsStorageUtil.save();
        }
```

- [ ] **Step 3: Add import** at the top of `FindItemAddOn.java` with the other `utils.json` imports:
```java
import io.myzticbean.finditemaddon.utils.json.PlayerPrefsStorageUtil;
```

- [ ] **Step 4: Compile check**

```
set JAVA_HOME=C:\Data\jdk-25.0.3
C:\Data\apache-maven-3.9.15\bin\mvn.cmd clean compile -q
```
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/io/myzticbean/finditemaddon/FindItemAddOn.java
git commit -m "feat: hook PlayerPrefsStorageUtil load/save into plugin lifecycle"
```

---

## Task 5: Free Slots 47/48/50/51 in `PaginatedMenu`

**Files:**
- Modify: `src/main/java/io/myzticbean/finditemaddon/handlers/gui/PaginatedMenu.java`

`addMenuBottomBar()` currently places `GUI_FILLER_ITEM` at slots 47, 48, 50, 51. Remove those four lines so `FoundShopsMenu` can place the sort/filter buttons there instead.

- [ ] **Step 1: Remove the four filler placements**

Find this block inside `addMenuBottomBar()`:
```java
        inventory.setItem(47, super.GUI_FILLER_ITEM);
        inventory.setItem(48, super.GUI_FILLER_ITEM);
        inventory.setItem(50, super.GUI_FILLER_ITEM);
        inventory.setItem(51, super.GUI_FILLER_ITEM);
```
Delete those four lines. The method should now only set slots 45, 46, 49, 52, 53.

- [ ] **Step 2: Compile check**

```
set JAVA_HOME=C:\Data\jdk-25.0.3
C:\Data\apache-maven-3.9.15\bin\mvn.cmd clean compile -q
```
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add src/main/java/io/myzticbean/finditemaddon/handlers/gui/PaginatedMenu.java
git commit -m "refactor: free nav bar slots 47/48/50/51 for sort/filter buttons"
```

---

## Task 6: Add Sort/Filter Rendering to `FoundShopsMenu`

**Files:**
- Modify: `src/main/java/io/myzticbean/finditemaddon/handlers/gui/menus/FoundShopsMenu.java`

Add four new private methods and update `setMenuItems()` to call them.

- [ ] **Step 1: Add imports** at the top of `FoundShopsMenu.java` (alongside existing imports):

```java
import io.myzticbean.finditemaddon.models.PlayerSortFilterPrefs;
import io.myzticbean.finditemaddon.models.enums.SortMode;
import io.myzticbean.finditemaddon.utils.json.PlayerPrefsStorageUtil;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
```

- [ ] **Step 2: Update `setMenuItems()` to call the two new helpers**

Find the existing `setMenuItems()` opening:
```java
    @Override
    public void setMenuItems(List<FoundShopItemModel> foundShops) {
        // Add the bottom navigation bar to the menu
        addMenuBottomBar();

        // If no shops were found, return early
        if (foundShops == null || foundShops.isEmpty()) {
            return;
        }
```
Replace with:
```java
    @Override
    public void setMenuItems(List<FoundShopItemModel> foundShops) {
        // Add the bottom navigation bar to the menu
        addMenuBottomBar();
        // Populate sort/filter buttons in slots 47, 48, 50, 51
        setSortFilterButtons();

        // If no shops were found, return early
        if (foundShops == null || foundShops.isEmpty()) {
            return;
        }
```
Then find the loop that adds shops (uses `MAX_ITEMS_PER_PAGE`). Replace:
```java
        int maxItemsPerPage = MAX_ITEMS_PER_PAGE;
        // Iterate through the slots for this page
        for (int guiSlotCounter = 0; guiSlotCounter < maxItemsPerPage; guiSlotCounter++) {
            // Calculate the index in the foundShops list for the current slot
            index = maxItemsPerPage * page + guiSlotCounter;
            if (index >= foundShops.size()) {
                break;
            }

            FoundShopItemModel foundShop = foundShops.get(guiSlotCounter);
```
With:
```java
        // Apply player's sort/filter preferences lazily — raw list is never modified
        List<FoundShopItemModel> viewList = applyPrefs(foundShops);

        int maxItemsPerPage = MAX_ITEMS_PER_PAGE;
        for (int guiSlotCounter = 0; guiSlotCounter < maxItemsPerPage; guiSlotCounter++) {
            index = maxItemsPerPage * page + guiSlotCounter;
            if (index >= viewList.size()) {
                break;
            }

            FoundShopItemModel foundShop = viewList.get(index);
```

- [ ] **Step 3: Add `applyPrefs()` method** (add anywhere in the private methods section):

```java
    /**
     * Applies the player's sort/filter preferences to the raw shop list.
     * Returns the raw list directly when no preferences are active (zero overhead path).
     * The raw list is never modified; the returned list is a fresh view computed on demand.
     * Runs inside runAtEntity (main/region thread), so player.getLocation() is safe.
     */
    private List<FoundShopItemModel> applyPrefs(List<FoundShopItemModel> raw) {
        Player player = playerMenuUtility.getOwner();
        if (player == null) return raw;

        PlayerSortFilterPrefs prefs = PlayerPrefsStorageUtil.getPrefs(player.getUniqueId());

        // Short-circuit: nothing to do — return raw directly with zero allocation
        if (prefs.getSortMode() == SortMode.DEFAULT
                && !prefs.isFilterHasStock()
                && !prefs.isFilterSameWorld()
                && !prefs.isFilterExcludeOwnShops()) {
            return raw;
        }

        // Filter nulls first — mirrors the original null-guard in setMenuItems()
        Stream<FoundShopItemModel> stream = raw.stream().filter(java.util.Objects::nonNull);

        // --- Filters (each is a cheap predicate, applied in ascending cost order) ---

        if (prefs.isFilterHasStock()) {
            // Keep shops with actual stock/space. Sentinel values: -1 and MAX_VALUE mean unlimited.
            // -2 means unknown (cache miss) — excluded when filter is ON to avoid showing stale data.
            stream = stream.filter(s -> {
                int v = s.getRemainingStockOrSpace();
                return v > 0 || v == -1 || v == Integer.MAX_VALUE;
            });
        }

        if (prefs.isFilterSameWorld()) {
            World playerWorld = player.getWorld();
            stream = stream.filter(s -> playerWorld.equals(s.getShopLocation().getWorld()));
        }

        if (prefs.isFilterExcludeOwnShops()) {
            java.util.UUID playerUUID = player.getUniqueId();
            stream = stream.filter(s -> !s.getShopOwner().equals(playerUUID));
        }

        // --- Sort (at most one comparator applied) ---

        Comparator<FoundShopItemModel> comparator = switch (prefs.getSortMode()) {
            case PRICE_ASC  -> Comparator.comparingDouble(FoundShopItemModel::getShopPrice);
            case PRICE_DESC -> Comparator.comparingDouble(FoundShopItemModel::getShopPrice).reversed();
            case DISTANCE_ASC -> {
                // Use distanceSquared (no sqrt) for performance. Cross-world shops have no
                // meaningful distance — push them to the end rather than throwing.
                Location playerLoc = player.getLocation();
                yield Comparator.comparingDouble((FoundShopItemModel s) -> {
                    if (!playerLoc.getWorld().equals(s.getShopLocation().getWorld())) {
                        return Double.MAX_VALUE;
                    }
                    return playerLoc.distanceSquared(s.getShopLocation());
                });
            }
            default -> null;
        };

        if (comparator != null) {
            stream = stream.sorted(comparator);
        }

        return stream.toList();
    }
```

- [ ] **Step 4: Add `setSortFilterButtons()` method**:

```java
    /**
     * Places the sort and filter control buttons into nav bar slots 47, 48, 50, 51.
     * Called from setMenuItems() after addMenuBottomBar() has populated the other slots.
     */
    private void setSortFilterButtons() {
        Player player = playerMenuUtility.getOwner();
        if (player == null) return;
        PlayerSortFilterPrefs prefs = PlayerPrefsStorageUtil.getPrefs(player.getUniqueId());

        inventory.setItem(47, buildSortButton(prefs.getSortMode()));
        inventory.setItem(48, buildFilterButton("Has Stock/Space",   prefs.isFilterHasStock()));
        inventory.setItem(50, buildFilterButton("Same World Only",   prefs.isFilterSameWorld()));
        inventory.setItem(51, buildFilterButton("Exclude Own Shops", prefs.isFilterExcludeOwnShops()));
    }
```

- [ ] **Step 5: Add `buildSortButton()` method**:

```java
    /**
     * Builds the sort cycle button for slot 47.
     * Display name reflects the currently active sort mode.
     */
    private ItemStack buildSortButton(SortMode mode) {
        String label = switch (mode) {
            case PRICE_ASC    -> "&aSort: Price ↑";
            case PRICE_DESC   -> "&aSort: Price ↓";
            case DISTANCE_ASC -> "&aSort: Distance ↑";
            default           -> "&7Sort: Default";
        };
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName(ColorTranslator.translateColorCodes(label));
        meta.setLore(java.util.List.of(ColorTranslator.translateColorCodes("&7Click to cycle sort mode")));
        item.setItemMeta(meta);
        return item;
    }
```

- [ ] **Step 6: Add `buildFilterButton()` method**:

```java
    /**
     * Builds a filter toggle button.
     *
     * @param label  Human-readable filter name shown in the display name.
     * @param active Whether this filter is currently ON.
     */
    private ItemStack buildFilterButton(String label, boolean active) {
        ItemStack item = new ItemStack(active ? Material.LIME_DYE : Material.GRAY_DYE);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        String prefix = active ? "&a✔ " : "&7✘ ";
        meta.setDisplayName(ColorTranslator.translateColorCodes(prefix + label));
        meta.setLore(java.util.List.of(ColorTranslator.translateColorCodes("&7Click to toggle")));
        item.setItemMeta(meta);
        return item;
    }
```

- [ ] **Step 7: Compile check**

```
set JAVA_HOME=C:\Data\jdk-25.0.3
C:\Data\apache-maven-3.9.15\bin\mvn.cmd clean compile -q
```
Expected: BUILD SUCCESS

- [ ] **Step 8: Commit**

```bash
git add src/main/java/io/myzticbean/finditemaddon/handlers/gui/menus/FoundShopsMenu.java
git commit -m "feat: add lazy sort/filter rendering to FoundShopsMenu"
```

---

## Task 7: Wire Up Click Handlers + Final Build

**Files:**
- Modify: `src/main/java/io/myzticbean/finditemaddon/handlers/gui/menus/FoundShopsMenu.java`

Add four new cases to the existing `switch` in `handleNavigationClick()`.

- [ ] **Step 1: Add cases 47, 48, 50, 51 to `handleNavigationClick()`**

First, extract `Player` at the top of `handleNavigationClick()` (before the switch). The method signature is `private boolean handleNavigationClick(InventoryClickEvent event, int slot)` — `player` is not currently extracted there:
```java
Player player = (Player) event.getWhoClicked();
```

Then find the existing switch inside `handleNavigationClick()`. It currently has cases 45, 46, 52, 53. Add four new cases:

```java
        case 47 -> {
            // Sort cycle
            PlayerSortFilterPrefs prefs47 = PlayerPrefsStorageUtil.getPrefs(player.getUniqueId());
            prefs47.setSortMode(prefs47.getSortMode().next());
            PlayerPrefsStorageUtil.saveAsync();
            page = 0;
            super.open(super.playerMenuUtility.getPlayerShopSearchResult());
            yield true;
        }
        case 48 -> {
            // Filter: Has Stock/Space toggle
            PlayerSortFilterPrefs prefs48 = PlayerPrefsStorageUtil.getPrefs(player.getUniqueId());
            prefs48.setFilterHasStock(!prefs48.isFilterHasStock());
            PlayerPrefsStorageUtil.saveAsync();
            page = 0;
            super.open(super.playerMenuUtility.getPlayerShopSearchResult());
            yield true;
        }
        case 50 -> {
            // Filter: Same World Only toggle
            PlayerSortFilterPrefs prefs50 = PlayerPrefsStorageUtil.getPrefs(player.getUniqueId());
            prefs50.setFilterSameWorld(!prefs50.isFilterSameWorld());
            PlayerPrefsStorageUtil.saveAsync();
            page = 0;
            super.open(super.playerMenuUtility.getPlayerShopSearchResult());
            yield true;
        }
        case 51 -> {
            // Filter: Exclude Own Shops toggle
            PlayerSortFilterPrefs prefs51 = PlayerPrefsStorageUtil.getPrefs(player.getUniqueId());
            prefs51.setFilterExcludeOwnShops(!prefs51.isFilterExcludeOwnShops());
            PlayerPrefsStorageUtil.saveAsync();
            page = 0;
            super.open(super.playerMenuUtility.getPlayerShopSearchResult());
            yield true;
        }
```

Place these inside the `switch` statement before the `default -> false` case.

Note: `player` is already available as `(Player) event.getWhoClicked()` from `handleMenu()` — pass it in or extract it at the top of `handleNavigationClick()` if not already present. Check the existing method signature; if `player` is not available, extract it:
```java
Player player = (Player) event.getWhoClicked();
```

- [ ] **Step 2: Full build with shaded jar**

```
set JAVA_HOME=C:\Data\jdk-25.0.3
C:\Data\apache-maven-3.9.15\bin\mvn.cmd clean package -q
```
Expected: BUILD SUCCESS, `target/QSFindItemAddOn-*.jar` produced

- [ ] **Step 3: Commit**

```bash
git add src/main/java/io/myzticbean/finditemaddon/handlers/gui/menus/FoundShopsMenu.java
git commit -m "feat: wire sort/filter click handlers in FoundShopsMenu nav bar"
```

---

## Verification Checklist

Deploy `target/QSFindItemAddOn-*.jar` to a Paper + QuickShop-Hikari test server.

- [ ] Run `/finditem TO_SELL diamond` — GUI opens, 4 new buttons visible in nav bar
- [ ] **Sort — Default**: results order unchanged from pre-feature behaviour
- [ ] **Sort — Price ↑**: click HOPPER button, GUI refreshes, cheapest shop first
- [ ] **Sort — Price ↓**: click again, most expensive first
- [ ] **Sort — Distance ↑**: click again, nearest shop first; cross-world shops pushed to end
- [ ] **Sort — cycles back to Default**: click once more
- [ ] **Filter — Has Stock/Space**: toggle ON → shops with 0 stock/space disappear; toggle OFF → they return
- [ ] **Filter — Same World Only**: toggle ON → only current-world shops shown
- [ ] **Filter — Exclude Own Shops**: toggle ON → player's own shops hidden
- [ ] **Combine**: enable all 3 filters simultaneously — results are intersection of all conditions
- [ ] **Page reset**: apply a filter while on page 3 — GUI returns to page 1
- [ ] **Persistence (within session)**: close GUI, reopen — same sort/filter state shown
- [ ] **Persistence (across restart)**: restart server, reopen GUI — state still applied
- [ ] **Folia**: confirm no thread-safety errors in Folia server logs
- [ ] **Empty result set**: enable filters that eliminate all shops — empty inventory, no crash
