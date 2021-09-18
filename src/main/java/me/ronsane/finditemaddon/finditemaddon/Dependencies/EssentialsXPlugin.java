package me.ronsane.finditemaddon.finditemaddon.Dependencies;

import com.earth2me.essentials.Essentials;
import me.ronsane.finditemaddon.finditemaddon.Utils.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EssentialsXPlugin {

    private static Essentials essAPI = null;
    private static List<EssentialWarpModel> allWarpsList = null;

    public static void setup() {
        if(Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
            essAPI = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
            if(essAPI != null) {
                LoggerUtils.logInfo("Found Essentials");
//                for(int i = 0; i < 1000; i++) {
//                    try {
//                        essAPI.getWarps().setWarp("esswarp_" + i, new Location(Bukkit.getWorld("world"), -135.5f, 71f, -286.5f));
//                    }
//                    catch(Exception ignored) {}
//                }

            }
        }
    }

    public static boolean isEnabled() {
        return essAPI != null;
    }

    public static Essentials getAPI() {
        return essAPI;
    }

    public static List<EssentialWarpModel> getAllWarps() {
        return allWarpsList;
    }

    public static void updateAllWarps() {
        if(essAPI.isEnabled()) {
            Collection<String> allWarps = EssentialsXPlugin.getAPI().getWarps().getList();
            allWarpsList = new ArrayList<>();
            allWarps.forEach(warp -> {
                try {
                    EssentialWarpModel essWarp = new EssentialWarpModel();
                    essWarp.warpName = warp;
                    essWarp.warpLoc = essAPI.getWarps().getWarp(warp);
                    allWarpsList.add(essWarp);
                } catch (Exception ignored) { }
            });
        }
    }

}
