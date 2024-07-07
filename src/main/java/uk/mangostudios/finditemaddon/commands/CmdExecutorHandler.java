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
package uk.mangostudios.finditemaddon.commands;

import uk.mangostudios.finditemaddon.config.ConfigManager;
import uk.mangostudios.finditemaddon.FindItemAddOn;
import uk.mangostudios.finditemaddon.gui.ShopsGui;
import uk.mangostudios.finditemaddon.gui.impl.ShopItem;
import uk.mangostudios.finditemaddon.external.QuickShopHandler;
import uk.mangostudios.finditemaddon.util.Colourify;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CmdExecutorHandler {

    /**
     * Handles the main shop search process
     *
     * @param isBuying Whether the player is buying or selling
     * @param player   Player who is running the command
     * @param matcher  Specifies Item ID or Item name
     */
    public void handleShopSearch(boolean isBuying, Player player, String matcher) {
        player.sendMessage(Colourify.colour(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + FindItemAddOn.getConfigProvider().SHOP_SEARCH_LOADING_MSG));

        QuickShopHandler quickShopApi = FindItemAddOn.getQsApiInstance();
        Material mat = Material.getMaterial(matcher.toUpperCase());
        if (mat != null) {
            List<ShopItem> searchResultList = quickShopApi.findItemBasedOnTypeFromAllShops(new ItemStack(mat), isBuying, player);
            if (!searchResultList.isEmpty()) {
                ShopsGui.open(player, matcher, searchResultList);
                return;
            }
        }

        List<ShopItem> searchResultList = quickShopApi.findItemBasedOnDisplayNameFromAllShops(matcher, isBuying, player);
        if (!searchResultList.isEmpty()) {
            ShopsGui.open(player, matcher, searchResultList);
            return;
        }

        if (!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().NO_SHOP_FOUND_MSG)) {
            player.sendMessage(Colourify.colour(
                    FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + FindItemAddOn.getConfigProvider().NO_SHOP_FOUND_MSG));
        }
    }

    /**
     * Handles plugin reload
     *
     * @param commandSender Who is the command sender: console or player
     */
    public void handlePluginReload(CommandSender commandSender) {
        ConfigManager.reloadConfig();
        ConfigManager.saveConfig();
        FindItemAddOn.initConfigProvider();
        commandSender.sendMessage(Colourify.colour("<green>Plugin reloaded!"));
    }
}

