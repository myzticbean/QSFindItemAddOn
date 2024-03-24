package io.myzticbean.finditemaddon.QuickShopHandler;

import cc.carm.lib.easysql.api.SQLQuery;
import com.ghostchu.quickshop.QuickShop;
import com.ghostchu.quickshop.api.QuickShopAPI;
import com.ghostchu.quickshop.api.command.CommandContainer;
import com.ghostchu.quickshop.api.obj.QUser;
import com.ghostchu.quickshop.api.shop.Shop;
import com.ghostchu.quickshop.api.shop.permission.BuiltInShopPermission;
import com.ghostchu.quickshop.database.DataTables;
import io.myzticbean.finditemaddon.Commands.QSSubCommands.FindItemCmdHikariImpl;
import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.Models.CachedShop;
import io.myzticbean.finditemaddon.Models.FoundShopItemModel;
import io.myzticbean.finditemaddon.Models.ShopSearchActivityModel;
import io.myzticbean.finditemaddon.Utils.Defaults.PlayerPerms;
import io.myzticbean.finditemaddon.Utils.JsonStorageUtils.HiddenShopStorageUtil;
import io.myzticbean.finditemaddon.Utils.LoggerUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Implementation of QSApi for Hikari
 *
 * @author myzticbean
 */
public class QSHikariAPIHandler implements QSApi<QuickShop, Shop> {

    private final QuickShopAPI api;

    private final String pluginVersion;

    private final ConcurrentMap<Long, CachedShop> shopCache;

    private final int SHOP_CACHE_TIMEOUT_SECONDS = 5*60;

    private final boolean isQSHikariShopCacheImplemented;

    public QSHikariAPIHandler() {
        api = QuickShopAPI.getInstance();
        pluginVersion = Bukkit.getPluginManager().getPlugin("QuickShop-Hikari").getDescription().getVersion();
        LoggerUtils.logInfo("Initializing Shop caching");
        shopCache = new ConcurrentHashMap<>();
        isQSHikariShopCacheImplemented = checkIfQSHikariShopCacheImplemented();
    }

    public List<FoundShopItemModel> findItemBasedOnTypeFromAllShops(ItemStack item, boolean toBuy, Player searchingPlayer) {
        LoggerUtils.logDebugInfo("Is MAIN Thread?" + Bukkit.isPrimaryThread());
        long begin = System.currentTimeMillis();
        List<FoundShopItemModel> shopsFoundList = new ArrayList<>();
        List<Shop> allShops;
        if (FindItemAddOn.getConfigProvider().SEARCH_LOADED_SHOPS_ONLY) {
            allShops = new ArrayList<>(api.getShopManager().getLoadedShops());
        } else {
            allShops = getAllShops();
        }
        LoggerUtils.logDebugInfo(QS_TOTAL_SHOPS_ON_SERVER + allShops.size());
        for(Shop shopIterator : allShops) {

            // @TODO: Testing
//            testQuickShopHikariExternalCache(shopIterator);
            LoggerUtils.logDebugInfo("1: ToBuy: " + toBuy + " Location: " + shopIterator.getLocation() + " | Stock: " + getRemainingStockOrSpaceFromShopCache(shopIterator, true) + " | Space: " + getRemainingStockOrSpaceFromShopCache(shopIterator, false));
            LoggerUtils.logDebugInfo("2: ToBuy: " + toBuy + " Location: " + shopIterator.getLocation() + " | Stock: " + shopIterator.getRemainingStock() + " | Space: " + shopIterator.getRemainingSpace() + "\n");

            // check for quickshop hikari internal per-shop based search permission
            if(shopIterator.playerAuthorize(searchingPlayer.getUniqueId(), BuiltInShopPermission.SEARCH)
                    // check for blacklisted worlds
                    && (!FindItemAddOn.getConfigProvider().getBlacklistedWorlds().contains(shopIterator.getLocation().getWorld())
                    && shopIterator.getItem().getType().equals(item.getType())
//                    && (toBuy ? getRemainingStockOrSpaceFromShopCache(shopIterator, true) != 0 : getRemainingStockOrSpaceFromShopCache(shopIterator, false) != 0)
                    && (toBuy ? shopIterator.isSelling() : shopIterator.isBuying()))
                    // check for shop if hidden
                    && (!HiddenShopStorageUtil.isShopHidden(shopIterator))) {
                if(checkIfShopToBeIgnoredForFullOrEmpty(toBuy, shopIterator)) {
                    continue;
                }
                shopsFoundList.add(new FoundShopItemModel(
                        shopIterator.getPrice(),
                        QSApi.processStockOrSpace((toBuy ? getRemainingStockOrSpaceFromShopCache(shopIterator, true) : getRemainingStockOrSpaceFromShopCache(shopIterator, false))),
                        shopIterator.getOwner().getUniqueIdOptional().orElse(new UUID(0, 0)),
                        shopIterator.getLocation(),
                        shopIterator.getItem()
                ));
            }
        }
        List<FoundShopItemModel> sortedShops = handleShopSorting(toBuy, shopsFoundList);
        QSApi.logTimeTookMsg(begin);
        return sortedShops;
    }

    @NotNull
    static List<FoundShopItemModel> handleShopSorting(boolean toBuy, List<FoundShopItemModel> shopsFoundList) {
        if(!shopsFoundList.isEmpty()) {
            int sortingMethod = 2;
            try {
                sortingMethod = FindItemAddOn.getConfigProvider().SHOP_SORTING_METHOD;
            }
            catch(Exception e) {
                LoggerUtils.logError("Invalid value in config.yml : 'shop-sorting-method'");
                LoggerUtils.logError("Defaulting to sorting by prices method");
            }
            return QSApi.sortShops(sortingMethod, shopsFoundList, toBuy);
        }
        return shopsFoundList;
    }

    public List<FoundShopItemModel> findItemBasedOnDisplayNameFromAllShops(String displayName, boolean toBuy, Player searchingPlayer) {
        LoggerUtils.logDebugInfo("Is MAIN Thread?" + Bukkit.isPrimaryThread());
        long begin = System.currentTimeMillis();
        List<FoundShopItemModel> shopsFoundList = new ArrayList<>();
        List<Shop> allShops;
        if (FindItemAddOn.getConfigProvider().SEARCH_LOADED_SHOPS_ONLY) {
            allShops = new ArrayList<>(api.getShopManager().getLoadedShops());
        } else {
            allShops = getAllShops();
        }
        LoggerUtils.logDebugInfo(QS_TOTAL_SHOPS_ON_SERVER + allShops.size());
        for(Shop shopIterator : allShops) {
            // check for quickshop hikari internal per-shop based search permission
            if(shopIterator.playerAuthorize(searchingPlayer.getUniqueId(), BuiltInShopPermission.SEARCH)
                    // check for blacklisted worlds
                    && !FindItemAddOn.getConfigProvider().getBlacklistedWorlds().contains(shopIterator.getLocation().getWorld())
                    // match the item based on query
                    && shopIterator.getItem().hasItemMeta()
                    && Objects.requireNonNull(shopIterator.getItem().getItemMeta()).hasDisplayName()
                    && (shopIterator.getItem().getItemMeta().getDisplayName().toLowerCase().contains(displayName.toLowerCase())
//                    && (toBuy ? getRemainingStockOrSpaceFromShopCache(shopIterator, true) != 0 : getRemainingStockOrSpaceFromShopCache(shopIterator, false) != 0)
                    && (toBuy ? shopIterator.isSelling() : shopIterator.isBuying()))
                    // check for shop if hidden
                    && !HiddenShopStorageUtil.isShopHidden(shopIterator)) {
                if(checkIfShopToBeIgnoredForFullOrEmpty(toBuy, shopIterator)) {
                    continue;
                }
                shopsFoundList.add(new FoundShopItemModel(
                        shopIterator.getPrice(),
                        QSApi.processStockOrSpace((toBuy ? getRemainingStockOrSpaceFromShopCache(shopIterator, true) : getRemainingStockOrSpaceFromShopCache(shopIterator, false))),
                        shopIterator.getOwner().getUniqueIdOptional().orElse(new UUID(0, 0)),
                        shopIterator.getLocation(),
                        shopIterator.getItem()
                ));
            }
        }
        List<FoundShopItemModel> sortedShops = handleShopSorting(toBuy, shopsFoundList);
        QSApi.logTimeTookMsg(begin);
        return sortedShops;
    }

    public List<FoundShopItemModel> fetchAllItemsFromAllShops(boolean toBuy, Player searchingPlayer) {
        LoggerUtils.logDebugInfo("Is MAIN Thread?" + Bukkit.isPrimaryThread());
        long begin = System.currentTimeMillis();
        List<FoundShopItemModel> shopsFoundList = new ArrayList<>();
        List<Shop> allShops;
        if (FindItemAddOn.getConfigProvider().SEARCH_LOADED_SHOPS_ONLY) {
            allShops = new ArrayList<>(api.getShopManager().getLoadedShops());
        } else {
            allShops = getAllShops();
        }
        LoggerUtils.logDebugInfo(QS_TOTAL_SHOPS_ON_SERVER + allShops.size());
        for(Shop shopIterator : allShops) {
            // check for quickshop hikari internal per-shop based search permission
            if(shopIterator.playerAuthorize(searchingPlayer.getUniqueId(), BuiltInShopPermission.SEARCH)
                    // check for blacklisted worlds
                    && (!FindItemAddOn.getConfigProvider().getBlacklistedWorlds().contains(shopIterator.getLocation().getWorld())
//                    && (toBuy ? getRemainingStockOrSpaceFromShopCache(shopIterator, true) != 0 : getRemainingStockOrSpaceFromShopCache(shopIterator, false) != 0)
                    && (toBuy ? shopIterator.isSelling() : shopIterator.isBuying()))
                    // check for shop if hidden
                    && (!HiddenShopStorageUtil.isShopHidden(shopIterator))) {
                if(checkIfShopToBeIgnoredForFullOrEmpty(toBuy, shopIterator)) {
                    continue;
                }
                shopsFoundList.add(new FoundShopItemModel(
                        shopIterator.getPrice(),
                        QSApi.processStockOrSpace((toBuy ? getRemainingStockOrSpaceFromShopCache(shopIterator, true) : getRemainingStockOrSpaceFromShopCache(shopIterator, false))),
                        shopIterator.getOwner().getUniqueIdOptional().orElse(new UUID(0, 0)),
                        shopIterator.getLocation(),
                        shopIterator.getItem()
                ));
            }
        }
        List<FoundShopItemModel> sortedShops = new ArrayList<>(shopsFoundList);
        if(!shopsFoundList.isEmpty()) {
            int sortingMethod = 1;
            sortedShops = QSApi.sortShops(sortingMethod, shopsFoundList, toBuy);
        }
        QSApi.logTimeTookMsg(begin);
        return sortedShops;
    }

    public Material getShopSignMaterial() {
        return com.ghostchu.quickshop.util.Util.getSignMaterial();
    }

    public Shop findShopAtLocation(Block block) {
        Location loc = new Location(block.getWorld(), block.getX(), block.getY(), block.getZ());
        return api.getShopManager().getShop(loc);
    }

    public boolean isShopOwnerCommandRunner(Player player, Shop shop) {
        LoggerUtils.logDebugInfo("Shop owner: " + shop.getOwner() + " | Player: " + player.getUniqueId());
        return shop.getOwner().getUniqueId().toString().equalsIgnoreCase(player.getUniqueId().toString());
    }

    @Override
    public List<Shop> getAllShops() {
        return api.getShopManager().getAllShops();
    }

    @Override
    public List<ShopSearchActivityModel> syncShopsListForStorage(List<ShopSearchActivityModel> globalShopsList) {
        // copy all shops from shops list in API to a temp globalShopsList
        // now check shops from temp globalShopsList in current globalShopsList and pull playerVisit data
        List<ShopSearchActivityModel> tempGlobalShopsList = new ArrayList<>();
        for (Shop shop_i : getAllShops()) {
            Location shopLoc = shop_i.getLocation();
            tempGlobalShopsList.add(new ShopSearchActivityModel(
                    shopLoc.getWorld().getName(),
                    shopLoc.getX(),
                    shopLoc.getY(),
                    shopLoc.getZ(),
                    shopLoc.getPitch(),
                    shopLoc.getYaw(),
                    convertQUserToUUID(shop_i.getOwner()).toString(),
                    new ArrayList<>(),
                    false
            ));
        }

        for (ShopSearchActivityModel shop_temp : tempGlobalShopsList) {
            ShopSearchActivityModel tempShopToRemove = null;
            for (ShopSearchActivityModel shop_global : globalShopsList) {
                if (shop_temp.getWorldName().equalsIgnoreCase(shop_global.getWorldName())
                        && shop_temp.getX() == shop_global.getX()
                        && shop_temp.getY() == shop_global.getY()
                        && shop_temp.getZ() == shop_global.getZ()
                        && shop_temp.getShopOwnerUUID().equalsIgnoreCase(shop_global.getShopOwnerUUID())
                ) {
                    shop_temp.setPlayerVisitList(shop_global.getPlayerVisitList());
                    shop_temp.setHiddenFromSearch(shop_global.isHiddenFromSearch());
                    tempShopToRemove = shop_global;
                    break;
                }
            }
            if (tempShopToRemove != null)
                globalShopsList.remove(tempShopToRemove);
        }
        return tempGlobalShopsList;
    }

    /**
     * Register finditem sub-command for /qs
     * Unregister /qs find
     */
    @Override
    public void registerSubCommand() {
        LoggerUtils.logInfo("Unregistered find sub-command for /qs");
        for (CommandContainer cmdContainer : api.getCommandManager().getRegisteredCommands()) {
            if (cmdContainer.getPrefix().equalsIgnoreCase("find")) {
                api.getCommandManager().unregisterCmd(cmdContainer);
                break;
            }
        }
        LoggerUtils.logInfo("Registered finditem sub-command for /qs");
        api.getCommandManager().registerCmd(
                CommandContainer.builder()
                        .prefix("finditem")
                        .permission(PlayerPerms.FINDITEM_USE.value())
                        .hidden(false)
                        .description(locale -> Component.text("Search for items from all shops using an interactive GUI"))
                        .executor(new FindItemCmdHikariImpl())
                        .build());
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
     * @param toBuy
     * @param shop
     * @return If shop needs to be ignored from list
     */
    private boolean checkIfShopToBeIgnoredForFullOrEmpty(boolean toBuy, Shop shop) {
        boolean ignoreEmptyChests = FindItemAddOn.getConfigProvider().IGNORE_EMPTY_CHESTS;
        if(ignoreEmptyChests) {
            if(toBuy) {
                return getRemainingStockOrSpaceFromShopCache(shop, true) == 0;

            } else return getRemainingStockOrSpaceFromShopCache(shop, false) == 0;

        }
        return false;
    }

    /**
     * Fallback to fetching info from ShopCache to avoid lag
     * @param shop QuickShop Shop instance
     * @param fetchRemainingStock True if fetching remaining stock, False if fetching remaining space
     * @return
     */
    private int getRemainingStockOrSpaceFromShopCache___test(Shop shop, boolean fetchRemainingStock) {
        LoggerUtils.logDebugInfo("Shop ID: " + shop.getShopId());
        CachedShop cachedShop = shopCache.get(shop.getShopId());
        if (cachedShop == null || QSApi.isTimeDifferenceGreaterThanSeconds(cachedShop.getLastFetched(), new Date(), SHOP_CACHE_TIMEOUT_SECONDS)) {
            cachedShop = CachedShop.builder()
                    .shopId(shop.getShopId())
                    .remainingStock(shop.getRemainingStock())
                    .remainingSpace(shop.getRemainingSpace())
                    .lastFetched(new Date())
                    .build();
            shopCache.put(cachedShop.getShopId(), cachedShop);
            LoggerUtils.logDebugInfo("Adding to ShopCache: " + shop.getShopId());
        }
        return (fetchRemainingStock ? cachedShop.getRemainingStock() : cachedShop.getRemainingSpace());
    }

    private int getRemainingStockOrSpaceFromShopCache(Shop shop, boolean fetchRemainingStock) {
        LoggerUtils.logDebugInfo("Shop ID: " + shop.getShopId());
        String mainVersionStr = pluginVersion.split("\\.")[0];
        int mainVersion = Integer.parseInt(mainVersionStr);
        if (mainVersion >= 6) {
            // New feature available
            return (fetchRemainingStock ? shop.getRemainingStock() : shop.getRemainingSpace());
        }
        else {
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
                LoggerUtils.logDebugInfo("Adding to ShopCache: " + shop.getShopId());
            }
            return (fetchRemainingStock ? cachedShop.getRemainingStock() : cachedShop.getRemainingSpace());
        }
    }

    // An empty ResultSet or a value that returns -1 means that the value is not cached.
    // Normally when space is -1, stock is not, and vice versa.
    // In special cases, neither value may be -1.
    // If Stock 0 -> Shop is admin or no stock
    private void testQuickShopHikariExternalCache(Shop shop) throws RuntimeException {
        boolean fetchRemainingStock = false;
        long shopId = shop.getShopId();
        try (SQLQuery query = DataTables.EXTERNAL_CACHE.createQuery()
                .addCondition("shop", shopId)
                .selectColumns("space", "stock")
                .setLimit(1)
                .build()
                .execute(); ResultSet resultSet = query.getResultSet()) {
            if(resultSet.next()){
                long stock = resultSet.getLong("stock");
                long space = resultSet.getLong("space");
                // stock or space can be `-1`
                LoggerUtils.logWarning("1: Location: " + shop.getLocation() + " | Stock: " + stock + " | Space: " + space);
            }else{
                // no cached data
                LoggerUtils.logWarning("No cached data found!");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkIfQSHikariShopCacheImplemented() {
        String mainVersionStr = pluginVersion.split("\\.")[0];
        int mainVersion = Integer.parseInt(mainVersionStr);
        return mainVersion >= 6;
    }

    @Override
    public boolean isQSShopCacheImplemented() {
        return isQSHikariShopCacheImplemented;
    }
}
