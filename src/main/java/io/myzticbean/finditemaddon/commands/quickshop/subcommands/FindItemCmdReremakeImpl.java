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
package io.myzticbean.finditemaddon.commands.quickshop.subcommands;

import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.handlers.command.CmdExecutorHandler;
import io.myzticbean.finditemaddon.models.enums.PlayerPermsEnum;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.maxgamer.quickshop.api.command.CommandHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author myzticbean
 */
public class FindItemCmdReremakeImpl implements CommandHandler<Player> {

    private final String hideSubCommand;
    private final String revealShopSubCommand;
    private final CmdExecutorHandler cmdExecutor;
    private final List<String> itemsList = new ArrayList<>();
    private final List<String> buyOrSellList = new ArrayList<>();

    public FindItemCmdReremakeImpl() {
        if(StringUtils.isBlank(FindItemAddOn.getConfigProvider().FIND_ITEM_HIDESHOP_AUTOCOMPLETE)) {
            this.hideSubCommand = "hideshop";
        }
        else {
            this.hideSubCommand = FindItemAddOn.getConfigProvider().FIND_ITEM_HIDESHOP_AUTOCOMPLETE;
        }

        if(StringUtils.isEmpty(FindItemAddOn.getConfigProvider().FIND_ITEM_REVEALSHOP_AUTOCOMPLETE)
                || StringUtils.containsIgnoreCase(
                FindItemAddOn.getConfigProvider().FIND_ITEM_REVEALSHOP_AUTOCOMPLETE, " ")) {
            this.revealShopSubCommand = "revealshop";
        }
        else {
            this.revealShopSubCommand = FindItemAddOn.getConfigProvider().FIND_ITEM_REVEALSHOP_AUTOCOMPLETE;
        }
        cmdExecutor = new CmdExecutorHandler();
    }

    @Override
    public void onCommand(Player commandSender, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            commandSender.sendMessage(ColorTranslator.translateColorCodes(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&cIncorrect usage!"));
        }
        else if(args.length == 1) {
            if(commandSender.hasPermission(PlayerPermsEnum.FINDITEM_HIDESHOP.value()) && !FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_REMOVE_HIDE_REVEAL_SUBCMDS) {
                if(args[0].equalsIgnoreCase(hideSubCommand)) {
                    cmdExecutor.handleHideShop(commandSender);
                } else if(args[0].equalsIgnoreCase(revealShopSubCommand)) {
                    cmdExecutor.handleRevealShop(commandSender);
                } else {
                    commandSender.sendMessage(ColorTranslator.translateColorCodes(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&cIncorrect usage!"));
                }
            } else {
                commandSender.sendMessage(ColorTranslator.translateColorCodes(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&cYou don't have permission to use that!"));
            }
        } else {
            cmdExecutor.handleShopSearch(args[0], commandSender, args[1]);
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player sender, @NotNull String commandLabel, @NotNull String[] args) {
        if(itemsList.isEmpty()) {
            for(Material mat : Material.values()) {
                itemsList.add(mat.name());
            }
        }
        if(buyOrSellList.isEmpty()) {
            // to-buy
            if(StringUtils.isEmpty(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_BUY_AUTOCOMPLETE)
                    || StringUtils.containsIgnoreCase(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_BUY_AUTOCOMPLETE, " ")) {
                buyOrSellList.add("TO_BUY");
            }
            else {
                buyOrSellList.add(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_BUY_AUTOCOMPLETE);
            }
            // to-sell
            if(StringUtils.isEmpty(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_SELL_AUTOCOMPLETE)
                    || StringUtils.containsIgnoreCase(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_SELL_AUTOCOMPLETE, " ")) {
                buyOrSellList.add("TO_SELL");
            }
            else {
                buyOrSellList.add(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_SELL_AUTOCOMPLETE);
            }
            // hide
            if(sender.hasPermission(PlayerPermsEnum.FINDITEM_HIDESHOP.value()) && !FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_REMOVE_HIDE_REVEAL_SUBCMDS) {
                buyOrSellList.add(FindItemAddOn.getConfigProvider().FIND_ITEM_HIDESHOP_AUTOCOMPLETE);
                buyOrSellList.add(FindItemAddOn.getConfigProvider().FIND_ITEM_REVEALSHOP_AUTOCOMPLETE);
            }
        }
        List<String> result = new ArrayList<>();
        if(args.length == 1) {
            for(String a : buyOrSellList) {
                if(a.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(a);
                }
            }
            return result;
        }
        else if(args.length == 2) {
            for(String a : itemsList) {
                if(a.toLowerCase().startsWith(args[1].toLowerCase())) {
                    result.add(a);
                }
            }
            return result;
        }
        else {
            return null;
        }

    }
}
