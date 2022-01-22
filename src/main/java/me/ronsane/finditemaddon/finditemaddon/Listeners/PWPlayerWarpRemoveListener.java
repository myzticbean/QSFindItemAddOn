package me.ronsane.finditemaddon.finditemaddon.Listeners;

import com.olziedev.playerwarps.api.events.PlayerWarpRemoveEvent;
import me.ronsane.finditemaddon.finditemaddon.Dependencies.PlayerWarpsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PWPlayerWarpRemoveListener implements Listener {
    @EventHandler
    public void onPlayerWarpRemove(PlayerWarpRemoveEvent event) {
        PlayerWarpsPlugin.updateWarpsOnEventCall(event.getPlayerWarp(), true);
    }
}
