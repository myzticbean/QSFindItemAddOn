package io.myzticbean.finditemaddon.Utils;

public enum PlayerPerms {
    FINDITEM_ADMIN("finditem.admin"),
    FINDITEM_USE("finditem.use"),
    FINDITEM_HIDESHOP("finditem.hideshop"),
    FINDITEM_RELOAD("finditem.reload"),
    FINDITEM_RESTART("finditem.restart"),
    FINDITEM_SHOPTP("finditem.shoptp");

    private final String permName;

    PlayerPerms(String perm) {
        permName = perm;
    }

    @Override
    public String toString() {
        return permName;
    }
}
