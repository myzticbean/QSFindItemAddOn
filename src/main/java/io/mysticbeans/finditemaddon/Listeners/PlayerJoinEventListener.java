package io.mysticbeans.finditemaddon.Listeners;

import io.mysticbeans.finditemaddon.FindItemAddOn;
import io.mysticbeans.finditemaddon.Utils.PlayerPerms;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinEventListener implements Listener {
    private String prefix = "&8[" +
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
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(player.isOp() || player.hasPermission(PlayerPerms.FINDITEM_ADMIN.toString())) {
            if(FindItemAddOn.getPluginOutdated()) {
                player.sendMessage(ColorTranslator.translateColorCodes(
                        prefix
                                + "&#59b300Hey &#73e600" + player.getName() + "&#59b300! Plugin has an update... (Version: "
                                + FindItemAddOn.getInstance().getDescription().getVersion().replace("-SNAPSHOT", "") + ")"));

                player.sendMessage(ColorTranslator.translateColorCodes(
                        prefix
                                + "&#59b300Download here: &#a3a3c2&nhttps://www.spigotmc.org/resources/"
                                + FindItemAddOn.getPluginID() + "/"));
            }
        }
    }
}
