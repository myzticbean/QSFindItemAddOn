package me.ronsane.finditemaddon.finditemaddon.Events;

import me.ronsane.finditemaddon.finditemaddon.FindItemAddOn;
import me.ronsane.finditemaddon.finditemaddon.GUIHandler.FoundShopsGUI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class EventInventoryClick implements Listener {

    @EventHandler
    public void onPlayerInventoryClick(InventoryClickEvent event) {
        try {
//            BuyOrSellGUI.onBuyOrSellGUIInvClick(event);
            FoundShopsGUI.onFoundShopsGUIInvClick(event);
        }
        catch(Exception e) {
            Bukkit.getLogger().severe(FindItemAddOn.PluginPrefix + "Exception occurred!");
            e.printStackTrace();
        }
    }
}
