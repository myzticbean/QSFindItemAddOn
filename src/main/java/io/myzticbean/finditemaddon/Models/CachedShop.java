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
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package io.myzticbean.finditemaddon.Models;

import org.bukkit.Location;

import java.util.Date;

public class CachedShop {
    private long shopId;
    private Location shopLocation;
    private int remainingStock;
    private int remainingSpace;
    private Date lastFetched;

    public CachedShop(long shopId, Location shopLocation, int remainingStock, int remainingSpace, Date lastFetched) {
        this.shopId = shopId;
        this.shopLocation = shopLocation;
        this.remainingStock = remainingStock;
        this.remainingSpace = remainingSpace;
        this.lastFetched = lastFetched;
    }

    public long getShopId() {
        return shopId;
    }

    public Location getShopLocation() {
        return shopLocation;
    }

    public int getRemainingStock() {
        return remainingStock;
    }

    public int getRemainingSpace() {
        return remainingSpace;
    }

    public Date getLastFetched() {
        return lastFetched;
    }

}
