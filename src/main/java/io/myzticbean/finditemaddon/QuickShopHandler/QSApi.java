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

import io.myzticbean.finditemaddon.Models.FoundShopItemModel;
import io.myzticbean.finditemaddon.Models.ShopSearchActivityModel;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface QSApi<QSType, Shop> {

    String QS_REMAINING_STOCK_OR_SPACE = "Remaining Stock/Space: ";

    List<FoundShopItemModel> findItemBasedOnTypeFromAllShops(ItemStack item, boolean toBuy, Player searchingPlayer);

    List<FoundShopItemModel> findItemBasedOnDisplayNameFromAllShops(String displayName, boolean toBuy, Player searchingPlayer);

    List<FoundShopItemModel> fetchAllItemsFromAllShops(boolean toBuy, Player searchingPlayer);

    Material getShopSignMaterial();

    Shop findShopAtLocation(Block block);

    boolean isShopOwnerCommandRunner(Player player, Shop shop);

    List<Shop> getAllShops();

    List<ShopSearchActivityModel> syncShopsListForStorage(List<ShopSearchActivityModel> globalShopsList);

    UUID convertNameToUuid(String playerName);

    int processUnknownStockSpace(Location shopLoc, boolean toBuy);

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
        if(stockOrSpace == -1)
            return Integer.MAX_VALUE;
        return stockOrSpace;
    }

    static boolean isTimeDifferenceGreaterThanSeconds(Date date1, Date date2, int seconds) {
        Instant instant1 = date1.toInstant();
        Instant instant2 = date2.toInstant();

        Duration duration = Duration.between(instant1, instant2);
        long secondsDifference = Math.abs(duration.getSeconds());

        return secondsDifference >= seconds;
    }

}
