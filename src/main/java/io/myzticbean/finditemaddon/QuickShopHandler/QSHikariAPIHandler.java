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
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package io.myzticbean.finditemaddon.QuickShopHandler;

import com.ghostchu.quickshop.QuickShop;
import com.ghostchu.quickshop.api.QuickShopAPI;
import com.ghostchu.quickshop.api.obj.QUser;
import com.ghostchu.quickshop.api.shop.Shop;
import com.ghostchu.quickshop.api.shop.permission.BuiltInShopPermission;
import com.ghostchu.quickshop.util.Util;
import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.Models.FoundShopItemModel;
import io.myzticbean.finditemaddon.Models.ShopSearchActivityModel;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class QSHikariAPIHandler implements QSApi<QuickShop, Shop> {

    private final QuickShopAPI api;

    public QSHikariAPIHandler() {
        api = QuickShopAPI.getInstance();
    }

    public List<FoundShopItemModel> findItemBasedOnTypeFromAllShops(ItemStack item, boolean toBuy, Player searchingPlayer) {
        List<FoundShopItemModel> shopsFoundList = new ArrayList<>();
        List<Shop> allShops = fetchAllShopsFromQS();
        for (Shop shopIterator : allShops) {
//            LoggerUtils.logDebugInfo("1: ToBuy: " + toBuy + " Location: " + shopIterator.getLocation() + " | Stock: " + getRemainingStockOrSpaceFromShopCache(shopIterator, true) + " | Space: " + getRemainingStockOrSpaceFromShopCache(shopIterator, false));
//            LoggerUtils.logDebugInfo("2: ToBuy: " + toBuy + " Location: " + shopIterator.getLocation() + " | Stock: " + shopIterator.getRemainingStock() + " | Space: " + shopIterator.getRemainingSpace());
            // check for quickshop hikari internal per-shop based search permission
            if (shopIterator.playerAuthorize(searchingPlayer.getUniqueId(), BuiltInShopPermission.SEARCH)
                    // check for blacklisted worlds
                    && (!FindItemAddOn.getConfigProvider().getBlacklistedWorlds().contains(shopIterator.getLocation().getWorld())
                    && shopIterator.getItem().getType().equals(item.getType())
                    && (toBuy ? shopIterator.isSelling() : shopIterator.isBuying()))) {
                int stockOrSpace = (toBuy ? getRemainingStockOrSpaceFromShopCache(shopIterator, true) : getRemainingStockOrSpaceFromShopCache(shopIterator, false));
                if (checkIfShopToBeIgnoredForFullOrEmpty(stockOrSpace)) {
                    continue;
                }
                shopsFoundList.add(new FoundShopItemModel(
                        shopIterator.getPrice(),
                        QSApi.processStockOrSpace(stockOrSpace),
                        shopIterator.getOwner().getUniqueIdOptional().orElse(new UUID(0, 0)),
                        shopIterator.getLocation(),
                        shopIterator.getItem(),
                        toBuy
                ));
            }
        }
        return handleShopSorting(toBuy, shopsFoundList);
    }

    @NotNull
    static List<FoundShopItemModel> handleShopSorting(boolean toBuy, List<FoundShopItemModel> shopsFoundList) {
        if (!shopsFoundList.isEmpty()) {
            int sortingMethod = 2;
            try {
                sortingMethod = FindItemAddOn.getConfigProvider().SHOP_SORTING_METHOD;
            } catch (Exception ignored) {
            }
            return QSApi.sortShops(sortingMethod, shopsFoundList, toBuy);
        }
        return shopsFoundList;
    }

    public List<FoundShopItemModel> findItemBasedOnDisplayNameFromAllShops(String matcher, boolean toBuy, Player searchingPlayer) {
        List<FoundShopItemModel> shopsFoundList = new ArrayList<>();
        List<Shop> allShops = fetchAllShopsFromQS();
        for (Shop shopIterator : allShops) {
            // check for quickshop hikari internal per-shop based search permission
            if (shopIterator.playerAuthorize(searchingPlayer.getUniqueId(), BuiltInShopPermission.SEARCH)
                    // check for blacklisted worlds
                    && !FindItemAddOn.getConfigProvider().getBlacklistedWorlds().contains(shopIterator.getLocation().getWorld())
                    // match the item based on a query
                    && PlainTextComponentSerializer.plainText().serialize(shopIterator.getItem().displayName()).toLowerCase().contains(matcher.replace("_", " ").toLowerCase())
                    && (toBuy ? shopIterator.isSelling() : shopIterator.isBuying())) {
                int stockOrSpace = (toBuy ? getRemainingStockOrSpaceFromShopCache(shopIterator, true) : getRemainingStockOrSpaceFromShopCache(shopIterator, false));
                if (checkIfShopToBeIgnoredForFullOrEmpty(stockOrSpace)) {
                    continue;
                }
                shopsFoundList.add(new FoundShopItemModel(
                        shopIterator.getPrice(),
                        QSApi.processStockOrSpace(stockOrSpace),
                        shopIterator.getOwner().getUniqueIdOptional().orElse(new UUID(0, 0)),
                        shopIterator.getLocation(),
                        shopIterator.getItem(),
                        toBuy
                ));
            }
        }
        return handleShopSorting(toBuy, shopsFoundList);
    }

    public List<FoundShopItemModel> fetchAllItemsFromAllShops(boolean toBuy, Player searchingPlayer) {
        List<FoundShopItemModel> shopsFoundList = new ArrayList<>();
        List<Shop> allShops = fetchAllShopsFromQS();
        for (Shop shopIterator : allShops) {
            // check for quickshop hikari internal per-shop based search permission
            if (shopIterator.playerAuthorize(searchingPlayer.getUniqueId(), BuiltInShopPermission.SEARCH)
                    // check for blacklisted worlds
                    && (!FindItemAddOn.getConfigProvider().getBlacklistedWorlds().contains(shopIterator.getLocation().getWorld())
                    && (toBuy ? shopIterator.isSelling() : shopIterator.isBuying()))) {
                int stockOrSpace = (toBuy ? getRemainingStockOrSpaceFromShopCache(shopIterator, true) : getRemainingStockOrSpaceFromShopCache(shopIterator, false));
                if (checkIfShopToBeIgnoredForFullOrEmpty(stockOrSpace)) {
                    continue;
                }
                shopsFoundList.add(new FoundShopItemModel(
                        shopIterator.getPrice(),
                        QSApi.processStockOrSpace(stockOrSpace),
                        shopIterator.getOwner().getUniqueIdOptional().orElse(new UUID(0, 0)),
                        shopIterator.getLocation(),
                        shopIterator.getItem(),
                        toBuy
                ));
            }
        }
        List<FoundShopItemModel> sortedShops = new ArrayList<>(shopsFoundList);
        if (!shopsFoundList.isEmpty()) {
            int sortingMethod = 1;
            sortedShops = QSApi.sortShops(sortingMethod, shopsFoundList, toBuy);
        }
        return sortedShops;
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
        Location loc = new Location(block.getWorld(), block.getX(), block.getY(), block.getZ());
        return api.getShopManager().getShop(loc);
    }

    public boolean isShopOwnerCommandRunner(Player player, Shop shop) {
        return shop.getOwner().getUniqueId() == player.getUniqueId();
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
     *
     * @return If shop needs to be ignored from list
     */
    private boolean checkIfShopToBeIgnoredForFullOrEmpty(int stockOrSpace) {
        boolean ignoreEmptyChests = FindItemAddOn.getConfigProvider().IGNORE_EMPTY_CHESTS;
        if (ignoreEmptyChests) {
            return stockOrSpace == 0;
        }
        return false;
    }

    private int getRemainingStockOrSpaceFromShopCache(Shop shop, boolean fetchRemainingStock) {
//        LoggerUtils.logDebugInfo("Shop ID: " + shop.getShopId());
        Util.ensureThread(true);
        return (fetchRemainingStock ? shop.getRemainingStock() : shop.getRemainingSpace());
    }

    @Override
    public int processUnknownStockSpace(Location shopLoc, boolean toBuy) {
        // This process needs to run in MAIN thread
        Util.ensureThread(false);
        Shop qsShop = api.getShopManager().getShop(shopLoc);
        if (qsShop != null) {
            return (toBuy ? qsShop.getRemainingStock() : qsShop.getRemainingSpace());
        } else {
            return -2;
        }
    }
}
