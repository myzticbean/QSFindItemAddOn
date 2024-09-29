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
package io.myzticbean.finditemaddon.utils.enums;

import org.bukkit.entity.Player;

/**
 * List of all the permissions used by the addon
 * @author myzticbean
 */
public enum PlayerPermsEnum {
    FINDITEM_ADMIN("finditem.admin"),
    FINDITEM_USE("finditem.use"),
    FINDITEM_HIDESHOP("finditem.hideshop"),
    FINDITEM_RELOAD("finditem.reload"),

    @Deprecated
    FINDITEM_RESTART("finditem.restart"),

    FINDITEM_SHOPTP("finditem.shoptp"),
    FINDITEM_SHOPTP_OWN("finditem.shoptp.own"),
    FINDITEM_SHOPTP_BYPASS_DELAY("finditem.shoptp-delay.bypass"),
    FINDITEM_SHOPTP_BYPASS_SAFETYCHECK("finditem.shoptp.bypass-safetycheck");

    private final String permName;

    PlayerPermsEnum(String perm) {
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
