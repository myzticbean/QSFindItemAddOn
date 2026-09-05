/**
 * QSFindItemAddOn: An Minecraft add-on plugin for the QuickShop Hikari
 * and Reremake Shop plugins for Spigot server platform.
 * Copyright (C) 2021  myzticbean
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.myzticbean.finditemaddon.quickshop.impl;

import com.ghostchu.quickshop.QuickShop;
import com.ghostchu.quickshop.QuickShopBukkit;
import com.ghostchu.quickshop.api.QuickShopAPI;
import com.ghostchu.quickshop.api.command.CommandContainer;
import com.ghostchu.quickshop.api.obj.QUser;
import com.ghostchu.quickshop.api.shop.Shop;
import com.ghostchu.quickshop.api.shop.permission.BuiltInShopPermission;
import com.ghostchu.quickshop.util.Util;
import io.myzticbean.finditemaddon.quickshop.QSApi;
import io.myzticbean.finditemaddon.commands.quickshop.subcommands.FindItemCmdHikariImpl;
import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.models.CachedShop;
import io.myzticbean.finditemaddon.models.FoundShopItemModel;
import io.myzticbean.finditemaddon.models.ShopSearchActivityModel;
import io.myzticbean.finditemaddon.models.enums.PlayerPermsEnum;
import io.myzticbean.finditemaddon.utils.async.VirtualThreadScheduler;
import io.myzticbean.finditemaddon.utils.json.HiddenShopStorageUtil;
import io.myzticbean.finditemaddon.utils.log.Logger;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

/**
 * Implementation of QSApi for Hikari
 * @author myzticbean
 */
public class QSHikariAPIHandler implements QSApi<QuickShopAPI, Shop> {

    private static final int SHOP_CACHE_TIMEOUT_SECONDS = 5*60;
    private static final int PERMISSION_CHECK_TIMEOUT_SECONDS = 5;
    private static final int CHUNK_LOAD_TIMEOUT_SECONDS = 5;
    /** Shared timeout for the stock/space reads in {@link #resolveStockOrSpaceAsync}. */
    private static final int STOCK_READ_TIMEOUT_SECONDS = 5;
    private final QuickShopAPI api;
    private final String pluginVersion;
    private final ConcurrentMap<Long, CachedShop> shopCache;
    private final boolean isQSHikariShopCacheImplemented;

    public QSHikariAPIHandler() {
        api = QuickShopAPI.getInstance();
        pluginVersion = Bukkit.getPluginManager().getPlugin("QuickShop-Hikari").getDescription().getVersion();
        Logger.logInfo("Initializing Shop caching");
        shopCache = new ConcurrentHashMap<>();
        isQSHikariShopCacheImplemented = checkIfQSHikariShopCacheImplemented();
    }

    public CompletableFuture<List<FoundShopItemModel>> findItemBasedOnTypeFromAllShops(ItemStack item, boolean toBuy, Player searchingPlayer) {
        return searchShops(
            shop -> shop.getItem().getType().equals(item.getType()) && (toBuy ? shop.isSelling() : shop.isBuying()),
            toBuy, searchingPlayer);
    }

    public CompletableFuture<List<FoundShopItemModel>> findItemBasedOnDisplayNameFromAllShops(String displayName, boolean toBuy, Player searchingPlayer) {
        return searchShops(
            shop -> shop.getItem().hasItemMeta()
                && Objects.requireNonNull(shop.getItem().getItemMeta()).hasDisplayName()
                && shop.getItem().getItemMeta().getDisplayName().toLowerCase().contains(displayName.toLowerCase())
                && (toBuy ? shop.isSelling() : shop.isBuying()),
            toBuy, searchingPlayer);
    }

    public CompletableFuture<List<FoundShopItemModel>> fetchAllItemsFromAllShops(boolean toBuy, Player searchingPlayer) {
        return searchShops(
            shop -> toBuy ? shop.isSelling() : shop.isBuying(),
            toBuy, searchingPlayer);
    }

    private CompletableFuture<List<FoundShopItemModel>> searchShops(Predicate<Shop> itemFilter, boolean toBuy, Player searchingPlayer) {
        var begin = Instant.now();
        return VirtualThreadScheduler.supplyAsync(() -> {
            List<FoundShopItemModel> shopsFoundList = new CopyOnWriteArrayList<>();
            List<Shop> allShops = fetchAllShopsFromQS();
            Logger.logDebugInfo(QS_TOTAL_SHOPS_ON_SERVER + allShops.size());
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (Shop shopIterator : allShops) {
                futures.add(processShopMatchFuture(itemFilter, toBuy, shopIterator, shopsFoundList, searchingPlayer));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            List<FoundShopItemModel> sortedShops = handleShopSorting(toBuy, shopsFoundList);
            QSApi.logTimeTookMsg(begin);
            return sortedShops;
        });
    }

    /**
     * Checks permission and evaluates the item filter/match for a single shop on the entity's
     * region thread (via the Folia-safe scheduler), since {@code itemFilter}, the BentoBox lock
     * check, and {@code Shop#getItem()} touch Bukkit/QuickShop APIs (shop item, meta, location -
     * {@code getItem()} fires a {@code ShopItemEvent} internally) that are not safe to call
     * off-thread ({@link BuiltInShopPermission#SEARCH}). The matched item is captured here, on the
     * main thread, so the async phase below never has to call {@code getItem()} again.
     * <p>
     * If matched, stock/space is then read and the shop is added to the list on a virtual thread
     * (see {@link #resolveStockOrSpace}). As of QuickShop-Hikari 6.3.0.0 the stock/space getters no
     * longer assert they are off the main thread - {@code getRemainingStock()} performs its own
     * region hop internally - but running off-region is still what we want, so that the search does
     * not occupy a region thread for the duration.
     * <p>
     * Any failure for a single shop is logged and skipped rather than failing the whole search.
     */
    private CompletableFuture<Void> processShopMatchFuture(Predicate<Shop> itemFilter, boolean toBuy, Shop shopIterator,
                                                             List<FoundShopItemModel> shopsFoundList, Player searchingPlayer) {
        CompletableFuture<ItemStack> matchFuture = new CompletableFuture<>();
        FindItemAddOn.getScheduler().runAtEntity(searchingPlayer, _ -> {
            try {
                boolean isMatch = shopIterator.playerAuthorize(searchingPlayer.getUniqueId(), BuiltInShopPermission.SEARCH)
                        && !FindItemAddOn.getConfigProvider().getBlacklistedWorlds().contains(shopIterator.bukkitLocation().getWorld())
                        && !HiddenShopStorageUtil.isShopHidden(shopIterator)
                        && isWithinSearchDistance(shopIterator.bukkitLocation(), searchingPlayer.getLocation())
                        && itemFilter.test(shopIterator)
                        && !isShopInLockedBentoboxIsland(shopIterator, searchingPlayer);
                matchFuture.complete(isMatch ? shopIterator.getItem() : null);
            } catch (Exception e) {
                matchFuture.completeExceptionally(e);
            }
        });
        // If the player disconnects before the scheduler fires, the future would never
        // complete and allOf().join() would block the virtual thread indefinitely.
        return matchFuture.orTimeout(PERMISSION_CHECK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    if (ex instanceof TimeoutException) {
                        Logger.logDebugInfo("Shop search processing timed out for player: " + searchingPlayer.getName());
                    } else {
                        Logger.logWarning("Skipping shop during search due to error (" + ex.getClass().getSimpleName() + "): " + ex.getMessage());
                    }
                    return null;
                })
                // Async, not just off the region thread for its own sake: QuickShop's
                // queryShopInventoryCacheInDatabase() (used inside finalizeMatchedShop) asserts its
                // caller is already off the main/region thread and throws otherwise.
                .thenComposeAsync(matchedItem -> matchedItem != null
                        ? finalizeMatchedShop(toBuy, shopIterator, matchedItem, shopsFoundList)
                        : CompletableFuture.completedFuture(null), VirtualThreadScheduler.executor());
    }

    private boolean isWithinSearchDistance(Location shopLocation, Location playerLocation) {
        int maxDistance = FindItemAddOn.getConfigProvider().SHOP_SEARCH_MAX_DISTANCE;
        if (maxDistance <= 0) return true;
        if (!shopLocation.getWorld().equals(playerLocation.getWorld())) return true;
        return shopLocation.distanceSquared(playerLocation) <= (double) maxDistance * maxDistance;
    }

    /**
     * Replaces {@code Shop#getPrice()}, deprecated for removal in QuickShop-Hikari 6.3.0.0.
     * {@code Shop} became generic there but {@code ShopManager} still hands out raw {@code Shop},
     * so {@code price()} erases to {@code Object} and needs the cast.
     */
    private static double getShopPrice(@NotNull Shop shop) {
        return (Double) shop.price();
    }

    /**
     * Checks if the shop owner has enough balance to buy at least one item
     * @param shop The shop to check
     * @return true if owner has enough balance, false otherwise
     */
    private static boolean isOwnerHavingEnoughBalance(@NotNull Shop shop) {
        // Skip check for admin shops
        if (shop.getOwner().getUniqueIdOptional().isEmpty()) {
            return true;
        }

        double price = getShopPrice(shop);
        double itemAmount = shop.getItem().getAmount();
        double pricePerTransaction = price * itemAmount;

        var economy = getQuickShop().getEconomyManager().provider();

        if (economy == null) {
            Logger.logError("Economy provider not found!");
            return false;
        }

        var qUser = shop.getOwner();
        var uuid = qUser.getUniqueIdIfRealPlayer().orElse(null);
        // return true if not a real player
        if(Objects.isNull(uuid)) {
            return true;
        }
        var bukkitPlayer = Bukkit.getOfflinePlayer(uuid);
        var world = bukkitPlayer.getLocation().getWorld();
        if(Objects.isNull(world)) {
            Logger.logError("Shop owner hasn't played before ever in the server (?) - ShopInfo: " + shop.getOwner().getUsername());
            return true;
        }
        var currency = shop.getCurrency();

        // Get owner's balance through QuickShop API
        return economy.balance(qUser, world.getName(), currency).compareTo(BigDecimal.valueOf(pricePerTransaction)) >= 0;
    }

    private static QuickShop getQuickShop() {
        return ((QuickShopBukkit) QuickShopAPI.getPluginInstance()).getQuickShop();
    }

    @NotNull
    static List<FoundShopItemModel> handleShopSorting(boolean toBuy, @NotNull List<FoundShopItemModel> shopsFoundList) {
        Logger.logDebugInfo("Matching shops found: " + shopsFoundList.size());
        if(!shopsFoundList.isEmpty()) {
            int sortingMethod = 2;
            try {
                sortingMethod = FindItemAddOn.getConfigProvider().SHOP_SORTING_METHOD;
            }
            catch(Exception e) {
                Logger.logError("Invalid value in config.yml : 'shop-sorting-method'");
                Logger.logError("Defaulting to sorting by prices method");
            }
            return QSApi.sortShops(sortingMethod, shopsFoundList, toBuy);
        }
        return shopsFoundList;
    }

    private List<Shop> fetchAllShopsFromQS() {
        List<Shop> allShops;
        if (FindItemAddOn.getConfigProvider().SEARCH_LOADED_SHOPS_ONLY) {
            allShops = new ArrayList<>(api.getShopManager().getLoadedShops());
        } else {
            allShops = getAllShops();
        }
        return allShops;
    }

    public Material getShopSignMaterial() {
        return com.ghostchu.quickshop.util.Util.getSignMaterial();
    }

    public Shop findShopAtLocation(Block block) {
        Location loc = block.getLocation(); // Simpler way to get location

        // Try getting shop directly first
        Shop shop = api.getShopManager().getShopIncludeAttached(loc);

        // If no shop found and block is a chest, check if it's a double chest
        if (shop == null && block.getType() == Material.CHEST) {
            Block secondHalf = Util.getSecondHalf(block);
            if (secondHalf != null) {
                shop = api.getShopManager().getShopIncludeAttached(secondHalf.getLocation());
            }
        }

        return shop;
    }

    @Override
    public boolean isShopOwnerCommandRunner(Player player, Shop shop) {
        Logger.logDebugInfo("Shop owner: " + shop.getOwner() + " | Player: " + player.getUniqueId());
        return shop.getOwner().getUniqueId().equals(player.getUniqueId());
    }

    @Override
    public List<Shop> getAllShops() {
        return api.getShopManager().getAllShops();
    }

    @Override
    public List<ShopSearchActivityModel> syncShopsListForStorage(List<ShopSearchActivityModel> globalShopsList) {
        long start = System.currentTimeMillis();
        // copy all shops from shops list in API to a temp globalShopsList
        // now check shops from temp globalShopsList in current globalShopsList and pull playerVisit data
        List<ShopSearchActivityModel> tempGlobalShopsList = new ArrayList<>();
        getAllShops().forEach(shopItem -> {
            Location shopLoc = shopItem.bukkitLocation();
            tempGlobalShopsList.add(new ShopSearchActivityModel(
                    shopLoc.getWorld().getName(),
                    shopLoc.getX(),
                    shopLoc.getY(),
                    shopLoc.getZ(),
                    shopLoc.getPitch(),
                    shopLoc.getYaw(),
                    convertQUserToUUID(shopItem.getOwner()).toString(),
                    new ArrayList<>(),
                    false
            ));
        });

        // Index the persisted shop list by location+owner key for O(1) lookup
        Map<String, ShopSearchActivityModel> globalShopsMap = HashMap.newHashMap(globalShopsList.size());
        for (ShopSearchActivityModel shop_global : globalShopsList) {
            if (shop_global != null) {
                globalShopsMap.put(shopKey(shop_global), shop_global);
            }
        }
        // For each live shop, pull saved visit/hidden state from the index if it exists.
        // remove() is used so each persisted entry is consumed at most once.
        for (ShopSearchActivityModel shopTemp : tempGlobalShopsList) {
            ShopSearchActivityModel shopGlobal = globalShopsMap.remove(shopKey(shopTemp));
            if (shopGlobal != null) {
                shopTemp.setPlayerVisitList(shopGlobal.getPlayerVisitList());
                shopTemp.setHiddenFromSearch(shopGlobal.isHiddenFromSearch());
            }
        }
        Logger.logDebugInfo("Shops List sync complete. Time took: " + (System.currentTimeMillis() - start) + "ms.");
        return tempGlobalShopsList;
    }

    private static String shopKey(ShopSearchActivityModel shop) {
        return shop.getWorldName().toLowerCase() + ":" + shop.getX() + ":" + shop.getY() + ":" + shop.getZ() + ":" + shop.getShopOwnerUUID().toLowerCase();
    }

    /**
     * Register finditem sub-command for /qs
     * Unregister /qs find
     */
    @Override
    public void registerSubCommand() {
        Logger.logInfo("Unregistered find sub-command for /qs");
        for (CommandContainer cmdContainer : api.getCommandManager().getRegisteredCommands()) {
            if (cmdContainer.getPrefix().equalsIgnoreCase("find")) {
                api.getCommandManager().unregisterCmd(cmdContainer);
                break;
            }
        }
        Logger.logInfo("Registered finditem sub-command for /qs");
        api.getCommandManager().registerCmd(
                CommandContainer.builder()
                        .prefix("finditem")
                        .permission(PlayerPermsEnum.FINDITEM_USE.value())
                        .hidden(false)
                        .description(locale -> Component.text("Search for items from all shops using an interactive GUI"))
                        .executor(new FindItemCmdHikariImpl())
                        .build());
    }

    @Override
    public boolean isQSShopCacheImplemented() {
        return isQSHikariShopCacheImplemented;
    }

    @Override
    public int processUnknownStockSpace(Location shopLoc, boolean toBuy) {
        // This process needs to run in MAIN thread
        Util.ensureThread(false);
        Logger.logDebugInfo("Fetching stock/space from MAIN thread...");
        Shop qsShop = api.getShopManager().getShop(shopLoc);
        if(qsShop != null) {
            return (toBuy ? qsShop.getRemainingStock() : qsShop.getRemainingSpace());
        } else {
            return -2;
        }
    }

    private UUID convertQUserToUUID(QUser qUser) {
        Optional<UUID> uuid = qUser.getUniqueIdOptional();
        if (uuid.isPresent()) {
            return uuid.get();
        }
        String username = qUser.getUsernameOptional().orElse("Unknown");
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
    }

    public UUID convertNameToUuid(String playerName) {
        return api.getPlayerFinder().name2Uuid(playerName);
    }

    /**
     * If IGNORE_EMPTY_CHESTS is true -> do not add empty stock or space
     * If to buy -> If shop has no stock -> based on ignore flag, decide to include it or not
     * If to sell -> If shop has no space -> based on ignore flag, decide to include it or not
     * @param stockOrSpace Stock or space
     * @return If shop needs to be ignored from list
     */
    private boolean isShopToBeIgnoredForFullOrEmpty(int stockOrSpace) {
        boolean ignoreEmptyChests = FindItemAddOn.getConfigProvider().IGNORE_EMPTY_CHESTS;
        if(ignoreEmptyChests) {
            return stockOrSpace == 0;
        }
        return false;
    }

    private int getRemainingStockOrSpaceFromShopCache(Shop shop, boolean fetchRemainingStock) {
        if (isQSHikariShopCacheImplemented) {
            // New feature available
            int stockOrSpace = (fetchRemainingStock ? shop.getRemainingStock() : shop.getRemainingSpace());
            Logger.logDebugInfo("Stock/Space from cache: " + stockOrSpace);
            return stockOrSpace;
        } else {
            // Show warning
            Logger.logWarning("Update recommended to QuickShop-Hikari v6+! You are still using v" + pluginVersion);
            // PREPARE FOR LAG
            CachedShop cachedShop = shopCache.get(shop.getShopId());
            if (cachedShop == null || QSApi.isTimeDifferenceGreaterThanSeconds(cachedShop.getLastFetched(), new Date(), SHOP_CACHE_TIMEOUT_SECONDS)) {
                cachedShop = CachedShop.builder()
                        .shopId(shop.getShopId())
                        .remainingStock(shop.getRemainingStock())
                        .remainingSpace(shop.getRemainingSpace())
                        .lastFetched(new Date())
                        .build();
                shopCache.put(cachedShop.getShopId(), cachedShop);
                Logger.logDebugInfo("Adding to ShopCache: " + shop.getShopId());
            }
            return (fetchRemainingStock ? cachedShop.getRemainingStock() : cachedShop.getRemainingSpace());
        }
    }

    private boolean checkIfQSHikariShopCacheImplemented() {
        String mainVersionStr = pluginVersion.split("\\.")[0];
        int mainVersion = Integer.parseInt(mainVersionStr);
        return mainVersion >= 6;
    }

    private boolean isShopInLockedBentoboxIsland(Shop shop, Player searchingPlayer) {
        if (FindItemAddOn.getConfigProvider().BENTOBOX_IGNORE_LOCKED_ISLAND_SHOPS
                && Objects.nonNull(FindItemAddOn.getBentoboxPlugin())
                && FindItemAddOn.getBentoboxPlugin().isIslandLocked(shop.bukkitLocation(), searchingPlayer)) {
            Logger.logDebugInfo("Shop is in locked BentoBox island - ignoring");
            return true;
        }
        return false;
    }

    /**
     * Resolves a matched shop's stock/space, applies the empty/full and owner-balance filters, and
     * appends it to the results.
     * <p>
     * The continuation is pinned to a virtual thread because the upstream stage completes on a
     * QuickShop region or database thread, neither of which should run the economy lookup in
     * {@link #isOwnerHavingEnoughBalance}. {@code matchedItem} is passed in rather than re-read via
     * {@code shopIterator.getItem()}, which fires a {@code ShopItemEvent} and must stay on the
     * region thread where the match was evaluated.
     */
    private CompletableFuture<Void> finalizeMatchedShop(boolean toBuy, Shop shopIterator, ItemStack matchedItem, List<FoundShopItemModel> shopsFoundList) {
        Logger.logDebugInfo("Shop match found: " + shopIterator.bukkitLocation());
        return resolveStockOrSpaceAsync(shopIterator, toBuy)
                .thenAcceptAsync(stockOrSpace -> {
                    if (isShopToBeIgnoredForFullOrEmpty(stockOrSpace)) {
                        return;
                    }
                    // check if owner has enough balance for buying shops
                    if (!toBuy && !isOwnerHavingEnoughBalance(shopIterator)) {
                        Logger.logDebugInfo("Shop Owner is poor");
                        return;
                    }
                    shopsFoundList.add(new FoundShopItemModel(
                            getShopPrice(shopIterator),
                            QSApi.processStockOrSpace(stockOrSpace),
                            shopIterator.getOwner().getUniqueIdOptional().orElse(new UUID(0, 0)),
                            shopIterator.bukkitLocation(),
                            matchedItem,
                            toBuy
                    ));
                }, VirtualThreadScheduler.executor())
                .exceptionally(ex -> {
                    Logger.logWarning("Skipping shop during search due to error ("
                            + ex.getClass().getSimpleName() + "): " + ex.getMessage());
                    return null;
                });
    }

    /**
     * Resolves a matched shop's stock (buying) or space (selling), preferring the cheapest source:
     * unlimited shop, then a live read if the chunk is loaded, then QuickShop's persisted inventory
     * count cache, and only then a chunk force-load (see issue #111). Never blocks.
     * <p>
     * Unlimited is checked first both because {@link QSApi#processStockOrSpace(int)} maps its
     * {@code -1} to {@link Integer#MAX_VALUE} and because it removes the ambiguity in QuickShop's
     * {@code -1} count-cache sentinel, which conflates "unlimited" with "never calculated".
     */
    private CompletableFuture<Integer> resolveStockOrSpaceAsync(Shop shop, boolean toBuy) {
        if (shop.isUnlimited()) {
            return CompletableFuture.completedFuture(-1);
        }
        if (isShopChunkLoaded(shop)) {
            // No getRemainingSpaceAsync() counterpart exists, so the sell path stays synchronous.
            return toBuy
                    ? shop.getRemainingStockAsync().orTimeout(STOCK_READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                            .exceptionally(ex -> logAndDefault("Live stock read failed", shop, ex, 0))
                    : VirtualThreadScheduler.supplyAsync(() -> getRemainingStockOrSpaceFromShopCache(shop, false));
        }
        return readStockOrSpaceFromQsCountCacheAsync(shop, toBuy)
                .thenComposeAsync(cachedValue -> {
                    if (cachedValue >= 0) {
                        return CompletableFuture.completedFuture(cachedValue);
                    }
                    Logger.logDebugInfo("Falling back to chunk force-load for shop: " + shop.getShopId());
                    return VirtualThreadScheduler.supplyAsync(
                            () -> fetchLiveStockOrSpaceForUnloadedChunk(shop, toBuy));
                }, VirtualThreadScheduler.executor());
    }

    /**
     * Reads stock/space from QuickShop's persisted inventory count cache, which survives the chunk
     * being unloaded.
     * <p>
     * Any negative result means "unusable, ask someone else". QuickShop documents {@code -1} as
     * "never calculated or unlimited" and {@code -2} as "not cached yet", but a third case occurs
     * in practice: {@code InternalListener#shopInventoryCalc} writes both stock and space on every
     * {@code ShopInventoryCalculateEvent}, and {@code ContainerShop} fires that event with
     * {@code -1} in whichever field it did not just compute - so a stock calculation clobbers the
     * cached space and vice versa.
     */
    private CompletableFuture<Integer> readStockOrSpaceFromQsCountCacheAsync(Shop shop, boolean toBuy) {
        return api.getShopManager()
                .queryShopInventoryCacheInDatabase(shop)
                .orTimeout(STOCK_READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .thenApplyAsync(countCache -> {
                    int cachedValue = (toBuy ? countCache.getStock() : countCache.getSpace());
                    Logger.logDebugInfo("QS inventory count cache for shop " + shop.getShopId() + ": " + cachedValue);
                    return cachedValue;
                }, VirtualThreadScheduler.executor())
                .exceptionally(ex -> logAndDefault("Failed to read QS inventory count cache", shop, ex, -2));
    }

    private static int logAndDefault(String what, Shop shop, Throwable ex, int fallback) {
        Logger.logDebugInfo(what + " for shop " + shop.getShopId() + ": "
                + ex.getClass().getSimpleName() + " - " + ex.getMessage());
        return fallback;
    }

    private boolean isShopChunkLoaded(Shop shop) {
        Location loc = shop.bukkitLocation();
        var world = loc.getWorld();
        return world != null && world.isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
    }

    /**
     * Forces the shop's chunk to load on its owning region thread (Folia-safe via
     * {@link com.tcoded.folialib.impl.PlatformScheduler#runAtLocation}), re-reads stock/space with
     * the chunk actually present, then unloads the chunk again if this call is what loaded it.
     */
    private int fetchLiveStockOrSpaceForUnloadedChunk(Shop shop, boolean toBuy) {
        Location loc = shop.bukkitLocation();
        CompletableFuture<Integer> future = new CompletableFuture<>();
        FindItemAddOn.getScheduler().runAtLocation(loc, _ -> {
            try {
                var world = loc.getWorld();
                int chunkX = loc.getBlockX() >> 4;
                int chunkZ = loc.getBlockZ() >> 4;
                boolean wasLoaded = world != null && world.isChunkLoaded(chunkX, chunkZ);
                if (!wasLoaded && world != null) {
                    world.getChunkAt(chunkX, chunkZ);
                }
                int liveStockOrSpace = toBuy ? shop.getRemainingStock() : shop.getRemainingSpace();
                if (!wasLoaded && world != null) {
                    world.unloadChunk(chunkX, chunkZ, true);
                }
                future.complete(liveStockOrSpace);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future.orTimeout(CHUNK_LOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    Logger.logDebugInfo("Failed to load chunk to verify live stock/space for shop at "
                            + loc + ": " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
                    return 0;
                })
                .join();
    }
}
