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

import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.utils.log.Logger;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Bukkit/Paper implementation of {@link PlatformBridge}.
 * All direct Bukkit API calls are contained here; nothing outside this class
 * should import Bukkit types purely for player-interaction purposes.
 */
public class BukkitPlatformBridge implements PlatformBridge {

    // Detected once at class-load time; true when running on Paper (Adventure API available).
    private static final boolean HAS_PAPER_ACTION_BAR = detectPaperActionBar();

    private static boolean detectPaperActionBar() {
        try {
            Player.class.getMethod("sendActionBar", net.kyori.adventure.text.ComponentLike.class);
            return true;
        } catch (NoSuchMethodException _) {
            return false;
        }
    }

    @Override
    public void sendMessage(HumanEntity player, String message) {
        FindItemAddOn.getScheduler()
                .runAtEntity(player, t -> player.sendMessage(ColorTranslator.translateColorCodes(message)));
    }

    @Override
    public void sendActionBar(Player player, String message) {
        if (HAS_PAPER_ACTION_BAR) {
            // Paper / Adventure path — proper Unicode, gradient, hex support
            player.sendActionBar(
                    LegacyComponentSerializer.legacySection()
                            .deserialize(ColorTranslator.translateColorCodes(message)));
        } else {
            // Spigot BungeeCord fallback
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    ColorTranslator.translateColorCodesToTextComponent(message));
        }
    }

    @Override
    public void teleport(Player player, Location location) {
        FindItemAddOn.getScheduler()
                .teleportAsync(player, location, PlayerTeleportEvent.TeleportCause.PLUGIN)
                .thenAccept(isTeleported -> Logger.logDebugInfo("Player teleported to shop: " + isTeleported));
    }

    @Override
    public boolean hasPermission(Player player, String permission) {
        if (Bukkit.isPrimaryThread()) {
            return player.hasPermission(permission);
        }
        try {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            FindItemAddOn.getScheduler().runAtEntity(player, t -> future.complete(player.hasPermission(permission)));
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
