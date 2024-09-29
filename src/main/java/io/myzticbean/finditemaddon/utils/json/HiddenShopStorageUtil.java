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
package io.myzticbean.finditemaddon.utils.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.models.HiddenShopModel;
import io.myzticbean.finditemaddon.models.ShopSearchActivityModel;
import io.myzticbean.finditemaddon.utils.log.Logger;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.maxgamer.quickshop.api.shop.Shop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author myzticbean
 */
@UtilityClass
public class HiddenShopStorageUtil {

    static List<HiddenShopModel> hiddenShopsList = new ArrayList<>();
    static final String HIDDEN_SHOP_STORAGE_JSON_FILE_NAME = "hiddenShops.json";

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
    }

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
    }

    public static void loadHiddenShopsFromFile() {
        Gson gson = new Gson();
        File file = new File(FindItemAddOn.getInstance().getDataFolder().getAbsolutePath() + "/" + HIDDEN_SHOP_STORAGE_JSON_FILE_NAME);
        if(file.exists()) {
            try {
                Reader reader = new FileReader(file);
                HiddenShopModel[] h = gson.fromJson(reader, HiddenShopModel[].class);
                if(h != null) {
                    hiddenShopsList = new ArrayList<>(Arrays.asList(h));
                }
                else {
                    hiddenShopsList = new ArrayList<>();
                }
                Logger.logInfo("Loaded hidden shops from file");
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
            Logger.logInfo("Saved hidden shops to file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
