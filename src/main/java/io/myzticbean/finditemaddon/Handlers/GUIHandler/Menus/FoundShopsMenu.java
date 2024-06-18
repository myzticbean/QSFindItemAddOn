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
package io.myzticbean.finditemaddon.Handlers.GUIHandler.Menus;

import io.myzticbean.finditemaddon.Dependencies.EssentialsXPlugin;
import io.myzticbean.finditemaddon.Dependencies.PlayerWarpsPlugin;
import io.myzticbean.finditemaddon.Dependencies.WGPlugin;
import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.Handlers.GUIHandler.PaginatedMenu;
import io.myzticbean.finditemaddon.Handlers.GUIHandler.PlayerMenuUtility;
import io.myzticbean.finditemaddon.Models.FoundShopItemModel;
import io.myzticbean.finditemaddon.Utils.Defaults.NearestWarpModeEnum;
import io.myzticbean.finditemaddon.Utils.Defaults.PlayerPermsEnum;
import io.myzticbean.finditemaddon.Utils.Defaults.ShopLorePlaceholdersEnum;
import io.myzticbean.finditemaddon.Utils.JsonStorageUtils.ShopSearchActivityStorageUtil;
import io.myzticbean.finditemaddon.Utils.LocationUtils;
import io.myzticbean.finditemaddon.Utils.LoggerUtils;
import io.myzticbean.finditemaddon.Utils.WarpUtils.EssentialWarpsUtil;
import io.myzticbean.finditemaddon.Utils.WarpUtils.PlayerWarpsUtil;
import io.myzticbean.finditemaddon.Utils.WarpUtils.WGRegionUtils;
import io.papermc.lib.PaperLib;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Handler class for FoundShops GUI
 * @author myzticbean
 */
public class FoundShopsMenu extends PaginatedMenu {

    public static final String SHOP_STOCK_UNLIMITED = "Unlimited";
    public static final String SHOP_STOCK_UNKNOWN = "Unknown";
    private static final String NO_WARP_NEAR_SHOP_ERROR_MSG = "No Warp near this shop";
    private static final String NO_WG_REGION_NEAR_SHOP_ERROR_MSG = "No WG Region near this shop";
    private static final String NAMEDSPACE_KEY_LOCATION_DATA = "locationData";

    public FoundShopsMenu(PlayerMenuUtility playerMenuUtility, List<FoundShopItemModel> searchResult) {
        super(playerMenuUtility, searchResult);
    }

    @Override
    public String getMenuName() {
        if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().SHOP_SEARCH_GUI_TITLE)) {
            return ColorTranslator.translateColorCodes(FindItemAddOn.getConfigProvider().SHOP_SEARCH_GUI_TITLE);
        }
        else {
            return ColorTranslator.translateColorCodes("&l» &rShops");
        }
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        // Previous Page
        if(event.getSlot() == 45) {
            handleMenuClickForNavToPrevPage(event);
        }
        // First Page
        else if(event.getSlot() == 46) {
            handleFirstPageClick(event);
        }
        // Last Page
        else if(event.getSlot() == 52) {
            handleLastPageClick(event);
        }
        // Next Page
        else if(event.getSlot() == 53) {
            handleMenuClickForNavToNextPage(event);
        }
        // Issue #31: Removing condition 'event.getCurrentItem().getType().equals(Material.BARRIER)'
        else if(event.getSlot() == 49) {
            event.getWhoClicked().closeInventory();
        }
        else if(event.getCurrentItem().getType().equals(Material.AIR)) {
            // do nothing
            LoggerUtils.logDebugInfo(event.getWhoClicked().getName() + " just clicked on AIR!");
        }
        else {
            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();
            ItemMeta meta = item.getItemMeta();
            NamespacedKey key = new NamespacedKey(FindItemAddOn.getInstance(), NAMEDSPACE_KEY_LOCATION_DATA);
            if(!meta.getPersistentDataContainer().isEmpty() && meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                String locData = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                List<String> locDataList = Arrays.asList(locData.split("\\s*,\\s*"));
                if(FindItemAddOn.getConfigProvider().TP_PLAYER_DIRECTLY_TO_SHOP) {
                    if(playerMenuUtility.getOwner().hasPermission(PlayerPermsEnum.FINDITEM_SHOPTP.value())) {
                        World world = Bukkit.getWorld(locDataList.get(0));
                        int locX = Integer.parseInt(locDataList.get(1));
                        int locY = Integer.parseInt(locDataList.get(2));
                        int locZ = Integer.parseInt(locDataList.get(3));
                        Location shopLocation = new Location(world, locX, locY, locZ);
                        UUID shopOwner = ShopSearchActivityStorageUtil.getShopOwnerUUID(shopLocation);
                        if(player.getUniqueId().equals(shopOwner) && !PlayerPermsEnum.canPlayerTpToOwnShop(player)) {
                            player.sendMessage(ColorTranslator.translateColorCodes(
                                    FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + FindItemAddOn.getConfigProvider().SHOP_TP_NO_PERMISSION_MSG));
                        }
                        Location locToTeleport = LocationUtils.findSafeLocationAroundShop(shopLocation);
                        if(locToTeleport != null) {
                            // Add Player Visit Entry
                            ShopSearchActivityStorageUtil.addPlayerVisitEntryAsync(shopLocation, player);

                            // Add Short Blindness effect... maybe?
                            // TODO: 16/06/22 Make this an option in config -> player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 0, false, false, false));

                            // If EssentialsX is enabled, register last location before teleporting to make /back work
                            if(EssentialsXPlugin.isEnabled()) {
                                EssentialsXPlugin.getAPI().getUser(player).setLastLocation();
                            }
                            // Check for TP delay
                            if(StringUtils.isNumeric(FindItemAddOn.getConfigProvider().TP_DELAY_IN_SECONDS)
                                && !"0".equals(FindItemAddOn.getConfigProvider().TP_DELAY_IN_SECONDS)
                                && !PlayerPermsEnum.hasShopTpDelayBypassPermOrAdmin(player)) {
                                long delay = Long.parseLong(FindItemAddOn.getConfigProvider().TP_DELAY_IN_SECONDS);
                                LoggerUtils.logDebugInfo("Teleporting delay is set to: " + delay);
                                String tpDelayMsg = FindItemAddOn.getConfigProvider().TP_DELAY_MESSAGE;
                                if(!StringUtils.isEmpty(tpDelayMsg)) {
                                    player.sendMessage(
                                            ColorTranslator.translateColorCodes(
                                                    FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + replaceDelayPlaceholder(tpDelayMsg,delay)));
                                }
                                Bukkit.getScheduler().scheduleSyncDelayedTask(
                                        FindItemAddOn.getInstance(),
                                        () -> PaperLib.teleportAsync(player, locToTeleport, PlayerTeleportEvent.TeleportCause.PLUGIN),
                                        delay*20);
                            } else {
                                PaperLib.teleportAsync(player, locToTeleport, PlayerTeleportEvent.TeleportCause.PLUGIN);
                            }
                        }
                        else {
                            if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().UNSAFE_SHOP_AREA_MSG)) {
                                player.sendMessage(ColorTranslator.translateColorCodes(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX
                                        + FindItemAddOn.getConfigProvider().UNSAFE_SHOP_AREA_MSG));
                            }
                        }
                    }
                    else {
                        if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().SHOP_TP_NO_PERMISSION_MSG)) {
                            playerMenuUtility
                                    .getOwner()
                                    .sendMessage(ColorTranslator.translateColorCodes(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX
                                            + FindItemAddOn.getConfigProvider().SHOP_TP_NO_PERMISSION_MSG));
                            event.getWhoClicked().closeInventory();
                        }
                    }
                    player.closeInventory();
                }
                else if(FindItemAddOn.getConfigProvider().TP_PLAYER_TO_NEAREST_WARP
                    // if list size = 1, it contains Warp name
                    && locDataList.size() == 1) {
                    String warpName = locDataList.get(0);
                    if(FindItemAddOn.getConfigProvider().NEAREST_WARP_MODE == 1) {
                        Bukkit.dispatchCommand(player, "essentials:warp " + warpName);
                    }
                    else if(FindItemAddOn.getConfigProvider().NEAREST_WARP_MODE == 2) {
                        PlayerWarpsPlugin.executeWarpPlayer(player, warpName);
                    }
                }
            }
            else {
                LoggerUtils.logError("PersistentDataContainer doesn't have the right kind of data!");
            }
        }
    }

    private void handleMenuClickForNavToNextPage(InventoryClickEvent event) {
        if(!((index + 1) >= super.playerMenuUtility.getPlayerShopSearchResult().size())) {
            page = page + 1;
            super.open(super.playerMenuUtility.getPlayerShopSearchResult());
        }
        else {
            if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().SHOP_NAV_LAST_PAGE_ALERT_MSG)) {
                event.getWhoClicked().sendMessage(
                        ColorTranslator.translateColorCodes(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + FindItemAddOn.getConfigProvider().SHOP_NAV_LAST_PAGE_ALERT_MSG));
            }
        }
    }

    private void handleMenuClickForNavToPrevPage(InventoryClickEvent event) {
        if(page == 0) {
            if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().SHOP_NAV_FIRST_PAGE_ALERT_MSG)) {
                event.getWhoClicked().sendMessage(
                        ColorTranslator.translateColorCodes(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX
                                + FindItemAddOn.getConfigProvider().SHOP_NAV_FIRST_PAGE_ALERT_MSG));
            }
        }
        else {
            page = page - 1;
            super.open(super.playerMenuUtility.getPlayerShopSearchResult());
        }
    }

    private void handleFirstPageClick(InventoryClickEvent event) {
        if(page == 0) {
            if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().SHOP_NAV_FIRST_PAGE_ALERT_MSG)) {
                event.getWhoClicked().sendMessage(
                        ColorTranslator.translateColorCodes(
                                FindItemAddOn.getConfigProvider().PLUGIN_PREFIX
                                        + FindItemAddOn.getConfigProvider().SHOP_NAV_FIRST_PAGE_ALERT_MSG));
            }
        } else {
            page = 0;
            super.open(super.playerMenuUtility.getPlayerShopSearchResult());
        }
    }

    private void handleLastPageClick(InventoryClickEvent event) {
        int listSize = super.playerMenuUtility.getPlayerShopSearchResult().size();
        if(!((index + 1) >= listSize)) {
            double totalPages = listSize / maxItemsPerPage;
            if(totalPages % 10 == 0) {
                page = (int) Math.floor(totalPages);
                LoggerUtils.logDebugInfo("Floor page value: " + page);
            }
            else {
                page = (int) Math.ceil(totalPages);
                LoggerUtils.logDebugInfo("Ceiling page value: " + page);
            }
            super.open(super.playerMenuUtility.getPlayerShopSearchResult());
        }
        else {
            if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().SHOP_NAV_LAST_PAGE_ALERT_MSG)) {
                event.getWhoClicked().sendMessage(
                        ColorTranslator.translateColorCodes(
                                FindItemAddOn.getConfigProvider().PLUGIN_PREFIX
                                        + FindItemAddOn.getConfigProvider().SHOP_NAV_LAST_PAGE_ALERT_MSG));
            }
        }
    }


    /**
     * Empty method in case we need to handle static GUI icons in future
     */
    @Override
    public void setMenuItems() {
        // Just overriding
    }

    /**
     * Sets the slots in the search result GUI
     * @param foundShops List of found shops
     */
    @Override
    public void setMenuItems(List<FoundShopItemModel> foundShops) {
        addMenuBottomBar();
        if(foundShops != null && !foundShops.isEmpty()) {
            int maxItemsPerPage = super.maxItemsPerPage;
            for(int guiSlotCounter = 0; guiSlotCounter < maxItemsPerPage; guiSlotCounter++) {
                index = maxItemsPerPage * page + guiSlotCounter;
                if(index >= foundShops.size()) {
                    break;
                }
                if(foundShops.get(index) != null) {
                    // Place Search Results here
                    FoundShopItemModel foundShopIter = foundShops.get(index);
                    NamespacedKey key = new NamespacedKey(FindItemAddOn.getInstance(), NAMEDSPACE_KEY_LOCATION_DATA);
                    ItemStack item = new ItemStack(foundShopIter.getItem().getType());
                    ItemMeta meta = item.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    com.olziedev.playerwarps.api.warp.Warp nearestPlayerWarp = null;
                    String nearestEWarp = null;

                    // set shop item's lore first
                    if(foundShopIter.getItem().hasItemMeta()) {
                        meta = foundShopIter.getItem().getItemMeta();
                        if(meta.hasLore()) {
                            for(String s : meta.getLore()) {
                                lore.add(ColorTranslator.translateColorCodes(s));
                            }
                        }
                    }
                    List<String> shopItemLore = FindItemAddOn.getConfigProvider().SHOP_GUI_ITEM_LORE;
                    // now set the search related lore
                    int nearestWarpMode = FindItemAddOn.getConfigProvider().NEAREST_WARP_MODE;
                    for(String shopItemLoreIter : shopItemLore) {
                        if(shopItemLoreIter.contains(ShopLorePlaceholdersEnum.NEAREST_WARP.value())) {
                            switch(nearestWarpMode) {
                                case 1:
                                    // EssentialWarp: Check nearest warp
                                    if(EssentialsXPlugin.isEnabled()) {
                                        nearestEWarp = new EssentialWarpsUtil().findNearestWarp(foundShopIter.getShopLocation());
                                        if(nearestEWarp != null && !StringUtils.isEmpty(nearestEWarp)) {
                                            lore.add(ColorTranslator.translateColorCodes(shopItemLoreIter.replace(ShopLorePlaceholdersEnum.NEAREST_WARP.value(), nearestEWarp)));
                                        }
                                        else {
                                            lore.add(ColorTranslator.translateColorCodes(shopItemLoreIter.replace(ShopLorePlaceholdersEnum.NEAREST_WARP.value(), NO_WARP_NEAR_SHOP_ERROR_MSG)));
                                        }
                                    }
                                    break;
                                case 2:
                                    // PlayerWarp: Check nearest warp
                                    if(PlayerWarpsPlugin.getIsEnabled()) {
                                        nearestPlayerWarp = new PlayerWarpsUtil().findNearestWarp(foundShopIter.getShopLocation(), foundShopIter.getShopOwner());
                                        if(nearestPlayerWarp != null) {
                                            lore.add(ColorTranslator.translateColorCodes(shopItemLoreIter.replace(ShopLorePlaceholdersEnum.NEAREST_WARP.value(), nearestPlayerWarp.getWarpName())));
                                        }
                                        else {
                                            lore.add(ColorTranslator.translateColorCodes(shopItemLoreIter.replace(ShopLorePlaceholdersEnum.NEAREST_WARP.value(), NO_WARP_NEAR_SHOP_ERROR_MSG)));
                                        }
                                    }
                                    break;
                                case 3:
                                    // WG Region: Check nearest WG Region
                                    if(WGPlugin.isEnabled()) {
                                        String nearestWGRegion = new WGRegionUtils().findNearestWGRegion((foundShopIter.getShopLocation()));
                                        if(nearestWGRegion != null && !StringUtils.isEmpty(nearestWGRegion)) {
                                            lore.add(ColorTranslator.translateColorCodes(shopItemLoreIter.replace(ShopLorePlaceholdersEnum.NEAREST_WARP.value(), nearestWGRegion)));
                                        }
                                        else {
                                            lore.add(ColorTranslator.translateColorCodes(shopItemLoreIter.replace(ShopLorePlaceholdersEnum.NEAREST_WARP.value(), NO_WG_REGION_NEAR_SHOP_ERROR_MSG)));
                                        }
                                    }
                                    break;
                                default:
                                    LoggerUtils.logDebugInfo("Invalid value in 'nearest-warp-mode' in config.yml!");
                            }
                        }
                        else {
                            lore.add(ColorTranslator.translateColorCodes(replaceLorePlaceholders(shopItemLoreIter, foundShopIter)));
                        }
                    }

                    if(FindItemAddOn.getConfigProvider().TP_PLAYER_DIRECTLY_TO_SHOP) {
                        if(playerMenuUtility.getOwner().hasPermission(PlayerPermsEnum.FINDITEM_SHOPTP.value())) {
                            lore.add(ColorTranslator.translateColorCodes(FindItemAddOn.getConfigProvider().CLICK_TO_TELEPORT_MSG));
                        }
                    }
                    assert meta != null;
                    meta.setLore(lore);

                    // storing location data in item persistent storage
                    String locData = StringUtils.EMPTY;
                    // store the coordinates
                    if(FindItemAddOn.getConfigProvider().TP_PLAYER_DIRECTLY_TO_SHOP) {
                        locData = Objects.requireNonNull(foundShopIter.getShopLocation().getWorld()).getName() + ","
                                + foundShopIter.getShopLocation().getBlockX() + ","
                                + foundShopIter.getShopLocation().getBlockY() + ","
                                + foundShopIter.getShopLocation().getBlockZ();
                    }
                    else if(FindItemAddOn.getConfigProvider().TP_PLAYER_TO_NEAREST_WARP) {
                        // if Nearest Warp is set to EssentialsX Warps, store the warp name
                        if(nearestWarpMode == NearestWarpModeEnum.ESSENTIAL_WARPS.value()) {
                            if(nearestEWarp != null) {
                                locData = nearestEWarp;
                            }
                        }
                        // if Nearest Warp is set to PlayerWarps, store the warp name
                        else if(nearestWarpMode == NearestWarpModeEnum.PLAYER_WARPS.value() && nearestPlayerWarp != null) {
                            locData = nearestPlayerWarp.getWarpName();
                        }
                    }
                    meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, locData);

                    // handling custom model data
                    if(Objects.requireNonNull(foundShopIter.getItem().getItemMeta()).hasCustomModelData()) {
                        meta.setCustomModelData(foundShopIter.getItem().getItemMeta().getCustomModelData());
                    }
                    item.setItemMeta(meta);
                    inventory.addItem(item);
                }
            }
        }
    }

    /**
     * Replaces all the placeholders in the Shop item lore in GUI
     * @param text Line of lore
     * @param shop Shop instance
     * @return Line of lore replaced with placeholder values
     */
    private String replaceLorePlaceholders(String text, FoundShopItemModel shop) {

        if(text.contains(ShopLorePlaceholdersEnum.ITEM_PRICE.value())) {
            text = text.replace(ShopLorePlaceholdersEnum.ITEM_PRICE.value(), formatNumber(shop.getShopPrice()));
        }
        if(text.contains(ShopLorePlaceholdersEnum.SHOP_STOCK.value())) {
            if(shop.getRemainingStockOrSpace() == -2) {
                // if -2 (cache doesn't have value) -> try to fetch from MAIN thread
                int stockOrSpace = processUnknownStockSpace(shop);
                if(stockOrSpace == -2) {
                    text = text.replace(ShopLorePlaceholdersEnum.SHOP_STOCK.value(), SHOP_STOCK_UNKNOWN);
                }
                else {
                    text = text.replace(ShopLorePlaceholdersEnum.SHOP_STOCK.value(),
                            (stockOrSpace == -1 ? SHOP_STOCK_UNLIMITED : String.valueOf(stockOrSpace)));
                }
            } else if(shop.getRemainingStockOrSpace() == Integer.MAX_VALUE) {
                text = text.replace(ShopLorePlaceholdersEnum.SHOP_STOCK.value(), SHOP_STOCK_UNLIMITED);
            } else {
                text = text.replace(ShopLorePlaceholdersEnum.SHOP_STOCK.value(), String.valueOf(shop.getRemainingStockOrSpace()));
            }
        }
        if(text.contains(ShopLorePlaceholdersEnum.SHOP_OWNER.value())) {
            OfflinePlayer shopOwner = Bukkit.getOfflinePlayer(shop.getShopOwner());
            if(shopOwner.getName() != null) {
                text = text.replace(ShopLorePlaceholdersEnum.SHOP_OWNER.value(), shopOwner.getName());
            }
            else {
                // set a generic name for shops with no owner name
                text = text.replace(ShopLorePlaceholdersEnum.SHOP_OWNER.value(), "Admin");
            }
        }
        if(text.contains(ShopLorePlaceholdersEnum.SHOP_LOCATION.value())) {
            text = text.replace(ShopLorePlaceholdersEnum.SHOP_LOCATION.value(),
                    shop.getShopLocation().getBlockX() + ", "
                    + shop.getShopLocation().getBlockY() + ", "
                    + shop.getShopLocation().getBlockZ());
        }
        if(text.contains(ShopLorePlaceholdersEnum.SHOP_WORLD.value())) {
            text = text.replace(ShopLorePlaceholdersEnum.SHOP_WORLD.value(), Objects.requireNonNull(shop.getShopLocation().getWorld()).getName());
        }
        // Added in v2.0
        if(text.contains(ShopLorePlaceholdersEnum.SHOP_VISITS.value())) {
            text = text.replace(ShopLorePlaceholdersEnum.SHOP_VISITS.value(), String.valueOf(ShopSearchActivityStorageUtil.getPlayerVisitCount(shop.getShopLocation())));
        }
        return text;
    }

    private int processUnknownStockSpace(FoundShopItemModel shop) {
        return FindItemAddOn.getQsApiInstance().processUnknownStockSpace(shop.getShopLocation(), shop.isToBuy());
    }

    private String replaceDelayPlaceholder(String tpDelayMsg, long delay) {
        return tpDelayMsg.replace("{DELAY}", String.valueOf(delay));
    }

    public static String formatNumber(double number) {
        if(FindItemAddOn.getConfigProvider().SHOP_GUI_USE_SHORTER_CURRENCY_FORMAT) {
            if (number < 100_000) {
                return String.format("%,.2f", number);
            } else if (number < 1_000_000) {
                return String.format("%.2fK", number / 1_000.0);
            } else if (number < 1_000_000_000) {
                return String.format("%.2fM", number / 1_000_000.0);
            } else if (number < 1_000_000_000_000L) {
                return String.format("%.2fB", number / 1_000_000_000.0);
            } else {
                return String.format("%.2fT", number / 1_000_000_000_000.0);
            }
        } else {
            return String.format("%,.2f", number);
        }
    }
}
