package io.myzticbean.finditemaddon.ConfigUtil;

import io.myzticbean.finditemaddon.Utils.LoggerUtils;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class ConfigProvider {

    public final String PLUGIN_PREFIX = ColorTranslator.translateColorCodes(ConfigSetup.get().getString("plugin-prefix"));
    public final List<String> FIND_ITEM_COMMAND_ALIAS = (List<String>) ConfigSetup.get().getList("find-item-command.command-alias");
    public final String FIND_ITEM_TO_BUY_AUTOCOMPLETE = ConfigSetup.get().getString("find-item-command.to-buy-autocomplete");
    public final String FIND_ITEM_TO_SELL_AUTOCOMPLETE = ConfigSetup.get().getString("find-item-command.to-sell-autocomplete");
    public final String FIND_ITEM_HIDESHOP_AUTOCOMPLETE = ConfigSetup.get().getString("find-item-command.hideshop-autocomplete");
    public final String FIND_ITEM_REVEALSHOP_AUTOCOMPLETE = ConfigSetup.get().getString("find-item-command.revealshop-autocomplete");
    public final String FIND_ITEM_CMD_INCORRECT_USAGE_MSG = ConfigSetup.get().getString("find-item-command.incorrect-usage-message");
    public final String FIND_ITEM_CMD_INVALID_MATERIAL_MSG = ConfigSetup.get().getString("find-item-command.invalid-material-message");
    public final String FIND_ITEM_CMD_HIDING_SHOP_OWNER_INVALID_MSG = ConfigSetup.get().getString("find-item-command.hiding-shop-owner-invalid-message");
    public final String SHOP_SEARCH_LOADING_MSG = ConfigSetup.get().getString("find-item-command.shop-search-loading-message");
    public final String NO_SHOP_FOUND_MSG = ConfigSetup.get().getString("find-item-command.no-shop-found-message");
    public final String FIND_ITEM_CMD_NO_PERMISSION_MSG = ConfigSetup.get().getString("find-item-command.find-item-command-no-permission-message");
    public final String FIND_ITEM_CMD_SHOP_HIDE_SUCCESS_MSG = ConfigSetup.get().getString("find-item-command.shop-hide-success-message");
    public final String FIND_ITEM_CMD_SHOP_REVEAL_SUCCESS_MSG = ConfigSetup.get().getString("find-item-command.shop-reveal-success-message");
    public final String FIND_ITEM_CMD_SHOP_ALREADY_HIDDEN_MSG = ConfigSetup.get().getString("find-item-command.shop-already-hidden-message");
    public final String FIND_ITEM_CMD_SHOP_ALREADY_PUBLIC_MSG = ConfigSetup.get().getString("find-item-command.shop-already-public-message");
    public final String FIND_ITEM_CMD_INVALID_SHOP_BLOCK_MSG = ConfigSetup.get().getString("find-item-command.invalid-shop-block-message");
    public final int SHOP_SORTING_METHOD = ConfigSetup.get().getInt("shop-sorting-method");
    public final boolean SEARCH_LOADED_SHOPS_ONLY = ConfigSetup.get().getBoolean("search-loaded-shops-only");
    public final String SHOP_SEARCH_GUI_TITLE = ConfigSetup.get().getString("shop-search-gui-title");
    public final int NEAREST_WARP_MODE = ConfigSetup.get().getInt("nearest-warp-mode");
    public final List<String> SHOP_GUI_ITEM_LORE = (List<String>) ConfigSetup.get().getList("shop-gui-item-lore");
    public final boolean TP_PLAYER_DIRECTLY_TO_SHOP = ConfigSetup.get().getBoolean("player-shop-teleportation.direct-shop-tp-mode.tp-player-directly-to-shop");
    public final String CLICK_TO_TELEPORT_MSG = ConfigSetup.get().getString("player-shop-teleportation.direct-shop-tp-mode.click-to-teleport-message");
    public final String SHOP_TP_NO_PERMISSION_MSG = ConfigSetup.get().getString("player-shop-teleportation.direct-shop-tp-mode.shop-tp-no-permission-message");
    public final String UNSAFE_SHOP_AREA_MSG = ConfigSetup.get().getString("player-shop-teleportation.direct-shop-tp-mode.unsafe-shop-area-message");
    public final String TP_DELAY_IN_SECONDS = ConfigSetup.get().getString("player-shop-teleportation.direct-shop-tp-mode.tp-delay-in-seconds");
    public final String TP_DELAY_MESSAGE = ConfigSetup.get().getString("player-shop-teleportation.direct-shop-tp-mode.tp-delay-message");
    public final boolean TP_PLAYER_TO_NEAREST_WARP = ConfigSetup.get().getBoolean("player-shop-teleportation.nearest-warp-tp-mode.tp-player-to-nearest-warp");
    public final String SHOP_GUI_BACK_BUTTON_MATERIAL = ConfigSetup.get().getString("shop-gui-back-button-material");
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
    public final int SHOP_PLAYER_VISIT_COOLDOWN_IN_MINUTES = ConfigSetup.get().getInt("shop-player-visit-cooldown-in-minutes");
    public final boolean IGNORE_ADMIN_SHOPS = ConfigSetup.get().getBoolean("ignore-admin-shops");
    public final boolean IGNORE_EMPTY_CHESTS = ConfigSetup.get().getBoolean("ignore-empty-chests");
    public final String SHOP_GUI_GOTO_FIRST_PAGE_BUTTON_MATERIAL = ConfigSetup.get().getString("shop-gui-goto-first-page-button-material");
    public final String SHOP_GUI_GOTO_FIRST_PAGE_BUTTON_TEXT = ConfigSetup.get().getString("shop-gui-goto-first-page-button-text");
    public final String SHOP_GUI_GOTO_LAST_PAGE_BUTTON_MATERIAL = ConfigSetup.get().getString("shop-gui-goto-last-page-button-material");
    public final String SHOP_GUI_GOTO_LAST_PAGE_BUTTON_TEXT = ConfigSetup.get().getString("shop-gui-goto-last-page-button-text");
    public final String SHOP_GUI_GOTO_FIRST_PAGE_BUTTON_CMD = ConfigSetup.get().getString("shop-gui-goto-first-page-button-custom-model-data");
    public final String SHOP_GUI_GOTO_LAST_PAGE_BUTTON_CMD = ConfigSetup.get().getString("shop-gui-goto-last-page-button-custom-model-data");
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
