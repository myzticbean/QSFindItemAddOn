package io.myzticbean.finditemaddon.Dependencies;

import com.olziedev.playerwarps.api.PlayerWarpsAPI;
import com.olziedev.playerwarps.api.warp.Warp;
import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.Utils.LoggerUtils;
import org.bukkit.Bukkit;

import java.util.List;

public class PlayerWarpsPlugin {

    private static boolean isEnabled = false;
    private static List<Warp> allWarpsList = null;
    private static final String ALL_WARPS_LIST_CLASSPATH = PlayerWarpsPlugin.class.getCanonicalName() + ".allWarpsList";

    private PlayerWarpsPlugin() { }

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

    public static void updateAllWarpsFromAPI() {
        if(isEnabled) {
            LoggerUtils.logInfo("Updating Player warps list...");
            allWarpsList = PlayerWarpsPlugin.getAPI().getPlayerWarps(false);
            LoggerUtils.logInfo("Update complete! Found " + getAllWarps().size() + " warps.");
        }
    }

    public static void updateWarpsOnEventCall(Warp warp, boolean isRemoved) {
        LoggerUtils.logDebugInfo("Got a PlayerWarps event call... checking nearest-warp-mode");
        if(FindItemAddOn.getConfigProvider().NEAREST_WARP_MODE == 2) {
            LoggerUtils.logDebugInfo("nearest-warp-mode found set to 2");
            if (PlayerWarpsPlugin.getIsEnabled()) {
                LoggerUtils.logDebugInfo("PlayerWarps plugin is enabled");
                tryUpdateWarps(warp, isRemoved, 1);
            }
        }
        else {
            LoggerUtils.logDebugInfo("No update required to '" + ALL_WARPS_LIST_CLASSPATH + "' as PlayerWarps integration is disabled.");
        }
    }

    private static void tryUpdateWarps(Warp warp, boolean isRemoved, int updateTrialSequence) {
        // Issue #21 Fix: Adding a NPE check
        if(allWarpsList != null) {
            if(isRemoved) {
                if(allWarpsList.contains(warp)) {
                    allWarpsList.remove(warp);
                    LoggerUtils.logDebugInfo("Warp removed from allWarpsList: " + warp.getWarpName());
                } else {
                    LoggerUtils.logError("Error occurred while updating '" + ALL_WARPS_LIST_CLASSPATH + "'. Warp name: '" + warp.getWarpName() + "' does not exist!");
                }
            }
            else {
                allWarpsList.add(warp);
                LoggerUtils.logDebugInfo("New warp added to allWarpsList: " + warp.getWarpName());
            }
        }
        else {
            // Issue #21 Fix: forcing update of allWarpsList
            if(updateTrialSequence == 1) {
                updateAllWarpsFromAPI();
                tryUpdateWarps(warp, isRemoved, 2);
            } else {
                StringBuilder errorMsg = new StringBuilder();
                errorMsg.append("Error occurred while updating '" + ALL_WARPS_LIST_CLASSPATH + "' as it is null! ");
                errorMsg.append("Please install PlayerWarps by Olzie-12 if you would like to use 'nearest-warp-mode' as 2. ");
                errorMsg.append("If PlayerWarps plugin is installed and issue persists, please restart your server!");
                LoggerUtils.logError(errorMsg.toString());
            }
        }
    }
}
