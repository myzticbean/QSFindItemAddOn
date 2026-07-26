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

import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.config.ConfigSetup;
import io.myzticbean.finditemaddon.handlers.gui.menus.FoundShopsMenu;
import io.myzticbean.finditemaddon.models.FoundShopItemModel;
import io.myzticbean.finditemaddon.models.enums.PlayerPermsEnum;
import io.myzticbean.finditemaddon.quickshop.impl.QSHikariAPIHandler;
import io.myzticbean.finditemaddon.utils.PlayerUtil;
import io.myzticbean.finditemaddon.utils.async.VirtualThreadScheduler;
import io.myzticbean.finditemaddon.utils.json.HiddenShopStorageUtil;
import io.myzticbean.finditemaddon.utils.log.Logger;
import io.myzticbean.finditemaddon.utils.warp.WarpUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.List;

/**
 * Handler for different parameters of /finditem command
 * @author myzticbean
 */
public class CmdExecutorHandler {

    private static final String THIS_COMMAND_CAN_ONLY_BE_RUN_FROM_IN_GAME = "This command can only be run from in game";
    public static final String NO_PERMISSION = "&cNo permission!";
    private static final String SEARCH_ERROR_MSG = "&cAn error occurred while searching for shops. Please try again or contact an admin.";

    /**
     * Handles the main shop search process
     * @param buySellSubCommand Whether player is buying or selling
     * @param commandSender Who is the command sender: console or player
     * @param itemArg Specifies Item ID or Item name
     */
    public void handleShopSearch(String buySellSubCommand, CommandSender commandSender, String itemArg) {
        VirtualThreadScheduler.runTaskAsync(() -> {
            if (!(commandSender instanceof Player player)) {
                Logger.logInfo(THIS_COMMAND_CAN_ONLY_BE_RUN_FROM_IN_GAME);
                return;
            }
            if (!player.hasPermission(PlayerPermsEnum.FINDITEM_USE.value())) {
                PlayerUtil.sendMessage(player, getPluginPrefix() + NO_PERMISSION);
                return;
            }

            // Show searching... message
            if (!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().SHOP_SEARCH_LOADING_MSG)) {
                PlayerUtil.sendMessage(player, getPluginPrefix() + FindItemAddOn.getConfigProvider().SHOP_SEARCH_LOADING_MSG);
            }

            boolean isBuying;
            if(StringUtils.isEmpty(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_BUY_AUTOCOMPLETE)
                    || StringUtils.containsIgnoreCase(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_BUY_AUTOCOMPLETE, " ")) {
                isBuying = buySellSubCommand.equalsIgnoreCase("to_buy");
            } else {
                isBuying = buySellSubCommand.equalsIgnoreCase(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_BUY_AUTOCOMPLETE);
            }

            if(itemArg.equalsIgnoreCase("*") && !FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_DISABLE_SEARCH_ALL_SHOPS) {
                // If QS Hikari installed and Shop Cache feature available (>6), then run in async thread (Fix for Issue #12)
                // UPDATE: No need to run in async thread as it is already running in async thread (Assume everyone has QS-Hikari v6.* installed)
                FindItemAddOn
                        .getQsApiInstance()
                        .fetchAllItemsFromAllShops(isBuying, player)
                        .thenAccept(searchResultList -> this.openShopMenu(player, searchResultList, FindItemAddOn.getConfigProvider().NO_SHOP_FOUND_MSG))
                        .exceptionally(ex -> this.handleShopSearchError(player, ex));
            } else {
                Material mat = Material.getMaterial(itemArg.toUpperCase());
                if(this.checkMaterialBlacklist(mat)) {
                    PlayerUtil.sendMessage(player, getPluginPrefix() + "&cThis material is not allowed.");
                    return;
                }
                if (mat != null && mat.isItem()) {
                    Logger.logDebugInfo("Material found: " + mat);
                    // If QS Hikari installed and Shop Cache feature available (>6), then run in async thread (Fix for Issue #12)
                    // UPDATE: No need to run in async thread as it is already running in async thread (Assume everyone has QS-Hikari v6.* installed)
                    FindItemAddOn
                            .getQsApiInstance()
                            .findItemBasedOnTypeFromAllShops(new ItemStack(mat), isBuying, player)
                            .thenAccept(searchResultList -> this.openShopMenu(player, searchResultList, FindItemAddOn.getConfigProvider().NO_SHOP_FOUND_MSG))
                            .exceptionally(ex -> this.handleShopSearchError(player, ex));
                } else {
                    Logger.logDebugInfo("Material not found! Performing query based search..");
                    // If QS Hikari installed and Shop Cache feature available (>6), then run in async thread (Fix for Issue #12)
                    // UPDATE: No need to run in async thread as it is already running in async thread (Assume everyone has QS-Hikari v6.* installed)
                    FindItemAddOn
                            .getQsApiInstance()
                            .findItemBasedOnDisplayNameFromAllShops(itemArg, isBuying, player)
                            .thenAccept(searchResultList -> this.openShopMenu(player, searchResultList, FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_INVALID_MATERIAL_MSG))
                            .exceptionally(ex -> this.handleShopSearchError(player, ex));
                }
            }
        });
    }

    private Void handleShopSearchError(Player player, Throwable ex) {
        Throwable cause = ex;
        while (cause instanceof java.util.concurrent.CompletionException && cause.getCause() != null) {
            cause = cause.getCause();
        }
        Logger.logError("Shop search failed for player " + player.getName() + ": " + cause.getMessage());
        PlayerUtil.sendMessage(player, getPluginPrefix() + SEARCH_ERROR_MSG);
        return null;
    }

    private void openShopMenu(Player player, List<FoundShopItemModel> searchResultList, String errorMsg) {
        if (!searchResultList.isEmpty()) {
            FoundShopsMenu menu = new FoundShopsMenu(FindItemAddOn.getPlayerMenuUtility(player), searchResultList);
            menu.open(searchResultList);
        } else {
            if (!StringUtils.isEmpty(errorMsg)) {
                PlayerUtil.sendMessage(player, getPluginPrefix() + errorMsg);
            }
        }
    }

    private boolean checkMaterialBlacklist(Material mat) {
        return FindItemAddOn.getConfigProvider().getBlacklistedMaterials().contains(mat);
    }

    /**
     * Handles the shop hiding feature
     * @param commandSender Who is the command sender: console or player
     */
    public void handleHideShop(CommandSender commandSender) {
        if(commandSender instanceof Player player) {
            if(player.hasPermission(PlayerPermsEnum.FINDITEM_HIDESHOP.value())) {
                Block playerLookAtBlock = player.getTargetBlock(null, 3);
                Logger.logDebugInfo("TargetBlock found: " + playerLookAtBlock.getType());
                hideHikariShop((com.ghostchu.quickshop.api.shop.Shop) FindItemAddOn.getQsApiInstance().findShopAtLocation(playerLookAtBlock), player);
            }
            else {
                PlayerUtil.sendMessage(player, getPluginPrefix() + NO_PERMISSION);
            }
        } else {
            Logger.logInfo(THIS_COMMAND_CAN_ONLY_BE_RUN_FROM_IN_GAME);
        }
    }

    /**
     * Handles the shop reveal feature
     * @param commandSender Who is the command sender: console or player
     */
    public void handleRevealShop(CommandSender commandSender) {
        if (commandSender instanceof Player player) {
            if(player.hasPermission(PlayerPermsEnum.FINDITEM_HIDESHOP.value())) {
                Block playerLookAtBlock = player.getTargetBlock(null, 5);
                if(playerLookAtBlock != null) {
                    Logger.logDebugInfo("TargetBlock found: " + playerLookAtBlock.getType());
                    revealShop((com.ghostchu.quickshop.api.shop.Shop) FindItemAddOn.getQsApiInstance().findShopAtLocation(playerLookAtBlock), player);
                } else {
                    Logger.logDebugInfo("TargetBlock is null!");
                }
            }
            else {
                PlayerUtil.sendMessage(player, getPluginPrefix() + NO_PERMISSION);
            }
        } else {
            Logger.logInfo(THIS_COMMAND_CAN_ONLY_BE_RUN_FROM_IN_GAME);
        }
    }

    /**
     * Handles plugin reload
     * @param commandSender Who is the command sender: console or player
     */
    public void handlePluginReload(CommandSender commandSender) {
        VirtualThreadScheduler.runTaskAsync(() -> {
            if (commandSender instanceof Player player) {
                if(player.hasPermission(PlayerPermsEnum.FINDITEM_RELOAD.value()) || player.hasPermission(PlayerPermsEnum.FINDITEM_ADMIN.value())) {
                    FindItemAddOn.getScheduler().runNextTick(t -> {
                        ConfigSetup.reloadConfig();
                        ConfigSetup.checkForMissingProperties();
                        ConfigSetup.saveConfig();
                        FindItemAddOn.initConfigProvider();
                    });
                    PlayerUtil.sendMessage(player, getPluginPrefix() + "&aConfig reloaded!");
                    var allServerShops = FindItemAddOn.getQsApiInstance().getAllShops();
                    if(allServerShops.isEmpty()) {
                        PlayerUtil.sendMessage(player, getPluginPrefix() + "&6Found &e0 &6shops on the server. If you ran &e/qs reload &6recently, please restart your server!");
                    } else {
                        PlayerUtil.sendMessage(player, getPluginPrefix() + "&aFound &e" + allServerShops.size() + " &ashops on the server.");
                    }
                    WarpUtils.updateWarps();
                } else {
                    PlayerUtil.sendMessage(player, getPluginPrefix() + NO_PERMISSION);
                }
            } else {    // commandSender is console
                FindItemAddOn.getScheduler().runNextTick(t -> {
                    ConfigSetup.reloadConfig();
                    ConfigSetup.checkForMissingProperties();
                    ConfigSetup.saveConfig();
                    FindItemAddOn.initConfigProvider();
                });
                var allServerShops = FindItemAddOn.getQsApiInstance().getAllShops();
                if(allServerShops.isEmpty()) {
                    Logger.logWarning("&6Found &e0 &6shops on the server. If you ran &e/qs reload &6recently, please restart your server!");
                } else {
                    Logger.logInfo("&aFound &e" + allServerShops.size() + " &ashops on the server.");
                }
                WarpUtils.updateWarps();
            }
        });
    }

    /**
     * Handles hide shop for QuickShop Hikari
     * @param shop
     * @param player
     */
    private void hideHikariShop(com.ghostchu.quickshop.api.shop.Shop shop, Player player) {
        if(shop != null) {
            QSHikariAPIHandler qsHikariAPIHandler = (QSHikariAPIHandler) FindItemAddOn.getQsApiInstance();
            // check if command runner same as shop owner
            if(qsHikariAPIHandler.isShopOwnerCommandRunner(player, shop)) {
                if(!HiddenShopStorageUtil.isShopHidden(shop)) {
                    HiddenShopStorageUtil.handleShopSearchVisibilityAsync(shop, true);
                    PlayerUtil.sendMessage(player, getPluginPrefix() + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_SHOP_HIDE_SUCCESS_MSG);
                } else {
                    PlayerUtil.sendMessage(player, getPluginPrefix() + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_SHOP_ALREADY_HIDDEN_MSG);
                }
            } else {
                PlayerUtil.sendMessage(player, getPluginPrefix() + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_HIDING_SHOP_OWNER_INVALID_MSG);
            }
        } else {
            PlayerUtil.sendMessage(player, getPluginPrefix() + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_INVALID_SHOP_BLOCK_MSG);
        }
    }

    /**
     * Handles reveal shop for QuickShop Hikari
     * @param shop
     * @param player
     */
    private void revealShop(com.ghostchu.quickshop.api.shop.Shop shop, Player player) {
        if(shop != null) {
            QSHikariAPIHandler qsHikariAPIHandler = (QSHikariAPIHandler) FindItemAddOn.getQsApiInstance();
            // check if command runner same as shop owner
            if(qsHikariAPIHandler.isShopOwnerCommandRunner(player, shop)) {
                if(HiddenShopStorageUtil.isShopHidden(shop)) {
                    HiddenShopStorageUtil.handleShopSearchVisibilityAsync(shop, false);
                    PlayerUtil.sendMessage(player, getPluginPrefix() + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_SHOP_REVEAL_SUCCESS_MSG);
                } else {
                    PlayerUtil.sendMessage(player, getPluginPrefix() + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_SHOP_ALREADY_PUBLIC_MSG);
                }
            } else {
                PlayerUtil.sendMessage(player, getPluginPrefix() + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_HIDING_SHOP_OWNER_INVALID_MSG);
            }
        } else {
            PlayerUtil.sendMessage(player, getPluginPrefix() + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_INVALID_SHOP_BLOCK_MSG);
        }
    }

    /**
     * Handles enabling/disabling debug mode
     * @param commandSender Who is the command sender: console or player
     * @param args Command arguments
     */
    public void handleDebugMode(CommandSender commandSender, String[] args) {
        VirtualThreadScheduler.runTaskAsync(() -> {
            if(args.length == 1) {
                if (commandSender instanceof  Player player) {
                    PlayerUtil.sendMessage(player, getPluginPrefix()
                            + "&7Debug is currently " + (FindItemAddOn.getConfigProvider().isDebugModeEnabled() ? "enabled." : "disabled.")
                            + "\nYou can change it by typing &e/finditemadmin debug-mode {enable | disable}\n");
                } else {
                    Logger.logInfo("\n\nDebug is currently " + (FindItemAddOn.getConfigProvider().isDebugModeEnabled() ? "enabled." : "disabled.")
                            + "\nYou can change it by typing /finditemadmin debug-mode {enable | disable}\n");
                }
                return;
            }
            String mode = args[1];
            boolean enable = mode.equalsIgnoreCase("enable");
            String message;
            if (commandSender instanceof Player player && !PlayerUtil.hasPermission(player, PlayerPermsEnum.FINDITEM_ADMIN.value())) {
                PlayerUtil.sendMessage(player, getPluginPrefix() + NO_PERMISSION);
                return;
            }
            // Update debug mode using ConfigProvider's setter
            FindItemAddOn.getConfigProvider().setDebugMode(enable);
            message = "Debug mode " + (enable ? "enabled" : "disabled") + " successfully.";
            if (commandSender instanceof Player player) {
                PlayerUtil.sendMessage(player, getPluginPrefix() + message);
            } else {
                Logger.logInfo(message);
            }
        });
    }

    public String getPluginPrefix() {
        return FindItemAddOn.getConfigProvider().PLUGIN_PREFIX;
    }
}
