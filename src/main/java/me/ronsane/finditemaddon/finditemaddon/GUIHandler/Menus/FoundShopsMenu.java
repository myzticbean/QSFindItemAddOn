package me.ronsane.finditemaddon.finditemaddon.GUIHandler.Menus;

import me.ronsane.finditemaddon.finditemaddon.GUIHandler.Menu;
import me.ronsane.finditemaddon.finditemaddon.GUIHandler.PlayerMenuUtility;
import org.bukkit.event.inventory.InventoryClickEvent;

public class FoundShopsMenu extends Menu {

    public FoundShopsMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "Shops found!";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {

    }

    @Override
    public void setMenuItems() {

    }
}
