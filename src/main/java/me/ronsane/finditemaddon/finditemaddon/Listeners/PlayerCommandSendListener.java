package me.ronsane.finditemaddon.finditemaddon.Listeners;

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
            blockedCommands.add("finditemaddon:finditem");
            blockedCommands.add("finditemaddon:searchitem");
            blockedCommands.add("finditemaddon:shopsearch");
            blockedCommands.add("finditemaddon:searchshop");
            event.getCommands().removeAll(blockedCommands);
        }
    }
}
