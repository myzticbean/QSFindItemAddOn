package io.myzticbean.finditemaddon.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Model for the Shop Item lore that will be shown in the search GUI
 */
@Getter
@AllArgsConstructor
public class FoundShopItemModel {
    private final double shopPrice;
    private final int remainingStockOrSpace;
    private final UUID shopOwner;
    private final Location shopLocation;
    private final ItemStack item;
    private final boolean toBuy;
}
