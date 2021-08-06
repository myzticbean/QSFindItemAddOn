package me.ronsane.finditemaddon.finditemaddon.Utils;

import me.ronsane.finditemaddon.finditemaddon.FindItemAddOn;
import org.bukkit.Bukkit;

public class LoggerUtils {
    public static void logInfo(String text) {
        if(FindItemAddOn.getInstance().getConfig().getBoolean("debug-mode")) {
            Bukkit.getLogger().info(
                    FindItemAddOn.PluginPrefix + text);
        }
    }
}
