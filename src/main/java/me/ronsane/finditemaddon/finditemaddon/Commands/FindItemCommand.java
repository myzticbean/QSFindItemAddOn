package me.ronsane.finditemaddon.finditemaddon.Commands;

import me.ronsane.finditemaddon.finditemaddon.ConfigUtil.ConfigSetup;
import me.ronsane.finditemaddon.finditemaddon.FindItemAddOn;
import me.ronsane.finditemaddon.finditemaddon.GUIHandler.Menus.FoundShopsMenu;
import me.ronsane.finditemaddon.finditemaddon.QuickShopHandler.QuickShopAPIHandler;
import me.ronsane.finditemaddon.finditemaddon.Utils.CommonUtils;
import me.ronsane.finditemaddon.finditemaddon.Utils.LoggerUtils;
import me.ronsane.finditemaddon.finditemaddon.Utils.WarpUtils.WarpUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.maxgamer.quickshop.shop.Shop;

import java.util.ArrayList;
import java.util.List;

public class FindItemCommand implements CommandExecutor {

    private final List<String> findItemCommandAliases;

    public FindItemCommand() {
        findItemCommandAliases = new ArrayList<>();
        findItemCommandAliases.add("finditem");
        findItemCommandAliases.add("searchitem");
        findItemCommandAliases.add("shopsearch");
        findItemCommandAliases.add("searchshop");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, String label, String[] args) {
        if(findItemCommandAliases.contains(label.toLowerCase())) {
            if (!(sender instanceof Player)) {
                if(args.length == 1){
                    if(args[0].equalsIgnoreCase("reload")) {
//                        FindItemAddOn.getInstance().reloadConfig();
                        ConfigSetup.reloadConfig();
                        ConfigSetup.checkForMissingProperties();
                        ConfigSetup.saveConfig();
                        FindItemAddOn.initConfigProvider();
                        List<Shop> allServerShops = new QuickShopAPIHandler().getQsPluginInstance().getShopManager().getAllShops();
                        if(allServerShops.size() == 0) {
                            LoggerUtils.logWarning("&6Found &e0 &6shops on the server. If you ran &e/qs reload &6recently, please restart your server!");
                        }
                        else {
                            LoggerUtils.logInfo("&aFound &e" + allServerShops.size() + " &ashops on the server.");
                        }
                    }
                    else if(args[0].equalsIgnoreCase("restart")) {
                        Bukkit.getPluginManager().disablePlugin(FindItemAddOn.getInstance());
                        Bukkit.getPluginManager().enablePlugin(FindItemAddOn.getPlugin(FindItemAddOn.class));
//                        FindItemAddOn.getInstance().reloadConfig();
//                        FindItemAddOn.initConfigProvider();
                        LoggerUtils.logInfo("&aPlugin restarted!");
                        List<Shop> allServerShops = new QuickShopAPIHandler().getQsPluginInstance().getShopManager().getAllShops();
                        if(allServerShops.size() == 0) {
                            LoggerUtils.logWarning("&6Found &e0 &6shops on the server. If you ran &e/qs reload &6recently, please restart your server!");
                        }
                        else {
                            LoggerUtils.logInfo("&aFound &e" + allServerShops.size() + " &ashops on the server.");
                        }
                    }
                    else if(args[0].equalsIgnoreCase("updatelist")) {
                        WarpUtils.updateWarps();
                    }
                    else {
                        LoggerUtils.logInfo("&CIncorrect usage! Try &e/finditem &freload");
                    }
                }
                else {
                    LoggerUtils.logInfo("This command can only be run from in game");
                }
            }
            else {
                Player player = (Player) sender;
                if(player.hasPermission("finditem.use")) {
                    if(args.length < 1 || args.length > 2) {
                        player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_INCORRECT_USAGE_MSG));
                    }
                    else if(args.length == 1){
                        if(args[0].equalsIgnoreCase("reload")) {
                            if(player.hasPermission("finditem.reload") || player.hasPermission("finditem.admin")) {
                                ConfigSetup.reloadConfig();
                                ConfigSetup.checkForMissingProperties();
                                ConfigSetup.saveConfig();
                                FindItemAddOn.initConfigProvider();
                                player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&aConfig reloaded!"));
                                List<Shop> allServerShops = new QuickShopAPIHandler().getQsPluginInstance().getShopManager().getAllShops();
                                if(allServerShops.size() == 0) {
                                    player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&6Found &e0 &6shops on the server. If you ran &e/qs reload &6recently, please restart your server!"));
                                }
                                else {
                                    player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&aFound &e" + allServerShops.size() + " &ashops on the server."));
                                }
                            }
                            else {
                                player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&cNo permission!"));
                            }
                        }
                        else if(args[0].equalsIgnoreCase("restart")) {
                            if(player.hasPermission("finditem.restart") || player.hasPermission("finditem.admin")) {
                                Bukkit.getPluginManager().disablePlugin(FindItemAddOn.getInstance());
                                Bukkit.getPluginManager().enablePlugin(FindItemAddOn.getPlugin(FindItemAddOn.class));
//                                FindItemAddOn.getInstance().reloadConfig();
//                                FindItemAddOn.initConfigProvider();
                                player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&aPlugin restarted!"));
                                List<Shop> allServerShops = new QuickShopAPIHandler().getQsPluginInstance().getShopManager().getAllShops();
                                if(allServerShops.size() == 0) {
                                    player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&6Found &e0 &6shops on the server. If you ran &e/qs reload &6recently, please restart your server!"));
                                }
                                else {
                                    player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&aFound &e" + allServerShops.size() + " &ashops on the server."));
                                }
                            }
                            else {
                                player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&cNo permission!"));
                            }
                        }
                        else if(args[0].equalsIgnoreCase("updatelist")) {
                            if(player.hasPermission("finditem.admin")) {
                                player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&eUpdating warps/regions list..."));
                                WarpUtils.updateWarps();
                                player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&eUpdate complete!"));
                            }
                            else {
                                player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&cNo permission!"));
                            }
                        }
                        else {
                            player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&CIncorrect usage! Try &e/finditem &freload"));
                        }
                    }
                    else {
                        if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().SHOP_SEARCH_LOADING_MSG)) {
                            player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + FindItemAddOn.getConfigProvider().SHOP_SEARCH_LOADING_MSG));
                        }
                        boolean isBuying;
                        if(StringUtils.isEmpty(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_BUY_AUTOCOMPLETE)
                                || StringUtils.containsIgnoreCase(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_BUY_AUTOCOMPLETE, " ")) {
                            isBuying = args[0].equalsIgnoreCase("to-buy");
                        }
                        else {
                            isBuying = args[0].equalsIgnoreCase(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_BUY_AUTOCOMPLETE);
                        }
                        Material mat = Material.getMaterial(args[1]);
                        if(mat != null) {
                            LoggerUtils.logDebugInfo("Material found: " + mat.toString());
//                            List<Shop> searchResult = QuickShopAPIHandler.findItemBasedOnTypeFromAllShops(new ItemStack(mat), isBuying);
                            List<Shop> searchResult = new QuickShopAPIHandler().findItemBasedOnTypeFromAllShops(new ItemStack(mat), isBuying);
                            if(searchResult.size() > 0) {
                                FoundShopsMenu menu = new FoundShopsMenu(FindItemAddOn.getPlayerMenuUtility(player), searchResult);
                                menu.open(searchResult);
                            }
                            else {
                                if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().NO_SHOP_FOUND_MSG)) {
                                    player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + FindItemAddOn.getConfigProvider().NO_SHOP_FOUND_MSG));
                                }
                            }
                        }
                        else {
                            LoggerUtils.logDebugInfo("Material not found! Performing query based search..");
//                            List<Shop> searchResult = QuickShopAPIHandler.findItemBasedOnDisplayNameFromAllShops(args[1], isBuying);
                            List<Shop> searchResult = new QuickShopAPIHandler().findItemBasedOnDisplayNameFromAllShops(args[1], isBuying);
                            if(searchResult.size() > 0) {
                                FoundShopsMenu menu = new FoundShopsMenu(FindItemAddOn.getPlayerMenuUtility(player), searchResult);
                                menu.open(searchResult);
                            }
                            else {
                                // Invalid Material
                                if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_INVALID_MATERIAL_MSG)) {
                                    player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_INVALID_MATERIAL_MSG));
                                }
                            }
                        }
                    }
                }
                else {
                    // No Permission
                    if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_NO_PERMISSION_MSG)) {
                        player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_NO_PERMISSION_MSG));
                    }
                }
            }
        }
        return true;
    }
}
