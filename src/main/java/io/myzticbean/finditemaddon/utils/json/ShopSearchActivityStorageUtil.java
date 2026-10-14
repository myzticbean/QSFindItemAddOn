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
import io.myzticbean.finditemaddon.utils.async.VirtualThreadScheduler;
import io.myzticbean.finditemaddon.utils.log.Logger;
import lombok.Getter;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author myzticbean
 */
public class ShopSearchActivityStorageUtil {

    private static final String SHOP_SEARCH_ACTIVITY_JSON_FILE_NAME = "shops.json";

    @Getter
    private static final Map<String, Long> cooldowns = new HashMap<>();

    @Getter
    private static volatile List<ShopSearchActivityModel> globalShopsList = new CopyOnWriteArrayList<>();

    /**
     * Location index over {@link #globalShopsList}, so per-shop lookups (run once per shop on every search)
     * are O(1) instead of a scan of every shop on the server. Holds the first entry per location, which is
     * what the linear scans it replaced returned. Only mutated alongside the list, in this class.
     */
    private static volatile Map<LocationKey, ShopSearchActivityModel> shopsByLocation = new ConcurrentHashMap<>();

    /** Matches {@link ShopSearchActivityModel#compareWith(String, double, double, double)}: case-insensitive world, exact coordinates. */
    private record LocationKey(String worldName, double x, double y, double z) {
        static LocationKey of(String worldName, double x, double y, double z) {
            // "+ 0.0" folds -0.0 into 0.0: record equality compares doubles bitwise, compareWith() uses ==
            return new LocationKey(worldName.toLowerCase(Locale.ROOT), x + 0.0, y + 0.0, z + 0.0);
        }

        static LocationKey of(Location location) {
            return of(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
        }
    }

    /**
     * @return the stored entry for the shop at this location, or null if there is none
     */
    @Nullable
    public static ShopSearchActivityModel findShop(@NotNull Location shopLocation) {
        return shopsByLocation.get(LocationKey.of(shopLocation));
    }

    private static void setGlobalShopsList(List<ShopSearchActivityModel> shops) {
        Map<LocationKey, ShopSearchActivityModel> index = new ConcurrentHashMap<>(shops.size() * 2);
        shops.forEach(shop -> addToIndex(index, shop));
        globalShopsList = new CopyOnWriteArrayList<>(shops);
        shopsByLocation = index;
    }

    private static void addToIndex(Map<LocationKey, ShopSearchActivityModel> index, ShopSearchActivityModel shop) {
        // compareWith() never matches a null world name, so neither does the index
        if (shop.getWorldName() != null) {
            index.putIfAbsent(LocationKey.of(shop.getWorldName(), shop.getX(), shop.getY(), shop.getZ()), shop);
        }
    }

    /**
     * Returns true if cooldown is not present
     * @param player
     * @return
     */
    private static boolean handleCooldownIfPresent(Location shopLocation, Player player) {
        ShopSearchActivityModel shopSearchActivity = findShop(shopLocation);
        if (shopSearchActivity != null) {
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
        Logger.logDebugInfo(ColorTranslator.translateColorCodes("&6Shop not found, returning false for cooldown check"));
        return false;
    }

    public static void syncShops() {
        setGlobalShopsList(FindItemAddOn.getQsApiInstance().syncShopsListForStorage(globalShopsList));
    }

    /**
     * QuickShop Reremake
     * @param shop
     */
    @Deprecated
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
        addToIndex(shopsByLocation, shopModel);
    }

    /**
     * QuickShop Hikari
     * @param shop
     */
    public static void addShop(com.ghostchu.quickshop.api.shop.Shop shop) {
        ShopSearchActivityModel shopModel = new ShopSearchActivityModel(
                shop.bukkitLocation().getWorld().getName(),
                shop.bukkitLocation().getX(),
                shop.bukkitLocation().getY(),
                shop.bukkitLocation().getZ(),
                shop.bukkitLocation().getPitch(),
                shop.bukkitLocation().getYaw(),
                shop.getOwner().toString(),
                new ArrayList<>(),
                false
        );
        globalShopsList.add(shopModel);
        addToIndex(shopsByLocation, shopModel);
    }

    /**
     * QuickShop Hikari
     * @param shop
     */
    public static void removeShop(com.ghostchu.quickshop.api.shop.Shop shop) {
        Location shopLocation = shop.bukkitLocation();
        globalShopsList.removeIf(shopSearchActivity -> shopSearchActivity.compareWith(
                shopLocation.getWorld().getName(),
                shopLocation.getX(),
                shopLocation.getY(),
                shopLocation.getZ()
        ));
        shopsByLocation.remove(LocationKey.of(shopLocation));
    }

    public static void loadShopsFromFile() {
        Gson gson = new GsonBuilder().create();
        File file = new File(getShopJsonFilePath());
        if(file.exists()) {
            try {
                Reader reader = new FileReader(file);
                ShopSearchActivityModel[] shopsArray = gson.fromJson(reader, ShopSearchActivityModel[].class);
                if(shopsArray != null) {
                    // Filter out any null elements from the array
                    setGlobalShopsList(Arrays.stream(shopsArray)
                            .filter(Objects::nonNull)
                            .toList());
                }
                else {
                    setGlobalShopsList(List.of());
                }
                Logger.logInfo("Loaded shops from file");
            } catch (FileNotFoundException e) {
                Logger.logError("Failed to load shops from file", e);
            }
        }
        syncShops();
    }

    public static void saveShopsToFile() {
        Gson gson = new GsonBuilder().create();
        File file = new File(getShopJsonFilePath());
        file.getParentFile().mkdir();
        try {
            file.createNewFile();
            Writer writer = new FileWriter(file, false);
            gson.toJson(globalShopsList, writer);
            writer.flush();
            writer.close();
            Logger.logInfo("Saved shops to file");
        } catch (IOException e) {
            Logger.logError("Failed to save shops to file", e);
        }
    }

    private static @NotNull String getShopJsonFilePath() {
        String path = FindItemAddOn.getInstance().getDataFolder().getAbsolutePath() + File.separator + SHOP_SEARCH_ACTIVITY_JSON_FILE_NAME;
        Logger.logDebugInfo("shops.json file path: " + path);
        return path;
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
        VirtualThreadScheduler.runTaskAsync(() -> {
            if(handleCooldownIfPresent(shopLocation, visitingPlayer)) {
                ShopSearchActivityModel shopSearchActivity = findShop(shopLocation);
                if (shopSearchActivity != null) {
                    PlayerShopVisitModel playerShopVisit = new PlayerShopVisitModel();
                    playerShopVisit.setPlayerUUID(visitingPlayer.getUniqueId());
                    playerShopVisit.setVisitDateTime();
                    shopSearchActivity.getPlayerVisitList().add(playerShopVisit);
                    Logger.logDebugInfo("Added new player visit entry at " + shopLocation);
                }
            }
        });
    }

    public static int getPlayerVisitCount(Location shopLocation) {
        ShopSearchActivityModel shopSearchActivity = findShop(shopLocation);
        return shopSearchActivity != null ? shopSearchActivity.getPlayerVisitList().size() : 0;
    }

    @Nullable
    @Deprecated
    public static OfflinePlayer getShopOwner(@NotNull Location shopLocation) {
        ShopSearchActivityModel shopSearchActivity = findShop(shopLocation);
        return shopSearchActivity != null
                ? Bukkit.getOfflinePlayer(UUID.fromString(shopSearchActivity.getShopOwnerUUID()))
                : null;
    }

    @Nullable
    public static UUID getShopOwnerUUID(@NotNull Location shopLocation) {
        ShopSearchActivityModel shopSearchActivity = findShop(shopLocation);
        if (shopSearchActivity == null) {
            return null;
        }
        String uuidStr = shopSearchActivity.getShopOwnerUUID();
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException _) {
            if (!FindItemAddOn.isQSReremakeInstalled()) {
                UUID uuid = FindItemAddOn.getQsApiInstance().convertNameToUuid(uuidStr);
                shopSearchActivity.setShopOwnerUUID(uuid.toString());
            }
        }
        return UUID.fromString(shopSearchActivity.getShopOwnerUUID());
    }
}
