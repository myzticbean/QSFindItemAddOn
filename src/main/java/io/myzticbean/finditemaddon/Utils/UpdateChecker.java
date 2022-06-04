package io.myzticbean.finditemaddon.Utils;

import io.myzticbean.finditemaddon.FindItemAddOn;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {

    private final String prefix = "&8[" +
            "&#55a800Q" +
            "&#5ea800S" +
            "&#66a800F" +
            "&#6ea900i" +
            "&#76a900n" +
            "&#7da900d" +
            "&#84a900I" +
            "&#8ba900t" +
            "&#92a900e" +
            "&#98a900m" +
            "&#9ea900A" +
            "&#a4a900d" +
            "&#aaa800d" +
            "&#b0a800O" +
            "&#b6a800n" +
            "&8] ";
    private final int resourceId;

    public UpdateChecker(int resourceId) {
        this.resourceId = resourceId;
    }

    public void getLatestVersion(Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(FindItemAddOn.getInstance(), () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream();
                 Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                LoggerUtils.logError("Update checker is broken, can't find an update!" + exception.getMessage());
            }
        });
    }

    public void notifyPlayerAboutUpdateOnJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(player.isOp() || player.hasPermission(PlayerPerms.FINDITEM_ADMIN.toString())) {
            if(FindItemAddOn.getPluginOutdated()) {
                player.sendMessage(ColorTranslator.translateColorCodes(
                        prefix
                                + "&#59b300Hey &#73e600" + player.getName() + "&#59b300! Plugin has an update... You are still on v"
                                + FindItemAddOn.getInstance().getDescription().getVersion()));

                player.sendMessage(ColorTranslator.translateColorCodes(
                        prefix
                                + "&#59b300Download here: &#a3a3c2&nhttps://www.spigotmc.org/resources/"
                                + FindItemAddOn.getPluginID() + "/"));
            }
        }
    }

}
