package me.ronsane.finditemaddon.finditemaddon.QuickShopHandler;

import me.ronsane.finditemaddon.finditemaddon.GUIHandler.FoundShopsGUI;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.maxgamer.quickshop.shop.Shop;

import java.util.List;

public class SearchHandler {

    public static Inventory searchForShops(Material material, boolean isBuying) {
        List<Shop> foundShops = QuickShopAPIHandler.findItemFromAllShops(new ItemStack(material), isBuying);

        if(foundShops.isEmpty()) {
            return null;
        }
        if(foundShops.size() > 54) {
            return FoundShopsGUI.createGUI(foundShops.subList(0,54));
        }
        else {
            return FoundShopsGUI.createGUI(foundShops);
        }
    }

}
