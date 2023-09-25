package io.myzticbean.finditemaddon.Listeners;

import com.olziedev.playerwarps.api.events.warp.PlayerWarpCreateEvent;
import io.myzticbean.finditemaddon.Dependencies.PlayerWarpsPlugin;
import io.myzticbean.finditemaddon.FindItemAddOn;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PWPlayerWarpCreateEventListener implements Listener {
    @EventHandler
    public void onPlayerWarpCreate(PlayerWarpCreateEvent event) {
        // Issue #24 Fix: Converted updateWarpsOnEventCall() call to async
        Bukkit.getScheduler().runTaskAsynchronously(FindItemAddOn.getInstance(),
                () -> PlayerWarpsPlugin.updateWarpsOnEventCall(event.getPlayerWarp(), false));
    }
}
