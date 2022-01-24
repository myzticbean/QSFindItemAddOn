package me.ronsane.finditemaddon.finditemaddon.GUIHandler;

import me.ronsane.finditemaddon.finditemaddon.Models.FoundShopItemModel;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerMenuUtility {
    private Player owner;

    private List<FoundShopItemModel> playerShopSearchResult;

    public List<FoundShopItemModel> getPlayerShopSearchResult() {
        return playerShopSearchResult;
    }

    public void setPlayerShopSearchResult(List<FoundShopItemModel> playerShopSearchResult) {
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
