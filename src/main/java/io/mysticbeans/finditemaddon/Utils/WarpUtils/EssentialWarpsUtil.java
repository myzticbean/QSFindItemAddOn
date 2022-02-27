package io.mysticbeans.finditemaddon.Utils.WarpUtils;

import io.mysticbeans.finditemaddon.Dependencies.EssentialsXPlugin;
import io.mysticbeans.finditemaddon.FindItemAddOn;
import io.mysticbeans.finditemaddon.Models.EssentialWarpModel;
import io.mysticbeans.finditemaddon.Utils.CommonUtils;
import io.mysticbeans.finditemaddon.Utils.LoggerUtils;
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
            allWarps.forEach(warp -> {
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
