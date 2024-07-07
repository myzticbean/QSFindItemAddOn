package uk.mangostudios.finditemaddon.commands.impl;

import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import uk.mangostudios.finditemaddon.cache.HiddenShopsCache;
import uk.mangostudios.finditemaddon.gui.ManageShopsGui;

public class HideShopCommands extends AbstractCommand {

    @Command("finditem|shopsearch|searchshop manage hideshop")
    private void onHide(Player player) {
        HiddenShopsCache.getInstance().hideShop(player, null);
    }

    @Command("finditem|shopsearch|searchshop manage unhideshop")
    private void onUnhide(Player player) {
        HiddenShopsCache.getInstance().unhideShop(player, null);
    }

    @Command("finditem|shopsearch|searchshop manage")
    private void onManage(Player player) {
        ManageShopsGui.open(player);
    }

}
