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
package uk.mangostudios.finditemaddon.listener;

import com.olziedev.playerwarps.api.events.warp.PlayerWarpCreateEvent;
import uk.mangostudios.finditemaddon.external.PlayerWarpsHandler;
import uk.mangostudios.finditemaddon.FindItemAddOn;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerWarpCreateEventListener implements Listener {
    @EventHandler
    public void onPlayerWarpCreate(PlayerWarpCreateEvent event) {
        // Issue #24 Fix: Converted updateWarpsOnEventCall() call to async
        Bukkit.getScheduler().runTaskAsynchronously(FindItemAddOn.getInstance(),
                () -> PlayerWarpsHandler.updateWarpsOnEventCall(event.getPlayerWarp(), false));
    }
}
