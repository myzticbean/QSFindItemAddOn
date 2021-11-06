package me.ronsane.finditemaddon.finditemaddon;

import me.ronsane.finditemaddon.finditemaddon.Commands.FindItemCmdTabCompleter;
import me.ronsane.finditemaddon.finditemaddon.Commands.FindItemCommand;
import me.ronsane.finditemaddon.finditemaddon.ConfigUtil.ConfigProvider;
import me.ronsane.finditemaddon.finditemaddon.ConfigUtil.ConfigSetup;
import me.ronsane.finditemaddon.finditemaddon.Dependencies.EssentialsXPlugin;
import me.ronsane.finditemaddon.finditemaddon.Dependencies.PlayerWarpsPlugin;
import me.ronsane.finditemaddon.finditemaddon.Dependencies.WGPlugin;
import me.ronsane.finditemaddon.finditemaddon.GUIHandler.PlayerMenuUtility;
import me.ronsane.finditemaddon.finditemaddon.Listeners.MenuListener;
import me.ronsane.finditemaddon.finditemaddon.Listeners.PlayerCommandSendListener;
import me.ronsane.finditemaddon.finditemaddon.Metrics.Metrics;
import me.ronsane.finditemaddon.finditemaddon.ScheduledTasks.Task15MinInterval;
import me.ronsane.finditemaddon.finditemaddon.Utils.HiddenShopStorageUtil;
import me.ronsane.finditemaddon.finditemaddon.Utils.LoggerUtils;
import me.ronsane.finditemaddon.finditemaddon.Utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Objects;

public final class FindItemAddOn extends JavaPlugin {

    private static Plugin plugin;
    public FindItemAddOn() { plugin = this; }
    public static Plugin getInstance() { return plugin; }
    public static String serverVersion;
    private final static int bsPLUGIN_METRIC_ID = 12382;
    private static ConfigProvider configProvider;

    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        LoggerUtils.logInfo("A QuickShop AddOn by &cronsane");
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        ConfigSetup.setupConfig();
        ConfigSetup.get().options().copyDefaults(true);
        ConfigSetup.checkForMissingProperties();
        ConfigSetup.saveConfig();
        initConfigProvider();

        if(!Bukkit.getPluginManager().isPluginEnabled("QuickShop")) {
            LoggerUtils.logError("&cQuickShop is required to use this addon. Please install QuickShop and try again!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        else {
            LoggerUtils.logInfo("Found QuickShop");
        }

        serverVersion = Bukkit.getServer().getVersion();
        LoggerUtils.logInfo("Server version found: " + serverVersion);
        initCommands();
        initEvents();

        PlayerWarpsPlugin.setup();
//        PWarpPlugin.setup();
        EssentialsXPlugin.setup();
        WGPlugin.setup();

        HiddenShopStorageUtil.loadHiddenShopsFromFile();

        // Initiate batch tasks
        LoggerUtils.logInfo("Registering tasks");
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Task15MinInterval(), 0, 15*60*20);

        // init metrics
        LoggerUtils.logInfo("Registering bStats metrics");
        Metrics metrics = new Metrics(this, bsPLUGIN_METRIC_ID);

        // Check for plugin updates
        new UpdateChecker(this, 95104).getLatestVersion(version -> {
            if(this.getDescription().getVersion().equalsIgnoreCase(version)) {
                LoggerUtils.logInfo("&2Plugin is up to date!");
            } else {
                LoggerUtils.logWarning("Plugin has an update! (Version: " + this.getDescription().getVersion() + ")");
                LoggerUtils.logWarning("Download the latest version here: &7https://www.spigotmc.org/resources/95104/");
            }
        });

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        HiddenShopStorageUtil.saveHiddenShopsToFile();
        LoggerUtils.logInfo("Bye!");
    }

    private void initCommands() {
        LoggerUtils.logInfo("Registering commands");
        Objects.requireNonNull(this.getCommand("finditem")).setExecutor(new FindItemCommand());
        Objects.requireNonNull(this.getCommand("finditem")).setTabCompleter(new FindItemCmdTabCompleter());
    }

    private void initEvents() {
        LoggerUtils.logInfo("Registering events");
        this.getServer().getPluginManager().registerEvents(new PlayerCommandSendListener(), this);
        this.getServer().getPluginManager().registerEvents(new MenuListener(), this);
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
}
