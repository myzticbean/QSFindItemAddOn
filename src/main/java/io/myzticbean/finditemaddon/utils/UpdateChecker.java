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
package io.myzticbean.finditemaddon.utils;

import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.models.enums.PlayerPermsEnum;
import io.myzticbean.finditemaddon.utils.api.modrinth.ModrinthService;
import io.myzticbean.finditemaddon.utils.async.VirtualThreadScheduler;
import io.myzticbean.finditemaddon.utils.log.Logger;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * @author myzticbean
 */
public class UpdateChecker {

    private static final String SPIGOT_DOWNLOAD_LINK = "https://www.spigotmc.org/resources/" + FindItemAddOn.getPluginID() + "/";
    private static final String MODRINTH_DOWNLOAD_LINK = "https://modrinth.com/plugin/shop-search/versions/";
    private final boolean suppressUpdateNotifications;
    private final ModrinthService modrinthService;

    public UpdateChecker() {
        this.suppressUpdateNotifications = FindItemAddOn.getConfigProvider().SUPPRESS_UPDATE_NOTIFICATIONS;
        this.modrinthService = new ModrinthService();
    }

    public void isUpdateAvailable(Consumer<Boolean> updateAvailabilityConsumer) {
        VirtualThreadScheduler.runTaskAsync(() -> {
            var projectVersions = modrinthService.getProjectVersions(FindItemAddOn.getModrinthProjectSlug());
            if (projectVersions == null || projectVersions.isEmpty()) return;
            var latestVersionDetails = projectVersions.getFirst();
            var latestVersion = latestVersionDetails.getVersionNumber();
            Logger.logDebugInfo("Checking for updates:: Spigot Version: " + FindItemAddOn.getInstance().getDescription().getVersion() + " | Modrinth Latest Version: " + latestVersion);
            updateAvailabilityConsumer.accept(FindItemAddOn.getInstance().getDescription().getVersion().equals(latestVersion));
            if(latestVersion.toLowerCase().contains("snapshot")) {
                Logger.logWarning("Plugin has a new snapshot version available! (Version: " + latestVersion + ")");
            }
            else {
                Logger.logWarning("Plugin has a new update available! (Version: " + latestVersion + ")");
            }
            Logger.logWarning("Download here: https://modrinth.com/plugin/shop-search/version/" + latestVersion);
        });
    }

    @Deprecated(since = "v2.0.7.7")
    public void getLatestVersion(Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(FindItemAddOn.getInstance(), () -> {
            try (
                    InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + FindItemAddOn.getPluginID()).openStream();
                    Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                Logger.logError("Update checker is broken, can't find an update!" + exception.getMessage());
            }
        });
    }

    public void notifyPlayerAboutUpdateOnJoin(PlayerJoinEvent event) {
        if(suppressUpdateNotifications) {
            return;
        }
        Player player = event.getPlayer();
        if ((player.isOp() || player.hasPermission(PlayerPermsEnum.FINDITEM_ADMIN.value()))
                && FindItemAddOn.isPluginOutdated()) {
            String prefix = "&8["
                    + "&#55a800Q"
                    + "&#5ea800S"
                    + "&#66a800F"
                    + "&#6ea900i"
                    + "&#76a900n"
                    + "&#7da900d"
                    + "&#84a900I"
                    + "&#8ba900t"
                    + "&#92a900e"
                    + "&#98a900m"
                    + "&#9ea900A"
                    + "&#a4a900d"
                    + "&#aaa800d"
                    + "&#b0a800O"
                    + "&#b6a800n"
                    + "&8] ";
            player.sendMessage(ColorTranslator.translateColorCodes(
                    prefix
                            + "&#59b300Hey &#73e600" + player.getName()
                            + "&#59b300! Plugin has an update... You are still on v"
                            + FindItemAddOn.getInstance().getDescription().getVersion()));
//            player.sendMessage(ColorTranslator.translateColorCodes(prefix + "&#59b300Download here: &#a3a3c2&n" + SPIGOT_DOWNLOAD_LINK));
            player.sendMessage(ColorTranslator.translateColorCodes(prefix + "&#59b300Download here: &#a3a3c2&n" + MODRINTH_DOWNLOAD_LINK));
        }
    }

}
