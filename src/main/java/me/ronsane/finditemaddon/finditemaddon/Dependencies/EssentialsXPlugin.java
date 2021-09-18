package me.ronsane.finditemaddon.finditemaddon.Dependencies;

import com.earth2me.essentials.Essentials;
import me.ronsane.finditemaddon.finditemaddon.Utils.LoggerUtils;
import org.bukkit.Bukkit;

public class EssentialsXPlugin {

    private static Essentials essAPI = null;

    public static void setup() {
        if(Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
            essAPI = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
            assert essAPI != null;
            LoggerUtils.logInfo("Found Essentials");
        }
    }

    public static boolean isEnabled() {
        return essAPI != null;
    }

    public static Essentials getAPI() {
        return essAPI;
    }

}
