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

import com.olziedev.playerwarps.api.PlayerWarpsAPI;
import com.olziedev.playerwarps.api.events.warp.PlayerWarpTeleportEvent;
import com.olziedev.playerwarps.api.warp.Warp;
import io.myzticbean.finditemaddon.dependencies.PlayerWarpsPlugin;
import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.utils.CommonUtils;
import io.myzticbean.finditemaddon.utils.log.Logger;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * @author myzticbean & lukemango
 */
@UtilityClass
public class PlayerWarpsUtil {

    @Nullable
    public static Warp findNearestWarp(Location shopLocation, UUID shopOwner) {
        Logger.logDebugInfo("Find nearest warp for shop location " + shopLocation);
        List<Warp> playersWarps = PlayerWarpsPlugin.getAllWarps().stream()
                .filter(warp -> warp.getWarpLocation().getWorld() != null)
                .filter(warp -> warp.getWarpLocation().getWorld().equals(shopLocation.getWorld().getName()))
                .toList();
        if(FindItemAddOn.getConfigProvider().ONLY_SHOW_PLAYER_OWNDED_WARPS) {
            playersWarps = playersWarps.stream()
                    .filter(warp -> warp.getWarpPlayer().getUUID().equals(shopOwner))
                    .toList();
        }
        if (!playersWarps.isEmpty()) {
            Map<Double, Warp> warpDistanceMap = new TreeMap<>();
            playersWarps.forEach(warp ->{
                        var distance3D = CommonUtils.calculateDistance3D(
                                shopLocation.getX(),
                                shopLocation.getY(),
                                shopLocation.getZ(),
                                warp.getWarpLocation().getX(),
                                warp.getWarpLocation().getY(),
                                warp.getWarpLocation().getZ()
                        );
                        warpDistanceMap.put(distance3D, warp);
                        Logger.logDebugInfo("Warp Distance: " + distance3D + " Warp Name: " + warp.getWarpName() + ", Warp World: " + warp.getWarpLocation().getWorld());
                    }
                );
            for (Map.Entry<Double, Warp> doubleWarpEntry : warpDistanceMap.entrySet()) {
                Double distance3D = doubleWarpEntry.getKey();
                Warp warp = doubleWarpEntry.getValue();
                Logger.logDebugInfo("Warp: " + warp.getWarpName() + " " + warp.isWarpLocked() + " Distance in 3D: " + distance3D);
                // Is the config set to not tp if player warp is locked, and if so, is the warp locked?
                // also check distance from shop (should not be too long)
                if (FindItemAddOn.getConfigProvider().DO_NOT_TP_IF_PLAYER_WARP_LOCKED
                        && doubleWarpEntry.getValue().isWarpLocked()
                        && distance3D > 500) {
                    continue;
                }
                return doubleWarpEntry.getValue();
            }
        }
        return null;
    }

    /**
     * Issue #24 Fix: Extracted method from FoundShopsMenu class
     * @param player
     * @param warpName
     */
    public static void executeWarpPlayer(Player player, String warpName) {
        PlayerWarpsAPI.getInstance(api -> {
            Warp playerWarp = api.getPlayerWarp(warpName, player);
            if(playerWarp != null) {
                playerWarp.getWarpLocation().teleportWarp(player, PlayerWarpTeleportEvent.Cause.PLAYER_WARP_MENU);
            }
            else {
                Logger.logError("&e" + player.getName() + " &cis trying to teleport to a PlayerWarp that does not exist!");
            }
        });
    }
}