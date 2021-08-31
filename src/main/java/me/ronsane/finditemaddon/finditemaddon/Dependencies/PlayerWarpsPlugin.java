package me.ronsane.finditemaddon.finditemaddon.Dependencies;

import com.olziedev.playerwarps.api.PlayerWarpsAPI;
import me.ronsane.finditemaddon.finditemaddon.Utils.LoggerUtils;
import org.bukkit.Bukkit;

public class PlayerWarpsPlugin {

    private static Boolean isEnabled = false;

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
}
