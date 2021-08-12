package me.ronsane.finditemaddon.finditemaddon.ConfigHandler;

import me.ronsane.finditemaddon.finditemaddon.FindItemAddOn;
import me.ronsane.finditemaddon.finditemaddon.Utils.CommonUtils;

import java.util.List;

public enum ConfigHandler {
    PLUGIN_PREFIX(FindItemAddOn.getInstance().getConfig().getString("plugin-prefix")),;
//    FIND_ITEM_CMD_INCORRECT_USAGE_MSG(FindItemAddOn.getInstance().getConfig().getString("find-item-command.incorrect-usage-message")),
//    FIND_ITEM_CMD_INVALID_MATERIAL_MSG(FindItemAddOn.getInstance().getConfig().getString("find-item-command.invalid-material-message")),
//    SHOP_SEARCH_LOADING_MSG(FindItemAddOn.getInstance().getConfig().getString("find-item-command.shop-search-loading-message")),
//    NO_SHOP_FOUND_MSG(FindItemAddOn.getInstance().getConfig().getString("find-item-command.no-shop-found-message")),
//    FIND_ITEM_CMD_NO_PERMISSION_MSG(FindItemAddOn.getInstance().getConfig().getString("find-item-command.find-item-command-no-permission-message")),
//    SHOP_SORTING_METHOD(FindItemAddOn.getInstance().getConfig().getInt("shop-sorting-method")),
//    SHOP_GUI_SHOW_ITEM_ENCHANTS(FindItemAddOn.getInstance().getConfig().getBoolean("shop-gui-show-item-enchants")),
//    SHOP_GUI_SHOW_ITEM_POTION_EFFECTS(FindItemAddOn.getInstance().getConfig().getBoolean("shop-gui-show-item-potion-effects")),
//    SHOP_GUI_ITEM_LORE((List<String>)FindItemAddOn.getInstance().getConfig().getList("shop-gui-item-lore")),
//    ALLOW_DIRECT_SHOP_TP(FindItemAddOn.getInstance().getConfig().getBoolean("allow-direct-shop-tp")),
//    CLICK_TO_TELEPORT_MSG(FindItemAddOn.getInstance().getConfig().getString("click-to-teleport-message")),
//    SHOP_TP_NO_PERMISSION_MSG(FindItemAddOn.getInstance().getConfig().getString("shop-tp-no-permission-message")),
//    UNSAFE_SHOP_AREA_MSG(FindItemAddOn.getInstance().getConfig().getString("unsafe-shop-area-message")),
//    SHOP_GUI_BACK_BUTTON_MATERIAL(FindItemAddOn.getInstance().getConfig().getString("shop-gui-back-button-material")),
//    SHOP_GUI_BACK_BUTTON_TEXT(FindItemAddOn.getInstance().getConfig().getString("shop-gui-back-button-text")),
//    SHOP_GUI_NEXT_BUTTON_MATERIAL(FindItemAddOn.getInstance().getConfig().getString("shop-gui-next-button-material")),
//    SHOP_GUI_NEXT_BUTTON_TEXT(FindItemAddOn.getInstance().getConfig().getString("shop-gui-next-button-text")),
//    SHOP_GUI_FILLER_ITEM(FindItemAddOn.getInstance().getConfig().getString("shop-gui-filler-item")),
//    SHOP_GUI_CLOSE_BUTTON_MATERIAL(FindItemAddOn.getInstance().getConfig().getString("shop-gui-close-button-material")),
//    SHOP_GUI_CLOSE_BUTTON_TEXT(FindItemAddOn.getInstance().getConfig().getString("shop-gui-close-button-text")),
//    SHOP_GUI_BACK_BUTTON_CMD(FindItemAddOn.getInstance().getConfig().getString("shop-gui-back-button-custom-model-data")),
//    SHOP_GUI_NEXT_BUTTON_CMD(FindItemAddOn.getInstance().getConfig().getString("shop-gui-next-button-custom-model-data")),
//    SHOP_GUI_CLOSE_BUTTON_CMD(FindItemAddOn.getInstance().getConfig().getString("shop-gui-close-button-custom-model-data")),
//    DEBUG_MODE(FindItemAddOn.getInstance().getConfig().getBoolean("debug-mode"));

    private String msg;
    private Boolean booleanValue;
    private int integerValue;
    private List<String> listItems;

    ConfigHandler(String msg) {
        this.msg = msg;
    }
    ConfigHandler(Boolean value) {
        this.booleanValue = value;
    }
    ConfigHandler(int value) { this.integerValue = value; }
    ConfigHandler(List<String> listItems) { this.listItems = listItems; }

    public String getString() {
        return CommonUtils.parseColors(this.msg);
    }
    public Boolean getBoolean() { return this.booleanValue; }
    public int getInt() { return this.integerValue; }
    public List<String> getListItems() { return this.listItems; }
}
