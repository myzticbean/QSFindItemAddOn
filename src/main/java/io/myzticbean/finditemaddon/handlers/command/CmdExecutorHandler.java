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
package io.myzticbean.finditemaddon.handlers.command;

import io.myzticbean.finditemaddon.config.ConfigSetup;
import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.handlers.gui.menus.FoundShopsMenu;
import io.myzticbean.finditemaddon.models.FoundShopItemModel;
import io.myzticbean.finditemaddon.utils.enums.PlayerPermsEnum;
import io.myzticbean.finditemaddon.utils.json.HiddenShopStorageUtil;
import io.myzticbean.finditemaddon.utils.log.Logger;
import io.myzticbean.finditemaddon.utils.warp.WarpUtils;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import org.apache.commons.lang3.StringUtils;
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
 * @author myzticbean
 */
public class CmdExecutorHandler {

    private static final String THIS_COMMAND_CAN_ONLY_BE_RUN_FROM_IN_GAME = "This command can only be run from in game";

    /**
     * Handles the main shop search process
     * @param buySellSubCommand Whether player is buying or selling
     * @param commandSender Who is the command sender: console or player
     * @param itemArg Specifies Item ID or Item name
     */
    public void handleShopSearch(String buySellSubCommand, CommandSender commandSender, String itemArg) {
        if (!(commandSender instanceof Player player)) {
            Logger.logInfo(THIS_COMMAND_CAN_ONLY_BE_RUN_FROM_IN_GAME);
        }
        else {
            if(player.hasPermission(PlayerPermsEnum.FINDITEM_USE.value())) {

                // Show searching... message
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

                if(itemArg.equalsIgnoreCase("*") && !FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_DISABLE_SEARCH_ALL_SHOPS) {
                    // If QS Hikari installed and Shop Cache feature available (>6), then run in async thread (Fix for Issue #12)
                    if(!FindItemAddOn.isQSReremakeInstalled() && FindItemAddOn.getQsApiInstance().isQSShopCacheImplemented()) {
                        Logger.logDebugInfo("Should run in async thread...");
                        Bukkit.getScheduler().runTaskAsynchronously(FindItemAddOn.getInstance(), () -> {
                            List<FoundShopItemModel> searchResultList = FindItemAddOn.getQsApiInstance().fetchAllItemsFromAllShops(isBuying, player);
                            if(!searchResultList.isEmpty()) {
                                FoundShopsMenu menu = new FoundShopsMenu(FindItemAddOn.getPlayerMenuUtility(player), searchResultList);
                                Bukkit.getScheduler().runTask(FindItemAddOn.getInstance(), () -> menu.open(searchResultList));
                            }
                            else {
                                if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().NO_SHOP_FOUND_MSG)) {
                                    player.sendMessage(ColorTranslator.translateColorCodes(
                                            FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + FindItemAddOn.getConfigProvider().NO_SHOP_FOUND_MSG));
                                }
                            }
                        });
                    } else {
                        // Else run in MAIN thread
                        List<FoundShopItemModel> searchResultList = FindItemAddOn.getQsApiInstance().fetchAllItemsFromAllShops(isBuying, player);
                        if(!searchResultList.isEmpty()) {
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
                }
                else {
                    Material mat = Material.getMaterial(itemArg.toUpperCase());
                    if(mat != null) {
                        Logger.logDebugInfo("Material found: " + mat);
                        // If QS Hikari installed and Shop Cache feature available (>6), then run in async thread (Fix for Issue #12)
                        if(!FindItemAddOn.isQSReremakeInstalled() && FindItemAddOn.getQsApiInstance().isQSShopCacheImplemented()) {
                            Bukkit.getScheduler().runTaskAsynchronously(FindItemAddOn.getInstance(), () -> {
                                List<FoundShopItemModel> searchResultList = FindItemAddOn.getQsApiInstance().findItemBasedOnTypeFromAllShops(new ItemStack(mat), isBuying, player);
                                if(!searchResultList.isEmpty()) {
                                    FoundShopsMenu menu = new FoundShopsMenu(FindItemAddOn.getPlayerMenuUtility(player), searchResultList);
                                    Bukkit.getScheduler().runTask(FindItemAddOn.getInstance(), () -> menu.open(searchResultList));
                                }
                                else {
                                    if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().NO_SHOP_FOUND_MSG)) {
                                        player.sendMessage(ColorTranslator.translateColorCodes(
                                                FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + FindItemAddOn.getConfigProvider().NO_SHOP_FOUND_MSG));
                                    }
                                }
                            });
                        } else {
                            List<FoundShopItemModel> searchResultList = FindItemAddOn.getQsApiInstance().findItemBasedOnTypeFromAllShops(new ItemStack(mat), isBuying, player);
                            if(!searchResultList.isEmpty()) {
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
                    }
                    else {
                        Logger.logDebugInfo("Material not found! Performing query based search..");
                        // If QS Hikari installed and Shop Cache feature available (>6), then run in async thread (Fix for Issue #12)
                        if(!FindItemAddOn.isQSReremakeInstalled() && FindItemAddOn.getQsApiInstance().isQSShopCacheImplemented()) {
                            Bukkit.getScheduler().runTaskAsynchronously(FindItemAddOn.getInstance(), () -> {
                                List<FoundShopItemModel> searchResultList = FindItemAddOn.getQsApiInstance().findItemBasedOnDisplayNameFromAllShops(itemArg, isBuying, player);
                                if(!searchResultList.isEmpty()) {
                                    FoundShopsMenu menu = new FoundShopsMenu(FindItemAddOn.getPlayerMenuUtility(player), searchResultList);
                                    Bukkit.getScheduler().runTask(FindItemAddOn.getInstance(), () -> menu.open(searchResultList));
                                }
                                else {
                                    // Invalid Material
                                    if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_INVALID_MATERIAL_MSG)) {
                                        player.sendMessage(ColorTranslator.translateColorCodes(
                                                FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_INVALID_MATERIAL_MSG));
                                    }
                                }
                            });
                        } else {
                            List<FoundShopItemModel> searchResultList = FindItemAddOn.getQsApiInstance().findItemBasedOnDisplayNameFromAllShops(itemArg, isBuying, player);
                            if(!searchResultList.isEmpty()) {
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
            Logger.logInfo(THIS_COMMAND_CAN_ONLY_BE_RUN_FROM_IN_GAME);
        }
        else {
            Player player = (Player) commandSender;
            if(player.hasPermission(PlayerPermsEnum.FINDITEM_HIDESHOP.value())) {
                Block playerLookAtBlock = player.getTargetBlock(null, 3);
                Logger.logDebugInfo("TargetBlock found: " + playerLookAtBlock.getType());
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
            Logger.logInfo(THIS_COMMAND_CAN_ONLY_BE_RUN_FROM_IN_GAME);
        }
        else {
            Player player = (Player) commandSender;
            if(player.hasPermission(PlayerPermsEnum.FINDITEM_HIDESHOP.value())) {
                Block playerLookAtBlock = player.getTargetBlock(null, 5);
                if(playerLookAtBlock != null) {
                    Logger.logDebugInfo("TargetBlock found: " + playerLookAtBlock.getType());
                    if(FindItemAddOn.isQSReremakeInstalled()) {
                        revealShop((Shop) FindItemAddOn.getQsApiInstance().findShopAtLocation(playerLookAtBlock), player);
                    }
                    else {
                        revealShop((com.ghostchu.quickshop.api.shop.Shop) FindItemAddOn.getQsApiInstance().findShopAtLocation(playerLookAtBlock), player);
                    }
                } else {
                    Logger.logDebugInfo("TargetBlock is null!");
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
    /*
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
     */

    /**
     * Handles the loading of hidden shops from file feature
     * @param commandSender Who is the command sender: console or player
     */
    /*
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
     */

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
                Logger.logWarning("&6Found &e0 &6shops on the server. If you ran &e/qs reload &6recently, please restart your server!");
            }
            else {
                Logger.logInfo("&aFound &e" + allServerShops.size() + " &ashops on the server.");
            }
            WarpUtils.updateWarps();
        }
        else {
            Player player = (Player) commandSender;
            if(player.hasPermission(PlayerPermsEnum.FINDITEM_RELOAD.value()) || player.hasPermission(PlayerPermsEnum.FINDITEM_ADMIN.value())) {
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
     * @deprecated
     * Handles plugin restart
     * @param commandSender Who is the command sender: console or player
     */
    @Deprecated(forRemoval = true)
    public void handlePluginRestart(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            Bukkit.getPluginManager().disablePlugin(FindItemAddOn.getInstance());
            Bukkit.getPluginManager().enablePlugin(FindItemAddOn.getPlugin(FindItemAddOn.class));
            Logger.logInfo("&aPlugin restarted!");
            List allServerShops = FindItemAddOn.getQsApiInstance().getAllShops();
            if(allServerShops.size() == 0) {
                Logger.logWarning("&6Found &e0 &6shops on the server. If you ran &e/qs reload &6recently, please restart your server!");
            }
            else {
                Logger.logInfo("&aFound &e" + allServerShops.size() + " &ashops on the server.");
            }
        }
        else {
            Player player = (Player) commandSender;
            if(player.hasPermission(PlayerPermsEnum.FINDITEM_RESTART.value()) || player.hasPermission(PlayerPermsEnum.FINDITEM_ADMIN.value())) {
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

    /**
     * Handles hide shop for QuickShop Reremake
     * @param shop
     * @param player
     */
    private void hideShop(org.maxgamer.quickshop.api.shop.Shop shop, Player player) {
        if(shop != null) {
            // check if command runner same as shop owner
            if(FindItemAddOn.getQsApiInstance().isShopOwnerCommandRunner(player, shop)) {
                if(!HiddenShopStorageUtil.isShopHidden(shop)) {
                    HiddenShopStorageUtil.handleShopSearchVisibilityAsync(shop, true);
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

    /**
     * Handles hide shop for QuickShop Hikari
     * @param shop
     * @param player
     */
    private void hideShop(com.ghostchu.quickshop.api.shop.Shop shop, Player player) {
        if(shop != null) {
            // check if command runner same as shop owner
            if(FindItemAddOn.getQsApiInstance().isShopOwnerCommandRunner(player, shop)) {
                if(!HiddenShopStorageUtil.isShopHidden(shop)) {
                    HiddenShopStorageUtil.handleShopSearchVisibilityAsync(shop, true);
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

    /**
     * Handles reveal shop for QuickShop Reremake
     * @param shop
     * @param player
     */
    private void revealShop(Shop shop, Player player) {
        if(shop != null) {
            // check if command runner same as shop owner
            if(FindItemAddOn.getQsApiInstance().isShopOwnerCommandRunner(player, shop)) {
                if(HiddenShopStorageUtil.isShopHidden(shop)) {
                    HiddenShopStorageUtil.handleShopSearchVisibilityAsync(shop, false);
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

    /**
     * Handles reveal shop for QuickShop Hikari
     * @param shop
     * @param player
     */
    private void revealShop(com.ghostchu.quickshop.api.shop.Shop shop, Player player) {
        if(shop != null) {
            // check if command runner same as shop owner
            if(FindItemAddOn.getQsApiInstance().isShopOwnerCommandRunner(player, shop)) {
                if(HiddenShopStorageUtil.isShopHidden(shop)) {
                    HiddenShopStorageUtil.handleShopSearchVisibilityAsync(shop, false);
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

