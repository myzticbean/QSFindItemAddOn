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
package io.myzticbean.finditemaddon.QuickShopHandler;

import io.myzticbean.finditemaddon.Commands.QSSubCommands.FindItemCmdReremakeImpl;
import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.Models.CachedShop;
import io.myzticbean.finditemaddon.Models.FoundShopItemModel;
import io.myzticbean.finditemaddon.Models.ShopSearchActivityModel;
import io.myzticbean.finditemaddon.Utils.Defaults.PlayerPermsEnum;
import io.myzticbean.finditemaddon.Utils.JsonStorageUtils.HiddenShopStorageUtil;
import io.myzticbean.finditemaddon.Utils.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.api.QuickShopAPI;
import org.maxgamer.quickshop.api.command.CommandContainer;
import org.maxgamer.quickshop.api.shop.Shop;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Implementation of QSApi for Reremake
 * @author myzticbean
 */
@Deprecated(since = "2.0.6.0")
public class QSReremakeAPIHandler implements QSApi<QuickShop, Shop> {

    private final QuickShopAPI api;
    private final QuickShop quickShop;
    private final ConcurrentMap<Location, CachedShop> shopCache;
    private final int SHOP_CACHE_TIMEOUT_SECONDS = 60;
    private final String QS_REREMAKE_PLUGIN_NAME = "QuickShop";

    public QSReremakeAPIHandler() {
        api = (QuickShopAPI) Bukkit.getPluginManager().getPlugin(QS_REREMAKE_PLUGIN_NAME);
        quickShop = (QuickShop) Bukkit.getPluginManager().getPlugin(QS_REREMAKE_PLUGIN_NAME);
        LoggerUtils.logInfo("Initializing Shop caching");
        shopCache = new ConcurrentHashMap<>();
    }

    @Override
    public List<FoundShopItemModel> findItemBasedOnTypeFromAllShops(ItemStack item, boolean toBuy, Player searchingPlayer) {
        var begin = Instant.now();
        List<FoundShopItemModel> shopsFoundList = new ArrayList<>();
        List<Shop> allShops;
        if(FindItemAddOn.getConfigProvider().SEARCH_LOADED_SHOPS_ONLY) {
            allShops = new ArrayList<>(api.getShopManager().getLoadedShops());
        }
        else {
            allShops = api.getShopManager().getAllShops();
        }
        logTotalShopsOnServer(allShops.size());
        for(Shop shop_i : allShops) {
            // check for blacklisted worlds
            if(!FindItemAddOn.getConfigProvider().getBlacklistedWorlds().contains(shop_i.getLocation().getWorld())
                    && shop_i.getItem().getType().equals(item.getType())
                    && (toBuy ? shop_i.isSelling() : shop_i.isBuying())) {
                if(checkIfShopToBeIgnoredForFullOrEmpty(toBuy, shop_i)) {
                    continue;
                }
                // check for shop if hidden
                if(!HiddenShopStorageUtil.isShopHidden(shop_i)) {
                    shopsFoundList.add(new FoundShopItemModel(
                            shop_i.getPrice(),
                            QSApi.processStockOrSpace((toBuy ? getRemainingStockOrSpaceFromShopCache(shop_i, true) : getRemainingStockOrSpaceFromShopCache(shop_i, false))),
                            shop_i.getOwner(),
                            shop_i.getLocation(),
                            shop_i.getItem(),
                            toBuy
                    ));
                }
            }
        }
        List<FoundShopItemModel> sortedShops = handleShopSorting(toBuy, shopsFoundList);
        QSApi.logTimeTookMsg(begin);
        return sortedShops;
    }

    @Override
    public List<FoundShopItemModel> findItemBasedOnDisplayNameFromAllShops(String displayName, boolean toBuy, Player searchingPlayer) {
        var begin = Instant.now();
        List<FoundShopItemModel> shopsFoundList = new ArrayList<>();
        List<Shop> allShops;
        if(FindItemAddOn.getConfigProvider().SEARCH_LOADED_SHOPS_ONLY) {
            allShops = new ArrayList<>(api.getShopManager().getLoadedShops());
        }
        else {
            allShops = api.getShopManager().getAllShops();
        }
        logTotalShopsOnServer(allShops.size());
        for(Shop shop_i : allShops) {
            if(!FindItemAddOn.getConfigProvider().getBlacklistedWorlds().contains(shop_i.getLocation().getWorld())) {
                if(shop_i.getItem().hasItemMeta()) {
                    if(Objects.requireNonNull(shop_i.getItem().getItemMeta()).hasDisplayName()) {
                        if(shop_i.getItem().getItemMeta().getDisplayName().toLowerCase().contains(displayName.toLowerCase())
//                                && (toBuy ? getRemainingStockOrSpaceFromShopCache(shop_i, true) != 0 : getRemainingStockOrSpaceFromShopCache(shop_i, false) != 0)
                                && (toBuy ? shop_i.isSelling() : shop_i.isBuying())) {
                            if(checkIfShopToBeIgnoredForFullOrEmpty(toBuy, shop_i))
                                continue;
                            // check for shop if hidden
                            if(!HiddenShopStorageUtil.isShopHidden(shop_i)) {
                                shopsFoundList.add(new FoundShopItemModel(
                                        shop_i.getPrice(),
                                        QSApi.processStockOrSpace((toBuy ? getRemainingStockOrSpaceFromShopCache(shop_i, true) : getRemainingStockOrSpaceFromShopCache(shop_i, false))),
                                        shop_i.getOwner(),
                                        shop_i.getLocation(),
                                        shop_i.getItem(),
                                        toBuy
                                ));
                            }
                        }
                    }
                }
            }
        }
        List<FoundShopItemModel> sortedShops = handleShopSorting(toBuy, shopsFoundList);
        QSApi.logTimeTookMsg(begin);
        return sortedShops;
    }

    @Override
    public List<FoundShopItemModel> fetchAllItemsFromAllShops(boolean toBuy, Player searchingPlayer) {
        var begin = Instant.now();
        List<FoundShopItemModel> shopsFoundList = new ArrayList<>();
        List<Shop> allShops;
        if(FindItemAddOn.getConfigProvider().SEARCH_LOADED_SHOPS_ONLY) {
            allShops = new ArrayList<>(api.getShopManager().getLoadedShops());
        }
        else {
            allShops = api.getShopManager().getAllShops();
        }
        logTotalShopsOnServer(allShops.size());
        for(Shop shop_i : allShops) {
            // check for blacklisted worlds
            if(!FindItemAddOn.getConfigProvider().getBlacklistedWorlds().contains(shop_i.getLocation().getWorld())
//                    && (toBuy ? getRemainingStockOrSpaceFromShopCache(shop_i, true) != 0 : getRemainingStockOrSpaceFromShopCache(shop_i, false) != 0)
                    && (toBuy ? shop_i.isSelling() : shop_i.isBuying())) {
                if(checkIfShopToBeIgnoredForFullOrEmpty(toBuy, shop_i))
                    continue;
                // check for shop if hidden
                if(!HiddenShopStorageUtil.isShopHidden(shop_i)) {
                    shopsFoundList.add(new FoundShopItemModel(
                            shop_i.getPrice(),
                            QSApi.processStockOrSpace((toBuy ? getRemainingStockOrSpaceFromShopCache(shop_i, true) : getRemainingStockOrSpaceFromShopCache(shop_i, false))),
                            shop_i.getOwner(),
                            shop_i.getLocation(),
                            shop_i.getItem(),
                            toBuy
                    ));
                }
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

    @Override
    public Material getShopSignMaterial() {
        return org.maxgamer.quickshop.util.Util.getSignMaterial();
    }

    @Override
    public Shop findShopAtLocation(Block block) {
        Location loc = new Location(block.getWorld(), block.getX(), block.getY(), block.getZ());
        return api.getShopManager().getShop(loc);
    }

    @Override
    public boolean isShopOwnerCommandRunner(Player player, Shop shop) {
        LoggerUtils.logDebugInfo("Shop owner: " + shop.getOwner() + " | Player: " + player.getUniqueId());
        return shop.getOwner().toString().equalsIgnoreCase(player.getUniqueId().toString());
    }

    @Override
    public List<Shop> getAllShops() {
        return api.getShopManager().getAllShops();
    }

    public List<ShopSearchActivityModel> syncShopsListForStorage(List<ShopSearchActivityModel> globalShopsList) {

        // copy all shops from shops list in API to a temp globalShopsList
        // now check shops from temp globalShopsList in current globalShopsList and pull playerVisit data
        List<ShopSearchActivityModel> tempGlobalShopsList = new ArrayList<>();

        for(Shop shop_i : getAllShops()) {
            Location shopLoc = shop_i.getLocation();
            tempGlobalShopsList.add(new ShopSearchActivityModel(
                    shopLoc.getWorld().getName(),
                    shopLoc.getX(),
                    shopLoc.getY(),
                    shopLoc.getZ(),
                    shopLoc.getPitch(),
                    shopLoc.getYaw(),
                    shop_i.getOwner().toString(),
                    new ArrayList<>(),
                    false
            ));
        }
        for(ShopSearchActivityModel shop_temp : tempGlobalShopsList) {
            ShopSearchActivityModel tempShopToRemove = null;
            for(ShopSearchActivityModel shop_global : globalShopsList) {
                if(shop_temp.getWorldName().equalsIgnoreCase(shop_global.getWorldName())
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
            if(tempShopToRemove != null)
                globalShopsList.remove(tempShopToRemove);
        }
        return tempGlobalShopsList;
    }

    /**
     * Register finditem sub-command for /qs
     */
    @Override
    public void registerSubCommand() {
        LoggerUtils.logInfo("Unregistered find sub-command for /qs");
        for(CommandContainer cmdContainer : api.getCommandManager().getRegisteredCommands()) {
            if(cmdContainer.getPrefix().equalsIgnoreCase("find")) {
                api.getCommandManager().unregisterCmd(cmdContainer);
                break;
            }
        }
        LoggerUtils.logInfo("Registered finditem sub-command for /qs");
        api.getCommandManager().registerCmd(
                CommandContainer.builder()
                        .prefix("finditem")
                        .permission(PlayerPermsEnum.FINDITEM_USE.value())
                        .hidden(false)
                        .description("Search for items from all shops using an interactive GUI")
                        .executor(new FindItemCmdReremakeImpl())
                        .build());
    }

    @Override
    public UUID convertNameToUuid(String playerName) {
        return null;
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
     *
     * @param shop
     * @return True if shop to be ignored
     */
    /*private boolean checkIfShopToBeIgnoredIfAdminShop(Shop shop) {
        boolean ignoreAdminShops = FindItemAddOn.getConfigProvider().IGNORE_ADMIN_SHOPS;
        if(ignoreAdminShops) {
            OfflinePlayer shopOwner = Bukkit.getOfflinePlayer(shop.getOwner());
            return shopOwner.getName() == null;
        }
        return false;
    }*/

    /**
     * Fallback to fetching info from ShopCache to avoid lag
     * @param shop QuickShop Shop instance
     * @param fetchRemainingStock True if fetching remaning stock, False if fetching remaining space
     * @return
     */
    private int getRemainingStockOrSpaceFromShopCache(Shop shop, boolean fetchRemainingStock) {
        LoggerUtils.logDebugInfo("Shop Location: " + shop.getLocation());
        CachedShop cachedShop = shopCache.get(shop.getLocation());
        if (cachedShop == null || QSApi.isTimeDifferenceGreaterThanSeconds(cachedShop.getLastFetched(), new Date(), SHOP_CACHE_TIMEOUT_SECONDS)) {
            Shop possibleCachedShop = getRemainingStockOrSpaceFromQSCache(shop);
            if(possibleCachedShop != null) {
                LoggerUtils.logDebugInfo("Shop found from QS cache: " + shop.getLocation());
                cachedShop = CachedShop.builder()
                                .shopLocation(possibleCachedShop.getLocation())
                                .remainingStock(possibleCachedShop.getRemainingStock())
                                .remainingSpace(possibleCachedShop.getRemainingSpace())
                                .lastFetched(new Date())
                                .build();
                shopCache.put(cachedShop.getShopLocation(), cachedShop);
                LoggerUtils.logDebugInfo("Added to ShopCache: " + shop.getLocation());
            } else {
                LoggerUtils.logDebugInfo("No shop found from QS cache for location: " + shop.getLocation());
            }
        } else {
            LoggerUtils.logDebugInfo("Shop found from cache: " + shop.getLocation());
        }
        return (fetchRemainingStock ?
                (cachedShop != null ? cachedShop.getRemainingStock() : -1)
                : (cachedShop != null ? cachedShop.getRemainingSpace() : -1));
    }

    /**
     * Fallback to fetching info from ShopCache to avoid lag
     * @param shop QuickShop Shop instance
     * @return
     */
    private @Nullable Shop getRemainingStockOrSpaceFromQSCache(Shop shop) {
        return quickShop.getShopCache().find(shop.getLocation(), false);
    }



    private static void logTotalShopsOnServer(int allShopsCount) {
        LoggerUtils.logDebugInfo("Total shops on server: " + allShopsCount);
    }

    @Override
    public boolean isQSShopCacheImplemented() {
        return false;
    }

    @Override
    public int processUnknownStockSpace(Location shopLoc, boolean toBuy) {
        // This method will not be called from QS-Reremake context, but keeping it here as a placeholder
        return -2;
    }


}
