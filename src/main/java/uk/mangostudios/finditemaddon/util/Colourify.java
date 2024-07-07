package uk.mangostudios.finditemaddon.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;
import java.util.stream.Collectors;

public class Colourify {

    /**
     * Convert a string to a coloured component
     * @param text The text to convert
     * @return The coloured component
     */
    public static Component colour(String text) {
        return MiniMessage.miniMessage().deserialize(text)
                .decoration(TextDecoration.ITALIC, false);
    }

    /**
     * Convert a list of strings to a list of coloured components
     * @param text The text to convert
     * @return The coloured components
     */
    public static List<Component> colour(List<String> text) {
        return text.stream().map(Colourify::colour).collect(Collectors.toList());
    }

}
