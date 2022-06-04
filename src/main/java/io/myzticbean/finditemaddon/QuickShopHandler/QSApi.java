package io.myzticbean.finditemaddon.QuickShopHandler;

import io.myzticbean.finditemaddon.Models.FoundShopItemModel;
import io.myzticbean.finditemaddon.Models.ShopSearchActivityModel;
import io.myzticbean.finditemaddon.Utils.LoggerUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public interface QSApi<T, V> {
    List<FoundShopItemModel> findItemBasedOnTypeFromAllShops(ItemStack item, boolean toBuy, Player searchingPlayer);
    List<FoundShopItemModel> findItemBasedOnDisplayNameFromAllShops(String displayName, boolean toBuy, Player searchingPlayer);
    List<FoundShopItemModel> fetchAllItemsFromAllShops(boolean toBuy, Player searchingPlayer);
    Material getShopSignMaterial();
    V findShopAtLocation(Block block);
    boolean isShopOwnerCommandRunner(Player player, V shop);
    List<V> getAllShops();
    List<ShopSearchActivityModel> syncShopsListForStorage(List<ShopSearchActivityModel> globalShopsList);
    void registerSubCommand();

    static List<FoundShopItemModel> sortShops(int sortingMethod, List<FoundShopItemModel> shopsFoundList) {
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
                LoggerUtils.logError("Invalid value in config.yml : 'shop-sorting-method'");
                LoggerUtils.logError("Defaulting to sorting by prices method");
                shopsFoundList.sort(Comparator.comparing(FoundShopItemModel::getShopPrice));
            }
        }
        return shopsFoundList;
    }
}
