package me.ronsane.finditemaddon.finditemaddon.GUIHandler;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.maxgamer.quickshop.shop.Shop;

import java.util.List;

// Based on an awesome tutorial from https://www.youtube.com/watch?v=xebH6M_7k18
// Gui pagination: https://www.youtube.com/watch?v=e80NO9Pgz7s
public abstract class Menu implements InventoryHolder {

    protected Inventory inventory;

    protected PlayerMenuUtility playerMenuUtility;

    protected ItemStack FILLER_GLASS = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

    public Menu(PlayerMenuUtility playerMenuUtility) {
        this.playerMenuUtility = playerMenuUtility;
        ItemMeta FILLER_GLASS_meta = this.FILLER_GLASS.getItemMeta();
        assert FILLER_GLASS_meta != null;
        FILLER_GLASS_meta.setDisplayName(" ");
        this.FILLER_GLASS.setItemMeta(FILLER_GLASS_meta);
    }

    public abstract String getMenuName();

    public abstract int getSlots();

    public abstract void handleMenu(InventoryClickEvent e);

    public abstract void setMenuItems();

    public abstract void setMenuItems(List<Shop> foundShops);

    public void open() {
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());
        this.setMenuItems();
        playerMenuUtility.getOwner().openInventory(inventory);
    }

    public void open(List<Shop> foundShops) {
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
