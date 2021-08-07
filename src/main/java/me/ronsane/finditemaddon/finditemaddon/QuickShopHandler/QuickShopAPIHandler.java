package me.ronsane.finditemaddon.finditemaddon.QuickShopHandler;

import org.bukkit.inventory.ItemStack;
import org.maxgamer.quickshop.api.QuickShopAPI;
import org.maxgamer.quickshop.shop.Shop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class QuickShopAPIHandler {
    public static List<Shop> findItemBasedOnTypeFromAllShops(ItemStack item, boolean toBuy) {
        List<Shop> shopsFound = new ArrayList<>();
        List<Shop> allShops = QuickShopAPI.getShopAPI().getAllShops();
        for(Shop shop_i : allShops) {
            if(shop_i.getItem().getType().equals(item.getType()) && shop_i.getRemainingStock() > 0
                    && (toBuy ? shop_i.isSelling() : shop_i.isBuying())) {
                shopsFound.add(shop_i);
            }
        }
        shopsFound.sort(Comparator.comparing(Shop::getPrice));
        return shopsFound;
    }

    public static List<Shop> findItemBasedOnDisplayNameFromAllShops(String displayName, boolean toBuy) {
        List<Shop> shopsFound = new ArrayList<>();
        List<Shop> allShops = QuickShopAPI.getShopAPI().getAllShops();
        for(Shop shop_i : allShops) {
            if(shop_i.getItem().hasItemMeta()) {
                if(shop_i.getItem().getItemMeta().hasDisplayName()) {
                    if(shop_i.getItem().getItemMeta().getDisplayName().toLowerCase().contains(displayName.toLowerCase())
                            && shop_i.getRemainingStock() > 0
                            && (toBuy ? shop_i.isSelling() : shop_i.isBuying())) {
                        shopsFound.add(shop_i);
                    }
                }
            }
        }
        shopsFound.sort(Comparator.comparing(Shop::getPrice));
        return shopsFound;
    }
}
