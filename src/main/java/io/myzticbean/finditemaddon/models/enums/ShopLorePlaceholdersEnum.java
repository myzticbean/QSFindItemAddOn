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
package io.myzticbean.finditemaddon.models.enums;

/**
 * List of all the placeholders used in the Shop lore in GUI
 * @author myzticbean
 */
public enum ShopLorePlaceholdersEnum {
    ITEM_PRICE("{ITEM_PRICE}"),
    SHOP_STOCK("{SHOP_STOCK}"),
    SHOP_OWNER("{SHOP_OWNER}"),
    SHOP_PER_ITEM_QTY("{ITEM_STACK_SIZE}"),
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
