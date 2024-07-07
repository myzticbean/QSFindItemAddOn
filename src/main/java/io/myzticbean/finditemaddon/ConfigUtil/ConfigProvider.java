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
package io.myzticbean.finditemaddon.ConfigUtil;

import io.myzticbean.finditemaddon.Utils.LoggerUtils;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @author myzticbean
 */
public class ConfigProvider {

    private static final String FIND_ITEM_COMMAND = "find-item-command.";
    private static final String PLAYER_SHOP_TELEPORTATION = "player-shop-teleportation.";
    private static final String DIRECT_SHOP_TP_MODE = "direct-shop-tp-mode.";
    private static final String NEAREST_WARP_TP_MODE = "nearest-warp-tp-mode.";
    private static final String SHOP_GUI = "shop-gui.";
    private static final String SHOP_GUI_NAVIGATION = "shop-navigation.";
    private static final String SHOP_GUI_CMD = "custom-model-data.";
    public final String PLUGIN_PREFIX = ColorTranslator.translateColorCodes(ConfigSetup.get().getString("plugin-prefix"));
    public final List<String> FIND_ITEM_COMMAND_ALIAS = (List<String>) ConfigSetup.get().getList(FIND_ITEM_COMMAND + "command-alias");
    public final String FIND_ITEM_TO_BUY_AUTOCOMPLETE = ConfigSetup.get().getString(FIND_ITEM_COMMAND + "to-buy-autocomplete");
    public final String FIND_ITEM_TO_SELL_AUTOCOMPLETE = ConfigSetup.get().getString(FIND_ITEM_COMMAND + "to-sell-autocomplete");
    public final String FIND_ITEM_HIDESHOP_AUTOCOMPLETE = ConfigSetup.get().getString(FIND_ITEM_COMMAND + "hideshop-autocomplete");
    public final String FIND_ITEM_REVEALSHOP_AUTOCOMPLETE = ConfigSetup.get().getString(FIND_ITEM_COMMAND + "revealshop-autocomplete");
    public final String FIND_ITEM_CMD_INCORRECT_USAGE_MSG = ConfigSetup.get().getString(FIND_ITEM_COMMAND + "incorrect-usage-message");
    public final String FIND_ITEM_CMD_INVALID_MATERIAL_MSG = ConfigSetup.get().getString(FIND_ITEM_COMMAND + "invalid-material-message");
    public final String SHOP_SEARCH_LOADING_MSG = ConfigSetup.get().getString(FIND_ITEM_COMMAND + "shop-search-loading-message");
    public final String NO_SHOP_FOUND_MSG = ConfigSetup.get().getString(FIND_ITEM_COMMAND + "no-shop-found-message");
    public final String FIND_ITEM_CMD_NO_PERMISSION_MSG = ConfigSetup.get().getString(FIND_ITEM_COMMAND + "find-item-command-no-permission-message");
    public final String FIND_ITEM_CMD_SHOP_HIDE_SUCCESS_MSG = ConfigSetup.get().getString(FIND_ITEM_COMMAND + "shop-hide-success-message");
    public final String FIND_ITEM_CMD_SHOP_REVEAL_SUCCESS_MSG = ConfigSetup.get().getString(FIND_ITEM_COMMAND + "shop-reveal-success-message");
    public final String FIND_ITEM_CMD_SHOP_ALREADY_HIDDEN_MSG = ConfigSetup.get().getString(FIND_ITEM_COMMAND + "shop-already-hidden-message");
    public final String FIND_ITEM_CMD_SHOP_ALREADY_PUBLIC_MSG = ConfigSetup.get().getString(FIND_ITEM_COMMAND + "shop-already-public-message");
    public final String FIND_ITEM_CMD_INVALID_SHOP_BLOCK_MSG = ConfigSetup.get().getString(FIND_ITEM_COMMAND + "invalid-shop-block-message");
    public final String FIND_ITEM_CMD_HIDING_SHOP_OWNER_INVALID_MSG = ConfigSetup.get().getString(FIND_ITEM_COMMAND + "hiding-shop-owner-invalid-message");
    public final boolean FIND_ITEM_CMD_REMOVE_HIDE_REVEAL_SUBCMDS = ConfigSetup.get().getBoolean(FIND_ITEM_COMMAND + "remove-hideshop-revealshop-subcommands");
    public final boolean FIND_ITEM_CMD_DISABLE_SEARCH_ALL_SHOPS = ConfigSetup.get().getBoolean(FIND_ITEM_COMMAND + "disable-search-all-shops");
    public final int SHOP_SORTING_METHOD = ConfigSetup.get().getInt("shop-sorting-method");
    public final boolean SEARCH_LOADED_SHOPS_ONLY = ConfigSetup.get().getBoolean("search-loaded-shops-only");
    public final String SHOP_SEARCH_GUI_TITLE = ConfigSetup.get().getString("shop-search-gui-title");
    public final int NEAREST_WARP_MODE = ConfigSetup.get().getInt("nearest-warp-mode");
    public final List<String> SHOP_GUI_ITEM_LORE = (List<String>) ConfigSetup.get().getList("shop-gui-item-lore");
    public final boolean TP_PLAYER_DIRECTLY_TO_SHOP = ConfigSetup.get().getBoolean(PLAYER_SHOP_TELEPORTATION + DIRECT_SHOP_TP_MODE + "tp-player-directly-to-shop");
    public final String CLICK_TO_TELEPORT_MSG = ConfigSetup.get().getString(PLAYER_SHOP_TELEPORTATION + DIRECT_SHOP_TP_MODE + "click-to-teleport-message");
    public final String SHOP_TP_NO_PERMISSION_MSG = ConfigSetup.get().getString(PLAYER_SHOP_TELEPORTATION + DIRECT_SHOP_TP_MODE + "shop-tp-no-permission-message");
    public final String UNSAFE_SHOP_AREA_MSG = ConfigSetup.get().getString(PLAYER_SHOP_TELEPORTATION + DIRECT_SHOP_TP_MODE + "unsafe-shop-area-message");
    public final String TP_DELAY_IN_SECONDS = ConfigSetup.get().getString(PLAYER_SHOP_TELEPORTATION + DIRECT_SHOP_TP_MODE + "tp-delay-in-seconds");
    public final String TP_DELAY_MESSAGE = ConfigSetup.get().getString(PLAYER_SHOP_TELEPORTATION + DIRECT_SHOP_TP_MODE + "tp-delay-message");
    public final String TP_TO_OWN_SHOP_NO_PERMISSION_MESSAGE = ConfigSetup.get().getString(PLAYER_SHOP_TELEPORTATION + DIRECT_SHOP_TP_MODE + "tp-to-own-shop-no-permission-message");
    public final boolean TP_PLAYER_TO_NEAREST_WARP = ConfigSetup.get().getBoolean(PLAYER_SHOP_TELEPORTATION + NEAREST_WARP_TP_MODE + "tp-player-to-nearest-warp");
    public final boolean DO_NOT_TP_IF_PLAYER_WARP_LOCKED = ConfigSetup.get().getBoolean(PLAYER_SHOP_TELEPORTATION + NEAREST_WARP_TP_MODE + "do-not-tp-if-warp-locked");
    public final boolean ONLY_SHOW_PLAYER_OWNDED_WARPS = ConfigSetup.get().getBoolean(PLAYER_SHOP_TELEPORTATION + NEAREST_WARP_TP_MODE + "only-show-player-owned-warps");
    /*public final String SHOP_GUI_BACK_BUTTON_MATERIAL = ConfigSetup.get().getString("shop-gui-back-button-material");
    public final String SHOP_GUI_BACK_BUTTON_TEXT = ConfigSetup.get().getString("shop-gui-back-button-text");
    public final String SHOP_GUI_NEXT_BUTTON_MATERIAL = ConfigSetup.get().getString("shop-gui-next-button-material");
    public final String SHOP_GUI_NEXT_BUTTON_TEXT = ConfigSetup.get().getString("shop-gui-next-button-text");
    public final String SHOP_GUI_FILLER_ITEM = ConfigSetup.get().getString("shop-gui-filler-item");
    public final String SHOP_GUI_CLOSE_BUTTON_MATERIAL = ConfigSetup.get().getString("shop-gui-close-button-material");
    public final String SHOP_GUI_CLOSE_BUTTON_TEXT = ConfigSetup.get().getString("shop-gui-close-button-text");
    public final String SHOP_GUI_BACK_BUTTON_CMD = ConfigSetup.get().getString("shop-gui-back-button-custom-model-data");
    public final String SHOP_GUI_NEXT_BUTTON_CMD = ConfigSetup.get().getString("shop-gui-next-button-custom-model-data");
    public final String SHOP_GUI_CLOSE_BUTTON_CMD = ConfigSetup.get().getString("shop-gui-close-button-custom-model-data");
    public final String SHOP_NAV_FIRST_PAGE_ALERT_MSG = ConfigSetup.get().getString("shop-navigation-first-page-alert-message");
    public final String SHOP_NAV_LAST_PAGE_ALERT_MSG = ConfigSetup.get().getString("shop-navigation-last-page-alert-message");
    public final String SHOP_GUI_GOTO_FIRST_PAGE_BUTTON_MATERIAL = ConfigSetup.get().getString("shop-gui-goto-first-page-button-material");
    public final String SHOP_GUI_GOTO_FIRST_PAGE_BUTTON_TEXT = ConfigSetup.get().getString("shop-gui-goto-first-page-button-text");
    public final String SHOP_GUI_GOTO_LAST_PAGE_BUTTON_MATERIAL = ConfigSetup.get().getString("shop-gui-goto-last-page-button-material");
    public final String SHOP_GUI_GOTO_LAST_PAGE_BUTTON_TEXT = ConfigSetup.get().getString("shop-gui-goto-last-page-button-text");
    public final String SHOP_GUI_GOTO_FIRST_PAGE_BUTTON_CMD = ConfigSetup.get().getString("shop-gui-goto-first-page-button-custom-model-data");
    public final String SHOP_GUI_GOTO_LAST_PAGE_BUTTON_CMD = ConfigSetup.get().getString("shop-gui-goto-last-page-button-custom-model-data");*/
    public final String SHOP_GUI_BACK_BUTTON_MATERIAL = ConfigSetup.get().getString(SHOP_GUI + "back-button-material");
    public final String SHOP_GUI_BACK_BUTTON_TEXT = ConfigSetup.get().getString(SHOP_GUI + "back-button-text");
    public final String SHOP_GUI_NEXT_BUTTON_MATERIAL = ConfigSetup.get().getString(SHOP_GUI + "next-button-material");
    public final String SHOP_GUI_NEXT_BUTTON_TEXT = ConfigSetup.get().getString(SHOP_GUI + "next-button-text");
    public final String SHOP_GUI_FILLER_ITEM = ConfigSetup.get().getString(SHOP_GUI + "filler-item");
    public final String SHOP_GUI_CLOSE_BUTTON_MATERIAL = ConfigSetup.get().getString(SHOP_GUI + "close-button-material");
    public final String SHOP_GUI_CLOSE_BUTTON_TEXT = ConfigSetup.get().getString(SHOP_GUI + "close-button-text");
    public final String SHOP_GUI_GOTO_FIRST_PAGE_BUTTON_MATERIAL = ConfigSetup.get().getString(SHOP_GUI + "goto-first-page-button-material");
    public final String SHOP_GUI_GOTO_FIRST_PAGE_BUTTON_TEXT = ConfigSetup.get().getString(SHOP_GUI + "goto-first-page-button-text");
    public final String SHOP_GUI_GOTO_LAST_PAGE_BUTTON_MATERIAL = ConfigSetup.get().getString(SHOP_GUI + "goto-last-page-button-material");
    public final String SHOP_GUI_GOTO_LAST_PAGE_BUTTON_TEXT = ConfigSetup.get().getString(SHOP_GUI + "goto-last-page-button-text");
    public final String SHOP_GUI_BACK_BUTTON_CMD = ConfigSetup.get().getString(SHOP_GUI + SHOP_GUI_CMD + "back-button-custom-model-data");
    public final String SHOP_GUI_NEXT_BUTTON_CMD = ConfigSetup.get().getString(SHOP_GUI + SHOP_GUI_CMD + "next-button-custom-model-data");
    public final String SHOP_GUI_CLOSE_BUTTON_CMD = ConfigSetup.get().getString(SHOP_GUI + SHOP_GUI_CMD + "close-button-custom-model-data");
    public final String SHOP_GUI_GOTO_FIRST_PAGE_BUTTON_CMD = ConfigSetup.get().getString(SHOP_GUI + SHOP_GUI_CMD + "goto-first-page-button-custom-model-data");
    public final String SHOP_GUI_GOTO_LAST_PAGE_BUTTON_CMD = ConfigSetup.get().getString(SHOP_GUI + SHOP_GUI_CMD + "goto-last-page-button-custom-model-data");
    public final String SHOP_NAV_FIRST_PAGE_ALERT_MSG = ConfigSetup.get().getString(SHOP_GUI + SHOP_GUI_NAVIGATION + "first-page-alert-message");
    public final String SHOP_NAV_LAST_PAGE_ALERT_MSG = ConfigSetup.get().getString(SHOP_GUI + SHOP_GUI_NAVIGATION + "last-page-alert-message");
    public final boolean SHOP_GUI_USE_SHORTER_CURRENCY_FORMAT = ConfigSetup.get().getBoolean(SHOP_GUI + "use-shorter-currency-format");
    public final int SHOP_PLAYER_VISIT_COOLDOWN_IN_MINUTES = ConfigSetup.get().getInt("shop-player-visit-cooldown-in-minutes");
    public final boolean IGNORE_EMPTY_CHESTS = ConfigSetup.get().getBoolean("ignore-empty-chests");
    public final List<String> BLACKLISTED_WORLDS = (List<String>) ConfigSetup.get().getList("blacklisted-worlds");
    public final boolean DEBUG_MODE = ConfigSetup.get().getBoolean("debug-mode");
    public final int CONFIG_VERSION = ConfigSetup.get().getInt("config-version");

    private final List<World> blacklistedWorldsList = new ArrayList<>();

    public ConfigProvider() {
        if(BLACKLISTED_WORLDS != null) {
            BLACKLISTED_WORLDS.forEach(world -> {
                World worldObj = Bukkit.getWorld(world);
                if(worldObj != null) {
                    blacklistedWorldsList.add(worldObj);
                }
            });
        }
        LoggerUtils.logInfo("Config loaded!");
    }

    public List<World> getBlacklistedWorlds() {
        return blacklistedWorldsList;
    }

    public boolean shopGUIItemLoreHasKey(String key) {
        if (SHOP_GUI_ITEM_LORE != null) {
            return SHOP_GUI_ITEM_LORE.stream().anyMatch(key::contains);
        }
        else {
            return false;
        }
    }
}
