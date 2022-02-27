package io.mysticbeans.finditemaddon.Listeners;

import com.olziedev.playerwarps.api.events.PlayerWarpCreateEvent;
import io.mysticbeans.finditemaddon.Dependencies.PlayerWarpsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PWPlayerWarpCreateListener implements Listener {
    @EventHandler
    public void onPlayerWarpCreate(PlayerWarpCreateEvent e) {
        PlayerWarpsPlugin.updateWarpsOnEventCall(e.getPlayerWarp(), false);
    }
}
