package me.ronsane.finditemaddon.finditemaddon;

import com.earth2me.essentials.Essentials;
import me.ronsane.finditemaddon.finditemaddon.Commands.FindItemCmdTabCompleter;
import me.ronsane.finditemaddon.finditemaddon.Commands.FindItemCommand;
import me.ronsane.finditemaddon.finditemaddon.Events.EventInventoryClick;
import me.ronsane.finditemaddon.finditemaddon.Events.EventPlayerCommandSend;
import me.ronsane.finditemaddon.finditemaddon.Utils.CommonUtils;
import me.ronsane.finditemaddon.finditemaddon.Utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class FindItemAddOn extends JavaPlugin {

    private static Plugin plugin;
    public FindItemAddOn() { plugin = this; }
    public static Plugin getInstance() { return plugin; }
    public static String PluginPrefix;
    public static Essentials essAPI;
    public static String serverVersion;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.saveDefaultConfig();
        PluginPrefix = CommonUtils.parseColors(Objects.requireNonNull(getInstance().getConfig().getString("PluginPrefix")) + "&r");
        if(!Bukkit.getPluginManager().isPluginEnabled("QuickShop")) {
            getServer().getPluginManager().disablePlugin(this);
            Bukkit.getLogger().info(
                    CommonUtils.parseColors(
                            "&cQuickShop is required to use this addon. Please install QuickShop and try again!"));
            return;
        }
        else {
            Bukkit.getLogger().info(PluginPrefix + "QuickShop found");
        }
        if(!Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
            getServer().getPluginManager().disablePlugin(this);
            Bukkit.getLogger().info(
                    CommonUtils.parseColors(
                            "&cEssentialsX is required to use this addon. Please install EssentialsX and try again!"));
            return;
        }
        else {
            essAPI = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
            Bukkit.getLogger().info(PluginPrefix + "Hooked to Essentials");
        }

        Bukkit.getLogger().info(PluginPrefix + CommonUtils.parseColors("A QuickShop AddOn by &cronsane"));
        Bukkit.getLogger().info(PluginPrefix + "Enabling plugin");
        serverVersion = Bukkit.getServer().getVersion();
        Bukkit.getLogger().info(PluginPrefix + "Server version found: " + serverVersion);
        initCommands();
        initEvents();
        LocationUtils.initDamagingBlocksList();
        LocationUtils.initNonSuffocatingBlocksList();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void initCommands() {
        Bukkit.getLogger().info(PluginPrefix + "Registering commands");
        Objects.requireNonNull(this.getCommand("finditem")).setExecutor(new FindItemCommand());
        Objects.requireNonNull(this.getCommand("finditem")).setTabCompleter(new FindItemCmdTabCompleter());
    }

    private void initEvents() {
        Bukkit.getLogger().info(PluginPrefix + "Registering events");
        this.getServer().getPluginManager().registerEvents(new EventInventoryClick(), this);
        this.getServer().getPluginManager().registerEvents(new EventPlayerCommandSend(), this);
    }
}
