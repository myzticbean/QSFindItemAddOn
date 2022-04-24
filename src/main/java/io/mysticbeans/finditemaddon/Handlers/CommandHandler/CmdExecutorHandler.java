package io.mysticbeans.finditemaddon.Handlers.CommandHandler;

import io.mysticbeans.finditemaddon.ConfigUtil.ConfigSetup;
import io.mysticbeans.finditemaddon.FindItemAddOn;
import io.mysticbeans.finditemaddon.Handlers.GUIHandler.Menus.FoundShopsMenu;
import io.mysticbeans.finditemaddon.Models.FoundShopItemModel;
import io.mysticbeans.finditemaddon.Utils.JsonStorageUtils.HiddenShopStorageUtil;
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

/**
 * Handler for different parameters of /finditem command
 * @author ronsane
 */
public class CmdExecutorHandler {

//    QSApi qsApi;
//
//    public CmdExecutorHandler() {
//        if(FindItemAddOn.isQSReremakeInstalled()) {
//            qsApi = new QSReremakeAPIHandler();
//        }
//        else {
//            qsApi = new QSHikariAPIHandler();
//        }
//    }

    /**
     * Handles the main shop search process
     * @param buySellSubCommand Whether player is buying or selling
     * @param commandSender Who is the command sender: console or player
     * @param itemArg Specifies Item ID or Item name
     */
    public void handleShopSearch(String buySellSubCommand, CommandSender commandSender, String itemArg) {
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
                    isBuying = buySellSubCommand.equalsIgnoreCase("to_buy");
                }
                else {
                    isBuying = buySellSubCommand.equalsIgnoreCase(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_BUY_AUTOCOMPLETE);
                }

                if(itemArg.equalsIgnoreCase("*")) {
                    List<FoundShopItemModel> searchResultList = FindItemAddOn.getQsApiInstance().fetchAllItemsFromAllShops(isBuying);
                    if(searchResultList.size() > 0) {
                        Bukkit.getScheduler().runTaskAsynchronously(FindItemAddOn.getInstance(), () -> {
                            FoundShopsMenu menu = new FoundShopsMenu(FindItemAddOn.getPlayerMenuUtility(player), searchResultList);
                            Bukkit.getScheduler().runTask(FindItemAddOn.getInstance(), () -> menu.open(searchResultList));
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
                    Material mat = Material.getMaterial(itemArg.toUpperCase());
                    if(mat != null) {
                        LoggerUtils.logDebugInfo("Material found: " + mat.toString());
                        List<FoundShopItemModel> searchResultList = FindItemAddOn.getQsApiInstance().findItemBasedOnTypeFromAllShops(new ItemStack(mat), isBuying);
                        if(searchResultList.size() > 0) {
                            Bukkit.getScheduler().runTaskAsynchronously(FindItemAddOn.getInstance(), () -> {
                                FoundShopsMenu menu = new FoundShopsMenu(FindItemAddOn.getPlayerMenuUtility(player), searchResultList);
                                Bukkit.getScheduler().runTask(FindItemAddOn.getInstance(), () -> menu.open(searchResultList));
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
                        List<FoundShopItemModel> searchResultList = FindItemAddOn.getQsApiInstance().findItemBasedOnDisplayNameFromAllShops(itemArg, isBuying);
                        if(searchResultList.size() > 0) {
                            Bukkit.getScheduler().runTaskAsynchronously(FindItemAddOn.getInstance(), () -> {
                                FoundShopsMenu menu = new FoundShopsMenu(FindItemAddOn.getPlayerMenuUtility(player), searchResultList);
                                Bukkit.getScheduler().runTask(FindItemAddOn.getInstance(), () -> menu.open(searchResultList));
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

            }
            else {
                player.sendMessage(ColorTranslator.translateColorCodes(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&cNo permission!"));
            }
        }
    }

    /**
     * Handles the shop hiding feature
     * @param commandSender Who is the command sender: console or player
     */
    public void handleHideShop(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            LoggerUtils.logInfo("This command can only be run from in game");
        }
        else {
            Player player = (Player) commandSender;
            if(player.hasPermission(PlayerPerms.FINDITEM_HIDESHOP.toString())) {
                Block playerLookAtBlock = player.getTargetBlock(null, 100);
                if(FindItemAddOn.isQSReremakeInstalled()) {
                    hideShop((Shop) FindItemAddOn.getQsApiInstance().findShopAtLocation(playerLookAtBlock), player);
                }
                else {
                    hideShop((com.ghostchu.quickshop.api.shop.Shop) FindItemAddOn.getQsApiInstance().findShopAtLocation(playerLookAtBlock), player);
                }
            }
            else {
                player.sendMessage(ColorTranslator.translateColorCodes(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&cNo permission!"));
            }
        }
    }

    /**
     * Handles the shop reveal feature
     * @param commandSender Who is the command sender: console or player
     */
    public void handleRevealShop(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            LoggerUtils.logInfo("This command can only be run from in game");
        }
        else {
            Player player = (Player) commandSender;
            if(player.hasPermission(PlayerPerms.FINDITEM_HIDESHOP.toString())) {
                Block playerLookAtBlock = player.getTargetBlock(null, 100);
                if(FindItemAddOn.isQSReremakeInstalled()) {
                    revealShop((Shop) FindItemAddOn.getQsApiInstance().findShopAtLocation(playerLookAtBlock), player);
                }
                else {
                    revealShop((com.ghostchu.quickshop.api.shop.Shop) FindItemAddOn.getQsApiInstance().findShopAtLocation(playerLookAtBlock), player);
                }
            }
            else {
                player.sendMessage(ColorTranslator.translateColorCodes(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&cNo permission!"));
            }
        }
    }

    /**
     * Handles the saving hidden shops to file feature
     * @param commandSender Who is the command sender: console or player
     */
    public void handleHiddenShopSavingToFile(CommandSender commandSender) {
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

    /**
     * Handles the loading of hidden shops from file feature
     * @param commandSender Who is the command sender: console or player
     */
    public void handleHiddenShopLoadingFromFile(CommandSender commandSender) {
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

    /**
     * Handles plugin reload
     * @param commandSender Who is the command sender: console or player
     */
    public void handlePluginReload(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            ConfigSetup.reloadConfig();
            ConfigSetup.checkForMissingProperties();
            ConfigSetup.saveConfig();
            FindItemAddOn.initConfigProvider();
            List allServerShops = FindItemAddOn.getQsApiInstance().getAllShops();
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
                List allServerShops = FindItemAddOn.getQsApiInstance().getAllShops();
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

    /**
     * Handles plugin restart
     * @param commandSender Who is the command sender: console or player
     */
    public void handlePluginRestart(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            Bukkit.getPluginManager().disablePlugin(FindItemAddOn.getInstance());
            Bukkit.getPluginManager().enablePlugin(FindItemAddOn.getPlugin(FindItemAddOn.class));
            LoggerUtils.logInfo("&aPlugin restarted!");
            List allServerShops = FindItemAddOn.getQsApiInstance().getAllShops();
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
                List allServerShops = FindItemAddOn.getQsApiInstance().getAllShops();
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

    private void hideShop(Shop shop, Player player) {
        if(shop != null) {
            // check if command runner same as shop owner
            if(FindItemAddOn.getQsApiInstance().isShopOwnerCommandRunner(player, shop)) {
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

    private void hideShop(com.ghostchu.quickshop.api.shop.Shop shop, Player player) {
        if(shop != null) {
            // check if command runner same as shop owner
            if(FindItemAddOn.getQsApiInstance().isShopOwnerCommandRunner(player, shop)) {
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

    private void revealShop(Shop shop, Player player) {
        if(shop != null) {
            // check if command runner same as shop owner
            if(FindItemAddOn.getQsApiInstance().isShopOwnerCommandRunner(player, shop)) {
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

    private void revealShop(com.ghostchu.quickshop.api.shop.Shop shop, Player player) {
        if(shop != null) {
            // check if command runner same as shop owner
            if(FindItemAddOn.getQsApiInstance().isShopOwnerCommandRunner(player, shop)) {
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
}

