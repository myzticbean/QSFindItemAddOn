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

/**
 * @author myzticbean
 */
public enum CustomCmdPlaceholdersEnum {

    PLAYER_NAME("{PLAYER_NAME}"),
    SHOP_LOC_X("{SHOP_LOC_X}"),
    SHOP_LOC_Y("{SHOP_LOC_Y}"),
    SHOP_LOC_Z("{SHOP_LOC_Z}");

    private final String placeholder;

    CustomCmdPlaceholdersEnum(String placeholder) {
        this.placeholder = placeholder;
    }

    public String value() {
        return placeholder;
    }

}
