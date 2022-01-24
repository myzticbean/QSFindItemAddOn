package me.ronsane.finditemaddon.finditemaddon.Utils.WarpUtils;

import me.ronsane.finditemaddon.finditemaddon.Dependencies.EssentialsXPlugin;
import me.ronsane.finditemaddon.finditemaddon.Dependencies.PlayerWarpsPlugin;
import me.ronsane.finditemaddon.finditemaddon.FindItemAddOn;

public class WarpUtils {
    public static void updateWarps() {
        if(FindItemAddOn.getConfigProvider().shopGUIItemLoreHasKey("{NEAREST_WARP}")) {
            if(FindItemAddOn.getConfigProvider().NEAREST_WARP_MODE == 1) {
                if(EssentialsXPlugin.isEnabled()) {
                    EssentialsXPlugin.updateAllWarps();
                }
            }
            else if(FindItemAddOn.getConfigProvider().NEAREST_WARP_MODE == 2) {
                if(PlayerWarpsPlugin.getIsEnabled()) {
                    PlayerWarpsPlugin.updateAllWarps();
                }
            }
        }
    }
}
