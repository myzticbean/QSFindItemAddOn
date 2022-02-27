package io.mysticbeans.finditemaddon.Utils.WarpUtils;

import com.olziedev.playerwarps.api.warp.Warp;
import io.mysticbeans.finditemaddon.Dependencies.PlayerWarpsPlugin;
import io.mysticbeans.finditemaddon.FindItemAddOn;
import io.mysticbeans.finditemaddon.Utils.CommonUtils;
import io.mysticbeans.finditemaddon.Utils.LoggerUtils;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PlayerWarpsUtil {

    @Nullable
    public Warp findNearestWarp(Location shopLocation) {
        List<Warp> allWarps = PlayerWarpsPlugin.getAllWarps();
        if(allWarps.size() > 0) {
            Map<Double, Warp> warpDistanceMap = new TreeMap<>();
            allWarps.forEach(warp -> {
                warpDistanceMap.put(CommonUtils.calculateDistance2D(
                        shopLocation.getX(),
                        shopLocation.getY(),
                        warp.getWarpLocation().getX(),
                        warp.getWarpLocation().getY()
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