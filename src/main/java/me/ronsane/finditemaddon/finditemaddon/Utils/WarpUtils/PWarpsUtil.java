package me.ronsane.finditemaddon.finditemaddon.Utils.WarpUtils;

import me.ronsane.finditemaddon.finditemaddon.Dependencies.PWarpPlugin;
import me.ronsane.finditemaddon.finditemaddon.Utils.CommonUtils;
import me.tks.playerwarp.Warp;
import me.tks.playerwarp.WarpList;
import org.bukkit.Location;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PWarpsUtil {

    public void findNearestWarp(Location shopLocation) {
        List<Warp> allWarps = PWarpPlugin.getAllWarps();
        Map<Double, Warp> warpDistanceMap = new TreeMap<>();
        allWarps.parallelStream().forEach((warp -> {
//            try {
//                warpDistanceMap.put(CommonUtils.calculateDistance2D(
//                        shopLocation.getX(),
//                        shopLocation.getY(),
//                        ((Location)(warp.getClass().getDeclaredField("loc"))).getX(),
//                        warp.getWarpLocation().getY()
//                ), warp);
//            } catch (NoSuchFieldException e) {
//                e.printStackTrace();
//            }
        }));
    }

}
