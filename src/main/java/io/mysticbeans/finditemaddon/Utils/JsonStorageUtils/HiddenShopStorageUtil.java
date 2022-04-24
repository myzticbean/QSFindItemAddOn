package io.mysticbeans.finditemaddon.Utils.JsonStorageUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.mysticbeans.finditemaddon.FindItemAddOn;
import io.mysticbeans.finditemaddon.Models.HiddenShopModel;
import io.mysticbeans.finditemaddon.Utils.LoggerUtils;
import org.maxgamer.quickshop.api.shop.Shop;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HiddenShopStorageUtil {

    protected static List<HiddenShopModel> hiddenShopsList = new ArrayList<>();
    protected static final String HIDDEN_SHOP_STORAGE_JSON_FILE_NAME = "hiddenShops.json";

    /**
     * QuickShop Reremake
     * @param shop
     */
    public static void addShop(Shop shop) {
        HiddenShopModel hiddenShop = new HiddenShopModel(
                shop.getLocation().getWorld().getName(),
                shop.getLocation().getX(),
                shop.getLocation().getY(),
                shop.getLocation().getZ(),
                shop.getLocation().getPitch(),
                shop.getLocation().getYaw(),
                shop.getOwner().toString());
        hiddenShopsList.add(hiddenShop);
    }

    /**
     * QuickShop Hikari
     * @param shop
     */
    public static void addShop(com.ghostchu.quickshop.api.shop.Shop shop) {
        HiddenShopModel hiddenShop = new HiddenShopModel(
                shop.getLocation().getWorld().getName(),
                shop.getLocation().getX(),
                shop.getLocation().getY(),
                shop.getLocation().getZ(),
                shop.getLocation().getPitch(),
                shop.getLocation().getYaw(),
                shop.getOwner().toString());
        hiddenShopsList.add(hiddenShop);
    }

    /**
     * QuickShop Reremake
     * @param shop
     */
    public static void deleteShop(Shop shop) {
        for(HiddenShopModel hiddenShop : hiddenShopsList) {
            if(hiddenShop.getWorldName().equalsIgnoreCase(shop.getLocation().getWorld().getName())
                && hiddenShop.getX() == shop.getLocation().getX()
                && hiddenShop.getY() == shop.getLocation().getY()
                && hiddenShop.getZ() == shop.getLocation().getZ()
                && hiddenShop.getPitch() == shop.getLocation().getPitch()
                && hiddenShop.getYaw() == shop.getLocation().getYaw()
                && Objects.equals(hiddenShop.getShopOwnerUUID(), shop.getOwner().toString())) {
                hiddenShopsList.remove(hiddenShop);
                break;
            }
        }
    }

    /**
     * QuickShop Hikari
     * @param shop
     */
    public static void deleteShop(com.ghostchu.quickshop.api.shop.Shop shop) {
        for(HiddenShopModel hiddenShop : hiddenShopsList) {
            if(hiddenShop.getWorldName().equalsIgnoreCase(shop.getLocation().getWorld().getName())
                    && hiddenShop.getX() == shop.getLocation().getX()
                    && hiddenShop.getY() == shop.getLocation().getY()
                    && hiddenShop.getZ() == shop.getLocation().getZ()
                    && hiddenShop.getPitch() == shop.getLocation().getPitch()
                    && hiddenShop.getYaw() == shop.getLocation().getYaw()
                    && Objects.equals(hiddenShop.getShopOwnerUUID(), shop.getOwner().toString())) {
                hiddenShopsList.remove(hiddenShop);
                break;
            }
        }
    }

    /**
     * QuickShop Reremake
     * @param shop
     * @return
     */
    public static boolean isShopHidden(Shop shop) {
        for(HiddenShopModel hiddenShop : hiddenShopsList) {
            if(hiddenShop.getWorldName().equalsIgnoreCase(shop.getLocation().getWorld().getName())
                    && hiddenShop.getX() == shop.getLocation().getX()
                    && hiddenShop.getY() == shop.getLocation().getY()
                    && hiddenShop.getZ() == shop.getLocation().getZ()
                    && hiddenShop.getPitch() == shop.getLocation().getPitch()
                    && hiddenShop.getYaw() == shop.getLocation().getYaw()
                    && Objects.equals(hiddenShop.getShopOwnerUUID(), shop.getOwner().toString())) {
                return true;
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
        for(HiddenShopModel hiddenShop : hiddenShopsList) {
            if(hiddenShop.getWorldName().equalsIgnoreCase(shop.getLocation().getWorld().getName())
                    && hiddenShop.getX() == shop.getLocation().getX()
                    && hiddenShop.getY() == shop.getLocation().getY()
                    && hiddenShop.getZ() == shop.getLocation().getZ()
                    && hiddenShop.getPitch() == shop.getLocation().getPitch()
                    && hiddenShop.getYaw() == shop.getLocation().getYaw()
                    && Objects.equals(hiddenShop.getShopOwnerUUID(), shop.getOwner().toString())) {
                return true;
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
                Type listType = new TypeToken<List<HiddenShopModel>>() {}.getType();
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
