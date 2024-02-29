package io.myzticbean.finditemaddon.Utils.Defaults;

import org.bukkit.entity.Player;

/**
 * List of all the permissions used by the addon
 * @author myzticbean
 */
public enum PlayerPerms {
    FINDITEM_ADMIN("finditem.admin"),
    FINDITEM_USE("finditem.use"),
    FINDITEM_HIDESHOP("finditem.hideshop"),
    FINDITEM_RELOAD("finditem.reload"),

    @Deprecated
    FINDITEM_RESTART("finditem.restart"),
    FINDITEM_SHOPTP("finditem.shoptp"),
    FINDITEM_SHOPTP_OWN("finditem.shoptp.own"),
    FINDITEM_SHOPTP_BYPASS_DELAY("finditem.shoptp-delay.bypass");

    private final String permName;

    PlayerPerms(String perm) {
        permName = perm;
    }

    public String value() {
        return permName;
    }

    public static boolean isAdmin(Player p) {
        return p.hasPermission(FINDITEM_ADMIN.permName);
    }

    public static boolean hasShopTpDelayBypassPermOrAdmin(Player p) {
        return (isAdmin(p) || hasShopTpDelayBypassPerm(p));
    }

    public static boolean hasShopTpDelayBypassPerm(Player p) {
        return p.hasPermission(FINDITEM_SHOPTP_BYPASS_DELAY.permName);
    }

    public static boolean canPlayerTpToOwnShop(Player p) {
        return p.hasPermission(FINDITEM_SHOPTP_OWN.permName);
    }
}
