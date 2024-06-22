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
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package io.myzticbean.finditemaddon.ConfigUtil;

import io.myzticbean.finditemaddon.FindItemAddOn;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigSetup {

    private static File configFile;
    private static FileConfiguration configFileConfiguration;

    public static void setupConfig() {
        configFile = new File(FindItemAddOn.getInstance().getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException ignored) {
            }
        }

        configFileConfiguration = YamlConfiguration.loadConfiguration(configFile);
    }

    public static void saveConfig() {
        try {
            configFileConfiguration.save(configFile);
        } catch (IOException ignored) {
        }
    }

    public static FileConfiguration get() {
        return configFileConfiguration;
    }

    public static void reloadConfig() {
        configFileConfiguration = YamlConfiguration.loadConfiguration(configFile);
    }
}
