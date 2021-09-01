package me.ronsane.finditemaddon.finditemaddon.ConfigUtil;

import me.ronsane.finditemaddon.finditemaddon.Utils.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigSetup {

    private static File file;
    private static FileConfiguration fileConfig;

    public static void setupConfig() {
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("QSFindItemAddOn").getDataFolder(), "config.yml");

        if(!file.exists()) {
            try {
                boolean isConfigGenerated = file.createNewFile();
                if(isConfigGenerated) {
                    LoggerUtils.logInfo("Generated a new config.yml");
                }
            }
            catch (IOException e) {
                LoggerUtils.logError("Error generating config.yml");
            }
        }

        fileConfig = YamlConfiguration.loadConfiguration(file);
    }

    public static void saveConfig() {
        try {
            fileConfig.save(file);
        }
        catch (IOException e) {
            LoggerUtils.logError("Error saving config.yml");
        }
    }

    public static void checkForMissingProperties() {
        if(!fileConfig.contains("config-version", true)) {
            fileConfig.set("config-version", 6);
        }
        if(!fileConfig.contains("search-loaded-shops-only", true)) {
            fileConfig.set("search-loaded-shops-only", false);
        }
    }

    public static FileConfiguration get() {
        return fileConfig;
    }

    public static void reloadConfig() {
        fileConfig = YamlConfiguration.loadConfiguration(file);
    }

}
