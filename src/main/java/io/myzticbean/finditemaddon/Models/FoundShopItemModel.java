package io.myzticbean.finditemaddon.Models;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Model for the Shop Item lore that will be shown in the search GUI
 */
public class FoundShopItemModel {
    private final double shopPrice;
    private final int remainingStock;
    private final UUID shopOwner;
    private final Location shopLocation;
    private final ItemStack item;

    public FoundShopItemModel(double price, int stock, UUID owner, Location loc, ItemStack item) {
        this.shopPrice = price;
        this.remainingStock = stock;
        this.shopOwner = owner;
        this.shopLocation = loc;
        this.item = item;
    }

    public double getPrice() { return shopPrice; }
    public int getRemainingStock() { return remainingStock; }
    public UUID getOwner() { return shopOwner; }
    public Location getLocation() { return shopLocation; }
    public ItemStack getItem() { return item; }
}
