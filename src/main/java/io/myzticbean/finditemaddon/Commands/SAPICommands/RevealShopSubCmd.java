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
package io.myzticbean.finditemaddon.Commands.SAPICommands;

import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.Handlers.CommandHandler.CmdExecutorHandler;
import me.kodysimpson.simpapi.command.SubCommand;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Sub Command Handler for /finditemadmin revealshop
 * @author myzticbean
 */
public class RevealShopSubCmd extends SubCommand {

    private final String revealShopSubCommand;
    private final CmdExecutorHandler cmdExecutor;

    public RevealShopSubCmd() {
        if(StringUtils.isEmpty(FindItemAddOn.getConfigProvider().FIND_ITEM_REVEALSHOP_AUTOCOMPLETE)
                || StringUtils.containsIgnoreCase(
                        FindItemAddOn.getConfigProvider().FIND_ITEM_REVEALSHOP_AUTOCOMPLETE, " ")) {
            revealShopSubCommand = "revealshop";
        }
        else {
            revealShopSubCommand = FindItemAddOn.getConfigProvider().FIND_ITEM_REVEALSHOP_AUTOCOMPLETE;
        }
        cmdExecutor = new CmdExecutorHandler();
    }

    @Override
    public String getName() {
        return revealShopSubCommand;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Run this command while looking at a hidden shop to make it public again";
    }

    @Override
    public String getSyntax() {
        return "/finditem " + revealShopSubCommand;
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        cmdExecutor.handleRevealShop(commandSender);
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] strings) {
        return null;
    }
}

