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
package io.myzticbean.finditemaddon.dependencies;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import io.myzticbean.finditemaddon.utils.log.Logger;
import org.bukkit.Bukkit;

/**
 * @author myzticbean
 */
public class WGPlugin {

    private static boolean isPluginEnabled;

    public static void setup() {
        if(Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            Logger.logInfo("Found WorldGuard");
            isPluginEnabled = true;
        }
        else {
            isPluginEnabled = false;
        }
    }

    public static WorldGuardPlugin getWgPluginInstance() {
        return WorldGuardPlugin.inst();
    }

    public static WorldGuard getWgInstance () { return WorldGuard.getInstance(); }

    public static boolean isEnabled() {
        return isPluginEnabled;
    }

}
