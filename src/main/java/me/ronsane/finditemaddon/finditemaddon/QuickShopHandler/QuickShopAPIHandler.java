package me.ronsane.finditemaddon.finditemaddon.QuickShopHandler;

import me.ronsane.finditemaddon.finditemaddon.FindItemAddOn;
import me.ronsane.finditemaddon.finditemaddon.Utils.LoggerUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.api.QuickShopAPI;
import org.maxgamer.quickshop.shop.Shop;

import java.util.*;

public class QuickShopAPIHandler {

    private final QuickShop qsPlugin;

    public QuickShopAPIHandler() {
        qsPlugin = QuickShop.getInstance();
    }

    public QuickShop getQsPluginInstance() {
        return qsPlugin;
    }

    public List<Shop> findItemBasedOnTypeFromAllShops(ItemStack item, boolean toBuy) {
        List<Shop> shopsFound = new ArrayList<>();
        assert QuickShopAPI.getShopAPI() != null;
        List<Shop> allShops;
        if(FindItemAddOn.getConfigProvider().SEARCH_LOADED_SHOPS_ONLY) {
            allShops = QuickShopAPI.getShopAPI().getLoadedShops();
        }
        else {
            allShops = QuickShopAPI.getShopAPI().getAllShops();
        }
        LoggerUtils.logDebugInfo("Total shops on server: " + allShops.size());
        allShops.forEach((shop_i) -> {
            if(shop_i.getItem().getType().equals(item.getType()) && shop_i.getRemainingStock() > 0
                    && (toBuy ? shop_i.isSelling() : shop_i.isBuying())) {
                shopsFound.add(shop_i);
            }
        });
        if(!shopsFound.isEmpty()) {
            int sortingMethod = 2;
            try {
                sortingMethod = FindItemAddOn.getConfigProvider().SHOP_SORTING_METHOD;
            }
            catch(Exception e) {
                LoggerUtils.logError("Invalid value in config.yml : 'shop-sorting-method'");
                LoggerUtils.logError("Defaulting to sorting by prices method");
            }
            return sortShops(sortingMethod, shopsFound);
        }
        return shopsFound;
    }

//    public static List<Shop> findItemBasedOnTypeFromAllShops(ItemStack item, boolean toBuy) {
//        List<Shop> shopsFound = new ArrayList<>();
//        assert QuickShopAPI.getShopAPI() != null;
//        List<Shop> allShops = QuickShopAPI.getShopAPI().getAllShops();
//        allShops.forEach((shop_i) -> {
//            if(shop_i.getItem().getType().equals(item.getType()) && shop_i.getRemainingStock() > 0
//                    && (toBuy ? shop_i.isSelling() : shop_i.isBuying())) {
//                shopsFound.add(shop_i);
//            }
//        });
//        if(!shopsFound.isEmpty()) {
//            int sortingMethod = 2;
//            try {
//                sortingMethod = FindItemAddOn.getConfigProvider().SHOP_SORTING_METHOD;
//            }
//            catch(Exception e) {
//                LoggerUtils.logError("Invalid value in config.yml : 'shop-sorting-method'");
//                LoggerUtils.logError("Defaulting to sorting by prices method");
//            }
//            return sortShops(sortingMethod, shopsFound);
//        }
//        return shopsFound;
//    }

    public List<Shop> findItemBasedOnDisplayNameFromAllShops(String displayName, boolean toBuy) {
        List<Shop> shopsFound = new ArrayList<>();
        assert QuickShopAPI.getShopAPI() != null;
        List<Shop> allShops;
        if(FindItemAddOn.getConfigProvider().SEARCH_LOADED_SHOPS_ONLY) {
            allShops = QuickShopAPI.getShopAPI().getLoadedShops();
        }
        else {
            allShops = QuickShopAPI.getShopAPI().getAllShops();
        }
        LoggerUtils.logDebugInfo("Total shops on server: " + allShops.size());
        for(Shop shop_i : allShops) {
            if(shop_i.getItem().hasItemMeta()) {
                if(Objects.requireNonNull(shop_i.getItem().getItemMeta()).hasDisplayName()) {
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
                sortingMethod = FindItemAddOn.getConfigProvider().SHOP_SORTING_METHOD;
            }
            catch(Exception e) {
                LoggerUtils.logError("Invalid value in config.yml : 'shop-sorting-method'");
                LoggerUtils.logError("Defaulting to sorting by prices method");
            }
            return sortShops(sortingMethod, shopsFound);
        }
        return shopsFound;
    }

//    public static List<Shop> findItemBasedOnDisplayNameFromAllShops(String displayName, boolean toBuy) {
//        List<Shop> shopsFound = new ArrayList<>();
//        assert QuickShopAPI.getShopAPI() != null;
//        List<Shop> allShops = QuickShopAPI.getShopAPI().getAllShops();
//        for(Shop shop_i : allShops) {
//            if(shop_i.getItem().hasItemMeta()) {
//                if(Objects.requireNonNull(shop_i.getItem().getItemMeta()).hasDisplayName()) {
//                    if(shop_i.getItem().getItemMeta().getDisplayName().toLowerCase().contains(displayName.toLowerCase())
//                            && shop_i.getRemainingStock() > 0
//                            && (toBuy ? shop_i.isSelling() : shop_i.isBuying())) {
//                        shopsFound.add(shop_i);
//                    }
//                }
//            }
//        }
//        if(!shopsFound.isEmpty()) {
//            int sortingMethod = 2;
//            try {
//                sortingMethod = FindItemAddOn.getConfigProvider().SHOP_SORTING_METHOD;
//            }
//            catch(Exception e) {
//                LoggerUtils.logError("Invalid value in config.yml : 'shop-sorting-method'");
//                LoggerUtils.logError("Defaulting to sorting by prices method");
//            }
//            return sortShops(sortingMethod, shopsFound);
//        }
//        return shopsFound;
//    }

    private List<Shop> sortShops(int sortingMethod, List<Shop> shopsFound) {
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

    public Material getShopSignMaterial() {
        return Material.getMaterial(
                Objects.requireNonNull(
                        QuickShop.getInstance().getConfig().getString("shop.sign-material")));
    }
}
