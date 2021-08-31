package me.ronsane.finditemaddon.finditemaddon.Utils.WarpUtils;

import com.olziedev.playerwarps.api.warp.Warp;
import me.ronsane.finditemaddon.finditemaddon.Dependencies.PlayerWarpsPlugin;
import me.ronsane.finditemaddon.finditemaddon.Utils.CommonUtils;
import me.ronsane.finditemaddon.finditemaddon.Utils.LoggerUtils;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PlayerWarpsUtil {

    @Nullable
    public Warp findNearestWarp(Location shopLocation) {
        List<Warp> allWarps = PlayerWarpsPlugin.getAPI().getPlayerWarps(false);
        if(allWarps.size() > 0) {
            Map<Double, Warp> warpDistanceMap = new TreeMap<>();
            allWarps.parallelStream().forEach(warp -> {
                warpDistanceMap.put(CommonUtils.calculateDistance2D(
                        shopLocation.getX(),
                        shopLocation.getY(),
                        warp.getWarpLocation().getX(),
                        warp.getWarpLocation().getY()
                ), warp);
            });
            for(Map.Entry<Double, Warp> entry : warpDistanceMap.entrySet()) {
                LoggerUtils.logDebugInfo(entry.getValue().getWarpName() + " : " + entry.getKey());
            }

            return warpDistanceMap.entrySet().iterator().next().getValue();
        }
        else {
            return null;
        }
    }

}