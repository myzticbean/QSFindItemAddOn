package me.ronsane.finditemaddon.finditemaddon.QuickShopHandler;

import org.bukkit.inventory.ItemStack;
import org.maxgamer.quickshop.api.QuickShopAPI;
import org.maxgamer.quickshop.shop.Shop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class QuickShopAPIHandler {
    public static List<Shop> findItemFromAllShops(ItemStack item, boolean toBuy) {
        List<Shop> shopsFound = new ArrayList<>();
        List<Shop> allShops = QuickShopAPI.getShopAPI().getAllShops();
        for(Shop shop_i : allShops) {
            if(shop_i.getItem().equals(item) && shop_i.getRemainingStock() > 0
                    && (toBuy ? shop_i.isSelling() : shop_i.isBuying())) {
                shopsFound.add(shop_i);
            }
        }
        shopsFound.sort(Comparator.comparing(Shop::getPrice));
        return shopsFound;
    }
}
