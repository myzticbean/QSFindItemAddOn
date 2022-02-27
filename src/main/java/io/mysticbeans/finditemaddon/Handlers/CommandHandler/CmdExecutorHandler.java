package io.mysticbeans.finditemaddon.Handlers.CommandHandler;

import io.mysticbeans.finditemaddon.ConfigUtil.ConfigSetup;
import io.mysticbeans.finditemaddon.FindItemAddOn;
import io.mysticbeans.finditemaddon.Handlers.GUIHandler.Menus.FoundShopsMenu;
import io.mysticbeans.finditemaddon.Models.FoundShopItemModel;
import io.mysticbeans.finditemaddon.QuickShopHandler.QuickShopAPIHandler;
import io.mysticbeans.finditemaddon.Utils.HiddenShopStorageUtil;
import io.mysticbeans.finditemaddon.Utils.LoggerUtils;
import io.mysticbeans.finditemaddon.Utils.PlayerPerms;
import io.mysticbeans.finditemaddon.Utils.WarpUtils.WarpUtils;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.maxgamer.quickshop.api.shop.Shop;

import java.util.List;

public class CmdExecutorHandler {

    public static void handleShopSearch(String buySellSubCommand, CommandSender commandSender, String itemArg) {
        if (!(commandSender instanceof Player)) {
            LoggerUtils.logInfo("This command can only be run from in game");
        }
        else {
            Player player = (Player) commandSender;
            if(player.hasPermission(PlayerPerms.FINDITEM_USE.toString())) {
                if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().SHOP_SEARCH_LOADING_MSG)) {
                    player.sendMessage(ColorTranslator.translateColorCodes(
                            FindItemAddOn.getConfigProvider().PLUGIN_PREFIX
                                    + FindItemAddOn.getConfigProvider().SHOP_SEARCH_LOADING_MSG));
                }
                boolean isBuying;
                if(StringUtils.isEmpty(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_BUY_AUTOCOMPLETE)
                        || StringUtils.containsIgnoreCase(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_BUY_AUTOCOMPLETE, " ")) {
                    isBuying = buySellSubCommand.equalsIgnoreCase("to-buy");
                }
                else {
                    isBuying = buySellSubCommand.equalsIgnoreCase(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_BUY_AUTOCOMPLETE);
                }
                Material mat = Material.getMaterial(itemArg.toUpperCase());
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
                            player.sendMessage(ColorTranslator.translateColorCodes(
                                    FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + FindItemAddOn.getConfigProvider().NO_SHOP_FOUND_MSG));
                        }
                    }
                }
                else {
                    LoggerUtils.logDebugInfo("Material not found! Performing query based search..");
                    List<FoundShopItemModel> searchResultList = new QuickShopAPIHandler().findItemBasedOnDisplayNameFromAllShops(itemArg, isBuying);
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
                            player.sendMessage(ColorTranslator.translateColorCodes(
                                    FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_INVALID_MATERIAL_MSG));
                        }
                    }
                }
            }
            else {
                player.sendMessage(ColorTranslator.translateColorCodes(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&cNo permission!"));
            }
        }
    }

    public static void handleHideShop(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            LoggerUtils.logInfo("This command can only be run from in game");
        }
        else {
            Player player = (Player) commandSender;
            if(player.hasPermission(PlayerPerms.FINDITEM_HIDESHOP.toString())) {
                Block playerLookAtBlock = player.getTargetBlock(null, 100);
                QuickShopAPIHandler qsAPI = new QuickShopAPIHandler();
                Shop shop = qsAPI.findShopAtLocation(playerLookAtBlock);
                if(shop != null) {
                    // check if command runner same as shop owner
                    if(qsAPI.isShopOwnerCommandRunner(player, shop)) {
                        if(!HiddenShopStorageUtil.isShopHidden(shop)) {
                            HiddenShopStorageUtil.addShop(shop);
                            player.sendMessage(ColorTranslator.translateColorCodes(
                                    FindItemAddOn.getConfigProvider().PLUGIN_PREFIX
                                            + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_SHOP_HIDE_SUCCESS_MSG));
                        }
                        else {
                            player.sendMessage(ColorTranslator.translateColorCodes(
                                    FindItemAddOn.getConfigProvider().PLUGIN_PREFIX
                                            + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_SHOP_ALREADY_HIDDEN_MSG));
                        }
                    }
                    else {
                        player.sendMessage(ColorTranslator.translateColorCodes(
                                FindItemAddOn.getConfigProvider().PLUGIN_PREFIX
                                        + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_HIDING_SHOP_OWNER_INVALID_MSG));
                    }
                }
                else {
                    player.sendMessage(ColorTranslator.translateColorCodes(
                            FindItemAddOn.getConfigProvider().PLUGIN_PREFIX
                                    + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_INVALID_SHOP_BLOCK_MSG));
                }
            }
            else {
                player.sendMessage(ColorTranslator.translateColorCodes(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&cNo permission!"));
            }
        }
    }

    public static void handleRevealShop(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            LoggerUtils.logInfo("This command can only be run from in game");
        }
        else {
            Player player = (Player) commandSender;
            if(player.hasPermission(PlayerPerms.FINDITEM_HIDESHOP.toString())) {
                Block playerLookAtBlock = player.getTargetBlock(null, 100);
                QuickShopAPIHandler qsAPI = new QuickShopAPIHandler();
                Shop shop = qsAPI.findShopAtLocation(playerLookAtBlock);
                if(shop != null) {
                    // check if command runner same as shop owner
                    if(qsAPI.isShopOwnerCommandRunner(player, shop)) {
                        if(HiddenShopStorageUtil.isShopHidden(shop)) {
                            HiddenShopStorageUtil.deleteShop(shop);
                            player.sendMessage(ColorTranslator.translateColorCodes(
                                    FindItemAddOn.getConfigProvider().PLUGIN_PREFIX
                                            + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_SHOP_REVEAL_SUCCESS_MSG));
                        }
                        else {
                            player.sendMessage(ColorTranslator.translateColorCodes(
                                    FindItemAddOn.getConfigProvider().PLUGIN_PREFIX
                                            + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_SHOP_ALREADY_PUBLIC_MSG));
                        }
                    }
                    else {
                        player.sendMessage(ColorTranslator.translateColorCodes(
                                FindItemAddOn.getConfigProvider().PLUGIN_PREFIX
                                        + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_HIDING_SHOP_OWNER_INVALID_MSG));
                    }
                }
                else {
                    player.sendMessage(ColorTranslator.translateColorCodes(
                            FindItemAddOn.getConfigProvider().PLUGIN_PREFIX
                                    + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_INVALID_SHOP_BLOCK_MSG));
                }
            }
            else {
                player.sendMessage(ColorTranslator.translateColorCodes(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&cNo permission!"));
            }
        }
    }

    public static void handleHiddenShopSavingToFile(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            LoggerUtils.logInfo("This command can only be run from in game");
        }
        else {
            Player player = (Player) commandSender;
            if(player.hasPermission(PlayerPerms.FINDITEM_ADMIN.toString())) {
                HiddenShopStorageUtil.saveHiddenShopsToFile();
                player.sendMessage(ColorTranslator.translateColorCodes(
                        FindItemAddOn.getConfigProvider().PLUGIN_PREFIX
                                + "&aSaved hidden shops!"));
            }
            else {
                player.sendMessage(
                        ColorTranslator.translateColorCodes(
                                FindItemAddOn.getConfigProvider().PLUGIN_PREFIX
                                        + "&cNo permission!"));
            }
        }
    }

    public static void handleHiddenShopLoadingFromFile(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            LoggerUtils.logInfo("This command can only be run from in game");
        }
        else {
            Player player = (Player) commandSender;
            if(player.hasPermission(PlayerPerms.FINDITEM_ADMIN.toString())) {
                HiddenShopStorageUtil.loadHiddenShopsFromFile();
                player.sendMessage(ColorTranslator.translateColorCodes(
                        FindItemAddOn.getConfigProvider().PLUGIN_PREFIX
                                + "&aSaved hidden shops!"));
            }
            else {
                player.sendMessage(
                        ColorTranslator.translateColorCodes(
                                FindItemAddOn.getConfigProvider().PLUGIN_PREFIX
                                        + "&cNo permission!"));
            }
        }
    }

    public static void handlePluginReload(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
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
        else {
            Player player = (Player) commandSender;
            if(player.hasPermission(PlayerPerms.FINDITEM_RELOAD.toString()) || player.hasPermission(PlayerPerms.FINDITEM_ADMIN.toString())) {
                ConfigSetup.reloadConfig();
                ConfigSetup.checkForMissingProperties();
                ConfigSetup.saveConfig();
                FindItemAddOn.initConfigProvider();
                player.sendMessage(ColorTranslator.translateColorCodes(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&aConfig reloaded!"));
                List<Shop> allServerShops = new QuickShopAPIHandler().getQsPluginInstance().getShopManager().getAllShops();
                if(allServerShops.size() == 0) {
                    player.sendMessage(ColorTranslator.translateColorCodes(
                            FindItemAddOn.getConfigProvider().PLUGIN_PREFIX
                                    + "&6Found &e0 &6shops on the server. If you ran &e/qs reload &6recently, please restart your server!"));
                }
                else {
                    player.sendMessage(ColorTranslator.translateColorCodes(
                            FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&aFound &e" + allServerShops.size() + " &ashops on the server."));
                }
                WarpUtils.updateWarps();
            }
            else {
                player.sendMessage(ColorTranslator.translateColorCodes(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&cNo permission!"));
            }
        }
    }

    public static void handlePluginRestart(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
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
        else {
            Player player = (Player) commandSender;
            if(player.hasPermission(PlayerPerms.FINDITEM_RESTART.toString()) || player.hasPermission(PlayerPerms.FINDITEM_ADMIN.toString())) {
                Bukkit.getPluginManager().disablePlugin(FindItemAddOn.getInstance());
                Bukkit.getPluginManager().enablePlugin(FindItemAddOn.getPlugin(FindItemAddOn.class));
                player.sendMessage(ColorTranslator.translateColorCodes(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&aPlugin restarted!"));
                List<Shop> allServerShops = new QuickShopAPIHandler().getQsPluginInstance().getShopManager().getAllShops();
                if(allServerShops.size() == 0) {
                    player.sendMessage(ColorTranslator.translateColorCodes(
                            FindItemAddOn.getConfigProvider().PLUGIN_PREFIX
                                    + "&6Found &e0 &6shops on the server. If you ran &e/qs reload &6recently, please restart your server!"));
                }
                else {
                    player.sendMessage(ColorTranslator.translateColorCodes(
                            FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&aFound &e" + allServerShops.size() + " &ashops on the server."));
                }
            }
            else {
                player.sendMessage(ColorTranslator.translateColorCodes(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&cNo permission!"));
            }
        }
    }
}

