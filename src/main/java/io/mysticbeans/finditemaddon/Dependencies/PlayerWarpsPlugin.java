package io.mysticbeans.finditemaddon.Dependencies;

import com.olziedev.playerwarps.api.PlayerWarpsAPI;
import com.olziedev.playerwarps.api.warp.Warp;
import io.mysticbeans.finditemaddon.Utils.LoggerUtils;
import org.bukkit.Bukkit;

import java.util.List;

public class PlayerWarpsPlugin {

    private static Boolean isEnabled = false;
    private static List<Warp> allWarpsList = null;

    public static void setup() {
        if(Bukkit.getPluginManager().isPluginEnabled("PlayerWarps")) {
            LoggerUtils.logInfo("Found PlayerWarps");
            isEnabled = true;
        }
    }

    public static boolean getIsEnabled() { return isEnabled; }

    public static PlayerWarpsAPI getAPI() {
        return PlayerWarpsAPI.getInstance();
    }

    public static List<Warp> getAllWarps() { return allWarpsList; }

    public static void updateAllWarps() {
        if(isEnabled) {
            LoggerUtils.logInfo("Updating Player warps list...");
            allWarpsList = PlayerWarpsPlugin.getAPI().getPlayerWarps(false);
            LoggerUtils.logInfo("Update complete! Found " + getAllWarps().size() + " warps.");
        }
    }

    public static void updateWarpsOnEventCall(Warp warp, boolean isRemoved) {
        if(isRemoved) {
            allWarpsList.remove(warp);
        }
        else {
            allWarpsList.add(warp);
        }
    }
}
