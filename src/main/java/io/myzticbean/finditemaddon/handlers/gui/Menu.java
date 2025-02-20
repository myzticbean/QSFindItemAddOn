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
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Based on an awesome tutorial from https://www.youtube.com/watch?v=xebH6M_7k18
 * Gui pagination: https://www.youtube.com/watch?v=e80NO9Pgz7s
 * 
 * @author myzticbean
 */
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
            ItemMeta FILLER_GLASS_meta = this.GUI_FILLER_ITEM.getItemMeta();
            assert FILLER_GLASS_meta != null;
            FILLER_GLASS_meta.setDisplayName(" ");
            this.GUI_FILLER_ITEM.setItemMeta(FILLER_GLASS_meta);
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
        Logger.logInfo("Opening inventory for player: " + playerMenuUtility.getOwner().getName());
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
