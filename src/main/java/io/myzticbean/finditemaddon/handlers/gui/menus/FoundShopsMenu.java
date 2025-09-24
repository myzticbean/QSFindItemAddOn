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
package io.myzticbean.finditemaddon.handlers.gui.menus;

import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.olziedev.playerwarps.api.warp.Warp;
import io.myzticbean.finditemaddon.config.ConfigProvider;
import io.myzticbean.finditemaddon.dependencies.EssentialsXPlugin;
import io.myzticbean.finditemaddon.dependencies.PlayerWarpsPlugin;
import io.myzticbean.finditemaddon.dependencies.ResidencePlugin;
import io.myzticbean.finditemaddon.dependencies.WGPlugin;
import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.handlers.gui.PaginatedMenu;
import io.myzticbean.finditemaddon.handlers.gui.PlayerMenuUtility;
import io.myzticbean.finditemaddon.models.FoundShopItemModel;
import io.myzticbean.finditemaddon.models.enums.CustomCmdPlaceholdersEnum;
import io.myzticbean.finditemaddon.models.enums.PlayerPermsEnum;
import io.myzticbean.finditemaddon.models.enums.ShopLorePlaceholdersEnum;
import io.myzticbean.finditemaddon.utils.json.ShopSearchActivityStorageUtil;
import io.myzticbean.finditemaddon.utils.LocationUtils;
import io.myzticbean.finditemaddon.utils.log.Logger;
import io.myzticbean.finditemaddon.utils.warp.EssentialWarpsUtil;
import io.myzticbean.finditemaddon.utils.warp.PlayerWarpsUtil;
import io.myzticbean.finditemaddon.utils.warp.ResidenceUtils;
import io.myzticbean.finditemaddon.utils.warp.WGRegionUtils;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Handler class for FoundShops GUI
 * 
 * @author myzticbean
 */
public class FoundShopsMenu extends PaginatedMenu {

    public static final String SHOP_STOCK_UNLIMITED = "Unlimited";
    public static final String SHOP_STOCK_UNKNOWN = "Unknown";
    private static final String NAMEDSPACE_KEY_LOCATION_DATA = "locationData";
    private final ConfigProvider configProvider;

    public FoundShopsMenu(PlayerMenuUtility playerMenuUtility, List<FoundShopItemModel> searchResult) {
        super(playerMenuUtility, searchResult);
        configProvider = FindItemAddOn.getConfigProvider();
    }

    @Override
    public String getMenuName() {
        if (!StringUtils.isEmpty(configProvider.SHOP_SEARCH_GUI_TITLE)) {
            return ColorTranslator.translateColorCodes(configProvider.SHOP_SEARCH_GUI_TITLE);
        } else {
            return ColorTranslator.translateColorCodes("&l» &rShops");
        }
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(@NotNull InventoryClickEvent event) {
        int slot = event.getSlot();
        Player player = (Player) event.getWhoClicked();

        // Handle navigation buttons (prev, next, first, last page)
        if (handleNavigationClick(event, slot)) {
            return;
        }

        // Close inventory if the close button (slot 49) is clicked
        if (slot == 49) {
            player.closeInventory();
            return;
        }

        // Ignore clicks on empty slots
        if (event.getCurrentItem().getType().equals(Material.AIR)) {
            Logger.logDebugInfo(player.getName() + " just clicked on AIR!");
            return;
        }

        // Handle clicks on shop items
        handleShopItemClick(event, player);
    }

    /**
     * Handles clicks on navigation buttons
     * 
     * @param event The InventoryClickEvent
     * @param slot  The clicked slot number
     * @return true if a navigation button was clicked, false otherwise
     */
    private boolean handleNavigationClick(InventoryClickEvent event, int slot) {
        return switch (slot) {
            case 45 -> {
                handleMenuClickForNavToPrevPage(event);
                yield true;
            }
            case 46 -> {
                handleFirstPageClick(event);
                yield true;
            }
            case 52 -> {
                handleLastPageClick(event);
                yield true;
            }
            case 53 -> {
                handleMenuClickForNavToNextPage(event);
                yield true;
            }
            default -> false;
        };
    }

    /**
     * Handles clicks on shop items in the inventory
     * 
     * @param event  The InventoryClickEvent
     * @param player The player who clicked
     */
    private void handleShopItemClick(@NotNull InventoryClickEvent event, Player player) {
        ItemStack item = event.getCurrentItem();
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(FindItemAddOn.getInstance(), NAMEDSPACE_KEY_LOCATION_DATA);

        // Check if the item has the required location data
        if (!meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            Logger.logError("PersistentDataContainer doesn't have the right kind of data!");
            return;
        }

        String locData = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        List<String> locDataList = Arrays.asList(locData.split("\\s*\\|\\|\\|\\s*"));

        // Handle direct teleportation to shop
        if (configProvider.TP_PLAYER_DIRECTLY_TO_SHOP && locDataList.size() > 1) {
            handleDirectShopTeleport(player, locDataList);
        }
        // Handle teleportation to nearest warp
        else if (configProvider.TP_PLAYER_TO_NEAREST_WARP && locDataList.size() == 1
                && !locDataList.get(0).isEmpty()) {
            handleWarpTeleport(player, locDataList.get(0));
        }

        // Execute custom commands if enabled
        handleCustomCommands(player, locDataList);
        player.closeInventory();
    }

    /**
     * Handles direct teleportation to a shop
     * 
     * @param player      The player to teleport
     * @param locDataList List containing location data
     */
    private void handleDirectShopTeleport(@NotNull Player player, List<String> locDataList) {
        // Check if player has permission to teleport
        if (!player.hasPermission(PlayerPermsEnum.FINDITEM_SHOPTP.value())) {
            sendNoPermissionMessage(player);
            return;
        }

        Location shopLocation = parseShopLocation(locDataList);
        if (shopLocation == null)
            return;

        // Check if player is teleporting to their own shop
        UUID shopOwner = ShopSearchActivityStorageUtil.getShopOwnerUUID(shopLocation);
        if (player.getUniqueId().equals(shopOwner) && !PlayerPermsEnum.canPlayerTpToOwnShop(player)) {
            player.sendMessage(ColorTranslator.translateColorCodes(
                    configProvider.PLUGIN_PREFIX + configProvider.SHOP_TP_NO_PERMISSION_MSG));
            return;
        }

        // Find a safe location around the shop
        Location locToTeleport = LocationUtils.findSafeLocationAroundShop(shopLocation, player);
        if (locToTeleport == null) {
            sendUnsafeAreaMessage(player);
            return;
        }

        // Record the visit and set last location for Essentials
        ShopSearchActivityStorageUtil.addPlayerVisitEntryAsync(shopLocation, player);
        if (EssentialsXPlugin.isEnabled())
            EssentialsXPlugin.setLastLocation(player);

        // Apply teleport delay if necessary, otherwise teleport immediately
        if (shouldApplyTeleportDelay(player)) {
            applyTeleportDelay(player, locToTeleport);
        } else {
            PaperLib.teleportAsync(player, locToTeleport, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }

    /**
     * Handles teleportation to the nearest warp
     * 
     * @param player   The player to teleport
     * @param warpName The name of the warp
     */
    private void handleWarpTeleport(Player player, String warpName) {
        switch (configProvider.NEAREST_WARP_MODE) {
            case 1:
                EssentialWarpsUtil.warpPlayer(player, warpName);
                break;
            case 2:
                PlayerWarpsUtil.executeWarpPlayer(player, warpName);
                break;
            case 4:
                ResidenceUtils.residenceTp(player, warpName);
                break;
        }
    }

    /**
     * Executes custom commands if enabled
     * 
     * @param player      The player who triggered the commands
     * @param locDataList List containing location data
     */
    private void handleCustomCommands(Player player, List<String> locDataList) {
        if (configProvider.CUSTOM_CMDS_RUN_ENABLED && !configProvider.CUSTOM_CMDS_LIST.isEmpty()
                && locDataList.size() > 1) {
            Location shopLocation = parseShopLocation(locDataList);
            if (shopLocation == null)
                return;

            for (String cmd : configProvider.CUSTOM_CMDS_LIST) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        replaceCustomCmdPlaceholders(cmd, player, shopLocation));
            }
        }
    }

    /**
     * Parses location data into a Bukkit Location
     * 
     * @param locDataList List containing location data
     * @return A Bukkit Location, or null if parsing fails
     */
    private @Nullable Location parseShopLocation(@NotNull List<String> locDataList) {
        if (locDataList.size() <= 1)
            return null;
        World world = Bukkit.getWorld(locDataList.get(0));
        int locX = Integer.parseInt(locDataList.get(1));
        int locY = Integer.parseInt(locDataList.get(2));
        int locZ = Integer.parseInt(locDataList.get(3));
        return new Location(world, locX, locY, locZ);
    }

    /**
     * Sends a no permission message to the player
     * 
     * @param player The player to send the message to
     */
    private void sendNoPermissionMessage(Player player) {
        if (!StringUtils.isEmpty(configProvider.SHOP_TP_NO_PERMISSION_MSG)) {
            player.sendMessage(ColorTranslator.translateColorCodes(configProvider.PLUGIN_PREFIX
                    + configProvider.SHOP_TP_NO_PERMISSION_MSG));
        }
    }

    /**
     * Sends an unsafe area message to the player
     * 
     * @param player The player to send the message to
     */
    private void sendUnsafeAreaMessage(Player player) {
        if (!StringUtils.isEmpty(configProvider.UNSAFE_SHOP_AREA_MSG)) {
            player.sendMessage(ColorTranslator.translateColorCodes(configProvider.PLUGIN_PREFIX
                    + configProvider.UNSAFE_SHOP_AREA_MSG));
        }
    }

    /**
     * Checks if a teleport delay should be applied
     * 
     * @param player The player to check
     * @return true if a delay should be applied, false otherwise
     */
    private boolean shouldApplyTeleportDelay(Player player) {
        return StringUtils.isNumeric(configProvider.TP_DELAY_IN_SECONDS)
                && !"0".equals(configProvider.TP_DELAY_IN_SECONDS)
                && !PlayerPermsEnum.hasShopTpDelayBypassPermOrAdmin(player);
    }

    /**
     * Applies a teleport delay and sends a message to the player
     * 
     * @param player        The player to apply the delay to
     * @param locToTeleport The location to teleport to after the delay
     */
    private void applyTeleportDelay(Player player, Location locToTeleport) {
        long delay = Long.parseLong(configProvider.TP_DELAY_IN_SECONDS);
        Logger.logDebugInfo("Teleporting delay is set to: " + delay);
        String tpDelayMsg = configProvider.TP_DELAY_MESSAGE;
        if (!StringUtils.isEmpty(tpDelayMsg)) {
            player.sendMessage(ColorTranslator.translateColorCodes(
                    configProvider.PLUGIN_PREFIX + replaceDelayPlaceholder(tpDelayMsg, delay)));
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(
                FindItemAddOn.getInstance(),
                () -> PaperLib.teleportAsync(player, locToTeleport, PlayerTeleportEvent.TeleportCause.PLUGIN),
                delay * 20);
    }

    /**
     * Handles navigation to the next page
     * 
     * @param event The InventoryClickEvent
     */
    private void handleMenuClickForNavToNextPage(InventoryClickEvent event) {
        if (!((index + 1) >= super.playerMenuUtility.getPlayerShopSearchResult().size())) {
            page = page + 1;
            super.open(super.playerMenuUtility.getPlayerShopSearchResult());
        } else {
            if (!StringUtils.isEmpty(configProvider.SHOP_NAV_LAST_PAGE_ALERT_MSG)) {
                event.getWhoClicked().sendMessage(
                        ColorTranslator.translateColorCodes(
                                configProvider.PLUGIN_PREFIX + configProvider.SHOP_NAV_LAST_PAGE_ALERT_MSG));
            }
        }
    }

    /**
     * Handles navigation to the previous page
     * 
     * @param event The InventoryClickEvent
     */
    private void handleMenuClickForNavToPrevPage(InventoryClickEvent event) {
        if (page == 0) {
            if (!StringUtils.isEmpty(configProvider.SHOP_NAV_FIRST_PAGE_ALERT_MSG)) {
                event.getWhoClicked().sendMessage(
                        ColorTranslator.translateColorCodes(configProvider.PLUGIN_PREFIX
                                + configProvider.SHOP_NAV_FIRST_PAGE_ALERT_MSG));
            }
        } else {
            page = page - 1;
            super.open(super.playerMenuUtility.getPlayerShopSearchResult());
        }
    }

    /**
     * Handles navigation to the first page
     * 
     * @param event The InventoryClickEvent
     */
    private void handleFirstPageClick(InventoryClickEvent event) {
        if (page == 0) {
            if (!StringUtils.isEmpty(configProvider.SHOP_NAV_FIRST_PAGE_ALERT_MSG)) {
                event.getWhoClicked().sendMessage(
                        ColorTranslator.translateColorCodes(
                                configProvider.PLUGIN_PREFIX
                                        + configProvider.SHOP_NAV_FIRST_PAGE_ALERT_MSG));
            }
        } else {
            page = 0;
            super.open(super.playerMenuUtility.getPlayerShopSearchResult());
        }
    }

    /**
     * Handles navigation to the last page
     * 
     * @param event The InventoryClickEvent
     */
    private void handleLastPageClick(InventoryClickEvent event) {
        int listSize = super.playerMenuUtility.getPlayerShopSearchResult().size();
        if (!((index + 1) >= listSize)) {
            double totalPages = listSize / MAX_ITEMS_PER_PAGE;
            if (totalPages % 10 == 0) {
                page = (int) Math.floor(totalPages);
                Logger.logDebugInfo("Floor page value: " + page);
            } else {
                page = (int) Math.ceil(totalPages);
                Logger.logDebugInfo("Ceiling page value: " + page);
            }
            super.open(super.playerMenuUtility.getPlayerShopSearchResult());
        } else {
            if (!StringUtils.isEmpty(configProvider.SHOP_NAV_LAST_PAGE_ALERT_MSG)) {
                event.getWhoClicked().sendMessage(
                        ColorTranslator.translateColorCodes(
                                configProvider.PLUGIN_PREFIX
                                        + configProvider.SHOP_NAV_LAST_PAGE_ALERT_MSG));
            }
        }
    }

    /**
     * Sets the slots in the search result GUI
     * 
     * @param foundShops List of found shops
     */
    @Override
    public void setMenuItems(List<FoundShopItemModel> foundShops) {
        // Add the bottom navigation bar to the menu
        addMenuBottomBar();

        // If no shops were found, return early
        if (foundShops == null || foundShops.isEmpty()) {
            return;
        }

        int maxItemsPerPage = MAX_ITEMS_PER_PAGE;
        // Iterate through the slots for this page
        for (int guiSlotCounter = 0; guiSlotCounter < maxItemsPerPage; guiSlotCounter++) {
            // Calculate the index in the foundShops list for the current slot
            index = maxItemsPerPage * page + guiSlotCounter;
            if (index >= foundShops.size()) {
                break;
            }

            FoundShopItemModel foundShop = foundShops.get(index);
            if (foundShop == null) {
                continue;
            }

            // Create an ItemStack for the shop and add it to the inventory
            ItemStack item = createShopItem(foundShop);
            inventory.addItem(item);
        }
    }

    /**
     * Creates an ItemStack representing a shop
     * 
     * @param foundShop The shop to create an item for
     * @return An ItemStack representing the shop
     */
    private @NotNull ItemStack createShopItem(@NotNull FoundShopItemModel foundShop) {
        // Create a new ItemStack based on the shop's item
        ItemStack item = new ItemStack(foundShop.getItem().getType(), foundShop.getItem().getAmount());
        ItemMeta meta = foundShop.getItem().getItemMeta();
        if (meta == null) {
            meta = Bukkit.getItemFactory().getItemMeta(item.getType());
        }

        List<String> lore = new ArrayList<>();
        // Add lore to the item
        addItemLore(lore, foundShop);

        meta.setLore(lore);
        // Set location data in the item's metadata
        setLocationData(meta, foundShop);

        // Preserve custom model data if it exists
        if (foundShop.getItem().getItemMeta().hasCustomModelData()) {
            meta.setCustomModelData(foundShop.getItem().getItemMeta().getCustomModelData());
        }

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Adds lore to an item representing a shop
     * 
     * @param lore      The list to add lore to
     * @param foundShop The shop to create lore for
     */
    private void addItemLore(List<String> lore, FoundShopItemModel foundShop) {
        // Add existing item lore
        ItemMeta shopItemMeta = foundShop.getItem().getItemMeta();
        if (shopItemMeta != null && shopItemMeta.hasLore()) {
            for (String line : shopItemMeta.getLore()) {
                lore.add(ColorTranslator.translateColorCodes(line));
            }
        }

        // Add shop info lore
        for (String loreLine : configProvider.SHOP_GUI_ITEM_LORE) {
            if (loreLine.contains(ShopLorePlaceholdersEnum.NEAREST_WARP.value())) {
                String nearestWarpInfo = getNearestWarpInfo(foundShop);
                lore.add(ColorTranslator.translateColorCodes(
                        loreLine.replace(ShopLorePlaceholdersEnum.NEAREST_WARP.value(), nearestWarpInfo)));
            } else {
                lore.add(ColorTranslator.translateColorCodes(replaceLorePlaceholders(loreLine, foundShop)));
            }
        }

        // Add teleport info if applicable
        if (configProvider.TP_PLAYER_DIRECTLY_TO_SHOP
                && playerMenuUtility.getOwner().hasPermission(PlayerPermsEnum.FINDITEM_SHOPTP.value())) {
            lore.add(ColorTranslator.translateColorCodes(configProvider.CLICK_TO_TELEPORT_MSG));
        }
    }

    /**
     * Finds the nearest warp or region to a shop based on the configuration
     * 
     * @param foundShop The shop to find the nearest warp/region for
     * @return A string representing the nearest warp/region, or an error message if
     *         none found
     */
    private String getNearestWarpInfo(FoundShopItemModel foundShop) {
        int nearestWarpMode = configProvider.NEAREST_WARP_MODE;
        switch (nearestWarpMode) {
            case 1:
                // EssentialsX warps
                if (EssentialsXPlugin.isEnabled()) {
                    String nearestEWarp = EssentialWarpsUtil.findNearestWarp(foundShop.getShopLocation());
                    return (nearestEWarp != null && !StringUtils.isEmpty(nearestEWarp)) ? nearestEWarp
                            : configProvider.NO_WARP_NEAR_SHOP_ERROR_MSG;
                }
                break;
            case 2:
                // PlayerWarps
                if (PlayerWarpsPlugin.getIsEnabled()) {
                    Warp nearestPlayerWarp = PlayerWarpsUtil.findNearestWarp(foundShop.getShopLocation(),
                            foundShop.getShopOwner());
                    return (nearestPlayerWarp != null) ? nearestPlayerWarp.getWarpName()
                            : configProvider.NO_WARP_NEAR_SHOP_ERROR_MSG;
                }
                break;
            case 3:
                // WorldGuard regions
                if (WGPlugin.isEnabled()) {
                    String nearestWGRegion = new WGRegionUtils().findNearestWGRegion(foundShop.getShopLocation());
                    return (nearestWGRegion != null && !StringUtils.isEmpty(nearestWGRegion)) ? nearestWGRegion
                            : configProvider.NO_WG_REGION_NEAR_SHOP_ERROR_MSG;
                }
                break;
            case 4:
                // Residence plugin
                if (ResidencePlugin.isEnabled()) {
                    ClaimedResidence nearestResidence = ResidenceUtils
                            .findNearestResidence(foundShop.getShopLocation());
                    return (nearestResidence != null) ? ResidenceUtils.getResidenceName(nearestResidence)
                            : configProvider.NO_RESIDENCE_NEAR_SHOP_ERROR_MSG;
                }
                break;
            default:
                Logger.logDebugInfo("Invalid value in 'nearest-warp-mode' in config.yml!");
        }
        return configProvider.NO_WARP_NEAR_SHOP_ERROR_MSG;
    }

    /**
     * Sets location data in the item's metadata
     * 
     * @param meta      The ItemMeta to set the data on
     * @param foundShop The shop to get location data from
     */
    private void setLocationData(ItemMeta meta, FoundShopItemModel foundShop) {
        NamespacedKey key = new NamespacedKey(FindItemAddOn.getInstance(), NAMEDSPACE_KEY_LOCATION_DATA);
        String locData = "";

        if (configProvider.TP_PLAYER_DIRECTLY_TO_SHOP) {
            // Store exact coordinates for direct teleportation
            Location shopLoc = foundShop.getShopLocation();
            locData = String.format("%s|||%d|||%d|||%d", shopLoc.getWorld().getName(), shopLoc.getBlockX(),
                    shopLoc.getBlockY(), shopLoc.getBlockZ());
        } else if (configProvider.TP_PLAYER_TO_NEAREST_WARP) {
            // Store nearest warp info for warp teleportation
            locData = getNearestWarpInfo(foundShop);
        }

        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, locData);
    }

    /**
     * Replaces all the placeholders in the Shop item lore in GUI
     * 
     * @param text Line of lore
     * @param shop Shop instance
     * @return Line of lore replaced with placeholder values
     */
    private @NotNull String replaceLorePlaceholders(String text, @NotNull FoundShopItemModel shop) {
        text = text.replace(ShopLorePlaceholdersEnum.ITEM_PRICE.value(), formatNumber(shop.getShopPrice()));

        if (text.contains(ShopLorePlaceholdersEnum.SHOP_STOCK.value())) {
            int stock = shop.getRemainingStockOrSpace();
            String stockText;
            if (stock == -2) {
                // if -2 (cache doesn't have value) -> try to fetch from MAIN thread
                int stockOrSpace = processUnknownStockSpace(shop);
                stockText = (stockOrSpace == -2) ? SHOP_STOCK_UNKNOWN
                        : (stockOrSpace == -1 ? SHOP_STOCK_UNLIMITED : String.valueOf(stockOrSpace));
            } else {
                stockText = (stock == Integer.MAX_VALUE) ? SHOP_STOCK_UNLIMITED : String.valueOf(stock);
            }
            text = text.replace(ShopLorePlaceholdersEnum.SHOP_STOCK.value(), stockText);
        }

        text = text.replace(ShopLorePlaceholdersEnum.SHOP_PER_ITEM_QTY.value(),
                String.valueOf(shop.getItem().getAmount()));

        if (text.contains(ShopLorePlaceholdersEnum.SHOP_OWNER.value())) {
            OfflinePlayer shopOwner = Bukkit.getOfflinePlayer(shop.getShopOwner());
            // set a generic name for shops with no owner name ('Admin')
            String ownerName = shopOwner.getName() != null ? shopOwner.getName() : "Admin";
            text = text.replace(ShopLorePlaceholdersEnum.SHOP_OWNER.value(), ownerName);
        }

        if (text.contains(ShopLorePlaceholdersEnum.SHOP_LOCATION.value())) {
            Location loc = shop.getShopLocation();
            String locText = loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ();
            text = text.replace(ShopLorePlaceholdersEnum.SHOP_LOCATION.value(), locText);
        }

        text = text.replace(ShopLorePlaceholdersEnum.SHOP_WORLD.value(),
                Objects.requireNonNull(shop.getShopLocation().getWorld()).getName());

        // Added in v2.0
        text = text.replace(ShopLorePlaceholdersEnum.SHOP_VISITS.value(),
                String.valueOf(ShopSearchActivityStorageUtil.getPlayerVisitCount(shop.getShopLocation())));

        return text;
    }

    private int processUnknownStockSpace(FoundShopItemModel shop) {
        return FindItemAddOn.getQsApiInstance().processUnknownStockSpace(shop.getShopLocation(), shop.isToBuy());
    }

    private String replaceDelayPlaceholder(String tpDelayMsg, long delay) {
        return tpDelayMsg.replace("{DELAY}", String.valueOf(delay));
    }

    private String formatNumber(double number) {
        if (configProvider.SHOP_GUI_USE_SHORTER_CURRENCY_FORMAT) {
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

    private String replaceCustomCmdPlaceholders(String cmd, Player player, Location shopLoc) {
        return cmd
                .replace(CustomCmdPlaceholdersEnum.PLAYER_NAME.value(), player.getName())
                .replace(CustomCmdPlaceholdersEnum.SHOP_LOC_X.value(), Double.toString(shopLoc.getX()))
                .replace(CustomCmdPlaceholdersEnum.SHOP_LOC_Y.value(), Double.toString(shopLoc.getY()))
                .replace(CustomCmdPlaceholdersEnum.SHOP_LOC_Z.value(), Double.toString(shopLoc.getZ()))
                .replace(CustomCmdPlaceholdersEnum.SHOP_WORLD.value(), shopLoc.getWorld().getName());
    }
}
