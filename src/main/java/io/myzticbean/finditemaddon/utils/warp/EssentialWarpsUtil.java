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
package io.myzticbean.finditemaddon.utils.warp;

import io.myzticbean.finditemaddon.dependencies.EssentialsXPlugin;
import io.myzticbean.finditemaddon.models.EssentialWarpModel;
import io.myzticbean.finditemaddon.utils.CommonUtils;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author myzticbean
 */
@UtilityClass
public class EssentialWarpsUtil {

    @Nullable
    public static String findNearestWarp(Location shopLocation) {
        List<EssentialWarpModel> allWarps = EssentialsXPlugin.getAllWarps();
        if(allWarps != null && !allWarps.isEmpty()) {
            Map<Double, String> warpDistanceMap = new TreeMap<>();
            allWarps.forEach(warp -> {
                Double distance = CommonUtils.calculateDistance3D(
                        shopLocation.getX(),
                        shopLocation.getY(),
                        shopLocation.getZ(),
                        warp.warpLoc.getX(),
                        warp.warpLoc.getY(),
                        warp.warpLoc.getZ()
                );
                warpDistanceMap.put(distance, warp.warpName);
            });
            /*if(FindItemAddOn.getConfigProvider().DEBUG_MODE) {
                for(Map.Entry<Double, String> entry : warpDistanceMap.entrySet()) {
                    LoggerUtils.logDebugInfo(entry.getValue() + " : " + entry.getKey());
                }
            }*/
            return warpDistanceMap.entrySet().iterator().next().getValue();
        }
        else {
            return null;
        }
    }

    public static void warpPlayer(Player player, String warpName) {
        Bukkit.dispatchCommand(player, "essentials:warp " + warpName);
    }

}
