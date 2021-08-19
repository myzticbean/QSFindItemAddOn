package me.ronsane.finditemaddon.finditemaddon.ConfigProvider;

import me.ronsane.finditemaddon.finditemaddon.FindItemAddOn;
import me.ronsane.finditemaddon.finditemaddon.Utils.CommonUtils;
import me.ronsane.finditemaddon.finditemaddon.Utils.LoggerUtils;

import java.util.List;

public class ConfigProvider {

    public final String PLUGIN_PREFIX = CommonUtils.parseColors(FindItemAddOn.getInstance().getConfig().getString("plugin-prefix"));
    public final String FIND_ITEM_CMD_INCORRECT_USAGE_MSG = FindItemAddOn.getInstance().getConfig().getString("find-item-command.incorrect-usage-message");
    public final String FIND_ITEM_CMD_INVALID_MATERIAL_MSG = FindItemAddOn.getInstance().getConfig().getString("find-item-command.invalid-material-message");
    public final String SHOP_SEARCH_LOADING_MSG = FindItemAddOn.getInstance().getConfig().getString("find-item-command.shop-search-loading-message");
    public final String NO_SHOP_FOUND_MSG = FindItemAddOn.getInstance().getConfig().getString("find-item-command.no-shop-found-message");
    public final String FIND_ITEM_CMD_NO_PERMISSION_MSG = FindItemAddOn.getInstance().getConfig().getString("find-item-command.find-item-command-no-permission-message");
    public final int SHOP_SORTING_METHOD = FindItemAddOn.getInstance().getConfig().getInt("shop-sorting-method");
    public final List<String> SHOP_GUI_ITEM_LORE = (List<String>) FindItemAddOn.getInstance().getConfig().getList("shop-gui-item-lore");
    public final Boolean ALLOW_DIRECT_SHOP_TP = FindItemAddOn.getInstance().getConfig().getBoolean("allow-direct-shop-tp");
    public final String CLICK_TO_TELEPORT_MSG = FindItemAddOn.getInstance().getConfig().getString("click-to-teleport-message");
    public final String SHOP_TP_NO_PERMISSION_MSG = FindItemAddOn.getInstance().getConfig().getString("shop-tp-no-permission-message");
    public final String UNSAFE_SHOP_AREA_MSG = FindItemAddOn.getInstance().getConfig().getString("unsafe-shop-area-message");
    public final String SHOP_GUI_BACK_BUTTON_MATERIAL = FindItemAddOn.getInstance().getConfig().getString("shop-gui-back-button-material");
    public final String SHOP_GUI_BACK_BUTTON_TEXT = FindItemAddOn.getInstance().getConfig().getString("shop-gui-back-button-text");
    public final String SHOP_GUI_NEXT_BUTTON_MATERIAL = FindItemAddOn.getInstance().getConfig().getString("shop-gui-next-button-material");
    public final String SHOP_GUI_NEXT_BUTTON_TEXT = FindItemAddOn.getInstance().getConfig().getString("shop-gui-next-button-text");
    public final String SHOP_GUI_FILLER_ITEM = FindItemAddOn.getInstance().getConfig().getString("shop-gui-filler-item");
    public final String SHOP_GUI_CLOSE_BUTTON_MATERIAL = FindItemAddOn.getInstance().getConfig().getString("shop-gui-close-button-material");
    public final String SHOP_GUI_CLOSE_BUTTON_TEXT = FindItemAddOn.getInstance().getConfig().getString("shop-gui-close-button-text");
    public final String SHOP_GUI_BACK_BUTTON_CMD = FindItemAddOn.getInstance().getConfig().getString("shop-gui-back-button-custom-model-data");
    public final String SHOP_GUI_NEXT_BUTTON_CMD = FindItemAddOn.getInstance().getConfig().getString("shop-gui-next-button-custom-model-data");
    public final String SHOP_GUI_CLOSE_BUTTON_CMD = FindItemAddOn.getInstance().getConfig().getString("shop-gui-close-button-custom-model-data");
    public final String SHOP_NAV_FIRST_PAGE_ALERT_MSG = FindItemAddOn.getInstance().getConfig().getString("shop-navigation-first-page-alert-message");
    public final String SHOP_NAV_LAST_PAGE_ALERT_MSG = FindItemAddOn.getInstance().getConfig().getString("shop-navigation-last-page-alert-message");
    public final Boolean DEBUG_MODE = FindItemAddOn.getInstance().getConfig().getBoolean("debug-mode");

    public ConfigProvider() {
        LoggerUtils.logInfo("Config loaded!");
    }

}
