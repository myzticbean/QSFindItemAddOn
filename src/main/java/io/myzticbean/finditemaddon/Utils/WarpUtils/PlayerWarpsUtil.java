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
package io.myzticbean.finditemaddon.Utils.WarpUtils;

import com.olziedev.playerwarps.api.warp.Warp;
import io.myzticbean.finditemaddon.Dependencies.PlayerWarpsPlugin;
import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.Utils.CommonUtils;
import io.myzticbean.finditemaddon.Utils.LoggerUtils;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author myzticbean
 */
public class PlayerWarpsUtil {

    @Nullable
    public Warp findNearestWarp(Location shopLocation) {
        List<Warp> allWarps = PlayerWarpsPlugin.getAllWarps();
        if(!allWarps.isEmpty()) {
            Map<Double, Warp> warpDistanceMap = new TreeMap<>();
            allWarps.forEach(warp -> {
                warpDistanceMap.put(CommonUtils.calculateDistance3D(
                        shopLocation.getX(),
                        shopLocation.getY(),
                        shopLocation.getZ(),
                        warp.getWarpLocation().getX(),
                        warp.getWarpLocation().getY(),
                        warp.getWarpLocation().getZ()
                ), warp);
            });
            if(!FindItemAddOn.getConfigProvider().DO_NOT_TP_IF_PLAYER_WARP_LOCKED)
                return warpDistanceMap.entrySet().iterator().next().getValue(); // don't care if warp is locked or not
            else {
                // need to return a warp that is not locked
                Iterator<Map.Entry<Double, Warp>> iterator = warpDistanceMap.entrySet().iterator();
                // now keep looping until we find a warp that is not locked
                while(iterator.hasNext()) {
                    Warp warp = iterator.next().getValue();
                    LoggerUtils.logDebugInfo("warp: " + warp.getWarpName() + " " + warp.isWarpLocked());
                    if(!warp.isWarpLocked()) {
                        return warp;
                    }
                }
                LoggerUtils.logError("No PlayerWarp found that is in 'unlocked' state");
                return null;
            }
        }
        else {
            return null;
        }
    }
}