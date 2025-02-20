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

import io.myzticbean.finditemaddon.models.FoundShopItemModel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * @author myzticbean
 */
public class PlayerMenuUtility {
    private UUID owner;

    @Setter
    @Getter
    private List<FoundShopItemModel> playerShopSearchResult;

    public PlayerMenuUtility(Player owner) {
        this.owner = owner.getUniqueId();
    }

    public PlayerMenuUtility(UUID owner) {
        this.owner = owner;
    }

    @Nullable
    public Player getOwner() {
        return Bukkit.getPlayer(owner);
    }

    public void setOwner(Player owner) {
        this.owner = owner.getUniqueId();
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }
}
