package io.myzticbean.finditemaddon.Dependencies;

import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.api.ResidenceInterface;
import io.myzticbean.finditemaddon.Utils.LoggerUtils;
import org.bukkit.Bukkit;

/**
 * @author SengyEU
 */
public class ResidencePlugin {

    private static boolean isPluginEnabled;

    public static void setup() {
        if(Bukkit.getPluginManager().isPluginEnabled("Residence")) {
            LoggerUtils.logInfo("Found Residence");
            isPluginEnabled = true;
        }
        else {
            isPluginEnabled = false;
        }
    }


    public static ResidenceInterface getResidenceManager () {
        return ResidenceApi.getResidenceManager();
    }

    public static boolean isEnabled() {
        return isPluginEnabled;
    }

}
