package io.mysticbeans.finditemaddon.Utils.WarpUtils;

import io.mysticbeans.finditemaddon.Dependencies.EssentialsXPlugin;
import io.mysticbeans.finditemaddon.Dependencies.PlayerWarpsPlugin;
import io.mysticbeans.finditemaddon.FindItemAddOn;

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