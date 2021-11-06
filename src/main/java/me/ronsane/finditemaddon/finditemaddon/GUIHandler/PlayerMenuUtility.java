package me.ronsane.finditemaddon.finditemaddon.GUIHandler;

import org.bukkit.entity.Player;
import org.maxgamer.quickshop.api.shop.Shop;

import java.util.List;

public class PlayerMenuUtility {
    private Player owner;

    private List<Shop> playerShopSearchResult;

    public List<Shop> getPlayerShopSearchResult() {
        return playerShopSearchResult;
    }

    public void setPlayerShopSearchResult(List<Shop> playerShopSearchResult) {
        this.playerShopSearchResult = playerShopSearchResult;
    }

    public PlayerMenuUtility(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }
}
