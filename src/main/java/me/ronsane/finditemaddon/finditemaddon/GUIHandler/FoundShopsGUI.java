package me.ronsane.finditemaddon.finditemaddon.GUIHandler;

import me.ronsane.finditemaddon.finditemaddon.FindItemAddOn;
import me.ronsane.finditemaddon.finditemaddon.Utils.CommonUtils;
import me.ronsane.finditemaddon.finditemaddon.Utils.EnchantmentUtil;
import me.ronsane.finditemaddon.finditemaddon.Utils.LocationUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.maxgamer.quickshop.shop.Shop;

import java.util.*;

public class FoundShopsGUI {

    private static Inventory foundShopsGUI;

    @Deprecated
    public static Inventory createGUI(List<Shop> shopsList) {
        NamespacedKey key = new NamespacedKey(FindItemAddOn.getInstance(), "locationData");
        foundShopsGUI = Bukkit.createInventory(null, 54, "Shops found (Showing Top "+ shopsList.size() +")");
        ItemStack item = new ItemStack(shopsList.get(0).getItem().getType());
        ItemMeta meta = item.getItemMeta();
        List<String> lore;
        OfflinePlayer offlinePlayer;
        int guiSlotCounter = 0;
        for(Shop shop : shopsList) {
            lore = new ArrayList<>();
            if(shop.getItem().getItemMeta() != null) {
                // if custom display name exists
                if(!StringUtils.isEmpty(shop.getItem().getItemMeta().getDisplayName())) {
                    lore.add(CommonUtils.parseColors(shop.getItem().getItemMeta().getDisplayName()));
                }
                // If stored enchants exist and if enchantments not hidden
                if(shop.getItem().getItemMeta() instanceof EnchantmentStorageMeta) {
                    if(!shop.getItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) shop.getItem().getItemMeta();
                        Map<Enchantment, Integer> enchantmentsMap = enchantmentStorageMeta.getStoredEnchants();
                        for(Enchantment enchantment: enchantmentsMap.keySet()) {
                            lore.add(CommonUtils.parseColors("&7" + EnchantmentUtil.mapBukkitEnchantment(enchantment) + " "
                                    + EnchantmentUtil.toRoman(enchantmentsMap.get(enchantment))));
                        }
                    }
                }
                // checking if item has applied enchants and if enchantments not hidden
                if(shop.getItem().getItemMeta().hasEnchants()) {
                    if(!shop.getItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        Map<Enchantment, Integer> shopItemEnchants = shop.getItem().getItemMeta().getEnchants();
                        for(Enchantment enchantment: shopItemEnchants.keySet()) {
                            lore.add(CommonUtils.parseColors("&7" + EnchantmentUtil.mapBukkitEnchantment(enchantment) + " "
                                    + EnchantmentUtil.toRoman(shopItemEnchants.get(enchantment))));
                        }
                    }
                }
                // checking if item is Potion and if Potion effects not hidden
                if(shop.getItem().getItemMeta() instanceof PotionMeta) {
                    if(!shop.getItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_POTION_EFFECTS)) {
                        PotionMeta potionMeta = (PotionMeta) shop.getItem().getItemMeta();
                        PotionData potionData = potionMeta.getBasePotionData();
                        lore.add("");
                        lore.add(
                                CommonUtils.parseColors("&fPotion Effect: "
                                + "&7"
                                + CommonUtils.capitalizeFirstLetters(StringUtils.replace(potionData.getType().name().toLowerCase(), "_", " "))));
                    }
                }
                // if lore exists
                if(shop.getItem().getItemMeta().hasLore()) {
                    List<String> shopItemLore = shop.getItem().getItemMeta().getLore();
                    for(String s_i : shopItemLore) {
                        lore.add(CommonUtils.parseColors(s_i));
                    }
                }
                lore.add("");
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
            if(FindItemAddOn.getInstance().getConfig().getBoolean("allow-direct-shop-tp")) {
                lore.add("");
                lore.add(CommonUtils.parseColors("&6&lClick to teleport to the shop!"));
            }
            meta.setLore(lore);
            String locData = Objects.requireNonNull(shop.getLocation().getWorld()).getName() + ","
                            + shop.getLocation().getBlockX() + ","
                            + shop.getLocation().getBlockY() + ","
                            + shop.getLocation().getBlockZ();
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, locData);
            // handling custom model data
            if(shop.getItem().getItemMeta().hasCustomModelData()) {
                meta.setCustomModelData(shop.getItem().getItemMeta().getCustomModelData());
            }
            item.setItemMeta(meta);
            foundShopsGUI.setItem(guiSlotCounter, item);
            guiSlotCounter++;
        }
        return foundShopsGUI;
    }

    @Deprecated
    public static void onFoundShopsGUIInvClick(InventoryClickEvent event) {
        if(!event.getInventory().equals(foundShopsGUI)) { return; }

        if(event.getCurrentItem() == null) { return; }

        if(event.getCurrentItem().getItemMeta() == null) { return; }

        event.setCancelled(true);

        if(FindItemAddOn.getInstance().getConfig().getBoolean("allow-direct-shop-tp")) {
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
                    player.sendMessage(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + CommonUtils.parseColors(FindItemAddOn.getInstance().getConfig().getString("FindItemCommand.UnsafeShopAreaMessage")));
                }
                player.closeInventory();
            }
        }
    }
}
