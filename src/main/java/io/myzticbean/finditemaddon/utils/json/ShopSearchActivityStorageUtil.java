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
import io.myzticbean.finditemaddon.models.PlayerShopVisitModel;
import io.myzticbean.finditemaddon.models.ShopSearchActivityModel;
import io.myzticbean.finditemaddon.utils.log.Logger;
import lombok.Getter;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author myzticbean
 */
public class ShopSearchActivityStorageUtil {

    private static final String SHOP_SEARCH_ACTIVITY_JSON_FILE_NAME = "shops.json";

    @Getter
    private static final Map<String, Long> cooldowns = new HashMap<>();

    @Getter
    private static List<ShopSearchActivityModel> globalShopsList = new ArrayList<>();

    /**
     * Returns true if cooldown is not present
     * @param player
     * @return
     */
    private static boolean handleCooldownIfPresent(Location shopLocation, Player player) {

        // New logic
        for(ShopSearchActivityModel shopSearchActivity : globalShopsList) {
            if(shopSearchActivity.compareWith(
                    shopLocation.getWorld().getName(),
                    shopLocation.getX(),
                    shopLocation.getY(),
                    shopLocation.getZ()
            )) {
                List<PlayerShopVisitModel> playerShopVisitList = shopSearchActivity.getPlayerVisitList()
                        .stream()
                        .filter(p -> p.getPlayerUUID().equals(player.getUniqueId()))
                        .sorted(Comparator.comparing(PlayerShopVisitModel::getVisitDateTime))
                        .collect(Collectors.toCollection(ArrayList::new));

                boolean isCooldownTimeElapsed;
                if(playerShopVisitList.size() > 0) {
                    isCooldownTimeElapsed = Instant.now().minusSeconds(
                            FindItemAddOn.getConfigProvider().SHOP_PLAYER_VISIT_COOLDOWN_IN_MINUTES * 60)
                            .isAfter(playerShopVisitList.get(playerShopVisitList.size() - 1).getVisitDateTime());
                }
                else {
                    isCooldownTimeElapsed = true;
                }
                if(isCooldownTimeElapsed) {
                    Logger.logDebugInfo(ColorTranslator.translateColorCodes("&6" + player.getName() + " is out of cooldown"));
                    return true;
                }
                else {
                    Logger.logDebugInfo(ColorTranslator.translateColorCodes("&6" + player.getName() + " still has cooldown"));
                    return false;
                }
            }
        }
        Logger.logDebugInfo(ColorTranslator.translateColorCodes("&6Shop not found, returning false for cooldown check"));
        return false;
    }

    public static void syncShops() {
        globalShopsList = FindItemAddOn.getQsApiInstance().syncShopsListForStorage(globalShopsList);
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
    public static void addShop(com.ghostchu.quickshop.api.shop.Shop shop) {
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
    public static void removeShop(com.ghostchu.quickshop.api.shop.Shop shop) {
        Iterator<ShopSearchActivityModel> shopSearchActivityIterator = globalShopsList.iterator();
        while(shopSearchActivityIterator.hasNext()) {
            ShopSearchActivityModel shopSearchActivity = shopSearchActivityIterator.next();
            if(shopSearchActivity.compareWith(
                    shop.getLocation().getWorld().getName(),
                    shop.getLocation().getX(),
                    shop.getLocation().getY(),
                    shop.getLocation().getZ()
            )) {
                shopSearchActivityIterator.remove();
                return;
            }
        }
    }

    public static void loadShopsFromFile() {
        Gson gson = new GsonBuilder().create();
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
                Logger.logInfo("Loaded shops from file");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        globalShopsList = FindItemAddOn.getQsApiInstance().syncShopsListForStorage(globalShopsList);
    }

    public static void saveShopsToFile() {
        Gson gson = new GsonBuilder().create();
        File file = new File(FindItemAddOn.getInstance().getDataFolder().getAbsolutePath() + "/" + SHOP_SEARCH_ACTIVITY_JSON_FILE_NAME);
        file.getParentFile().mkdir();
        try {
            file.createNewFile();
            Writer writer = new FileWriter(file, false);
            gson.toJson(globalShopsList, writer);
            writer.flush();
            writer.close();
            Logger.logInfo("Saved shops to file");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                            Logger.logDebugInfo("Converted shop: " + shopSearchActivity);
                        }
                    }
                    HiddenShopStorageUtil.hiddenShopsList.remove(tempHiddenShop);
                }
            }
            Logger.logDebugInfo("Here we will delete the hiddenShops.json");
            hiddenShopsJsonfile.delete();
        }
        else {
            Logger.logDebugInfo("hiddenshops.json: No conversion required");
        }
    }

    public static void addPlayerVisitEntryAsync(Location shopLocation, Player visitingPlayer) {
        Bukkit.getScheduler().runTaskAsynchronously(FindItemAddOn.getInstance(), () -> {
            if(handleCooldownIfPresent(shopLocation, visitingPlayer)) {
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
                        globalShopsList.get(i).getPlayerVisitList().add(playerShopVisit);
                        Logger.logDebugInfo("Added new player visit entry at " + shopLocation.toString());
                        break;
                    }
                    i++;
                }
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

    @Nullable
    public static OfflinePlayer getShopOwner(@NotNull Location shopLocation) {
        for(ShopSearchActivityModel shopSearchActivity : globalShopsList) {
            if (shopSearchActivity.compareWith(
                    shopLocation.getWorld().getName(),
                    shopLocation.getX(),
                    shopLocation.getY(),
                    shopLocation.getZ()
            )) {
                return Bukkit.getOfflinePlayer(UUID.fromString(shopSearchActivity.getShopOwnerUUID()));
            }
        }
        return null;
    }

    @Nullable
    public static UUID getShopOwnerUUID(@NotNull Location shopLocation) {
        Iterator<ShopSearchActivityModel> globalShopsListIterator = globalShopsList.iterator();
        while(globalShopsListIterator.hasNext()) {
            ShopSearchActivityModel shopSearchActivity = globalShopsListIterator.next();
            if (shopSearchActivity.compareWith(
                    shopLocation.getWorld().getName(),
                    shopLocation.getX(),
                    shopLocation.getY(),
                    shopLocation.getZ()
            )) {
                String uuidStr = shopSearchActivity.getShopOwnerUUID();
                try {
                    return UUID.fromString(uuidStr);
                } catch (IllegalArgumentException e) {
                    if(!FindItemAddOn.isQSReremakeInstalled()) {
                        UUID uuid = FindItemAddOn.getQsApiInstance().convertNameToUuid(uuidStr);
                        int index = globalShopsList.indexOf(shopSearchActivity);
                        globalShopsList.get(index).setShopOwnerUUID(uuid.toString());
                    }
                }
                return UUID.fromString(shopSearchActivity.getShopOwnerUUID());
            }
        }
        return null;
    }
}
