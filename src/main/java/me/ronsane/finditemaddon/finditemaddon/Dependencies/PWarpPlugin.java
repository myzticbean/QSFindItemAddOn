package me.ronsane.finditemaddon.finditemaddon.Dependencies;

import me.ronsane.finditemaddon.finditemaddon.Utils.LoggerUtils;
import me.tks.playerwarp.PWarp;
import me.tks.playerwarp.Warp;
import me.tks.playerwarp.WarpList;
import org.bukkit.Bukkit;

import java.util.List;

public class PWarpPlugin {

    private static PWarp pWarp = null;

    public static void setup() {
        if(Bukkit.getPluginManager().isPluginEnabled("PWarp")) {
            LoggerUtils.logInfo("Found PWarp");
            pWarp = PWarp.getPlugin(PWarp.class);
        }
    }
    public static PWarp getAPI() {
        return pWarp;
    }
    public static List<Warp> getAllWarps() { return WarpList.read().getWarps(); }
}
