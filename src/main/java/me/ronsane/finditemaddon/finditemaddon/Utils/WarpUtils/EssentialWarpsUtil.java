package me.ronsane.finditemaddon.finditemaddon.Utils.WarpUtils;

import com.earth2me.essentials.Warps;
import com.earth2me.essentials.commands.WarpNotFoundException;
import com.olziedev.playerwarps.api.warp.Warp;
import me.ronsane.finditemaddon.finditemaddon.Dependencies.EssentialsXPlugin;
import me.ronsane.finditemaddon.finditemaddon.FindItemAddOn;
import me.ronsane.finditemaddon.finditemaddon.Utils.CommonUtils;
import me.ronsane.finditemaddon.finditemaddon.Utils.LoggerUtils;
import net.ess3.api.InvalidWorldException;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class EssentialWarpsUtil {
    @Nullable
    public String findNearestWarp(Location shopLocation) {
        Collection<String> allWarps = EssentialsXPlugin.getAPI().getWarps().getList();
        if(allWarps.size() > 0) {
            Map<Double, String> warpDistanceMap = new TreeMap<>();
            allWarps.parallelStream().forEach(warp -> {
                try {
                    Double distance = CommonUtils.calculateDistance2D(
                            shopLocation.getX(),
                            shopLocation.getY(),
                            EssentialsXPlugin.getAPI().getWarps().getWarp(warp).getX(),
                            EssentialsXPlugin.getAPI().getWarps().getWarp(warp).getY()
                    );
                    warpDistanceMap.put(distance, warp);
                } catch (WarpNotFoundException | InvalidWorldException e) {
                    LoggerUtils.logDebugInfo("Exception occurred with Essential Warp '" + warp + "': " + e.getMessage());
                    if(FindItemAddOn.getConfigProvider().DEBUG_MODE) {
                        e.printStackTrace();
                    }
                }
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
