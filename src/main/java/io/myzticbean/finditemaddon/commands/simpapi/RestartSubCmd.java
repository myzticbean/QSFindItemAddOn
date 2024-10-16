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
package io.myzticbean.finditemaddon.commands.simpapi;

import io.myzticbean.finditemaddon.handlers.command.CmdExecutorHandler;
import me.kodysimpson.simpapi.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Sub Command Handler for /finditem restart
 * @deprecated No longer used, will be removed in future versions
 * @author myzticbean
 */
@Deprecated
public class RestartSubCmd extends SubCommand {

    private final CmdExecutorHandler cmdExecutor;

    public RestartSubCmd() {
        cmdExecutor = new CmdExecutorHandler();
    }

    @Override
    public String getName() {
        return "restart";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Restarts the plugin (NOT recommended in most cases, restart server if necessary)";
    }

    @Override
    public String getSyntax() {
        return "/finditemadmin restart";
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        cmdExecutor.handlePluginRestart(commandSender);
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] strings) {
        return null;
    }
}

