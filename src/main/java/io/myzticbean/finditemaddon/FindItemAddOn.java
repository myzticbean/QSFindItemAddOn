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
package io.myzticbean.finditemaddon;

import io.myzticbean.finditemaddon.Commands.CmdExecutorHandler;
import io.myzticbean.finditemaddon.Commands.CommandManager;
import io.myzticbean.finditemaddon.Commands.impl.BuyCommand;
import io.myzticbean.finditemaddon.Commands.impl.ReloadCommand;
import io.myzticbean.finditemaddon.Commands.impl.SellCommand;
import io.myzticbean.finditemaddon.ConfigUtil.ConfigProvider;
import io.myzticbean.finditemaddon.ConfigUtil.ConfigSetup;
import io.myzticbean.finditemaddon.Dependencies.PlayerWarpsPlugin;
import io.myzticbean.finditemaddon.Listeners.HeadDatabaseApiListener;
import io.myzticbean.finditemaddon.Listeners.PWPlayerWarpCreateEventListener;
import io.myzticbean.finditemaddon.Listeners.PWPlayerWarpRemoveEventListener;
import io.myzticbean.finditemaddon.Dependencies.QuickShopApi;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class FindItemAddOn extends JavaPlugin {

    private static FindItemAddOn plugin;

    private static ConfigProvider configProvider;
    private static QuickShopApi quickShopApi;

    private CmdExecutorHandler cmdExecutorHandler;
    private CommandManager commandManager;

    private Economy econ;

    @Override
    public void onEnable() {
        plugin = this;

        // Handle config file
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        ConfigSetup.setupConfig();
        ConfigSetup.get().options().copyDefaults(true);
        ConfigSetup.saveConfig();
        initConfigProvider();

        this.initCommands();
        this.initVaultEconomy();

        // Run plugin startup logic after server is done loading
        Bukkit.getScheduler().scheduleSyncDelayedTask(FindItemAddOn.getInstance(), this::runPluginStartupTasks);
    }

    private void runPluginStartupTasks() {
        quickShopApi = new QuickShopApi();
        PlayerWarpsPlugin.setup();

        this.registerListeners();
    }

    private void initCommands() {
        cmdExecutorHandler = new CmdExecutorHandler();
        commandManager = new CommandManager();
        commandManager.registerCommand(
                new BuyCommand(cmdExecutorHandler),
                new SellCommand(cmdExecutorHandler),
                new ReloadCommand(cmdExecutorHandler)
        );
    }

    private void registerListeners() {
        // Register PlayerWarpsPlugin listeners
        this.getServer().getPluginManager().registerEvents(new PWPlayerWarpRemoveEventListener(), this);
        this.getServer().getPluginManager().registerEvents(new PWPlayerWarpCreateEventListener(), this);

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

    public static QuickShopApi getQsApiInstance() {
        return quickShopApi;
    }

    public static FindItemAddOn getInstance() {
        return plugin;
    }

    public Economy getEconomy() {
        return econ;
    }

}
