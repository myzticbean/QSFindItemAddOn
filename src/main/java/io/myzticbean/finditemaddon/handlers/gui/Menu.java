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
package io.myzticbean.finditemaddon.handlers.gui;

import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.models.FoundShopItemModel;
import io.myzticbean.finditemaddon.utils.log.Logger;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Based on an awesome tutorial from https://www.youtube.com/watch?v=xebH6M_7k18
 * Gui pagination: https://www.youtube.com/watch?v=e80NO9Pgz7s
 * 
 * @author myzticbean
 */
@SuppressWarnings({"java:S100", "java:S3776"})
public abstract class Menu implements InventoryHolder {

    protected Inventory inventory;

    protected PlayerMenuUtility playerMenuUtility;

    protected ItemStack GUI_FILLER_ITEM;

    protected Menu(PlayerMenuUtility playerMenuUtility) {
        this.playerMenuUtility = playerMenuUtility;

        assert FindItemAddOn.getConfigProvider().SHOP_GUI_FILLER_ITEM != null;
        Material fillerMaterial = Material.getMaterial(FindItemAddOn.getConfigProvider().SHOP_GUI_FILLER_ITEM);
        if (fillerMaterial == null) {
            fillerMaterial = Material.GRAY_STAINED_GLASS_PANE;
        }
        if (!fillerMaterial.isAir()) {
            GUI_FILLER_ITEM = new ItemStack(fillerMaterial);
            ItemMeta fillerItemItemMeta = this.GUI_FILLER_ITEM.getItemMeta();
            assert fillerItemItemMeta != null;
            fillerItemItemMeta.setDisplayName(" ");
            if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().SHOP_GUI_FILLER_ITEM_CMD)) {
                try {
                    fillerItemItemMeta.setCustomModelData(Integer.parseInt(FindItemAddOn.getConfigProvider().SHOP_GUI_FILLER_ITEM_CMD));
                    if(checkServerVersionIs_1_21_5_OrAbove()) {
                        var customModelDataComponent = fillerItemItemMeta.getCustomModelDataComponent();
                        customModelDataComponent.setFloats(List.of(Float.parseFloat(FindItemAddOn.getConfigProvider().SHOP_GUI_FILLER_ITEM_CMD)));
                        fillerItemItemMeta.setCustomModelDataComponent(customModelDataComponent);
                    }
                } catch (Exception e) {
                    Logger.logError("Error setting custom model data", e);
                }
            }
            this.GUI_FILLER_ITEM.setItemMeta(fillerItemItemMeta);
        }
    }

    private boolean checkServerVersionIs_1_21_5_OrAbove() {
        String serverVersionString = Bukkit.getVersion();
        // Example: "1.21-109-5a5035b (MC: 1.21)" or "git-Paper-123 (MC: 1.20.4)"
        Logger.logDebugInfo("Full Server Version for check: " + serverVersionString);
        Pattern pattern = Pattern.compile("\\(MC: ([\\d\\.]+)\\)");
        Matcher matcher = pattern.matcher(serverVersionString);
        if (matcher.find()) {
            String mcVersionStr = matcher.group(1); // This will be "1.21" or "1.20.4" etc.
            Logger.logDebugInfo("Extracted MC Version: " + mcVersionStr);
            String[] versionParts = mcVersionStr.split("\\.");
            // We need at least major and minor version numbers (e.g., "1.21")
            if (versionParts.length >= 2) {
                try {
                    int major = Integer.parseInt(versionParts[0]);
                    int minor = Integer.parseInt(versionParts[1]);
                    int patch = 0; // Default patch to 0 if not specified (e.g., for "1.21")
                    if (versionParts.length >= 3) {
                        // Attempt to parse patch version, removing any non-numeric characters
                        String patchStr = versionParts[2].replaceAll("[^0-9]", "");
                        if (!patchStr.isEmpty()) {
                            patch = Integer.parseInt(patchStr);
                        }
                    }
                    // Target version: 1.21.5
                    final int TARGET_MAJOR = 1;
                    final int TARGET_MINOR = 21;
                    final int TARGET_PATCH = 5;
                    // Compare versions
                    if (major > TARGET_MAJOR) {
                        return true; // e.g., 2.x.x is > 1.21.5
                    }
                    if (major == TARGET_MAJOR) {
                        if (minor > TARGET_MINOR) {
                            return true; // e.g., 1.22.x is > 1.21.5
                        }
                        if (minor == TARGET_MINOR) {
                            return patch >= TARGET_PATCH; // e.g., 1.21.5, 1.21.6 are >= 1.21.5
                        }
                    }
                    // All other cases are older versions (e.g., 1.20.x, 1.21.0-1.21.4)
                    return false;
                } catch (NumberFormatException e) {
                    Logger.logDebugInfo("Failed to parse MC version parts from: '" + mcVersionStr + "': " + e.getMessage());
                    return false;
                }
            } else {
                Logger.logDebugInfo("Could not parse MC version string '" + mcVersionStr + "' into at least major.minor parts.");
                return false;
            }
        } else {
            Logger.logDebugInfo("Could not find MC version pattern '(MC: ...)' in server version string: '" + serverVersionString + "'. Assuming older version or unable to determine.");
            return false;
        }
    }

    public abstract String getMenuName();

    public abstract int getSlots();

    public abstract void handleMenu(InventoryClickEvent e);

    public abstract void setMenuItems(List<FoundShopItemModel> foundShops);

    /*public void open() {
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());
        this.setMenuItems();
        playerMenuUtility.getOwner().openInventory(inventory);
    }*/

    public void open(List<FoundShopItemModel> foundShops) {
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());
        this.setMenuItems(foundShops);
        playerMenuUtility.getOwner().openInventory(inventory);
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
