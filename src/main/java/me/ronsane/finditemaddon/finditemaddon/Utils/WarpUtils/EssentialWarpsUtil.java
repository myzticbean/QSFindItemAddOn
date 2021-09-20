package me.ronsane.finditemaddon.finditemaddon.Utils.WarpUtils;

import me.ronsane.finditemaddon.finditemaddon.Models.EssentialWarpModel;
import me.ronsane.finditemaddon.finditemaddon.Dependencies.EssentialsXPlugin;
import me.ronsane.finditemaddon.finditemaddon.FindItemAddOn;
import me.ronsane.finditemaddon.finditemaddon.Utils.CommonUtils;
import me.ronsane.finditemaddon.finditemaddon.Utils.LoggerUtils;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EssentialWarpsUtil {

    @Nullable
    public String findNearestWarp(Location shopLocation) {
        List<EssentialWarpModel> allWarps = EssentialsXPlugin.getAllWarps();
        if(allWarps != null && allWarps.size() > 0) {
            Map<Double, String> warpDistanceMap = new TreeMap<>();
            allWarps.parallelStream().forEach(warp -> {
                Double distance = CommonUtils.calculateDistance2D(
                        shopLocation.getX(),
                        shopLocation.getY(),
                        warp.warpLoc.getX(),
                        warp.warpLoc.getY()
                );
                warpDistanceMap.put(distance, warp.warpName);
            });
            if(FindItemAddOn.getConfigProvider().DEBUG_MODE) {
                for(Map.Entry<Double, String> entry : warpDistanceMap.entrySet()) {
                    LoggerUtils.logDebugInfo(entry.getValue() + " : " + entry.getKey());
                }
            }
            return warpDistanceMap.entrySet().iterator().next().getValue();
        }
        else {
            return null;
        }
    }

}
