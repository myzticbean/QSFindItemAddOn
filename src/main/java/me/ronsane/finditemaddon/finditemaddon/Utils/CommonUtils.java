package me.ronsane.finditemaddon.finditemaddon.Utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommonUtils {

    public static String parseColors(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static void sendPlayerActionBar(Player player, String msg) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(parseColors(msg)));
    }
}
