package me.ronsane.finditemaddon.finditemaddon.Utils.WarpUtils;

import me.ronsane.finditemaddon.finditemaddon.Dependencies.PWarpPlugin;
import me.ronsane.finditemaddon.finditemaddon.FindItemAddOn;
import me.ronsane.finditemaddon.finditemaddon.Utils.CommonUtils;
import me.ronsane.finditemaddon.finditemaddon.Utils.LoggerUtils;
import me.tks.playerwarp.Warp;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PWarpsUtil {

    @Nullable
    public Warp findNearestWarp(Location shopLocation) {
        List<Warp> allWarps = PWarpPlugin.getAllWarps();
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
            if(FindItemAddOn.getConfigProvider().DEBUG_MODE) {
                for(Map.Entry<Double, Warp> entry : warpDistanceMap.entrySet()) {
                    LoggerUtils.logDebugInfo(entry.getValue().getName() + " : " + entry.getKey());
                }
            }

            return warpDistanceMap.entrySet().iterator().next().getValue();
        }
        else {
            return null;
        }
    }

}
