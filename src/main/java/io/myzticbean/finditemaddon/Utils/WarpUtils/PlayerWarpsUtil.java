package io.myzticbean.finditemaddon.Utils.WarpUtils;

import com.olziedev.playerwarps.api.warp.Warp;
import io.myzticbean.finditemaddon.Dependencies.PlayerWarpsPlugin;
import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.Utils.CommonUtils;
import io.myzticbean.finditemaddon.Utils.LoggerUtils;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
            if(FindItemAddOn.getConfigProvider().DEBUG_MODE) {
                for(Map.Entry<Double, Warp> entry : warpDistanceMap.entrySet()) {
                    LoggerUtils.logDebugInfo(entry.getValue().getWarpName() + " : " + entry.getKey());
                }
            }

            return warpDistanceMap.entrySet().iterator().next().getValue();
        }
        else {
            return null;
        }
    }

}