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
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * @author myzticbean & lukemango
 */
public class PlayerWarpsUtil {

    @Nullable
    public Warp findNearestWarp(Location shopLocation, UUID shopOwner) {
        List<Warp> playersWarps = PlayerWarpsPlugin.getAllWarps().stream()
                .filter(warp -> warp.getWarpLocation().getWorld() != null)
                .filter(warp -> warp.getWarpLocation().getWorld().equals(shopLocation.getWorld().getName()))
                .filter(warp -> warp.getWarpPlayer().getUUID().equals(shopOwner))
                .toList();

        if (!playersWarps.isEmpty()) {
            Map<Double, Warp> warpDistanceMap = new TreeMap<>();
            playersWarps.forEach(warp ->
                    warpDistanceMap.put(CommonUtils.calculateDistance3D(
                            shopLocation.getX(),
                            shopLocation.getY(),
                            shopLocation.getZ(),
                            warp.getWarpLocation().getX(),
                            warp.getWarpLocation().getY(),
                            warp.getWarpLocation().getZ()
                    ), warp));

            for (Map.Entry<Double, Warp> doubleWarpEntry : warpDistanceMap.entrySet()) {
                // Is the config set to not tp if player warp is locked, and if so, is the warp locked?
                if (FindItemAddOn.getConfigProvider().DO_NOT_TP_IF_PLAYER_WARP_LOCKED
                        && doubleWarpEntry.getValue().isWarpLocked()) {
                    continue;
                }

                return doubleWarpEntry.getValue();
            }
        }
        return null;
    }
}