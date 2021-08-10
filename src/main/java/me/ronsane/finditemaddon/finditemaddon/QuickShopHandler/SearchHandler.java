package me.ronsane.finditemaddon.finditemaddon.QuickShopHandler;

import me.ronsane.finditemaddon.finditemaddon.GUIHandler.FoundShopsGUI;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.maxgamer.quickshop.shop.Shop;

import java.util.List;

public class SearchHandler {

    public static Inventory searchForShops(Material material, boolean isBuying) {
        List<Shop> foundShops = QuickShopAPIHandler.findItemBasedOnTypeFromAllShops(new ItemStack(material), isBuying);

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

    public static Inventory searchForShops(String displayName, boolean isBuying) {
        List<Shop> foundShops = QuickShopAPIHandler.findItemBasedOnDisplayNameFromAllShops(displayName, isBuying);

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
