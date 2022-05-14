package io.myzticbean.finditemaddon.Dependencies;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import io.myzticbean.finditemaddon.Utils.LoggerUtils;
import org.bukkit.Bukkit;

public class WGPlugin {

    private static boolean isPluginEnabled;

    public static void setup() {
        if(Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            LoggerUtils.logInfo("Found WorldGuard");
            isPluginEnabled = true;
        }
        else {
            isPluginEnabled = false;
        }
    }

    public static WorldGuardPlugin getWgPluginInstance() {
        return WorldGuardPlugin.inst();
    }

    public static WorldGuard getWgInstance () { return WorldGuard.getInstance(); }

    public static boolean isEnabled() {
        return isPluginEnabled;
    }

}
