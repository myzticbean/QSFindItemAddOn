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
package uk.mangostudios.finditemaddon;

import uk.mangostudios.finditemaddon.cache.HiddenShopsCache;
import uk.mangostudios.finditemaddon.commands.CmdExecutorHandler;
import uk.mangostudios.finditemaddon.commands.CommandManager;
import uk.mangostudios.finditemaddon.commands.impl.BuyCommand;
import uk.mangostudios.finditemaddon.commands.impl.HideShopCommands;
import uk.mangostudios.finditemaddon.commands.impl.ReloadCommand;
import uk.mangostudios.finditemaddon.commands.impl.SellCommand;
import uk.mangostudios.finditemaddon.config.ConfigProvider;
import uk.mangostudios.finditemaddon.config.ConfigManager;
import uk.mangostudios.finditemaddon.external.PlayerWarpsHandler;
import uk.mangostudios.finditemaddon.listener.HeadDatabaseApiListener;
import uk.mangostudios.finditemaddon.listener.PlayerWarpCreateEventListener;
import uk.mangostudios.finditemaddon.listener.PlayerWarpRemoveEventListener;
import uk.mangostudios.finditemaddon.external.QuickShopHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class FindItemAddOn extends JavaPlugin {

    private static FindItemAddOn plugin;

    private static ConfigProvider configProvider;
    private static QuickShopHandler quickShopApi;

    private CmdExecutorHandler cmdExecutorHandler;
    private CommandManager commandManager;

    private HiddenShopsCache hiddenShopsCache;

    private Economy econ;

    @Override
    public void onEnable() {
        plugin = this;

        // Handle config file
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        ConfigManager.setupConfig();
        ConfigManager.get().options().copyDefaults(true);
        ConfigManager.saveConfig();
        initConfigProvider();

        this.initCommands();
        this.initVaultEconomy();

        // Init cache
        hiddenShopsCache = new HiddenShopsCache(plugin);

        // Run plugin startup logic after server is done loading
        Bukkit.getScheduler().scheduleSyncDelayedTask(FindItemAddOn.getInstance(), this::runPluginStartupTasks);
    }

    @Override
    public void onDisable() {
        hiddenShopsCache.shutdown();
    }

    private void runPluginStartupTasks() {
        quickShopApi = new QuickShopHandler();
        PlayerWarpsHandler.setup();

        this.registerListeners();
    }

    private void initCommands() {
        cmdExecutorHandler = new CmdExecutorHandler();
        commandManager = new CommandManager();
        commandManager.registerCommand(
                new BuyCommand(cmdExecutorHandler),
                new SellCommand(cmdExecutorHandler),
                new ReloadCommand(cmdExecutorHandler),
                new HideShopCommands()
        );
    }

    private void registerListeners() {
        // Register PlayerWarpsPlugin listeners
        this.getServer().getPluginManager().registerEvents(new PlayerWarpRemoveEventListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerWarpCreateEventListener(), this);

        // Register HeadDatabaseAPI listener
        this.getServer().getPluginManager().registerEvents(new HeadDatabaseApiListener(), this);
    }

    private void initVaultEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        econ = rsp.getProvider();
    }

    public static ConfigProvider getConfigProvider() {
        return configProvider;
    }

    public static void initConfigProvider() {
        configProvider = new ConfigProvider();
    }

    public static QuickShopHandler getQsApiInstance() {
        return quickShopApi;
    }

    public static FindItemAddOn getInstance() {
        return plugin;
    }

    public Economy getEconomy() {
        return econ;
    }

}
