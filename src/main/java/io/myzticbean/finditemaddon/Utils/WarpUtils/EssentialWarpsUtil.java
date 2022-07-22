package io.myzticbean.finditemaddon.Utils.WarpUtils;

import io.myzticbean.finditemaddon.Dependencies.EssentialsXPlugin;
import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.Models.EssentialWarpModel;
import io.myzticbean.finditemaddon.Utils.CommonUtils;
import io.myzticbean.finditemaddon.Utils.LoggerUtils;
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
