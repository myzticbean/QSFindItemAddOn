package io.myzticbean.finditemaddon.Utils;

import me.kodysimpson.simpapi.colors.ColorTranslator;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommonUtils {

    public static String parseColors(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static void sendPlayerActionBar(Player player, String msg) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, ColorTranslator.translateColorCodesToTextComponent(msg));
    }

    public static String capitalizeFirstLetters(String str) {
        char[] array = str.toCharArray();

        // Uppercase first letter.
        array[0] = Character.toUpperCase(array[0]);

        // Uppercase all letters that follow a whitespace character.
        for (int i = 1; i < array.length; i++) {
            if (Character.isWhitespace(array[i - 1])) {
                array[i] = Character.toUpperCase(array[i]);
            }
        }

        return new String(array);
    }

    public static Double calculateDistance2D(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }

    public static Double calculateDistance3D(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.pow((Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2)), 0.5);
    }
}
