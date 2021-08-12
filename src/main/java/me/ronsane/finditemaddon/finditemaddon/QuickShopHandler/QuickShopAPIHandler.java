package me.ronsane.finditemaddon.finditemaddon.QuickShopHandler;

import me.ronsane.finditemaddon.finditemaddon.ConfigHandler.ConfigHandler;
import me.ronsane.finditemaddon.finditemaddon.FindItemAddOn;
import me.ronsane.finditemaddon.finditemaddon.Utils.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;
import org.maxgamer.quickshop.api.QuickShopAPI;
import org.maxgamer.quickshop.shop.Shop;

import java.util.ArrayList;
import java.util.Collections;
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
        if(!shopsFound.isEmpty()) {
            int sortingMethod = 2;
            try {
                sortingMethod = FindItemAddOn.configProvider.SHOP_SORTING_METHOD;
            }
            catch(Exception e) {
                LoggerUtils.logError("Invalid value in config.yml : 'shop-sorting-method'");
                LoggerUtils.logError("Defaulting to sorting by prices method");
            }
            shopsFound = sortShops(sortingMethod, shopsFound);
        }
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
        if(!shopsFound.isEmpty()) {
            int sortingMethod = 2;
            try {
                sortingMethod = FindItemAddOn.configProvider.SHOP_SORTING_METHOD;
            }
            catch(Exception e) {
                LoggerUtils.logError("Invalid value in config.yml : 'shop-sorting-method'");
                LoggerUtils.logError("Defaulting to sorting by prices method");
            }
            shopsFound = sortShops(sortingMethod, shopsFound);
        }
        return shopsFound;
    }

    private static List<Shop> sortShops(int sortingMethod, List<Shop> shopsFound) {
        switch (sortingMethod) {
            // Random
            case 1 -> Collections.shuffle(shopsFound);
            // Based on prices (lower to higher)
            case 2 -> shopsFound.sort(Comparator.comparing(Shop::getPrice));
            // Based on stocks (higher to lower)
            case 3 -> {
                shopsFound.sort(Comparator.comparing(Shop::getRemainingStock));
                Collections.reverse(shopsFound);
            }
            default -> {
                LoggerUtils.logError("Invalid value in config.yml : 'shop-sorting-method'");
                LoggerUtils.logError("Defaulting to sorting by prices method");
                shopsFound.sort(Comparator.comparing(Shop::getPrice));
            }
        }
        return shopsFound;
    }
}
