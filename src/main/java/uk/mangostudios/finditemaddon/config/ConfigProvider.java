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
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package uk.mangostudios.finditemaddon.config;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class ConfigProvider {

    private static final String FIND_ITEM_COMMAND = "find-item-command.";
    private static final String SHOP_GUI = "shop-gui.";
    public final String PLUGIN_PREFIX = ConfigManager.get().getString("plugin-prefix");

    // Messages
    public final String SHOP_SEARCH_LOADING_MSG = ConfigManager.get().getString(FIND_ITEM_COMMAND + "shop-search-loading-message");
    public final String NO_SHOP_FOUND_MSG = ConfigManager.get().getString(FIND_ITEM_COMMAND + "no-shop-found-message");
    public final String SHOP_TP_BANNED_MSG = ConfigManager.get().getString(FIND_ITEM_COMMAND + "shop-tp-banned-message");
    public final String QUERY_TOO_SHORT_MSG = ConfigManager.get().getString(FIND_ITEM_COMMAND + "query-too-short-message");
    public final String NOT_LOOKING_AT_SHOP_MSG = ConfigManager.get().getString(FIND_ITEM_COMMAND + "not-looking-at-shop-message");
    public final String HIDDEN_SHOP_MSG = ConfigManager.get().getString(FIND_ITEM_COMMAND + "hidden-shop-message");
    public final String NOT_YOUR_SHOP_MSG = ConfigManager.get().getString(FIND_ITEM_COMMAND + "not-your-shop-message");
    public final String UNHIDDEN_SHOP_MSG = ConfigManager.get().getString(FIND_ITEM_COMMAND + "unhidden-shop-message");

    // GUI
    public final int SHOP_SORTING_METHOD = ConfigManager.get().getInt("shop-sorting-method");
    public final boolean SEARCH_LOADED_SHOPS_ONLY = ConfigManager.get().getBoolean("search-loaded-shops-only");
    public final String SHOP_SEARCH_GUI_TITLE = ConfigManager.get().getString("shop-search-gui-title");
    public final List<String> SHOP_GUI_ITEM_LORE = (List<String>) ConfigManager.get().getList("shop-gui-item-lore");
    public final List<String> MANAGE_SHOP_GUI_HIDDEN_ITEM_LORE = (List<String>) ConfigManager.get().getList("manage-shop-gui-hidden-item-lore");
    public final List<String> MANAGE_SHOP_GUI_SHOWN_ITEM_LORE = (List<String>) ConfigManager.get().getList("manage-shop-gui-shown-item-lore");
    public final String SHOP_GUI_BACK_BUTTON_MATERIAL = ConfigManager.get().getString(SHOP_GUI + "back-button-material");
    public final String SHOP_GUI_BACK_BUTTON_TEXT = ConfigManager.get().getString(SHOP_GUI + "back-button-text");
    public final String SHOP_GUI_NEXT_BUTTON_MATERIAL = ConfigManager.get().getString(SHOP_GUI + "next-button-material");
    public final String SHOP_GUI_NEXT_BUTTON_TEXT = ConfigManager.get().getString(SHOP_GUI + "next-button-text");
    public final String SHOP_GUI_CLOSE_BUTTON_MATERIAL = ConfigManager.get().getString(SHOP_GUI + "close-button-material");
    public final String SHOP_GUI_CLOSE_BUTTON_TEXT = ConfigManager.get().getString(SHOP_GUI + "close-button-text");

    // Config
    public final boolean IGNORE_EMPTY_CHESTS = ConfigManager.get().getBoolean("ignore-empty-chests");
    public final List<String> BLACKLISTED_WORLDS = (List<String>) ConfigManager.get().getList("blacklisted-worlds");

    private final List<World> blacklistedWorldsList = new ArrayList<>();

    public ConfigProvider() {
        if (BLACKLISTED_WORLDS != null) {
            BLACKLISTED_WORLDS.forEach(world -> {
                World worldObj = Bukkit.getWorld(world);
                if (worldObj != null) {
                    blacklistedWorldsList.add(worldObj);
                }
            });
        }
    }

    public List<World> getBlacklistedWorlds() {
        return blacklistedWorldsList;
    }

    public boolean shopGUIItemLoreHasKey(String key) {
        if (SHOP_GUI_ITEM_LORE != null) {
            return SHOP_GUI_ITEM_LORE.stream().anyMatch(key::contains);
        } else {
            return false;
        }
    }
}
