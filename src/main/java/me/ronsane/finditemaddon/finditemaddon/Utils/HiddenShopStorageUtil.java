package me.ronsane.finditemaddon.finditemaddon.Utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.ronsane.finditemaddon.finditemaddon.FindItemAddOn;
import me.ronsane.finditemaddon.finditemaddon.Models.HiddenShopModel;
import org.bukkit.configuration.file.FileConfiguration;
import org.maxgamer.quickshop.api.shop.Shop;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HiddenShopStorageUtil {

    private static List<HiddenShopModel> hiddenShopsList = new ArrayList<>();
//    private static Map<Location, UUID> hiddenShops = new HashMap<>();

    private static File hiddenShopsYamlFile;
    private static FileConfiguration hiddenShopsConfig;

    public static void addShop(Shop shop) {

//        hiddenShops.put(shop.getLocation(), shop.getOwner());

        HiddenShopModel hiddenShop = new HiddenShopModel(
                shop.getLocation().getWorld().getName(),
                shop.getLocation().getX(),
                shop.getLocation().getY(),
                shop.getLocation().getZ(),
                shop.getLocation().getPitch(),
                shop.getLocation().getYaw(),
                shop.getOwner().toString());
        hiddenShopsList.add(hiddenShop);
//        return hiddenShop;
    }

    public static void deleteShop(Shop shop) {

//        hiddenShops.remove(shop.getLocation());

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
//            if(new Location(Bukkit.getWorld(hiddenShop.getWorldName()), hiddenShop.getX(), hiddenShop.getY(), hiddenShop.getZ(), hiddenShop.getYaw(), hiddenShop.getPitch()).equals(shop.getLocation())) {
//                hiddenShopsList.remove(hiddenShop);
//                break;
//            }
        }
    }

    public static boolean isShopHidden(Shop shop) {
//        return hiddenShops.containsKey(shop.getLocation());
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

//    public static void loadHiddenShopsFromFile() {
//        hiddenShopsYamlFile = new File(Bukkit.getServer().getPluginManager().getPlugin("QSFindItemAddOn").getDataFolder(), "hiddenShops.yml");
//        if(!hiddenShopsYamlFile.exists()) {
//            hiddenShops = new HashMap<>();
//        }
//        else {
//            hiddenShopsConfig = YamlConfiguration.loadConfiguration(hiddenShopsYamlFile);
//            hiddenShops = new HashMap<>();
//            hiddenShopsConfig.getConfigurationSection("hiddenShops").getKeys(false).forEach(key -> {
//                hiddenShops.put((Location)key, UUID.fromString(hiddenShopsConfig.getString("hiddenShops." + key)));
//            });
//        }
//    }

//    public static void saveHiddenShopsToFile() {
//        hiddenShopsYamlFile = new File(Bukkit.getServer().getPluginManager().getPlugin("QSFindItemAddOn").getDataFolder(), "hiddenShops.yml");
//        for(Map.Entry<Location, UUID> entry : hiddenShops.entrySet()) {
//            hiddenShopsConfig.set("hiddenShops." + entry.getKey(), entry.getValue());
//        }
//        try {
//            hiddenShopsConfig.save(hiddenShopsYamlFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//            LoggerUtils.logError("Error saving hidden shops list to file");
//        }
//    }

    public static void loadHiddenShopsFromFile() {
        Gson gson = new Gson();
        File file = new File(FindItemAddOn.getInstance().getDataFolder().getAbsolutePath() + "/hiddenShops.json");
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
//        if(hiddenShopsList.size() > 0) {
            Gson gson = new Gson();
            File file = new File(FindItemAddOn.getInstance().getDataFolder().getAbsolutePath() + "/hiddenShops.json");
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
//        }
//        else {
//            LoggerUtils.logInfo("Hidden shops list empty, nothing to save to file.");
//        }
    }

}
