package me.ronsane.finditemaddon.finditemaddon.Commands;

import me.ronsane.finditemaddon.finditemaddon.ConfigUtil.ConfigSetup;
import me.ronsane.finditemaddon.finditemaddon.FindItemAddOn;
import me.ronsane.finditemaddon.finditemaddon.GUIHandler.Menus.FoundShopsMenu;
import me.ronsane.finditemaddon.finditemaddon.Models.FoundShopItemModel;
import me.ronsane.finditemaddon.finditemaddon.QuickShopHandler.QuickShopAPIHandler;
import me.ronsane.finditemaddon.finditemaddon.Utils.CommonUtils;
import me.ronsane.finditemaddon.finditemaddon.Utils.HiddenShopStorageUtil;
import me.ronsane.finditemaddon.finditemaddon.Utils.LoggerUtils;
import me.ronsane.finditemaddon.finditemaddon.Utils.WarpUtils.WarpUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.maxgamer.quickshop.api.shop.Shop;

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
            // If sender is a console
            if (!(sender instanceof Player)) {
                if(args.length == 1){
                    if(args[0].equalsIgnoreCase("reload")) {
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
                        WarpUtils.updateWarps();
                    }
                    else if(args[0].equalsIgnoreCase("restart")) {
                        Bukkit.getPluginManager().disablePlugin(FindItemAddOn.getInstance());
                        Bukkit.getPluginManager().enablePlugin(FindItemAddOn.getPlugin(FindItemAddOn.class));
                        LoggerUtils.logInfo("&aPlugin restarted!");
                        List<Shop> allServerShops = new QuickShopAPIHandler().getQsPluginInstance().getShopManager().getAllShops();
                        if(allServerShops.size() == 0) {
                            LoggerUtils.logWarning("&6Found &e0 &6shops on the server. If you ran &e/qs reload &6recently, please restart your server!");
                        }
                        else {
                            LoggerUtils.logInfo("&aFound &e" + allServerShops.size() + " &ashops on the server.");
                        }
                    }
//                    else if(args[0].equalsIgnoreCase("updatelist")) {
//                        WarpUtils.updateWarps();
//                    }
                    else {
                        LoggerUtils.logInfo("&CIncorrect usage! Try &e/finditem &freload");
                    }
                }
                else {
                    LoggerUtils.logInfo("This command can only be run from in game");
                }
            }
            // If sender is a player
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
                                WarpUtils.updateWarps();
                            }
                            else {
                                player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&cNo permission!"));
                            }
                        }
                        else if(args[0].equalsIgnoreCase("restart")) {
                            if(player.hasPermission("finditem.restart") || player.hasPermission("finditem.admin")) {
                                Bukkit.getPluginManager().disablePlugin(FindItemAddOn.getInstance());
                                Bukkit.getPluginManager().enablePlugin(FindItemAddOn.getPlugin(FindItemAddOn.class));
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
//                        else if(args[0].equalsIgnoreCase("updatelist")) {
//                            if(player.hasPermission("finditem.admin")) {
//                                player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&eUpdating warps/regions list..."));
//                                WarpUtils.updateWarps();
//                                player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&eUpdate complete!"));
//                            }
//                            else {
//                                player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&cNo permission!"));
//                            }
//                        }
                        else if(args[0].equalsIgnoreCase("hideshop")) {
                            if(player.hasPermission("finditem.hideshop")) {
                                Block playerLookAtBlock = player.getTargetBlock(null, 100);
                                QuickShopAPIHandler qsAPI = new QuickShopAPIHandler();
                                Shop shop = qsAPI.findShopAtLocation(playerLookAtBlock);
                                if(shop != null) {
                                    // check if command runner same as shop owner
                                    if(qsAPI.isShopOwnerCommandRunner(player, shop)) {
                                        if(!HiddenShopStorageUtil.isShopHidden(shop)) {
                                            HiddenShopStorageUtil.addShop(shop);
                                            player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_SHOP_HIDE_SUCCESS_MSG));
                                        }
                                        else {
                                            player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_SHOP_ALREADY_HIDDEN_MSG));
                                        }
                                    }
                                    else {
                                        player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_HIDING_SHOP_OWNER_INVALID_MSG));
                                    }
                                }
                                else {
                                    player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_INVALID_SHOP_BLOCK_MSG));
                                }
                            }
                            else {
                                player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&cNo permission!"));
                            }
                        }
                        else if(args[0].equalsIgnoreCase("revealshop")) {
                            if(player.hasPermission("finditem.hideshop")) {
                                Block playerLookAtBlock = player.getTargetBlock(null, 100);
                                QuickShopAPIHandler qsAPI = new QuickShopAPIHandler();
                                Shop shop = qsAPI.findShopAtLocation(playerLookAtBlock);
                                if(shop != null) {
                                    // check if command runner same as shop owner
                                    if(qsAPI.isShopOwnerCommandRunner(player, shop)) {
                                        if(HiddenShopStorageUtil.isShopHidden(shop)) {
                                            HiddenShopStorageUtil.deleteShop(shop);
                                            player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_SHOP_REVEAL_SUCCESS_MSG));
                                        }
                                        else {
                                            player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_SHOP_ALREADY_PUBLIC_MSG));
                                        }
                                    }
                                    else {
                                        player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_HIDING_SHOP_OWNER_INVALID_MSG));
                                    }
                                }
                                else {
                                    player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_INVALID_SHOP_BLOCK_MSG));
                                }
                            }
                            else {
                                player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&cNo permission!"));
                            }
                        }
                        else if(args[0].equalsIgnoreCase("save")) {
                            HiddenShopStorageUtil.saveHiddenShopsToFile();
                        }
                        else if(args[0].equalsIgnoreCase("load")) {
                            HiddenShopStorageUtil.loadHiddenShopsFromFile();
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
                        Material mat = Material.getMaterial(args[1].toUpperCase());
                        if(mat != null) {
                            LoggerUtils.logDebugInfo("Material found: " + mat.toString());
                            List<FoundShopItemModel> searchResultList = new QuickShopAPIHandler().findItemBasedOnTypeFromAllShops(new ItemStack(mat), isBuying);
                            if(searchResultList.size() > 0) {
                                Bukkit.getScheduler().runTaskAsynchronously(FindItemAddOn.getInstance(), new Runnable() {
                                    @Override
                                    public void run() {
                                        FoundShopsMenu menu = new FoundShopsMenu(FindItemAddOn.getPlayerMenuUtility(player), searchResultList);
                                        Bukkit.getScheduler().runTask(FindItemAddOn.getInstance(), new Runnable() {
                                            @Override
                                            public void run() {
                                                menu.open(searchResultList);
                                            }
                                        });
                                    }
                                });
                            }
                            else {
                                if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().NO_SHOP_FOUND_MSG)) {
                                    player.sendMessage(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + FindItemAddOn.getConfigProvider().NO_SHOP_FOUND_MSG));
                                }
                            }
                        }
                        else {
                            LoggerUtils.logDebugInfo("Material not found! Performing query based search..");
                            List<FoundShopItemModel> searchResultList = new QuickShopAPIHandler().findItemBasedOnDisplayNameFromAllShops(args[1], isBuying);
                            if(searchResultList.size() > 0) {
                                Bukkit.getScheduler().runTaskAsynchronously(FindItemAddOn.getInstance(), new Runnable() {
                                    @Override
                                    public void run() {
                                        FoundShopsMenu menu = new FoundShopsMenu(FindItemAddOn.getPlayerMenuUtility(player), searchResultList);
                                        Bukkit.getScheduler().runTask(FindItemAddOn.getInstance(), new Runnable() {
                                            @Override
                                            public void run() {
                                                menu.open(searchResultList);
                                            }
                                        });
                                    }
                                });
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
