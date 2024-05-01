package io.myzticbean.finditemaddon.Utils;

import io.myzticbean.finditemaddon.FindItemAddOn;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import org.bukkit.Bukkit;

/**
 * @author myzticbean
 */
public class LoggerUtils {

    public static final String QS_FIND_ITEM_ADD_ON = "[QSFindItemAddOn] ";
    public static final String QS_FIND_ITEM_ADD_ON_DEBUG_LOG = "[QSFindItemAddOn-DEBUG] ";

    public static void logDebugInfo(String text) {
        if(FindItemAddOn.getConfigProvider().DEBUG_MODE) {
            Bukkit.getLogger().warning(ColorTranslator.translateColorCodes(QS_FIND_ITEM_ADD_ON_DEBUG_LOG + getMainOrAsyncThreadLogText() + text));
        }
    }
    public static void logInfo(String text) {
        Bukkit.getLogger().info(ColorTranslator.translateColorCodes(QS_FIND_ITEM_ADD_ON + getMainOrAsyncThreadLogText() + text));
    }
    public static void logError(String text) {
        Bukkit.getLogger().severe(ColorTranslator.translateColorCodes(QS_FIND_ITEM_ADD_ON + text));
    }
    public static void logError(Exception e) {
        Bukkit.getLogger().severe(ColorTranslator.translateColorCodes(QS_FIND_ITEM_ADD_ON + e.getMessage()));
        e.printStackTrace();
    }
    public static void logWarning(String text) {
        Bukkit.getLogger().warning(ColorTranslator.translateColorCodes(QS_FIND_ITEM_ADD_ON + text));
    }

    private static String getMainOrAsyncThreadLogText() {
        return Bukkit.isPrimaryThread() ? "[MAIN] " : "[ASYNC] ";
    }
}
