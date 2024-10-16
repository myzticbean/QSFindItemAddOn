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
package io.myzticbean.finditemaddon.listeners;

import io.myzticbean.finditemaddon.FindItemAddOn;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author myzticbean
 */
public class PlayerCommandSendEventListener implements Listener {
    @EventHandler
    public void onPlayerCommandTab(PlayerCommandSendEvent event) {
        if(!event.getPlayer().isOp()) {
            List<String> blockedCommands = new ArrayList<>();
            blockedCommands.add("finditem:finditem");
            assert FindItemAddOn.getConfigProvider().FIND_ITEM_COMMAND_ALIAS != null;
            for(String cmd : FindItemAddOn.getConfigProvider().FIND_ITEM_COMMAND_ALIAS) {
                blockedCommands.add("finditem:" + cmd);
            }
            blockedCommands.add("finditemadmin:finditemadmin");
            blockedCommands.add("finditemadmin:fiadmin");
            blockedCommands.add("finditemadmin");
            blockedCommands.add("fiadmin");
            event.getCommands().removeAll(blockedCommands);
        }
    }
}
