/**
 * QSFindItemAddOn: An Minecraft add-on plugin for the QuickShop Hikari
 * and Reremake Shop plugins for Spigot server platform.
 * Copyright (C) 2021  myzticbean
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
