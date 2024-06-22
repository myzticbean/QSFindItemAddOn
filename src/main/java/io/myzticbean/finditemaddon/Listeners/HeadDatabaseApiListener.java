package io.myzticbean.finditemaddon.Listeners;

import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HeadDatabaseApiListener implements Listener {

    private HeadDatabaseAPI api;
    private static HeadDatabaseApiListener instance;

    public HeadDatabaseApiListener() {
        instance = this;
    }

    @EventHandler
    public void onDatabaseLoad(DatabaseLoadEvent e) {
        api = new HeadDatabaseAPI();
    }

    public HeadDatabaseAPI getApi() {
        return api;
    }

    public static HeadDatabaseApiListener getInstance() {
        return instance;
    }

}
