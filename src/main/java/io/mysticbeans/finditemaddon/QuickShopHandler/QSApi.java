package io.mysticbeans.finditemaddon.QuickShopHandler;

import io.mysticbeans.finditemaddon.Models.FoundShopItemModel;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface QSApi<T, V> {
    T getQsPluginInstance();
    List<FoundShopItemModel> findItemBasedOnTypeFromAllShops(ItemStack item, boolean toBuy);
    List<FoundShopItemModel> findItemBasedOnDisplayNameFromAllShops(String displayName, boolean toBuy);
    List<FoundShopItemModel> fetchAllItemsFromAllShops(boolean toBuy);
    Material getShopSignMaterial();
    V findShopAtLocation(Block block);
    boolean isShopOwnerCommandRunner(Player player, V shop);
    List<V> getAllShops();
}
