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
package io.myzticbean.finditemaddon.Dependencies;

import com.ghostchu.quickshop.api.QuickShopAPI;
import com.ghostchu.quickshop.api.shop.permission.BuiltInShopPermission;
import com.ghostchu.quickshop.util.Util;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.Models.FoundShopItemModel;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class QuickShopApi {

    private final QuickShopAPI api;
    private final Cache<ItemStack, List<FoundShopItemModel>> searchedItemStacks = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();
    private final Cache<String, List<FoundShopItemModel>> searchedStrings = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    public QuickShopApi() {
        api = QuickShopAPI.getInstance();
    }

    public List<FoundShopItemModel> findItemBasedOnTypeFromAllShops(ItemStack item, boolean toBuy, Player searchingPlayer) {
        if (searchedItemStacks.getIfPresent(item) != null) {
            return searchedItemStacks.getIfPresent(item);
        }

        List<FoundShopItemModel> shopsFoundList = new ArrayList<>();
        List<com.ghostchu.quickshop.api.shop.Shop> allShops = fetchAllShopsFromQS();
        for (com.ghostchu.quickshop.api.shop.Shop shopIterator : allShops) {
            if (shopIterator.playerAuthorize(searchingPlayer.getUniqueId(), BuiltInShopPermission.SEARCH)
                    && (!FindItemAddOn.getConfigProvider().getBlacklistedWorlds().contains(shopIterator.getLocation().getWorld())
                    && shopIterator.getItem().getType().equals(item.getType())
                    && (toBuy ? shopIterator.isSelling() : shopIterator.isBuying()))
            ) {
                int stockOrSpace = (toBuy ? getRemainingStockOrSpaceFromShopCache(shopIterator, true) : getRemainingStockOrSpaceFromShopCache(shopIterator, false));
                if (checkIfShopToBeIgnoredForFullOrEmpty(stockOrSpace)) {
                    continue;
                }

                // ensure shop owner has enough balance to sell the item (if selling)
                OfflinePlayer shopOwner = Bukkit.getOfflinePlayer(shopIterator.getOwner().getUniqueId());
                if (!toBuy && !FindItemAddOn.getInstance().getEconomy().has(shopOwner, shopIterator.getPrice())) {
                    continue;
                }

                shopsFoundList.add(new FoundShopItemModel(
                        shopIterator.getPrice(),
                        QuickShopApi.processStockOrSpace(stockOrSpace),
                        shopIterator.getOwner().getUniqueIdOptional().orElse(new UUID(0, 0)),
                        shopIterator.getLocation(),
                        shopIterator.getItem(),
                        toBuy
                ));
            }
        }

        this.searchedStrings.put(item.getType().name(), shopsFoundList);
        return handleShopSorting(toBuy, shopsFoundList);
    }

    public List<FoundShopItemModel> findItemBasedOnDisplayNameFromAllShops(String matcher, boolean toBuy, Player searchingPlayer) {
        if (searchedStrings.getIfPresent(matcher) != null) {
            return searchedStrings.getIfPresent(matcher);
        }

        List<FoundShopItemModel> shopsFoundList = new ArrayList<>();
        List<com.ghostchu.quickshop.api.shop.Shop> allShops = fetchAllShopsFromQS();
        for (com.ghostchu.quickshop.api.shop.Shop shopIterator : allShops) {
            if (shopIterator.playerAuthorize(searchingPlayer.getUniqueId(), BuiltInShopPermission.SEARCH)
                    && !FindItemAddOn.getConfigProvider().getBlacklistedWorlds().contains(shopIterator.getLocation().getWorld())
                    && PlainTextComponentSerializer.plainText().serialize(shopIterator.getItem().displayName()).toLowerCase().contains(matcher.replace("_", " ").toLowerCase())
                    && (toBuy ? shopIterator.isSelling() : shopIterator.isBuying())
            ) {
                int stockOrSpace = (toBuy ? getRemainingStockOrSpaceFromShopCache(shopIterator, true) : getRemainingStockOrSpaceFromShopCache(shopIterator, false));
                if (checkIfShopToBeIgnoredForFullOrEmpty(stockOrSpace)) {
                    continue;
                }

                // ensure shop owner has enough balance to sell the item (if selling)
                OfflinePlayer shopOwner = Bukkit.getOfflinePlayer(shopIterator.getOwner().getUniqueId());
                if (!toBuy && !FindItemAddOn.getInstance().getEconomy().has(shopOwner, shopIterator.getPrice())) {
                    continue;
                }

                shopsFoundList.add(new FoundShopItemModel(
                        shopIterator.getPrice(),
                        QuickShopApi.processStockOrSpace(stockOrSpace),
                        shopIterator.getOwner().getUniqueIdOptional().orElse(new UUID(0, 0)),
                        shopIterator.getLocation(),
                        shopIterator.getItem(),
                        toBuy
                ));
            }
        }

        this.searchedStrings.put(matcher, shopsFoundList);
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
            return QuickShopApi.sortShops(sortingMethod, shopsFoundList, toBuy);
        }
        return shopsFoundList;
    }

    private List<com.ghostchu.quickshop.api.shop.Shop> fetchAllShopsFromQS() {
        List<com.ghostchu.quickshop.api.shop.Shop> allShops;
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

    public com.ghostchu.quickshop.api.shop.Shop findShopAtLocation(Block block) {
        Location loc = new Location(block.getWorld(), block.getX(), block.getY(), block.getZ());
        return api.getShopManager().getShop(loc);
    }

    public boolean isShopOwnerCommandRunner(Player player, com.ghostchu.quickshop.api.shop.Shop shop) {
        return shop.getOwner().getUniqueId() == player.getUniqueId();
    }

    public List<com.ghostchu.quickshop.api.shop.Shop> getAllShops() {
        return api.getShopManager().getAllShops();
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

    private int getRemainingStockOrSpaceFromShopCache(com.ghostchu.quickshop.api.shop.Shop shop, boolean fetchRemainingStock) {
        Util.ensureThread(true);
        return (fetchRemainingStock ? shop.getRemainingStock() : shop.getRemainingSpace());
    }

    static List<FoundShopItemModel> sortShops(int sortingMethod, List<FoundShopItemModel> shopsFoundList, boolean toBuy) {
        switch (sortingMethod) {
            // Random
            case 1 -> Collections.shuffle(shopsFoundList);
            // Based on prices (lower to higher)
            case 2 -> shopsFoundList.sort(Comparator.comparing(FoundShopItemModel::shopPrice));
            // Based on stocks (higher to lower)
            case 3 -> {
                shopsFoundList.sort(Comparator.comparing(FoundShopItemModel::remainingStockOrSpace));
                Collections.reverse(shopsFoundList);
            }
            default -> {
                shopsFoundList.sort(Comparator.comparing(FoundShopItemModel::shopPrice));
            }
        }
        return shopsFoundList;
    }

    static int processStockOrSpace(int stockOrSpace) {
        if (stockOrSpace == -1)
            return Integer.MAX_VALUE;
        return stockOrSpace;
    }

}
