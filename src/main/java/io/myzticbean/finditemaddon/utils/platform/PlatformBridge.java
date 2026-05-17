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
package io.myzticbean.finditemaddon.utils.platform;

import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

/**
 * Abstracts all direct Bukkit player-interaction API calls so that future
 * Paper API changes only require updating the single {@link BukkitPlatformBridge}
 * implementation rather than every call site.
 *
 * @see BukkitPlatformBridge
 */
public interface PlatformBridge {

    /**
     * Sends a color-translated message to a player, dispatched on the entity's thread.
     */
    void sendMessage(HumanEntity player, String message);

    /**
     * Sends a color-translated action-bar message to a player.
     */
    void sendActionBar(Player player, String message);

    /**
     * Teleports a player to the given location using the platform scheduler.
     */
    void teleport(Player player, Location location);

    /**
     * Returns whether the player has the given permission, safe to call from any thread.
     */
    boolean hasPermission(Player player, String permission);
}
