package me.ronsane.finditemaddon.finditemaddon.Utils.WarpUtils;

import me.ronsane.finditemaddon.finditemaddon.Dependencies.EssentialsXPlugin;
import me.ronsane.finditemaddon.finditemaddon.FindItemAddOn;
import me.ronsane.finditemaddon.finditemaddon.Utils.LoggerUtils;

public class WarpUtils {
    public static void updateWarps() {
        if(FindItemAddOn.getConfigProvider().shopGUIItemLoreHasKey("{NEAREST_WARP}")) {
            LoggerUtils.logInfo("Updating warps/regions list...");
            if(FindItemAddOn.getConfigProvider().NEAREST_WARP_MODE == 1) {
                if(EssentialsXPlugin.isEnabled()) {
                    EssentialsXPlugin.updateAllWarps();
                }
            }
            LoggerUtils.logInfo("Update complete!");
        }
    }
}
