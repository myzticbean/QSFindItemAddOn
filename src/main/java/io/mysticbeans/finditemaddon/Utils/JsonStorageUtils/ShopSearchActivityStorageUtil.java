package io.mysticbeans.finditemaddon.Utils.JsonStorageUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.mysticbeans.finditemaddon.FindItemAddOn;
import io.mysticbeans.finditemaddon.Models.HiddenShopModel;
import io.mysticbeans.finditemaddon.Models.PlayerShopVisitModel;
import io.mysticbeans.finditemaddon.Models.ShopSearchActivityModel;
import io.mysticbeans.finditemaddon.Utils.LoggerUtils;
import lombok.Getter;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class ShopSearchActivityStorageUtil {

    private static final String SHOP_SEARCH_ACTIVITY_JSON_FILE_NAME = "shops.json";
    private static final String COOLDOWNS_YAML_FILE_NAME = "cooldowns.yml";

    @Getter
    private static final Map<String, Long> cooldowns = new HashMap<>();

    private static File cooldownsYaml;
    private static FileConfiguration cooldownsConfig;

    @Getter
    private static List<ShopSearchActivityModel> globalShopsList = new ArrayList<>();

    /**
     * Returns true if cooldown is not present
     * @param player
     * @return
     */
    private static boolean handleCooldownIfPresent(Player player) {
        // check player is inside hashmap
        if(cooldowns.containsKey(player.getName())) {
            // check if player still has cooldown
            if(cooldowns.get(player.getName()) > (System.currentTimeMillis()/1000)) {
                long timeLeft = (cooldowns.get(player.getName()) - System.currentTimeMillis()/1000);
                LoggerUtils.logDebugInfo(ColorTranslator.translateColorCodes("&6" + player.getName() + " still has cooldown of " + timeLeft + " seconds!"));
                return false;
            }
        }
        cooldowns.put(player.getName(), (System.currentTimeMillis()/1000) + FindItemAddOn.getConfigProvider().SHOP_PLAYER_VISIT_COOLDOWN_IN_MINUTES * 60);
        LoggerUtils.logDebugInfo(ColorTranslator.translateColorCodes("&aCooldown added for " + FindItemAddOn.getConfigProvider().SHOP_PLAYER_VISIT_COOLDOWN_IN_MINUTES * 60 + " seconds for " + player.getName()));
        return true;
    }

    public void syncShops() {
        globalShopsList = FindItemAddOn.getQsApiInstance().syncShopsListForStorage(globalShopsList);
    }

    public static void saveCooldowns() {
        try {
            for(Map.Entry<String, Long> entry : cooldowns.entrySet()) {
                cooldownsConfig.set("cooldowns." + entry.getKey(), entry.getValue());
            }
            cooldownsConfig.save(cooldownsYaml);
        }
        catch (IOException e) {
            LoggerUtils.logError("Error saving config.yml");
        }
    }

    public static void restoreCooldowns() {
        if(cooldownsConfig.isConfigurationSection("cooldowns")) {
            cooldownsConfig.getConfigurationSection("cooldowns").getKeys(false).forEach(key -> {
                long timeLeft = cooldownsConfig.getLong("cooldowns." + key);
                cooldowns.put(key, timeLeft);
            });
        }
    }

    /**
     * QuickShop Reremake
     * @param shop
     */
    public void addShop(org.maxgamer.quickshop.api.shop.Shop shop) {
        for(ShopSearchActivityModel shop_i : globalShopsList) {
            if(shop_i.getX() == shop.getLocation().getX()
                && shop_i.getY() == shop.getLocation().getY()
                && shop_i.getZ() == shop.getLocation().getZ()
                && shop_i.getWorldName().equalsIgnoreCase(shop.getLocation().getWorld().getName())
            ) {

                break;
            }
        }
        ShopSearchActivityModel shopModel = new ShopSearchActivityModel(
                shop.getLocation().getWorld().getName(),
                shop.getLocation().getX(),
                shop.getLocation().getY(),
                shop.getLocation().getZ(),
                shop.getLocation().getPitch(),
                shop.getLocation().getYaw(),
                shop.getOwner().toString(),
                new ArrayList<>(),
                false
        );
        globalShopsList.add(shopModel);
    }

    /**
     * QuickShop Hikari
     * @param shop
     */
    public void addShop(com.ghostchu.quickshop.api.shop.Shop shop) {
        ShopSearchActivityModel shopModel = new ShopSearchActivityModel(
                shop.getLocation().getWorld().getName(),
                shop.getLocation().getX(),
                shop.getLocation().getY(),
                shop.getLocation().getZ(),
                shop.getLocation().getPitch(),
                shop.getLocation().getYaw(),
                shop.getOwner().toString(),
                new ArrayList<>(),
                false
        );
        globalShopsList.add(shopModel);
    }

//    public void deleteShop(org.maxgamer.quickshop.api.shop.Shop shop) {
//
//    }
//
//    public void deleteShop(com.ghostchu.quickshop.api.shop.Shop shop) {
//
//    }

    public static void loadShopsFromFile() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(FindItemAddOn.getInstance().getDataFolder().getAbsolutePath() + "/" + SHOP_SEARCH_ACTIVITY_JSON_FILE_NAME);
        if(file.exists()) {
            try {
                Reader reader = new FileReader(file);
                ShopSearchActivityModel[] h = gson.fromJson(reader, ShopSearchActivityModel[].class);
                if(h != null) {
                    globalShopsList = new ArrayList<>(Arrays.asList(h));
                }
                else {
                    globalShopsList = new ArrayList<>();
                }
                LoggerUtils.logInfo("Loaded shops from file");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
//        else {
//            try {
//                file.createNewFile();
//                LoggerUtils.logInfo("Generated new " + SHOP_SEARCH_ACTIVITY_JSON_FILE_NAME);
//            } catch (IOException e) {
//                LoggerUtils.logError("Error generating " + SHOP_SEARCH_ACTIVITY_JSON_FILE_NAME);
//                e.printStackTrace();
//            }
//        }
        globalShopsList = FindItemAddOn.getQsApiInstance().syncShopsListForStorage(globalShopsList);
    }

    public static void saveShopsToFile() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(FindItemAddOn.getInstance().getDataFolder().getAbsolutePath() + "/" + SHOP_SEARCH_ACTIVITY_JSON_FILE_NAME);
        file.getParentFile().mkdir();
        try {
            file.createNewFile();
            Writer writer = new FileWriter(file, false);
            gson.toJson(globalShopsList, writer);
            writer.flush();
            writer.close();
            LoggerUtils.logInfo("Saved shops to file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setupCooldownsConfigFile() {
        cooldownsYaml = new File(FindItemAddOn.getInstance().getDataFolder(), COOLDOWNS_YAML_FILE_NAME);
        if(!cooldownsYaml.exists()) {
            try {
                boolean isConfigGenerated = cooldownsYaml.createNewFile();
                if(isConfigGenerated) {
                    LoggerUtils.logInfo("Generated a new " + COOLDOWNS_YAML_FILE_NAME);
                }
            } catch (IOException e) {
                LoggerUtils.logError("Error generating " + COOLDOWNS_YAML_FILE_NAME);
            }
        }
        cooldownsConfig = YamlConfiguration.loadConfiguration(cooldownsYaml);
    }

    public static void migrateHiddenShopsToShopsJson() {
        File hiddenShopsJsonfile = new File(FindItemAddOn.getInstance().getDataFolder().getAbsolutePath() + "/" + HiddenShopStorageUtil.HIDDEN_SHOP_STORAGE_JSON_FILE_NAME);
        if(hiddenShopsJsonfile.exists()) {
            HiddenShopStorageUtil.loadHiddenShopsFromFile();
            if(HiddenShopStorageUtil.hiddenShopsList.size() > 0) {
                for(int globalShopsList_i = 0 ; globalShopsList_i < globalShopsList.size() ; globalShopsList_i++) {
                    ShopSearchActivityModel shopSearchActivity = globalShopsList.get(globalShopsList_i);
                    HiddenShopModel tempHiddenShop = null;
                    for(HiddenShopModel hiddenShop_i : HiddenShopStorageUtil.hiddenShopsList) {
                        if(shopSearchActivity.compareWith(hiddenShop_i.getWorldName(),
                                hiddenShop_i.getX(),
                                hiddenShop_i.getY(),
                                hiddenShop_i.getZ(),
                                hiddenShop_i.getShopOwnerUUID())) {
                            tempHiddenShop = hiddenShop_i;
                            shopSearchActivity.setHiddenFromSearch(true);
                            globalShopsList.set(globalShopsList_i, shopSearchActivity);
                            LoggerUtils.logDebugInfo("Converted shop: " + shopSearchActivity);
                        }
                    }
                    HiddenShopStorageUtil.hiddenShopsList.remove(tempHiddenShop);
                }
            }
            LoggerUtils.logDebugInfo("Here we will delete the hiddenShops.json");
            hiddenShopsJsonfile.delete();
        }
        else {
            LoggerUtils.logDebugInfo("hiddenshops.json: No conversion required");
        }
    }

    public static void addPlayerVisitEntryAsync(Location shopLocation, Player visitingPlayer) {
        Bukkit.getScheduler().runTaskAsynchronously(FindItemAddOn.getInstance(), () -> {
            if(handleCooldownIfPresent(visitingPlayer)) {
                Iterator<ShopSearchActivityModel> shopSearchActivityIterator = globalShopsList.iterator();
                int i = 0;
                while(shopSearchActivityIterator.hasNext()) {
                    ShopSearchActivityModel shopSearchActivity = shopSearchActivityIterator.next();
                    if(shopSearchActivity.compareWith(
                            shopLocation.getWorld().getName(),
                            shopLocation.getX(),
                            shopLocation.getY(),
                            shopLocation.getZ()
                    )) {
                        PlayerShopVisitModel playerShopVisit = new PlayerShopVisitModel();
                        playerShopVisit.setPlayerUUID(visitingPlayer.getUniqueId());
                        playerShopVisit.setVisitDateTime();
//                        shopSearchActivity.getPlayerVisitList().add(playerShopVisit);
                        globalShopsList.get(i).getPlayerVisitList().add(playerShopVisit);
                        LoggerUtils.logDebugInfo("Added new player visit entry at " + shopLocation.toString());
                        break;
                    }
                    i++;
                }
//                for(ShopSearchActivityModel shopSearchActivity : globalShopsList) {
//                    if(shopSearchActivity.compareWith(
//                            shopLocation.getWorld().getName(),
//                            shopLocation.getX(),
//                            shopLocation.getY(),
//                            shopLocation.getZ()
//                    )) {
//                        PlayerShopVisitModel playerShopVisit = new PlayerShopVisitModel();
//                        playerShopVisit.setPlayerUUID(visitingPlayer.getUniqueId());
//                        playerShopVisit.setVisitDateTime();
//                        shopSearchActivity.getPlayerVisitList().add(playerShopVisit);
//                        LoggerUtils.logDebugInfo("Added new player visit entry at " + shopLocation.toString());
//                        break;
//                    }
//                }
            }
        });
    }

    public static int getPlayerVisitCount(Location shopLocation) {
        for(ShopSearchActivityModel shopSearchActivity : globalShopsList) {
            if (shopSearchActivity.compareWith(
                    shopLocation.getWorld().getName(),
                    shopLocation.getX(),
                    shopLocation.getY(),
                    shopLocation.getZ()
            )) {
                return shopSearchActivity.getPlayerVisitList().size();
            }
        }
        return 0;
    }
}
