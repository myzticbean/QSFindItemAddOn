package me.ronsane.finditemaddon.finditemaddon.QuickShopHandler;

import me.ronsane.finditemaddon.finditemaddon.FindItemAddOn;
import me.ronsane.finditemaddon.finditemaddon.Models.FoundShopItemModel;
import me.ronsane.finditemaddon.finditemaddon.Utils.HiddenShopStorageUtil;
import me.ronsane.finditemaddon.finditemaddon.Utils.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.api.QuickShopAPI;
import org.maxgamer.quickshop.api.shop.Shop;

import java.util.*;

public class QuickShopAPIHandler {

    private final QuickShop qsPlugin;

    private QuickShopAPI api;

    public QuickShopAPIHandler() {
        qsPlugin = QuickShop.getInstance();
        api = (QuickShopAPI) Bukkit.getPluginManager().getPlugin("QuickShop");
    }

    public QuickShop getQsPluginInstance() {
        return qsPlugin;
    }

    public List<FoundShopItemModel> findItemBasedOnTypeFromAllShops(ItemStack item, boolean toBuy) {
//        List<Shop> shopsFound = new ArrayList<>();
        List<FoundShopItemModel> shopsFoundList = new ArrayList<>();
        List<Shop> allShops;
        if(FindItemAddOn.getConfigProvider().SEARCH_LOADED_SHOPS_ONLY) {
            allShops = new ArrayList<>(api.getShopManager().getLoadedShops());
        }
        else {
            allShops = api.getShopManager().getAllShops();
        }
        LoggerUtils.logDebugInfo("Total shops on server: " + allShops.size());
        allShops.forEach((shop_i) -> {
            // check for blacklisted worlds
            if(!FindItemAddOn.getConfigProvider().getBlacklistedWorlds().contains(shop_i.getLocation().getWorld())
                    && shop_i.getItem().getType().equals(item.getType())
                    && shop_i.getRemainingStock() > 0
                    && (toBuy ? shop_i.isSelling() : shop_i.isBuying())) {
                // check for shop if hidden
                if(!HiddenShopStorageUtil.isShopHidden(shop_i)) {
//                    shopsFound.add(shop_i);
                    shopsFoundList.add(new FoundShopItemModel(
                            shop_i.getPrice(),
                            shop_i.getRemainingStock(),
                            shop_i.getOwner(),
                            shop_i.getLocation(),
                            shop_i.getItem()
                    ));
                }
            }
        });
        if(!shopsFoundList.isEmpty()) {
            int sortingMethod = 2;
            try {
                sortingMethod = FindItemAddOn.getConfigProvider().SHOP_SORTING_METHOD;
            }
            catch(Exception e) {
                LoggerUtils.logError("Invalid value in config.yml : 'shop-sorting-method'");
                LoggerUtils.logError("Defaulting to sorting by prices method");
            }
            return sortShops(sortingMethod, shopsFoundList);
        }
        return shopsFoundList;
    }

    public List<FoundShopItemModel> findItemBasedOnDisplayNameFromAllShops(String displayName, boolean toBuy) {
//        List<Shop> shopsFound = new ArrayList<>();
        List<FoundShopItemModel> shopsFoundList = new ArrayList<>();
        List<Shop> allShops;
        if(FindItemAddOn.getConfigProvider().SEARCH_LOADED_SHOPS_ONLY) {
            allShops = new ArrayList<>(api.getShopManager().getLoadedShops());
        }
        else {
            allShops = api.getShopManager().getAllShops();
        }
        LoggerUtils.logDebugInfo("Total shops on server: " + allShops.size());
        for(Shop shop_i : allShops) {
            if(!FindItemAddOn.getConfigProvider().getBlacklistedWorlds().contains(shop_i.getLocation().getWorld())) {
                if(shop_i.getItem().hasItemMeta()) {
                    if(Objects.requireNonNull(shop_i.getItem().getItemMeta()).hasDisplayName()) {
                        if(shop_i.getItem().getItemMeta().getDisplayName().toLowerCase().contains(displayName.toLowerCase())
                                && shop_i.getRemainingStock() > 0
                                && (toBuy ? shop_i.isSelling() : shop_i.isBuying())) {
                            // check for shop if hidden
                            if(!HiddenShopStorageUtil.isShopHidden(shop_i)) {
//                                shopsFound.add(shop_i);
                                shopsFoundList.add(new FoundShopItemModel(
                                        shop_i.getPrice(),
                                        shop_i.getRemainingStock(),
                                        shop_i.getOwner(),
                                        shop_i.getLocation(),
                                        shop_i.getItem()
                                ));
                            }
                        }
                    }
                }
            }
        }
        if(!shopsFoundList.isEmpty()) {
            int sortingMethod = 2;
            try {
                sortingMethod = FindItemAddOn.getConfigProvider().SHOP_SORTING_METHOD;
            }
            catch(Exception e) {
                LoggerUtils.logError("Invalid value in config.yml : 'shop-sorting-method'");
                LoggerUtils.logError("Defaulting to sorting by prices method");
            }
            return sortShops(sortingMethod, shopsFoundList);
        }
        return shopsFoundList;
    }

//    private List<Shop> sortShops(int sortingMethod, List<Shop> shopsFound) {
//        switch (sortingMethod) {
//            // Random
//            case 1 -> Collections.shuffle(shopsFound);
//            // Based on prices (lower to higher)
//            case 2 -> shopsFound.sort(Comparator.comparing(Shop::getPrice));
//            // Based on stocks (higher to lower)
//            case 3 -> {
//                shopsFound.sort(Comparator.comparing(Shop::getRemainingStock));
//                Collections.reverse(shopsFound);
//            }
//            default -> {
//                LoggerUtils.logError("Invalid value in config.yml : 'shop-sorting-method'");
//                LoggerUtils.logError("Defaulting to sorting by prices method");
//                shopsFound.sort(Comparator.comparing(Shop::getPrice));
//            }
//        }
//        return shopsFound;
//    }

    private List<FoundShopItemModel> sortShops(int sortingMethod, List<FoundShopItemModel> shopsFoundList) {
        switch (sortingMethod) {
            // Random
            case 1 -> Collections.shuffle(shopsFoundList);
            // Based on prices (lower to higher)
            case 2 -> shopsFoundList.sort(Comparator.comparing(FoundShopItemModel::getPrice));
            // Based on stocks (higher to lower)
            case 3 -> {
                shopsFoundList.sort(Comparator.comparing(FoundShopItemModel::getRemainingStock));
                Collections.reverse(shopsFoundList);
            }
            default -> {
                LoggerUtils.logError("Invalid value in config.yml : 'shop-sorting-method'");
                LoggerUtils.logError("Defaulting to sorting by prices method");
                shopsFoundList.sort(Comparator.comparing(FoundShopItemModel::getPrice));
            }
        }
        return shopsFoundList;
    }

    public Material getShopSignMaterial() {
        return org.maxgamer.quickshop.util.Util.getSignMaterial();
    }

    public Shop findShopAtLocation(Block block) {
        Location loc = new Location(block.getWorld(), block.getX(), block.getY(), block.getZ());
        return api.getShopManager().getShop(loc);
    }

    public boolean isShopOwnerCommandRunner(Player player, Shop shop) {
        return shop.getOwner().toString().equalsIgnoreCase(player.getUniqueId().toString());
    }

}
