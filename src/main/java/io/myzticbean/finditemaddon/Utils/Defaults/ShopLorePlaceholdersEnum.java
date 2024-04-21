package io.myzticbean.finditemaddon.Utils.Defaults;

/**
 * List of all the placeholds used in the Shop lore in GUI
 * @author myzticbean
 */
public enum ShopLorePlaceholdersEnum {
    ITEM_PRICE("{ITEM_PRICE}"),
    SHOP_STOCK("{SHOP_STOCK}"),
    SHOP_OWNER("{SHOP_OWNER}"),
    SHOP_LOCATION("{SHOP_LOC}"),
    SHOP_WORLD("{SHOP_WORLD}"),
    NEAREST_WARP("{NEAREST_WARP}"),
    SHOP_VISITS("{SHOP_VISITS}");
    private final String placeholder;

    ShopLorePlaceholdersEnum(String placeholder) {
        this.placeholder = placeholder;
    }
    public String value() {return placeholder;}
}
