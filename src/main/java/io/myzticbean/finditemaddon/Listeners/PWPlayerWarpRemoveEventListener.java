package io.myzticbean.finditemaddon.Listeners;

import com.olziedev.playerwarps.api.events.PlayerWarpRemoveEvent;
import io.myzticbean.finditemaddon.Dependencies.PlayerWarpsPlugin;
import io.myzticbean.finditemaddon.FindItemAddOn;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PWPlayerWarpRemoveEventListener implements Listener {
    @EventHandler
    public void onPlayerWarpRemove(PlayerWarpRemoveEvent event) {
        // Issue #24 Fix: Converted updateWarpsOnEventCall() call to async
        Bukkit.getScheduler().runTaskAsynchronously(FindItemAddOn.getInstance(),
                () -> PlayerWarpsPlugin.updateWarpsOnEventCall(event.getPlayerWarp(), true));
    }
}
