package io.mysticbeans.finditemaddon.QuickShopHandler;

import com.ghostchu.quickshop.QuickShop;
import com.ghostchu.quickshop.api.QuickShopAPI;
import com.ghostchu.quickshop.api.shop.Shop;
import io.mysticbeans.finditemaddon.FindItemAddOn;
import io.mysticbeans.finditemaddon.Models.FoundShopItemModel;
import io.mysticbeans.finditemaddon.Models.ShopSearchActivityModel;
import io.mysticbeans.finditemaddon.Utils.JsonStorageUtils.HiddenShopStorageUtil;
import io.mysticbeans.finditemaddon.Utils.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QSHikariAPIHandler implements QSApi<QuickShop, Shop> {

//    private final QuickShop qsPlugin;

    private QuickShopAPI api;

    public QSHikariAPIHandler() {
//        qsPlugin = QuickShop.getInstance();
        api = (QuickShopAPI) Bukkit.getPluginManager().getPlugin("QuickShop-Hikari");
    }

    public List<FoundShopItemModel> findItemBasedOnTypeFromAllShops(ItemStack item, boolean toBuy) {
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
            return QSApi.sortShops(sortingMethod, shopsFoundList);
        }
        return shopsFoundList;
    }

    public List<FoundShopItemModel> findItemBasedOnDisplayNameFromAllShops(String displayName, boolean toBuy) {
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
            return QSApi.sortShops(sortingMethod, shopsFoundList);
        }
        return shopsFoundList;
    }

    public List<FoundShopItemModel> fetchAllItemsFromAllShops(boolean toBuy) {
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
                    && shop_i.getRemainingStock() > 0
                    && (toBuy ? shop_i.isSelling() : shop_i.isBuying())) {
                // check for shop if hidden
                if(!HiddenShopStorageUtil.isShopHidden(shop_i)) {
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
            int sortingMethod = 1;
            return QSApi.sortShops(sortingMethod, shopsFoundList);
        }
        return shopsFoundList;
    }

    public Material getShopSignMaterial() {
        return com.ghostchu.quickshop.util.Util.getSignMaterial();
    }

    public Shop findShopAtLocation(Block block) {
        Location loc = new Location(block.getWorld(), block.getX(), block.getY(), block.getZ());
        return api.getShopManager().getShop(loc);
    }

    public boolean isShopOwnerCommandRunner(Player player, Shop shop) {
        return shop.getOwner().toString().equalsIgnoreCase(player.getUniqueId().toString());
    }

    @Override
    public List<Shop> getAllShops() {
        return api.getShopManager().getAllShops();
    }

    @Override
    public List<ShopSearchActivityModel> syncShopsListForStorage(List<ShopSearchActivityModel> globalShopsList) {
        // copy all shops from shops list in API to a temp globalShopsList
        // now check shops from temp globalShopsList in current globalShopsList and pull playerVisit data
        List<ShopSearchActivityModel> tempGlobalShopsList = new ArrayList<>();
        for(Shop shop_i : getAllShops()) {
            Location shopLoc = shop_i.getLocation();
            tempGlobalShopsList.add(new ShopSearchActivityModel(
                    shopLoc.getWorld().getName(),
                    shopLoc.getX(),
                    shopLoc.getY(),
                    shopLoc.getZ(),
                    shopLoc.getPitch(),
                    shopLoc.getYaw(),
                    shop_i.getOwner().toString(),
                    new ArrayList<>(),
                    false
            ));
        }

        for(ShopSearchActivityModel shop_temp : tempGlobalShopsList) {
            ShopSearchActivityModel tempShopToRemove = null;
            for(ShopSearchActivityModel shop_global : globalShopsList) {
                if(shop_temp.getWorldName().equalsIgnoreCase(shop_global.getWorldName())
                        && shop_temp.getX() == shop_global.getX()
                        && shop_temp.getY() == shop_global.getY()
                        && shop_temp.getZ() == shop_global.getZ()
                        && shop_temp.getShopOwnerUUID().equalsIgnoreCase(shop_global.getShopOwnerUUID())
                ) {
                    shop_temp.setPlayerVisitList(shop_global.getPlayerVisitList());
                    shop_temp.setHiddenFromSearch(shop_global.isHiddenFromSearch());
                    tempShopToRemove = shop_global;
                    break;
                }
            }
            if(tempShopToRemove != null)
                globalShopsList.remove(tempShopToRemove);
        }
        return tempGlobalShopsList;
    }

//    private List<FoundShopItemModel> sortShops(int sortingMethod, List<FoundShopItemModel> shopsFoundList) {
//        switch (sortingMethod) {
//            // Random
//            case 1 -> Collections.shuffle(shopsFoundList);
//            // Based on prices (lower to higher)
//            case 2 -> shopsFoundList.sort(Comparator.comparing(FoundShopItemModel::getPrice));
//            // Based on stocks (higher to lower)
//            case 3 -> {
//                shopsFoundList.sort(Comparator.comparing(FoundShopItemModel::getRemainingStock));
//                Collections.reverse(shopsFoundList);
//            }
//            default -> {
//                LoggerUtils.logError("Invalid value in config.yml : 'shop-sorting-method'");
//                LoggerUtils.logError("Defaulting to sorting by prices method");
//                shopsFoundList.sort(Comparator.comparing(FoundShopItemModel::getPrice));
//            }
//        }
//        return shopsFoundList;
//    }

}