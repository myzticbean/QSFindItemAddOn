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
    private static final int CURRENT_CONFIG_VERSION = 15;

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
            // Config 14
            if(configFileConfiguration.getInt("config-version") < 14) {
                configFileConfiguration.set("find-item-command.disable-search-all-shops", false);
            }
            // Config 15
            if(configFileConfiguration.getInt("config-version") < 15) {
                configFileConfiguration.set("player-shop-teleportation.nearest-warp-tp-mode.do-not-tp-if-warp-locked", true);
                // capture old config data
                String shopGuiBackButtonMaterial = configFileConfiguration.getString("shop-gui-back-button-material");
                String shopGuiBackButtonText = configFileConfiguration.getString("shop-gui-back-button-text");
                String shopGuiNextButtonMaterial = configFileConfiguration.getString("shop-gui-next-button-material");
                String shopGuiNNextButtonText = configFileConfiguration.getString("shop-gui-next-button-text");
                String shopGuiFillerItem = configFileConfiguration.getString("shop-gui-filler-item");
                String shopGuiCloseButtonMaterial = configFileConfiguration.getString("shop-gui-close-button-material");
                String shopGuiCloseButtonText = configFileConfiguration.getString("shop-gui-close-button-text");
                String shopGuiGotoFirstPageButtonMaterial = configFileConfiguration.getString("shop-gui-goto-first-page-button-material");
                String shopGuiGotoFirstPageButtonText = configFileConfiguration.getString("shop-gui-goto-first-page-button-text");
                String shopGuiGotoLastPageButtonMaterial = configFileConfiguration.getString("shop-gui-goto-last-page-button-material");
                String shopGuiGotoLastPageButtonText = configFileConfiguration.getString("shop-gui-goto-last-page-button-text");
                String shopNavFirstPageAlertMsg = configFileConfiguration.getString("shop-navigation-first-page-alert-message");
                String shopNavLastPageAlertMsg = configFileConfiguration.getString("shop-navigation-last-page-alert-message");
                String shopGuiBackButtonCMD = configFileConfiguration.getString("shop-gui-back-button-custom-model-data");
                String shopGuiNextButtonCMD = configFileConfiguration.getString("shop-gui-next-button-custom-model-data");
                String shopGuiCloseButtonCMD = configFileConfiguration.getString("shop-gui-close-button-custom-model-data");
                String shopGuiGotoFirstPageButtonCMD = configFileConfiguration.getString("shop-gui-goto-first-page-button-custom-model-data");
                String shopGuiGotoLastPageButtonCMD = configFileConfiguration.getString("shop-gui-goto-last-page-button-custom-model-data");
                // remove old config options
                configFileConfiguration.set("shop-gui-back-button-material", null);
                configFileConfiguration.set("shop-gui-back-button-text", null);
                configFileConfiguration.set("shop-gui-next-button-material", null);
                configFileConfiguration.set("shop-gui-next-button-text", null);
                configFileConfiguration.set("shop-gui-filler-item", null);
                configFileConfiguration.set("shop-gui-close-button-material", null);
                configFileConfiguration.set("shop-gui-close-button-text", null);
                configFileConfiguration.set("shop-gui-goto-first-page-button-material", null);
                configFileConfiguration.set("shop-gui-goto-first-page-button-text", null);
                configFileConfiguration.set("shop-gui-goto-last-page-button-material", null);
                configFileConfiguration.set("shop-gui-goto-last-page-button-text", null);
                configFileConfiguration.set("shop-navigation-first-page-alert-message", null);
                configFileConfiguration.set("shop-navigation-last-page-alert-message", null);
                configFileConfiguration.set("shop-gui-back-button-custom-model-data", null);
                configFileConfiguration.set("shop-gui-next-button-custom-model-data", null);
                configFileConfiguration.set("shop-gui-close-button-custom-model-data", null);
                configFileConfiguration.set("shop-navigation-first-page-button-custom-model-data", null);
                configFileConfiguration.set("shop-navigation-last-page-button-custom-model-data", null);
                configFileConfiguration.set("shop-gui-goto-first-page-button-custom-model-data", null);
                configFileConfiguration.set("shop-gui-goto-last-page-button-custom-model-data", null);
                // add the new config properties and transfer the data
                final String SHOP_GUI_OPTION = "shop-gui.";
                final String SHOP_GUI_NAVIGATION_OPTION = "shop-navigation.";
                final String SHOP_GUI_CMD_OPTION = "custom-model-data.";
                configFileConfiguration.set(SHOP_GUI_OPTION + "back-button-material", shopGuiBackButtonMaterial);
                configFileConfiguration.set(SHOP_GUI_OPTION + "back-button-text", shopGuiBackButtonText);
                configFileConfiguration.set(SHOP_GUI_OPTION + "next-button-material", shopGuiNextButtonMaterial);
                configFileConfiguration.set(SHOP_GUI_OPTION + "next-button-text", shopGuiNNextButtonText);
                configFileConfiguration.set(SHOP_GUI_OPTION + "filler-item", shopGuiFillerItem);
                configFileConfiguration.set(SHOP_GUI_OPTION + "close-button-material", shopGuiCloseButtonMaterial);
                configFileConfiguration.set(SHOP_GUI_OPTION + "close-button-text", shopGuiCloseButtonText);
                configFileConfiguration.set(SHOP_GUI_OPTION + "goto-first-page-button-material", shopGuiGotoFirstPageButtonMaterial);
                configFileConfiguration.set(SHOP_GUI_OPTION + "goto-first-page-button-text", shopGuiGotoFirstPageButtonText);
                configFileConfiguration.set(SHOP_GUI_OPTION + "goto-last-page-button-material", shopGuiGotoLastPageButtonMaterial);
                configFileConfiguration.set(SHOP_GUI_OPTION + "goto-last-page-button-text", shopGuiGotoLastPageButtonText);
                configFileConfiguration.set(SHOP_GUI_OPTION + SHOP_GUI_NAVIGATION_OPTION + "first-page-alert-message", shopNavFirstPageAlertMsg);
                configFileConfiguration.set(SHOP_GUI_OPTION + SHOP_GUI_NAVIGATION_OPTION + "last-page-alert-message", shopNavLastPageAlertMsg);
                configFileConfiguration.set(SHOP_GUI_OPTION + SHOP_GUI_CMD_OPTION + "back-button-custom-model-data", shopGuiBackButtonCMD);
                configFileConfiguration.set(SHOP_GUI_OPTION + SHOP_GUI_CMD_OPTION + "next-button-custom-model-data", shopGuiNextButtonCMD);
                configFileConfiguration.set(SHOP_GUI_OPTION + SHOP_GUI_CMD_OPTION + "close-button-custom-model-data", shopGuiCloseButtonCMD);
                configFileConfiguration.set(SHOP_GUI_OPTION + SHOP_GUI_CMD_OPTION + "goto-first-page-button-custom-model-data", shopGuiGotoFirstPageButtonCMD);
                configFileConfiguration.set(SHOP_GUI_OPTION + SHOP_GUI_CMD_OPTION + "goto-last-page-button-custom-model-data", shopGuiGotoLastPageButtonCMD);
            }

            // AT LAST
            // Moving debug-mode and config-version to the last
            final String DEBUG_MODE_OPTION = "debug-mode";
            final String CONFIG_VERSION_OPTION = "config-version";
            boolean userDefinedDebugMode = configFileConfiguration.getBoolean(DEBUG_MODE_OPTION);
            configFileConfiguration.set(DEBUG_MODE_OPTION, null);
            configFileConfiguration.set(DEBUG_MODE_OPTION, userDefinedDebugMode);
            configFileConfiguration.set(CONFIG_VERSION_OPTION, null);
            configFileConfiguration.set(CONFIG_VERSION_OPTION, CURRENT_CONFIG_VERSION);
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
