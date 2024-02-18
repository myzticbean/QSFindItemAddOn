package io.myzticbean.finditemaddon.ConfigUtil;

import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.Utils.LoggerUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigSetup {

    private static File configFile;
    private static File sampleConfigFile;
    private static FileConfiguration configFileConfiguration;
    private static final int CURRENT_CONFIG_VERSION = 13;

    public static void setupConfig() {
        configFile = new File(FindItemAddOn.getInstance().getDataFolder(), "config.yml");

        if(!configFile.exists()) {
            try {
                boolean isConfigGenerated = configFile.createNewFile();
                if(isConfigGenerated) {
                    LoggerUtils.logInfo("Generated a new config.yml");
                }
            }
            catch (IOException e) {
                LoggerUtils.logError("Error generating config.yml");
            }
        }

        configFileConfiguration = YamlConfiguration.loadConfiguration(configFile);
    }

    public static void saveConfig() {
        try {
            configFileConfiguration.save(configFile);
        }
        catch (IOException e) {
            LoggerUtils.logError("Error saving config.yml");
        }
    }

    public static void checkForMissingProperties() {
        if(!configFileConfiguration.contains("search-loaded-shops-only", true)) {
            configFileConfiguration.set("search-loaded-shops-only", false);
        }
        if(!configFileConfiguration.contains("nearest-warp-mode", true)) {
            configFileConfiguration.set("nearest-warp-mode", 1);
        }
        else {
            try {
                int nearestWarpMode = configFileConfiguration.getInt("nearest-warp-mode");
                if(nearestWarpMode != 1 && nearestWarpMode != 2 && nearestWarpMode != 3) {
                    LoggerUtils.logError("Invalid value for 'nearest-warp-mode' in config.yml!");
                    LoggerUtils.logError("Resetting by default to &e1");
                    configFileConfiguration.set("nearest-warp-mode", 1);
                }
            }
            catch(Exception e) {
                LoggerUtils.logError("Invalid value for 'nearest-warp-mode' in config.yml!");
                LoggerUtils.logError("Resetting by default to &e1");
                configFileConfiguration.set("nearest-warp-mode", 1);
            }
        }
        if(!configFileConfiguration.contains("shop-gui-item-lore", true)) {
            List<String> shopGUIItemLore = new ArrayList<>();
            shopGUIItemLore.add("");
            shopGUIItemLore.add("&fPrice: &a${ITEM_PRICE}");
            shopGUIItemLore.add("&fStock: &7{SHOP_STOCK}");
            shopGUIItemLore.add("&fOwner: &7{SHOP_OWNER}");
            shopGUIItemLore.add("&fLocation: &7{SHOP_LOC}");
            shopGUIItemLore.add("&fWorld: &7{SHOP_WORLD}");
            shopGUIItemLore.add("&fWarp: &7{NEAREST_WARP}");
            shopGUIItemLore.add("");
            configFileConfiguration.set("shop-gui-item-lore", shopGUIItemLore);
        }
        if(!configFileConfiguration.contains("blacklisted-worlds", true)) {
            List<String> blacklistedWorlds = new ArrayList<>();
            blacklistedWorlds.add("world_number_1");
            blacklistedWorlds.add("world_number_2");
            configFileConfiguration.set("blacklisted-worlds", blacklistedWorlds);
        }
        if(!configFileConfiguration.contains("find-item-command.hideshop-autocomplete", true)) {
            configFileConfiguration.set("find-item-command.hideshop-autocomplete", "hideshop");
        }
        if(!configFileConfiguration.contains("find-item-command.revealshop-autocomplete", true)) {
            configFileConfiguration.set("find-item-command.revealshop-autocomplete", "revealshop");
        }
        if(!configFileConfiguration.contains("find-item-command.shop-hide-success-message", true)) {
            configFileConfiguration.set("find-item-command.shop-hide-success-message", "&aShop is now hidden from search list!");
        }
        if(!configFileConfiguration.contains("find-item-command.shop-reveal-success-message", true)) {
            configFileConfiguration.set("find-item-command.shop-reveal-success-message", "&aShop is no longer hidden from search list!");
        }
        if(!configFileConfiguration.contains("find-item-command.shop-already-hidden-message", true)) {
            configFileConfiguration.set("find-item-command.shop-already-hidden-message", "&cThis shop is already hidden!");
        }
        if(!configFileConfiguration.contains("find-item-command.shop-already-public-message", true)) {
            configFileConfiguration.set("find-item-command.shop-already-public-message", "&cThis shop is already public!");
        }
        if(!configFileConfiguration.contains("find-item-command.invalid-shop-block-message", true)) {
            configFileConfiguration.set("find-item-command.invalid-shop-block-message", "&cThe block you are looking at is not a shop!");
        }
        if(!configFileConfiguration.contains("find-item-command.hiding-shop-owner-invalid-message", true)) {
            configFileConfiguration.set("find-item-command.hiding-shop-owner-invalid-message", "&cThat shop is not yours!");
        }

        // ALWAYS AT LAST
        if(!configFileConfiguration.contains("config-version", true)) {
            configFileConfiguration.set("config-version", CURRENT_CONFIG_VERSION);
        }
        else {
            // Automatic Config upgrade process
            // Config v10
            if(configFileConfiguration.getInt("config-version") < 10) {
                boolean allowDirectShopTp = configFileConfiguration.getBoolean("allow-direct-shop-tp");
                String clickToTeleportMsg = configFileConfiguration.getString("click-to-teleport-message");
                String shopTPNoPermMsg = configFileConfiguration.getString("shop-tp-no-permission-message");
                String unsafeShopAreaMsg = configFileConfiguration.getString("unsafe-shop-area-message");
                // clear existing config properties
                configFileConfiguration.set("allow-direct-shop-tp", null);
                configFileConfiguration.set("click-to-teleport-message", null);
                configFileConfiguration.set("shop-tp-no-permission-message", null);
                configFileConfiguration.set("unsafe-shop-area-message", null);
                // set the new properties
                configFileConfiguration.set("player-shop-teleportation.direct-shop-tp-mode.tp-player-directly-to-shop", allowDirectShopTp);
                if(clickToTeleportMsg == null || clickToTeleportMsg.isEmpty())
                    configFileConfiguration.set("player-shop-teleportation.direct-shop-tp-mode.click-to-teleport-message", "&6&lClick to teleport to the shop!");
                else
                    configFileConfiguration.set("player-shop-teleportation.direct-shop-tp-mode.click-to-teleport-message", clickToTeleportMsg);

                if(shopTPNoPermMsg == null || shopTPNoPermMsg.isEmpty())
                    configFileConfiguration.set("player-shop-teleportation.direct-shop-tp-mode.shop-tp-no-permission-message", "&cYou don't have permission to teleport to shop!");
                else
                    configFileConfiguration.set("player-shop-teleportation.direct-shop-tp-mode.shop-tp-no-permission-message", shopTPNoPermMsg);

                if(unsafeShopAreaMsg == null || unsafeShopAreaMsg.isEmpty())
                    configFileConfiguration.set("player-shop-teleportation.direct-shop-tp-mode.unsafe-shop-area-message", "&cThe area around the shop is unsafe!");
                else
                    configFileConfiguration.set("player-shop-teleportation.direct-shop-tp-mode.unsafe-shop-area-message", unsafeShopAreaMsg);

                configFileConfiguration.set("player-shop-teleportation.nearest-warp-tp-mode.tp-player-to-nearest-warp", false);
            }
            // Config v11
            if(configFileConfiguration.getInt("config-version") < 11) {
                List<String> cmdAliases = new ArrayList<>();
                cmdAliases.add("searchshop");
                cmdAliases.add("shopsearch");
                cmdAliases.add("searchitem");
                configFileConfiguration.set("find-item-command.command-alias", cmdAliases);

                boolean warpState = configFileConfiguration.getBoolean("player-shop-teleportation.nearest-warp-tp-mode.warp-player-to-nearest-warp");
                if(warpState)
                    configFileConfiguration.set("player-shop-teleportation.nearest-warp-tp-mode.tp-player-to-nearest-warp", true);
                // if warpState is false, don't bother updating tPState

                // remove an erroneous field added in previous version
                configFileConfiguration.set("player-shop-teleportation.nearest-warp-tp-mode.warp-player-to-nearest-warp", null);

                // Update GUI next/previous button to blank if set to default value for the new player-head icons
                if(configFileConfiguration.getString("shop-gui-back-button-material").equalsIgnoreCase("RED_CONCRETE")) {
                    configFileConfiguration.set("shop-gui-back-button-material", "");
                }
                if(configFileConfiguration.getString("shop-gui-next-button-material").equalsIgnoreCase("GREEN_CONCRETE")) {
                    configFileConfiguration.set("shop-gui-next-button-material", "");
                }

                // Add option to configure player visit cooldown
                configFileConfiguration.set("shop-player-visit-cooldown-in-minutes", 5);
            }
            // Config 12
            if(configFileConfiguration.getInt("config-version") < 12) {
                configFileConfiguration.set("player-shop-teleportation.direct-shop-tp-mode.tp-delay-in-seconds", 0);
                configFileConfiguration.set("player-shop-teleportation.direct-shop-tp-mode.tp-delay-message", "&6You will be teleported in &c{DELAY} &6seconds...");
            }
            // Config 13
            if(configFileConfiguration.getInt("config-version") < 13) {
                configFileConfiguration.set("find-item-command.remove-hideshop-revealshop-subcommands", false);
                configFileConfiguration.set("player-shop-teleportation.direct-shop-tp-mode.tp-to-own-shop-no-permission-message", "&cYou cannot teleport to your own shop!");
                configFileConfiguration.set("ignore-empty-chests", true);
                configFileConfiguration.set("shop-gui-goto-first-page-button-material", "");
                configFileConfiguration.set("shop-gui-goto-first-page-button-text", "&7&l« &cGo to First Page");
                configFileConfiguration.set("shop-gui-goto-first-page-button-custom-model-data", "");
                configFileConfiguration.set("shop-gui-goto-last-page-button-material", "");
                configFileConfiguration.set("shop-gui-goto-last-page-button-text", "&aGo to Last Page &7&l»");
                configFileConfiguration.set("shop-gui-goto-last-page-button-custom-model-data", "");
            }

            // AT LAST
            // Moving debug-mode and config-version to the last
            boolean userDefinedDebugMode = configFileConfiguration.getBoolean("debug-mode");
            configFileConfiguration.set("debug-mode", null);
            configFileConfiguration.set("debug-mode", userDefinedDebugMode);
            configFileConfiguration.set("config-version", null);
            configFileConfiguration.set("config-version", CURRENT_CONFIG_VERSION);
        }
    }

    public static FileConfiguration get() {
        return configFileConfiguration;
    }

    public static void reloadConfig() {
        configFileConfiguration = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Added in 2.0
     */
    public static void copySampleConfig() {
        FindItemAddOn.getInstance().saveResource("sample-config.yml", true);
    }
}
