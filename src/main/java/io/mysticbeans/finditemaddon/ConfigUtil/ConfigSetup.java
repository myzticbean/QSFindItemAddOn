package io.mysticbeans.finditemaddon.ConfigUtil;

import io.mysticbeans.finditemaddon.FindItemAddOn;
import io.mysticbeans.finditemaddon.Utils.LoggerUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigSetup {

    private static File file;
    private static FileConfiguration fileConfig;
    private static final int CURRENT_CONFIG_VERSION = 11;

    public static void setupConfig() {
        file = new File(FindItemAddOn.getInstance().getDataFolder(), "config.yml");

        if(!file.exists()) {
            try {
                boolean isConfigGenerated = file.createNewFile();
                if(isConfigGenerated) {
                    LoggerUtils.logInfo("Generated a new config.yml");
                }
            }
            catch (IOException e) {
                LoggerUtils.logError("Error generating config.yml");
            }
        }

        fileConfig = YamlConfiguration.loadConfiguration(file);
    }

    public static void saveConfig() {
        try {
            fileConfig.save(file);
        }
        catch (IOException e) {
            LoggerUtils.logError("Error saving config.yml");
        }
    }

    public static void checkForMissingProperties() {
        if(!fileConfig.contains("search-loaded-shops-only", true)) {
            fileConfig.set("search-loaded-shops-only", false);
        }
        if(!fileConfig.contains("nearest-warp-mode", true)) {
            fileConfig.set("nearest-warp-mode", 1);
        }
        else {
            try {
                int nearestWarpMode = fileConfig.getInt("nearest-warp-mode");
                if(nearestWarpMode != 1 && nearestWarpMode != 2 && nearestWarpMode != 3) {
                    LoggerUtils.logError("Invalid value for 'nearest-warp-mode' in config.yml!");
                    LoggerUtils.logError("Resetting by default to &e1");
                    fileConfig.set("nearest-warp-mode", 1);
                }
            }
            catch(Exception e) {
                LoggerUtils.logError("Invalid value for 'nearest-warp-mode' in config.yml!");
                LoggerUtils.logError("Resetting by default to &e1");
                fileConfig.set("nearest-warp-mode", 1);
            }
        }
        if(!fileConfig.contains("shop-gui-item-lore", true)) {
            List<String> shopGUIItemLore = new ArrayList<>();
            shopGUIItemLore.add("");
            shopGUIItemLore.add("&fPrice: &a${ITEM_PRICE}");
            shopGUIItemLore.add("&fStock: &7{SHOP_STOCK}");
            shopGUIItemLore.add("&fOwner: &7{SHOP_OWNER}");
            shopGUIItemLore.add("&fLocation: &7{SHOP_LOC}");
            shopGUIItemLore.add("&fWorld: &7{SHOP_WORLD}");
            shopGUIItemLore.add("&fWarp: &7{NEAREST_WARP}");
            shopGUIItemLore.add("");
            fileConfig.set("shop-gui-item-lore", shopGUIItemLore);
        }
        if(!fileConfig.contains("blacklisted-worlds", true)) {
            List<String> blacklistedWorlds = new ArrayList<>();
            blacklistedWorlds.add("world_number_1");
            blacklistedWorlds.add("world_number_2");
            fileConfig.set("blacklisted-worlds", blacklistedWorlds);
        }
        if(!fileConfig.contains("find-item-command.hideshop-autocomplete", true)) {
            fileConfig.set("find-item-command.hideshop-autocomplete", "hideshop");
        }
        if(!fileConfig.contains("find-item-command.revealshop-autocomplete", true)) {
            fileConfig.set("find-item-command.revealshop-autocomplete", "revealshop");
        }
        if(!fileConfig.contains("find-item-command.shop-hide-success-message", true)) {
            fileConfig.set("find-item-command.shop-hide-success-message", "&aShop is now hidden from search list!");
        }
        if(!fileConfig.contains("find-item-command.shop-reveal-success-message", true)) {
            fileConfig.set("find-item-command.shop-reveal-success-message", "&aShop is no longer hidden from search list!");
        }
        if(!fileConfig.contains("find-item-command.shop-already-hidden-message", true)) {
            fileConfig.set("find-item-command.shop-already-hidden-message", "&cThis shop is already hidden!");
        }
        if(!fileConfig.contains("find-item-command.shop-already-public-message", true)) {
            fileConfig.set("find-item-command.shop-already-public-message", "&cThis shop is already public!");
        }
        if(!fileConfig.contains("find-item-command.invalid-shop-block-message", true)) {
            fileConfig.set("find-item-command.invalid-shop-block-message", "&cThe block you are looking at is not a shop!");
        }
        if(!fileConfig.contains("find-item-command.hiding-shop-owner-invalid-message", true)) {
            fileConfig.set("find-item-command.hiding-shop-owner-invalid-message", "&cThat shop is not yours!");
        }

        // ALWAYS AT LAST
        if(!fileConfig.contains("config-version", true)) {
            fileConfig.set("config-version", CURRENT_CONFIG_VERSION);
        }
        else {
            // Config v10
            if(fileConfig.getInt("config-version") < 10) {
                boolean allowDirectShopTp = fileConfig.getBoolean("allow-direct-shop-tp");
                String clickToTeleportMsg = fileConfig.getString("click-to-teleport-message");
                String shopTPNoPermMsg = fileConfig.getString("shop-tp-no-permission-message");
                String unsafeShopAreaMsg = fileConfig.getString("unsafe-shop-area-message");
                // clear existing config properties
                fileConfig.set("allow-direct-shop-tp", null);
                fileConfig.set("click-to-teleport-message", null);
                fileConfig.set("shop-tp-no-permission-message", null);
                fileConfig.set("unsafe-shop-area-message", null);
                // set the new properties
                fileConfig.set("player-shop-teleportation.direct-shop-tp-mode.tp-player-directly-to-shop", allowDirectShopTp);
                if(clickToTeleportMsg == null || clickToTeleportMsg.isEmpty())
                    fileConfig.set("player-shop-teleportation.direct-shop-tp-mode.click-to-teleport-message", "&6&lClick to teleport to the shop!");
                else
                    fileConfig.set("player-shop-teleportation.direct-shop-tp-mode.click-to-teleport-message", clickToTeleportMsg);

                if(shopTPNoPermMsg == null || shopTPNoPermMsg.isEmpty())
                    fileConfig.set("player-shop-teleportation.direct-shop-tp-mode.shop-tp-no-permission-message", "&cYou don't have permission to teleport to shop!");
                else
                    fileConfig.set("player-shop-teleportation.direct-shop-tp-mode.shop-tp-no-permission-message", shopTPNoPermMsg);

                if(unsafeShopAreaMsg == null || unsafeShopAreaMsg.isEmpty())
                    fileConfig.set("player-shop-teleportation.direct-shop-tp-mode.unsafe-shop-area-message", "&cThe area around the shop is unsafe!");
                else
                    fileConfig.set("player-shop-teleportation.direct-shop-tp-mode.unsafe-shop-area-message", unsafeShopAreaMsg);

                fileConfig.set("player-shop-teleportation.nearest-warp-tp-mode.tp-player-to-nearest-warp", false);
            }
            // Config v11
            if(fileConfig.getInt("config-version") < 11) {
                List<String> cmdAliases = new ArrayList<>();
                cmdAliases.add("searchshop");
                cmdAliases.add("shopsearch");
                cmdAliases.add("searchitem");
                fileConfig.set("find-item-command.command-alias", cmdAliases);

                boolean warpState = fileConfig.getBoolean("player-shop-teleportation.nearest-warp-tp-mode.warp-player-to-nearest-warp");
                if(warpState)
                    fileConfig.set("player-shop-teleportation.nearest-warp-tp-mode.tp-player-to-nearest-warp", true);
                // if warpState is false, don't bother updating tPState

                // remove an erroneous field added in previous version
                fileConfig.set("player-shop-teleportation.nearest-warp-tp-mode.warp-player-to-nearest-warp", null);

                // Update GUI next/previous button to blank if set to default value for the new player-head icons
                if(fileConfig.getString("shop-gui-back-button-material").equalsIgnoreCase("RED_CONCRETE")) {
                    fileConfig.set("shop-gui-back-button-material", "");
                }
                if(fileConfig.getString("shop-gui-next-button-material").equalsIgnoreCase("GREEN_CONCRETE")) {
                    fileConfig.set("shop-gui-next-button-material", "");
                }

                // Add option to configure player visit cooldown
                fileConfig.set("shop-player-visit-cooldown-in-minutes", 5);
            }
            fileConfig.set("config-version", null);
            fileConfig.set("config-version", CURRENT_CONFIG_VERSION);
        }
    }

    public static FileConfiguration get() {
        return fileConfig;
    }

    public static void reloadConfig() {
        fileConfig = YamlConfiguration.loadConfiguration(file);
    }

}
