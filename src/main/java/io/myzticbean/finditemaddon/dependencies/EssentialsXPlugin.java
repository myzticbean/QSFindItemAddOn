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

import com.earth2me.essentials.Essentials;
import io.myzticbean.finditemaddon.models.EssentialWarpModel;
import io.myzticbean.finditemaddon.utils.log.Logger;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Hook for EssentialsX Plugin
 * @author myzticbean
 */
@UtilityClass
public class EssentialsXPlugin {

    private static Essentials essAPI = null;
    private static List<EssentialWarpModel> allWarpsList = null;

    public static void setup() {
        if(Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
            essAPI = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
            if(essAPI != null) {
                Logger.logInfo("Found Essentials");
            }
        }
    }

    public static boolean isEnabled() {
        return essAPI != null;
    }

    public static Essentials getAPI() {
        return essAPI;
    }

    public static List<EssentialWarpModel> getAllWarps() { return allWarpsList; }

    public static void updateAllWarps() {
        long start = System.currentTimeMillis();
        if(essAPI.isEnabled()) {
            Collection<String> allWarps = EssentialsXPlugin.getAPI().getWarps().getList();
            allWarpsList = new ArrayList<>();
            allWarps.forEach(warp -> {
                try {
                    EssentialWarpModel essWarp = new EssentialWarpModel();
                    essWarp.warpName = warp;
                    essWarp.warpLoc = essAPI.getWarps().getWarp(warp);
                    allWarpsList.add(essWarp);
                } catch (Exception ignored) { }
            });
        }
        Logger.logDebugInfo("Update complete for Essentials warps list! Found " + getAllWarps().size() + " warps. Time took: " + (System.currentTimeMillis() - start) + "ms.");
    }

    public static void setLastLocation(Player player) {
        if(essAPI.isEnabled()) {
            getAPI().getUser(player).setLastLocation();
        }
    }

}
