package io.mysticbeans.finditemaddon.Listeners;

import io.mysticbeans.finditemaddon.FindItemAddOn;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerCommandSendListener implements Listener {
    @EventHandler
    public void onPlayerCommandTab(PlayerCommandSendEvent event) {
        if(!event.getPlayer().isOp()) {
            List<String> blockedCommands = new ArrayList<>();
            blockedCommands.add("finditem:finditem");
            blockedCommands.add("finditem:" + FindItemAddOn.getConfigProvider().FIND_ITEM_COMMAND_ALIAS);
            event.getCommands().removeAll(blockedCommands);
        }
    }
}
