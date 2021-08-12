package me.ronsane.finditemaddon.finditemaddon.Utils;

import me.ronsane.finditemaddon.finditemaddon.ConfigHandler.ConfigHandler;
import me.ronsane.finditemaddon.finditemaddon.FindItemAddOn;
import org.bukkit.Bukkit;

public class LoggerUtils {
    public static void logDebugInfo(String text) {
        if(FindItemAddOn.configProvider.DEBUG_MODE) {
            Bukkit.getLogger().info(CommonUtils.parseColors("[QSFindItemAddOn] " + text));
        }
    }
    public static void logInfo(String text) {
        Bukkit.getLogger().info(CommonUtils.parseColors("[QSFindItemAddOn] " + text));
    }
    public static void logError(String text) {
        Bukkit.getLogger().severe(CommonUtils.parseColors("[QSFindItemAddOn] &c" + text));
    }
}
