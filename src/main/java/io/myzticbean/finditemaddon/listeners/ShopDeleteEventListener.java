package io.myzticbean.finditemaddon.listeners;

import com.ghostchu.quickshop.api.event.management.ShopDeleteEvent;
import io.myzticbean.finditemaddon.utils.json.ShopSearchActivityStorageUtil;
import io.myzticbean.finditemaddon.utils.log.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author myzticbean
 */
public class ShopDeleteEventListener implements Listener {
    @EventHandler
    public void onShopDelete(ShopDeleteEvent event) {
        Logger.logDebugInfo("Shop deleted!");
        event.shop().ifPresent(ShopSearchActivityStorageUtil::removeShop);
    }
}
