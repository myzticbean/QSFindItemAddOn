package io.myzticbean.finditemaddon.dependencies;

import io.myzticbean.finditemaddon.utils.log.Logger;
import me.ryanhamshire.GPFlags.GPFlags;
import org.bukkit.Bukkit;

public class GPFlagsPlugin {

    private static GPFlags gpFlags;

    public static void setup() {
        if(Bukkit.getPluginManager().isPluginEnabled("GPFlags")) {
            gpFlags = (GPFlags) Bukkit.getServer().getPluginManager().getPlugin("GPFlags");
            if(gpFlags != null) {
                Logger.logInfo("Found GPFlags");
            }
        }
    }

    public static boolean isEnabled() {
        return gpFlags.isEnabled();
    }

    public static GPFlags getAPI() {
        return gpFlags;
    }

}
