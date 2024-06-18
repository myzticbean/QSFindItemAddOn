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
package io.myzticbean.finditemaddon;

import io.myzticbean.finditemaddon.Commands.SAPICommands.BuySubCmd;
import io.myzticbean.finditemaddon.Commands.SAPICommands.HideShopSubCmd;
import io.myzticbean.finditemaddon.Commands.SAPICommands.ReloadSubCmd;
import io.myzticbean.finditemaddon.Commands.SAPICommands.RevealShopSubCmd;
import io.myzticbean.finditemaddon.Commands.SAPICommands.SellSubCmd;
import io.myzticbean.finditemaddon.ConfigUtil.ConfigProvider;
import io.myzticbean.finditemaddon.ConfigUtil.ConfigSetup;
import io.myzticbean.finditemaddon.Dependencies.EssentialsXPlugin;
import io.myzticbean.finditemaddon.Dependencies.PlayerWarpsPlugin;
import io.myzticbean.finditemaddon.Dependencies.WGPlugin;
import io.myzticbean.finditemaddon.Handlers.GUIHandler.PlayerMenuUtility;
import io.myzticbean.finditemaddon.Listeners.MenuListener;
import io.myzticbean.finditemaddon.Listeners.PWPlayerWarpCreateEventListener;
import io.myzticbean.finditemaddon.Listeners.PWPlayerWarpRemoveEventListener;
import io.myzticbean.finditemaddon.Listeners.PlayerCommandSendEventListener;
import io.myzticbean.finditemaddon.Listeners.PlayerJoinEventListener;
import io.myzticbean.finditemaddon.Listeners.PluginEnableEventListener;
import io.myzticbean.finditemaddon.Metrics.Metrics;
import io.myzticbean.finditemaddon.QuickShopHandler.QSApi;
import io.myzticbean.finditemaddon.QuickShopHandler.QSHikariAPIHandler;
import io.myzticbean.finditemaddon.QuickShopHandler.QSReremakeAPIHandler;
import io.myzticbean.finditemaddon.ScheduledTasks.Task15MinInterval;
import io.myzticbean.finditemaddon.Utils.Defaults.PlayerPermsEnum;
import io.myzticbean.finditemaddon.Utils.JsonStorageUtils.ShopSearchActivityStorageUtil;
import io.myzticbean.finditemaddon.Utils.LoggerUtils;
import io.myzticbean.finditemaddon.Utils.UpdateChecker;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import me.kodysimpson.simpapi.command.CommandManager;
import me.kodysimpson.simpapi.command.SubCommand;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author myzticbean
 */
public final class FindItemAddOn extends JavaPlugin {

    // ONLY FOR SNAPSHOT BUILDS
    // Change it to whenever you want your snapshot trial build to expire
    private static final boolean ENABLE_TRIAL_PERIOD = false;
    private static final int TRIAL_END_YEAR = 2024, TRIAL_END_MONTH = 5, TRIAL_END_DAY = 5;

    private static Plugin plugin;
    public FindItemAddOn() { plugin = this; }
    public static Plugin getInstance() { return plugin; }
    public static String serverVersion;
    private static final int BS_PLUGIN_METRIC_ID = 12382;
    private static final int SPIGOT_PLUGIN_ID = 95104;
    private static final int REPEATING_TASK_SCHEDULE_MINS = 15*60*20;
    private static ConfigProvider configProvider;
    private static boolean isPluginOutdated = false;
    private static boolean qSReremakeInstalled = false;
    private static boolean qSHikariInstalled = false;
    private static QSApi qsApi;

    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    @Override
    public void onLoad() {
        LoggerUtils.logInfo("A Shop Search AddOn for QuickShop developed by myzticbean");

        // Show warning if it's a snapshot build
        if(this.getDescription().getVersion().toLowerCase().contains("snapshot")) {
            LoggerUtils.logWarning("This is a SNAPSHOT build! NOT recommended for production servers.");
            LoggerUtils.logWarning("If you find any bugs, please report them here: https://github.com/myzticbean/QSFindItemAddOn/issues");
        }
    }
    @Override
    public void onEnable() {

        if(ENABLE_TRIAL_PERIOD) {
            LoggerUtils.logWarning("THIS IS A TRIAL BUILD!");
            LocalDateTime trialEndDate = LocalDate.of(TRIAL_END_YEAR, TRIAL_END_MONTH, TRIAL_END_DAY).atTime(LocalTime.MIDNIGHT);
            LocalDateTime today = LocalDateTime.now();
            Duration duration = Duration.between(trialEndDate, today);
            boolean hasPassed = Duration.ofDays(ChronoUnit.DAYS.between(today, trialEndDate)).isNegative();
            if(hasPassed) {
                LoggerUtils.logError("Your trial has expired! Please contact the developer.");
                getServer().getPluginManager().disablePlugin(this);
                return;
            } else {
                LoggerUtils.logWarning("You have " + Math.abs(duration.toDays()) + " days remaining in your trial.");
            }
        }

        if(!Bukkit.getPluginManager().isPluginEnabled("QuickShop")
                && !Bukkit.getPluginManager().isPluginEnabled("QuickShop-Hikari")) {
            LoggerUtils.logInfo("Delaying QuickShop hook as they are not enabled yet");
        }
        else if(Bukkit.getPluginManager().isPluginEnabled("QuickShop")) {
            qSReremakeInstalled = true;
        }
        else {
            qSHikariInstalled = true;
        }

        // Registering Bukkit event listeners
        initBukkitEventListeners();

        // Handle config file
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        ConfigSetup.setupConfig();
        ConfigSetup.get().options().copyDefaults(true);
        ConfigSetup.checkForMissingProperties();
        ConfigSetup.saveConfig();
        initConfigProvider();
        ConfigSetup.copySampleConfig();

        initCommands();

        // Run plugin startup logic after server is done loading
        Bukkit.getScheduler().scheduleSyncDelayedTask(FindItemAddOn.getInstance(), () -> runPluginStartupTasks());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if(qsApi != null) {
            ShopSearchActivityStorageUtil.saveShopsToFile();
        }
        else if(!ENABLE_TRIAL_PERIOD) {
            LoggerUtils.logError("Uh oh! Looks like either this plugin has crashed or you don't have QuickShop-Hikari or QuickShop-Reremake installed.");
        }
        LoggerUtils.logInfo("Bye!");
    }

    private void runPluginStartupTasks() {

        serverVersion = Bukkit.getServer().getVersion();
        LoggerUtils.logInfo("Server version found: " + serverVersion);

        if(!isQSReremakeInstalled() && !isQSHikariInstalled()) {
            LoggerUtils.logError("QuickShop is required to use this addon. Please install QuickShop and try again!");
            LoggerUtils.logError("Both QuickShop-Hikari and QuickShop-Reremake are supported by this addon.");
            LoggerUtils.logError("Download links:");
            LoggerUtils.logError("» QuickShop-Hikari: https://www.spigotmc.org/resources/100125");
            LoggerUtils.logError("» QuickShop-Reremake (Support ending soon): https://www.spigotmc.org/resources/62575");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        else if(isQSReremakeInstalled()) {
            LoggerUtils.logInfo("Found QuickShop-Reremake");
            qsApi = new QSReremakeAPIHandler();
            qsApi.registerSubCommand();
        } else {
            LoggerUtils.logInfo("Found QuickShop-Hikari");
            qsApi = new QSHikariAPIHandler();
            qsApi.registerSubCommand();
        }

        // Load all hidden shops from file
        ShopSearchActivityStorageUtil.loadShopsFromFile();

        // v2.0.0.0 - Migrating hiddenShops.json to shops.json
        ShopSearchActivityStorageUtil.migrateHiddenShopsToShopsJson();

        PlayerWarpsPlugin.setup();
        EssentialsXPlugin.setup();
        WGPlugin.setup();

        initExternalPluginEventListeners();

        // Initiate batch tasks
        LoggerUtils.logInfo("Registering tasks");
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Task15MinInterval(), 0, REPEATING_TASK_SCHEDULE_MINS);

        // init metrics
        LoggerUtils.logInfo("Registering anonymous bStats metrics");
        Metrics metrics = new Metrics(this, BS_PLUGIN_METRIC_ID);

        // Check for plugin updates
        new UpdateChecker(SPIGOT_PLUGIN_ID).getLatestVersion(version -> {
            if(this.getDescription().getVersion().equalsIgnoreCase(version)) {
                LoggerUtils.logInfo("Oh awesome! Plugin is up to date");
            } else {
                isPluginOutdated = true;
                if(version.toLowerCase().contains("snapshot")) {
                    LoggerUtils.logWarning("Plugin has a new snapshot version available! (Version: " + version + ")");
                }
                else {
                    LoggerUtils.logWarning("Plugin has a new update available! (Version: " + version + ")");
                }
                LoggerUtils.logWarning("Download here: https://www.spigotmc.org/resources/" + SPIGOT_PLUGIN_ID + "/");
            }
        });
    }

    private void initCommands() {
        LoggerUtils.logInfo("Registering commands");
        initFindItemCmd();
        initFindItemAdminCmd();
    }

    private void initBukkitEventListeners() {
        LoggerUtils.logInfo("Registering Bukkit event listeners");
        this.getServer().getPluginManager().registerEvents(new PluginEnableEventListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerCommandSendEventListener(), this);
        this.getServer().getPluginManager().registerEvents(new MenuListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinEventListener(), this);
    }
    private void initExternalPluginEventListeners() {
        LoggerUtils.logInfo("Registering external plugin event listeners");
        if(PlayerWarpsPlugin.getIsEnabled()) {
            this.getServer().getPluginManager().registerEvents(new PWPlayerWarpRemoveEventListener(), this);
            this.getServer().getPluginManager().registerEvents(new PWPlayerWarpCreateEventListener(), this);
        }
    }
    
    public static ConfigProvider getConfigProvider() {
        return configProvider;
    }

    public static void initConfigProvider() {
        configProvider = new ConfigProvider();
    }

    public static PlayerMenuUtility getPlayerMenuUtility(Player p){
        PlayerMenuUtility playerMenuUtility;
        if(playerMenuUtilityMap.containsKey(p)) {
            return playerMenuUtilityMap.get(p);
        }
        else {
            playerMenuUtility = new PlayerMenuUtility(p);
            playerMenuUtilityMap.put(p, playerMenuUtility);
            return playerMenuUtility;
        }
    }

    public static boolean getPluginOutdated() {
        return isPluginOutdated;
    }

    public static int getPluginID() {
        return SPIGOT_PLUGIN_ID;
    }

    private void initFindItemCmd() {
        List<String> alias;
        if(StringUtils.isEmpty(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_SELL_AUTOCOMPLETE)
                || StringUtils.containsIgnoreCase(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_SELL_AUTOCOMPLETE, " ")) {
            alias = Arrays.asList("shopsearch", "searchshop", "searchitem");
        }
        else {
            alias = FindItemAddOn.getConfigProvider().FIND_ITEM_COMMAND_ALIAS;
        }

        Class<? extends SubCommand>[] subCommands;
        if(FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_REMOVE_HIDE_REVEAL_SUBCMDS) {
            subCommands = new Class[] {
                    SellSubCmd.class, BuySubCmd.class
            };
        } else {
            subCommands = new Class[] {
                    SellSubCmd.class, BuySubCmd.class, HideShopSubCmd.class, RevealShopSubCmd.class
            };
        }
        // Register the subcommands under a core command
        try {
            CommandManager.createCoreCommand(
                    this,
                    "finditem",
                    "Search for items from all shops using an interactive GUI",
                    "/finditem",
                    (commandSender, subCommandList) -> {
                        commandSender.sendMessage(ColorTranslator.translateColorCodes(""));
                        commandSender.sendMessage(ColorTranslator.translateColorCodes("&7------------------------"));
                        commandSender.sendMessage(ColorTranslator.translateColorCodes("&6&lShop Search Commands"));
                        commandSender.sendMessage(ColorTranslator.translateColorCodes("&7------------------------"));
                        for (SubCommand subCommand : subCommandList) {
                            commandSender.sendMessage(ColorTranslator.translateColorCodes("&#ff9933" + subCommand.getSyntax() + " &#a3a3c2" + subCommand.getDescription()));
                        }
                        commandSender.sendMessage(ColorTranslator.translateColorCodes(""));
                        commandSender.sendMessage(ColorTranslator.translateColorCodes("&#b3b300Command alias:"));
                        alias.forEach(alias_i -> commandSender.sendMessage(ColorTranslator.translateColorCodes("&8&l» &#2db300/" + alias_i)));
                        commandSender.sendMessage(ColorTranslator.translateColorCodes(""));
                    },
                    alias,
                    subCommands);
            LoggerUtils.logInfo("Registered /finditem command");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LoggerUtils.logError(e);
        }
    }

    private void initFindItemAdminCmd() {
        List<String> alias = List.of("fiadmin");
        try {
            CommandManager.createCoreCommand(
                    this,
                    "finditemadmin",
                    "Admin command for Shop Search addon",
                    "/finditemadmin",
                    (commandSender, subCommandList) -> {
                        if (
                                (commandSender.isOp())
                                        || (!commandSender.isOp() && (commandSender.hasPermission(PlayerPermsEnum.FINDITEM_ADMIN.value())
                                        || commandSender.hasPermission(PlayerPermsEnum.FINDITEM_RELOAD.value())))
                        ) {
                            commandSender.sendMessage(ColorTranslator.translateColorCodes(""));
                            commandSender.sendMessage(ColorTranslator.translateColorCodes("&7-----------------------------"));
                            commandSender.sendMessage(ColorTranslator.translateColorCodes("&6&lShop Search Admin Commands"));
                            commandSender.sendMessage(ColorTranslator.translateColorCodes("&7-----------------------------"));

                            for (SubCommand subCommand : subCommandList) {
                                commandSender.sendMessage(ColorTranslator.translateColorCodes("&#ff1a1a" + subCommand.getSyntax() + " &#a3a3c2" + subCommand.getDescription()));
                            }
                            commandSender.sendMessage(ColorTranslator.translateColorCodes(""));
                            commandSender.sendMessage(ColorTranslator.translateColorCodes("&#b3b300Command alias:"));
                            alias.forEach(alias_i -> commandSender.sendMessage(ColorTranslator.translateColorCodes("&8&l» &#2db300/" + alias_i)));
                            commandSender.sendMessage(ColorTranslator.translateColorCodes(""));
                        }
                    },
                    alias,
                    ReloadSubCmd.class);
            LoggerUtils.logInfo("Registered /finditemadmin command");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LoggerUtils.logError(e);
        }
    }

    public static boolean isQSReremakeInstalled() {
        return qSReremakeInstalled;
    }

    public static boolean isQSHikariInstalled() {
        return qSHikariInstalled;
    }

    public static void setQSReremakeInstalled(boolean qSReremakeInstalled) {
        FindItemAddOn.qSReremakeInstalled = qSReremakeInstalled;
    }

    public static void setQSHikariInstalled(boolean qSHikariInstalled) {
        FindItemAddOn.qSHikariInstalled = qSHikariInstalled;
    }

    public static QSApi getQsApiInstance() {
        return qsApi;
    }

}
