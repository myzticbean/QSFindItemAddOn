package me.ronsane.finditemaddon.finditemaddon.Utils.WarpUtils;

import com.olziedev.playerwarps.api.warp.Warp;
import me.ronsane.finditemaddon.finditemaddon.Dependencies.PlayerWarpsPlugin;
import me.ronsane.finditemaddon.finditemaddon.Utils.CommonUtils;
import org.bukkit.Location;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PlayerWarpsUtil {

    public Warp findNearestWarp(Location shopLocation) {
        List<Warp> allWarps = PlayerWarpsPlugin.getAPI().getPlayerWarps(false);
        Map<Double, Warp> warpDistanceMap = new TreeMap<>();
        allWarps.forEach((warp) -> warpDistanceMap.put(CommonUtils.calculateDistance2D(
                shopLocation.getX(),
                shopLocation.getY(),
                warp.getWarpLocation().getX(),
                warp.getWarpLocation().getY()
        ), warp));
        return warpDistanceMap.entrySet().iterator().next().getValue();
    }

}