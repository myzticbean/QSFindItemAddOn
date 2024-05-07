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
package io.myzticbean.finditemaddon.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author myzticbean
 */
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ShopSearchActivityModel {

    private String worldName;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;
    private String shopOwnerUUID;
    private List<PlayerShopVisitModel> playerVisitList;
    private boolean isHiddenFromSearch;

    public boolean compareWith(String targetWorldName, double targetX, double targetY, double targetZ, String targetShopOwnerUUID) {
        return this.getWorldName().equalsIgnoreCase(targetWorldName)
                && this.getX() == targetX
                && this.getY() == targetY
                && this.getZ() == targetZ
                && this.getShopOwnerUUID().equalsIgnoreCase(targetShopOwnerUUID);
    }

    public boolean compareWith(String targetWorldName, double targetX, double targetY, double targetZ) {
        return this.getWorldName().equalsIgnoreCase(targetWorldName)
                && this.getX() == targetX
                && this.getY() == targetY
                && this.getZ() == targetZ;
    }

}
