package io.myzticbean.finditemaddon.Listeners;

import com.olziedev.playerwarps.api.events.PlayerWarpRemoveEvent;
import io.myzticbean.finditemaddon.Dependencies.PlayerWarpsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PWPlayerWarpRemoveEventListener implements Listener {
    @EventHandler
    public void onPlayerWarpRemove(PlayerWarpRemoveEvent event) {
        PlayerWarpsPlugin.updateWarpsOnEventCall(event.getPlayerWarp(), true);
    }
}
