package me.ronsane.finditemaddon.finditemaddon.GUIHandler;

import me.ronsane.finditemaddon.finditemaddon.FindItemAddOn;
import me.ronsane.finditemaddon.finditemaddon.Utils.CommonUtils;
import me.ronsane.finditemaddon.finditemaddon.Utils.LocationUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.maxgamer.quickshop.shop.Shop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FoundShopsGUI {
    private static Inventory foundShopsGUI;

    public static Inventory createGUI(List<Shop> shopsList) {
        NamespacedKey key = new NamespacedKey(FindItemAddOn.getInstance(), "locationData");
        foundShopsGUI = Bukkit.createInventory(null, 54, "Shops found (Top "+ shopsList.size() +")");
        ItemStack item = new ItemStack(shopsList.get(0).getItem().getType());
        ItemMeta meta = item.getItemMeta();
        List<String> lore;
        OfflinePlayer offlinePlayer;
        int guiSlotCounter = 0;
        for(Shop shop : shopsList) {
            lore = new ArrayList<>();
            lore.add("");
            if(shop.getItem().getItemMeta() != null && !StringUtils.isEmpty(shop.getItem().getItemMeta().getDisplayName())) {
                lore.add(CommonUtils.parseColors("&fItem name: &7" + shop.getItem().getItemMeta().getDisplayName()));
            }
            lore.add(CommonUtils.parseColors("&fPrice: &a" + FindItemAddOn.essAPI.getSettings().getCurrencySymbol() + shop.getPrice()));
            if(!shop.isBuying()) {
                lore.add(CommonUtils.parseColors("&fStock: &7" + shop.getRemainingStock()));
            }
            offlinePlayer = Bukkit.getOfflinePlayer(shop.getOwner());
            lore.add(CommonUtils.parseColors("&fOwner: &7" + offlinePlayer.getName()));
            lore.add((CommonUtils.parseColors("&fLocation: &7"
                    + shop.getLocation().getBlockX() + ", "
                    + shop.getLocation().getBlockY() + ", ")
                    + shop.getLocation().getBlockZ()));
            lore.add((CommonUtils.parseColors("&fWorld: &7" + Objects.requireNonNull(shop.getLocation().getWorld()).getName())));
            lore.add("");
            lore.add(CommonUtils.parseColors("&6&lClick to teleport to the shop!"));
            meta.setLore(lore);
            String locData = Objects.requireNonNull(shop.getLocation().getWorld()).getName() + ","
                            + shop.getLocation().getBlockX() + ","
                            + shop.getLocation().getBlockY() + ","
                            + shop.getLocation().getBlockZ();
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, locData);
            item.setItemMeta(meta);
            foundShopsGUI.setItem(guiSlotCounter, item);
            guiSlotCounter++;
        }
        return foundShopsGUI;
    }

    public static void onFoundShopsGUIInvClick(InventoryClickEvent event) {
        if(!event.getInventory().equals(foundShopsGUI)) { return; }

        if(event.getCurrentItem() == null) { return; }

        if(event.getCurrentItem().getItemMeta() == null) { return; }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        ItemStack item = event.getCurrentItem();
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(FindItemAddOn.getInstance(), "locationData");
        if(!meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            return;
        }
        else {
            String locData = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            List<String> locDataList = Arrays.asList(locData.split("\\s*,\\s*"));

            World world = Bukkit.getWorld(locDataList.get(0));
            int locX = Integer.parseInt(locDataList.get(1)), locY = Integer.parseInt(locDataList.get(2)), locZ = Integer.parseInt(locDataList.get(3));
            Location shopLocation = new Location(world, locX, locY, locZ);
            Location locToTeleport = LocationUtils.findSafeLocationAroundShop(shopLocation);
            if(locToTeleport != null) {
                player.teleport(locToTeleport);
            }
            else {
                player.sendMessage(FindItemAddOn.PluginPrefix + CommonUtils.parseColors(FindItemAddOn.getInstance().getConfig().getString("FindItemCommand.UnsafeShopAreaMessage")));
            }
            player.closeInventory();
        }
    }
}
