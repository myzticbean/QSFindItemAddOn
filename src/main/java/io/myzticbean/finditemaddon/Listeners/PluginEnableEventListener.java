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
package io.myzticbean.finditemaddon.Listeners;

import io.myzticbean.finditemaddon.FindItemAddOn;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

/**
 * @author myzticbean
 */
public class PluginEnableEventListener implements Listener {
    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        if(!FindItemAddOn.isQSReremakeInstalled() && !FindItemAddOn.isQSHikariInstalled()) {
//            if(!event.getPlugin().getName().equalsIgnoreCase("QuickShop")
//                    && !event.getPlugin().getName().equalsIgnoreCase("QuickShop-Hikari")) {
//                // do nothing
//            }
            if(event.getPlugin().getName().equalsIgnoreCase("QuickShop") && !FindItemAddOn.isQSHikariInstalled()) {
                FindItemAddOn.setQSReremakeInstalled(true);
            } else if (event.getPlugin().getName().equalsIgnoreCase("QuickShop-Hikari") && !FindItemAddOn.isQSReremakeInstalled()) {
                FindItemAddOn.setQSHikariInstalled(true);
            }
        }
    }
}
