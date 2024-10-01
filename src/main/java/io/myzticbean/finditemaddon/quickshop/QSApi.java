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
package io.myzticbean.finditemaddon.quickshop;

import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.models.FoundShopItemModel;
import io.myzticbean.finditemaddon.models.ShopSearchActivityModel;
import io.myzticbean.finditemaddon.utils.log.Logger;
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

/**
 * Interface for QS API.
 * Implement it depending on which API is being used (Reremake/Hikari).
 * @param <QSType>
 * @param <Shop>
 * @author myzticbean
 */
public interface QSApi<QSType, Shop> {

    String QS_TOTAL_SHOPS_ON_SERVER = "Total shops on server: ";
    String QS_REMAINING_STOCK_OR_SPACE = "Remaining Stock/Space: ";

    /**
     * Search based on Item Type from all server shops
     * @param item
     * @param toBuy
     * @param searchingPlayer
     * @return
     */
    List<FoundShopItemModel> findItemBasedOnTypeFromAllShops(ItemStack item, boolean toBuy, Player searchingPlayer);

    /**
     * Search based on display name of item from all server shops
     * @param displayName
     * @param toBuy
     * @param searchingPlayer
     * @return
     */
    List<FoundShopItemModel> findItemBasedOnDisplayNameFromAllShops(String displayName, boolean toBuy, Player searchingPlayer);

    /**
     * Fetch all items from all server shops
     * @param toBuy
     * @param searchingPlayer
     * @return
     */
    List<FoundShopItemModel> fetchAllItemsFromAllShops(boolean toBuy, Player searchingPlayer);

    Material getShopSignMaterial();

    Shop findShopAtLocation(Block block);

    boolean isShopOwnerCommandRunner(Player player, Shop shop);

    List<Shop> getAllShops();

    List<ShopSearchActivityModel> syncShopsListForStorage(List<ShopSearchActivityModel> globalShopsList);

    void registerSubCommand();
    UUID convertNameToUuid(String playerName);

    boolean isQSShopCacheImplemented();

    int processUnknownStockSpace(Location shopLoc, boolean toBuy);

    static List<FoundShopItemModel> sortShops(int sortingMethod, List<FoundShopItemModel> shopsFoundList, boolean toBuy) {
        switch (sortingMethod) {
            // Random
            case 1 -> Collections.shuffle(shopsFoundList);
            // Based on prices (lower to higher)
            case 2 -> shopsFoundList.sort(Comparator.comparing(FoundShopItemModel::getShopPrice));
            // Based on stocks (higher to lower)
            case 3 -> {
                shopsFoundList.sort(Comparator.comparing(FoundShopItemModel::getRemainingStockOrSpace));
                Collections.reverse(shopsFoundList);
            }
            default -> {
                Logger.logError("Invalid value in config.yml : 'shop-sorting-method'");
                Logger.logError("Defaulting to sorting by prices method");
                shopsFoundList.sort(Comparator.comparing(FoundShopItemModel::getShopPrice));
            }
        }
        if(FindItemAddOn.getConfigProvider().DEBUG_MODE)
            shopsFoundList.forEach(foundShopItem ->
                    Logger.logDebugInfo(QS_REMAINING_STOCK_OR_SPACE + foundShopItem.getRemainingStockOrSpace()));
        return shopsFoundList;
    }

    static int processStockOrSpace(int stockOrSpace) {
        if(stockOrSpace == -1)
            return Integer.MAX_VALUE;
        return stockOrSpace;
    }

    /**
     * Function to check if the time difference between two dates is greater than or equal to the specified seconds
     * @param date1
     * @param date2
     * @param seconds
     * @return
     */
    static boolean isTimeDifferenceGreaterThanSeconds(Date date1, Date date2, int seconds) {
        Instant instant1 = date1.toInstant();
        Instant instant2 = date2.toInstant();

        Duration duration = Duration.between(instant1, instant2);
        long secondsDifference = Math.abs(duration.getSeconds());

        Logger.logDebugInfo("Difference: " + secondsDifference);

        return secondsDifference >= seconds;
    }

    static void logTimeTookMsg(Instant timeStart) {
        Logger.logInfo("Shop search took " + Duration.between(timeStart, Instant.now()).toMillis() + "ms");
    }
}
