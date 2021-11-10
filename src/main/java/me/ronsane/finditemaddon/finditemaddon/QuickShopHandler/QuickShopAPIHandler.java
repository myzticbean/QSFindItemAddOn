package me.ronsane.finditemaddon.finditemaddon.QuickShopHandler;

import me.ronsane.finditemaddon.finditemaddon.FindItemAddOn;
import me.ronsane.finditemaddon.finditemaddon.Utils.HiddenShopStorageUtil;
import me.ronsane.finditemaddon.finditemaddon.Utils.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.api.QuickShopAPI;
import org.maxgamer.quickshop.api.shop.Shop;

import java.io.File;
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

    public List<Shop> findItemBasedOnTypeFromAllShops(ItemStack item, boolean toBuy) {
        List<Shop> shopsFound = new ArrayList<>();
//        assert QuickShopAPI.getShopAPI() != null;
        List<Shop> allShops;
        if(FindItemAddOn.getConfigProvider().SEARCH_LOADED_SHOPS_ONLY) {
//            allShops = QuickShopAPI.getShopAPI().getLoadedShops();
            allShops = new ArrayList<>(api.getShopManager().getLoadedShops());
        }
        else {
//            allShops = QuickShopAPI.getShopAPI().getAllShops();
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
                    shopsFound.add(shop_i);
                }
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

    public List<Shop> findItemBasedOnDisplayNameFromAllShops(String displayName, boolean toBuy) {
        List<Shop> shopsFound = new ArrayList<>();
//        assert QuickShopAPI.getShopAPI() != null;
        List<Shop> allShops;
        if(FindItemAddOn.getConfigProvider().SEARCH_LOADED_SHOPS_ONLY) {
//            allShops = QuickShopAPI.getShopAPI().getLoadedShops();
            allShops = new ArrayList<>(api.getShopManager().getLoadedShops());
        }
        else {
//            allShops = QuickShopAPI.getShopAPI().getAllShops();
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
                                shopsFound.add(shop_i);
                            }
                        }
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
//        File file = new File(QuickShop.getInstance().getDataFolder(), "config.yml");
//        FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
//
//        return Material.getMaterial(Objects.requireNonNull(fileConfig.getString("shop.sign-material")));
        return org.maxgamer.quickshop.util.Util.getSignMaterial();
//        return Material.getMaterial(
//                Objects.requireNonNull(
//                        QuickShop.getInstance().getConfig().getString("shop.sign-material")));
    }

    public Shop findShopAtLocation(Block block) {
//        assert QuickShopAPI.getShopAPI() != null;
        Location loc = new Location(block.getWorld(), block.getX(), block.getY(), block.getZ());
//        Optional<Shop> foundShop = api.getShopManager().getShop(block);
//        return foundShop.orElse(null);
        return api.getShopManager().getShop(loc);
    }

    public boolean isShopOwnerCommandRunner(Player player, Shop shop) {
        return shop.getOwner().toString().equalsIgnoreCase(player.getUniqueId().toString());
    }


}
