/**
 * QSFindItemAddOn: An Minecraft add-on plugin for the QuickShop Hikari
 * and Reremake Shop plugins for Spigot server platform.
 * Copyright (C) 2021  myzticbean
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.myzticbean.finditemaddon.Utils.JsonStorageUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.Models.HiddenShopModel;
import io.myzticbean.finditemaddon.Models.ShopSearchActivityModel;
import io.myzticbean.finditemaddon.Utils.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.maxgamer.quickshop.api.shop.Shop;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author myzticbean
 */
public class HiddenShopStorageUtil {

    protected static List<HiddenShopModel> hiddenShopsList = new ArrayList<>();
    protected static final String HIDDEN_SHOP_STORAGE_JSON_FILE_NAME = "hiddenShops.json";

    /**
     * QuickShop Reremake
     * @param shop
     */
    public static void handleShopSearchVisibilityAsync(Shop shop, boolean hideShop) {
        Bukkit.getScheduler().runTaskAsynchronously(FindItemAddOn.getInstance(), () -> {
            Iterator<ShopSearchActivityModel> shopSearchActivityIterator = ShopSearchActivityStorageUtil.getGlobalShopsList().iterator();
            int i = 0;
            while(shopSearchActivityIterator.hasNext()) {
                ShopSearchActivityModel shopSearchActivity = shopSearchActivityIterator.next();
                Location shopLocation = shop.getLocation();
                if(shopSearchActivity.compareWith(
                        shopLocation.getWorld().getName(),
                        shopLocation.getX(),
                        shopLocation.getY(),
                        shopLocation.getZ()
                )) {
                    ShopSearchActivityStorageUtil.getGlobalShopsList().get(i).setHiddenFromSearch(hideShop);
                    break;
                }
                i++;
            }
        });
//        HiddenShopModel hiddenShop = new HiddenShopModel(
//                shop.getLocation().getWorld().getName(),
//                shop.getLocation().getX(),
//                shop.getLocation().getY(),
//                shop.getLocation().getZ(),
//                shop.getLocation().getPitch(),
//                shop.getLocation().getYaw(),
//                shop.getOwner().toString());
//        hiddenShopsList.add(hiddenShop);
    }

    /**
     * QuickShop Hikari
     * @param shop
     */
    public static void handleShopSearchVisibilityAsync(com.ghostchu.quickshop.api.shop.Shop shop, boolean hideShop) {
        Bukkit.getScheduler().runTaskAsynchronously(FindItemAddOn.getInstance(), () -> {
            Iterator<ShopSearchActivityModel> shopSearchActivityIterator = ShopSearchActivityStorageUtil.getGlobalShopsList().iterator();
            int i = 0;
            while(shopSearchActivityIterator.hasNext()) {
                ShopSearchActivityModel shopSearchActivity = shopSearchActivityIterator.next();
                Location shopLocation = shop.getLocation();
                if(shopSearchActivity.compareWith(
                        shopLocation.getWorld().getName(),
                        shopLocation.getX(),
                        shopLocation.getY(),
                        shopLocation.getZ()
                )) {
                    ShopSearchActivityStorageUtil.getGlobalShopsList().get(i).setHiddenFromSearch(hideShop);
                    break;
                }
                i++;
            }
        });
//        HiddenShopModel hiddenShop = new HiddenShopModel(
//                shop.getLocation().getWorld().getName(),
//                shop.getLocation().getX(),
//                shop.getLocation().getY(),
//                shop.getLocation().getZ(),
//                shop.getLocation().getPitch(),
//                shop.getLocation().getYaw(),
//                shop.getOwner().toString());
//        hiddenShopsList.add(hiddenShop);
    }

    /**
     * QuickShop Reremake
     * @param shop
     */
//    public static void deleteShop(Shop shop) {
//        for(HiddenShopModel hiddenShop : hiddenShopsList) {
//            if(hiddenShop.getWorldName().equalsIgnoreCase(shop.getLocation().getWorld().getName())
//                && hiddenShop.getX() == shop.getLocation().getX()
//                && hiddenShop.getY() == shop.getLocation().getY()
//                && hiddenShop.getZ() == shop.getLocation().getZ()
//                && hiddenShop.getPitch() == shop.getLocation().getPitch()
//                && hiddenShop.getYaw() == shop.getLocation().getYaw()
//                && Objects.equals(hiddenShop.getShopOwnerUUID(), shop.getOwner().toString())) {
//                hiddenShopsList.remove(hiddenShop);
//                break;
//            }
//        }
//    }

    /**
     * QuickShop Hikari
     * @param shop
     */
//    public static void deleteShop(com.ghostchu.quickshop.api.shop.Shop shop) {
//        for(HiddenShopModel hiddenShop : hiddenShopsList) {
//            if(hiddenShop.getWorldName().equalsIgnoreCase(shop.getLocation().getWorld().getName())
//                    && hiddenShop.getX() == shop.getLocation().getX()
//                    && hiddenShop.getY() == shop.getLocation().getY()
//                    && hiddenShop.getZ() == shop.getLocation().getZ()
//                    && hiddenShop.getPitch() == shop.getLocation().getPitch()
//                    && hiddenShop.getYaw() == shop.getLocation().getYaw()
//                    && Objects.equals(hiddenShop.getShopOwnerUUID(), shop.getOwner().toString())) {
//                hiddenShopsList.remove(hiddenShop);
//                break;
//            }
//        }
//    }

    /**
     * QuickShop Reremake
     * @param shop
     * @return
     */
    public static boolean isShopHidden(Shop shop) {
        for(ShopSearchActivityModel shopSearchActivity : ShopSearchActivityStorageUtil.getGlobalShopsList()) {
            Location shopLocation = shop.getLocation();
            if(shopSearchActivity.compareWith(
                    shopLocation.getWorld().getName(),
                    shopLocation.getX(),
                    shopLocation.getY(),
                    shopLocation.getZ()
            )) {
                if(shopSearchActivity.isHiddenFromSearch()) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        return false;
//        for(HiddenShopModel hiddenShop : hiddenShopsList) {
//            if(hiddenShop.getWorldName().equalsIgnoreCase(shop.getLocation().getWorld().getName())
//                    && hiddenShop.getX() == shop.getLocation().getX()
//                    && hiddenShop.getY() == shop.getLocation().getY()
//                    && hiddenShop.getZ() == shop.getLocation().getZ()
//                    && hiddenShop.getPitch() == shop.getLocation().getPitch()
//                    && hiddenShop.getYaw() == shop.getLocation().getYaw()
//                    && Objects.equals(hiddenShop.getShopOwnerUUID(), shop.getOwner().toString())) {
//                return true;
//            }
//        }
//        return false;
    }

    /**
     * QuickShop Hikari
     * @param shop
     * @return
     */
    public static boolean isShopHidden(com.ghostchu.quickshop.api.shop.Shop shop) {
        for(ShopSearchActivityModel shopSearchActivity : ShopSearchActivityStorageUtil.getGlobalShopsList()) {
            Location shopLocation = shop.getLocation();
            if(shopSearchActivity.compareWith(
                    shopLocation.getWorld().getName(),
                    shopLocation.getX(),
                    shopLocation.getY(),
                    shopLocation.getZ()
            )) {
                if(shopSearchActivity.isHiddenFromSearch()) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        return false;
//        for(HiddenShopModel hiddenShop : hiddenShopsList) {
//            if(hiddenShop.getWorldName().equalsIgnoreCase(shop.getLocation().getWorld().getName())
//                    && hiddenShop.getX() == shop.getLocation().getX()
//                    && hiddenShop.getY() == shop.getLocation().getY()
//                    && hiddenShop.getZ() == shop.getLocation().getZ()
//                    && hiddenShop.getPitch() == shop.getLocation().getPitch()
//                    && hiddenShop.getYaw() == shop.getLocation().getYaw()
//                    && Objects.equals(hiddenShop.getShopOwnerUUID(), shop.getOwner().toString())) {
//                return true;
//            }
//        }
//        return false;
    }

    public static void loadHiddenShopsFromFile() {
        Gson gson = new Gson();
        File file = new File(FindItemAddOn.getInstance().getDataFolder().getAbsolutePath() + "/" + HIDDEN_SHOP_STORAGE_JSON_FILE_NAME);
        if(file.exists()) {
            try {
                Reader reader = new FileReader(file);
//                Type listType = new TypeToken<List<HiddenShopModel>>() {}.getType();
                HiddenShopModel[] h = gson.fromJson(reader, HiddenShopModel[].class);
                if(h != null) {
                    hiddenShopsList = new ArrayList<>(Arrays.asList(h));
                }
                else {
                    hiddenShopsList = new ArrayList<>();
                }
                LoggerUtils.logInfo("Loaded hidden shops from file");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveHiddenShopsToFile() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(FindItemAddOn.getInstance().getDataFolder().getAbsolutePath() + "/" + HIDDEN_SHOP_STORAGE_JSON_FILE_NAME);
        file.getParentFile().mkdir();
        try {
            file.createNewFile();
            Writer writer = new FileWriter(file, false);
            gson.toJson(hiddenShopsList, writer);
            writer.flush();
            writer.close();
            LoggerUtils.logInfo("Saved hidden shops to file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
