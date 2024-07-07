package uk.mangostudios.finditemaddon.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import uk.mangostudios.finditemaddon.FindItemAddOn;
import uk.mangostudios.finditemaddon.cache.HiddenShopsCache;
import uk.mangostudios.finditemaddon.external.QuickShopHandler;
import uk.mangostudios.finditemaddon.listener.HeadDatabaseApiListener;
import uk.mangostudios.finditemaddon.util.Colourify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageShopsGui {

    private final PaginatedGui gui = Gui.paginated()
            .title(Component.text("Manage Shops"))
            .rows(6)
            .disableAllInteractions()
            .create();

    public ManageShopsGui(Player player) {
        // Add the buttons
        gui.setItem(6, 1,
                ItemBuilder.from(this.getMaterial(FindItemAddOn.getConfigProvider().SHOP_GUI_BACK_BUTTON_MATERIAL))
                        .name(Colourify.colour(FindItemAddOn.getConfigProvider().SHOP_GUI_BACK_BUTTON_TEXT))
                        .asGuiItem(event -> gui.previous()));
        gui.setItem(6, 9,
                ItemBuilder.from(this.getMaterial(FindItemAddOn.getConfigProvider().SHOP_GUI_NEXT_BUTTON_MATERIAL))
                        .name(Colourify.colour(FindItemAddOn.getConfigProvider().SHOP_GUI_NEXT_BUTTON_TEXT))
                        .asGuiItem(event -> gui.next()));
        gui.setItem(6, 5,
                ItemBuilder.from(this.getMaterial(FindItemAddOn.getConfigProvider().SHOP_GUI_CLOSE_BUTTON_MATERIAL))
                        .name(Colourify.colour(FindItemAddOn.getConfigProvider().SHOP_GUI_CLOSE_BUTTON_TEXT))
                        .asGuiItem(event -> gui.close(event.getWhoClicked())));

        gui.setItem(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8), ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(Colourify.colour(" ")).asGuiItem());
        gui.setItem(List.of(46, 47, 48, 50, 51, 52), ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(Colourify.colour(" ")).asGuiItem());

        // Add the items
        Map<GuiItem, Integer> items = new HashMap<>(); // GuiItem, Distance
        QuickShopHandler.getInstance().getAllShopsFor(player).forEach(shop -> {
            ItemStack item = shop.getItem().clone();
            Location shopLocation = shop.getLocation();
            List<String> lore = new ArrayList<>();
            boolean isHidden = HiddenShopsCache.getInstance().isShopHidden(player, shopLocation);

            // Calculate the distance from the player to the shop
            int distance = 100000;
            if (player.getWorld().equals(shopLocation.getWorld())) {
                distance = (int) player.getLocation().distance(shopLocation);
            }

            // Add the lore
            String replacement = distance == 100000 ? "Other World" : String.valueOf(distance);
            if (isHidden) {
                for (String line : FindItemAddOn.getConfigProvider().MANAGE_SHOP_GUI_HIDDEN_ITEM_LORE) {
                    lore.add(line
                            .replace("<item>", PlainTextComponentSerializer.plainText().serialize(item.clone().displayName()))
                            .replace("<location>",
                                    "X: " + shop.getLocation().getBlockX()
                                            + ", Y: " + shop.getLocation().getBlockY()
                                            + ", Z: " + shop.getLocation().getBlockZ())
                            .replace("<world>", shop.getLocation().getWorld().getName())
                            .replace("<distance>", replacement)
                    );
                }
            } else {
                for (String line : FindItemAddOn.getConfigProvider().MANAGE_SHOP_GUI_SHOWN_ITEM_LORE) {
                    lore.add(line
                            .replace("<item>", PlainTextComponentSerializer.plainText().serialize(item.clone().displayName()))
                            .replace("<location>",
                                    "X: " + shop.getLocation().getBlockX()
                                            + ", Y: " + shop.getLocation().getBlockY()
                                            + ", Z: " + shop.getLocation().getBlockZ())
                            .replace("<world>", shop.getLocation().getWorld().getName())
                            .replace("<distance>", replacement)
                    );
                }
            }


            // Add the item to the GUI
            items.put(ItemBuilder.from(item.clone())
                    .name(item.clone().displayName().decoration(TextDecoration.ITALIC, false))
                    .lore(Colourify.colour(lore))
                    .asGuiItem(inventoryClickEvent -> {
                        if (isHidden) {
                            HiddenShopsCache.getInstance().unhideShop(player, shopLocation);
                            ManageShopsGui.open(player);
                        } else {
                            HiddenShopsCache.getInstance().hideShop(player, shopLocation);
                            ManageShopsGui.open(player);
                        }
                    }), distance);
        });

        // Sort the items by distance
        List<Map.Entry<GuiItem, Integer>> sortedEntries = new ArrayList<>(items.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue());

        for (Map.Entry<GuiItem, Integer> entry : sortedEntries) {
            gui.addItem(entry.getKey());
        }
    }

    public static void open(Player player) {
        new ManageShopsGui(player).gui.open(player);
    }

    private ItemStack getMaterial(String material) {
        try {
            return new ItemStack(Material.valueOf(material));
        } catch (IllegalArgumentException e) {
            return HeadDatabaseApiListener.getInstance().getApi().getItemHead(material.replace("hdb-", ""));
        }
    }
}
