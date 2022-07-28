package io.myzticbean.finditemaddon.Utils.Defaults;

/**
 * List of all the permissions used by the addon
 * @author ronsane
 */
public enum PlayerPerms {
    FINDITEM_ADMIN("finditem.admin"),
    FINDITEM_USE("finditem.use"),
    FINDITEM_HIDESHOP("finditem.hideshop"),
    FINDITEM_RELOAD("finditem.reload"),
    FINDITEM_RESTART("finditem.restart"),
    FINDITEM_SHOPTP("finditem.shoptp"),
    FINDITEM_VIEWSHOPSTATS("finditem.viewshopstats"),
    FINDITEM_VIEWSHOPSTATS_OTHERS("finditem.viewshopstats.others");

    private final String permName;

    PlayerPerms(String perm) {
        permName = perm;
    }

    public String value() {
        return permName;
    }
}
