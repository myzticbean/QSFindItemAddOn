package me.ronsane.finditemaddon.finditemaddon;

import com.earth2me.essentials.Essentials;
import me.ronsane.finditemaddon.finditemaddon.Commands.FindItemCmdTabCompleter;
import me.ronsane.finditemaddon.finditemaddon.Commands.FindItemCommand;
import me.ronsane.finditemaddon.finditemaddon.ConfigProvider.ConfigProvider;
import me.ronsane.finditemaddon.finditemaddon.Dependencies.PWarpPlugin;
import me.ronsane.finditemaddon.finditemaddon.Dependencies.PlayerWarpsPlugin;
import me.ronsane.finditemaddon.finditemaddon.GUIHandler.PlayerMenuUtility;
import me.ronsane.finditemaddon.finditemaddon.Listeners.MenuListener;
import me.ronsane.finditemaddon.finditemaddon.Listeners.PlayerCommandSendListener;
import me.ronsane.finditemaddon.finditemaddon.Metrics.Metrics;
import me.ronsane.finditemaddon.finditemaddon.Utils.LocationUtils;
import me.ronsane.finditemaddon.finditemaddon.Utils.LoggerUtils;
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
    public static String PluginInGamePrefix;
    public static Essentials essAPI;
    public static String serverVersion;
    private final static int bsPLUGIN_METRIC_ID = 12382;
    private static ConfigProvider configProvider;

    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<Player, PlayerMenuUtility>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        LoggerUtils.logInfo("A QuickShop AddOn by &cronsane");
        this.saveDefaultConfig();
        configProvider = new ConfigProvider();
        PluginInGamePrefix = configProvider.PLUGIN_PREFIX;
        if(!Bukkit.getPluginManager().isPluginEnabled("QuickShop")) {
            LoggerUtils.logError("&cQuickShop is required to use this addon. Please install QuickShop and try again!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        else {
            LoggerUtils.logInfo("Found QuickShop");
        }
        if(!Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
            LoggerUtils.logError("&cEssentialsX is required to use this addon. Please install EssentialsX and try again!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        else {
            essAPI = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
            LoggerUtils.logInfo("Found Essentials");
        }

        serverVersion = Bukkit.getServer().getVersion();
        LoggerUtils.logInfo("Server version found: " + serverVersion);
        initCommands();
        initEvents();
        LocationUtils.initDamagingBlocksList();
        LocationUtils.initNonSuffocatingBlocksList();

        PlayerWarpsPlugin.setup();
        PWarpPlugin.setup();

        // init metrics
        LoggerUtils.logInfo("Registering bStats metrics");
        Metrics metrics = new Metrics(this, bsPLUGIN_METRIC_ID);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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
