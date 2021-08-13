package me.ronsane.finditemaddon.finditemaddon.GUIHandler.Menus;

import me.ronsane.finditemaddon.finditemaddon.FindItemAddOn;
import me.ronsane.finditemaddon.finditemaddon.GUIHandler.PaginatedMenu;
import me.ronsane.finditemaddon.finditemaddon.GUIHandler.PlayerMenuUtility;
import me.ronsane.finditemaddon.finditemaddon.Utils.CommonUtils;
import me.ronsane.finditemaddon.finditemaddon.Utils.EnchantmentUtil;
import me.ronsane.finditemaddon.finditemaddon.Utils.LocationUtils;
import me.ronsane.finditemaddon.finditemaddon.Utils.LoggerUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.maxgamer.quickshop.shop.Shop;

import java.util.*;

public class FoundShopsMenu extends PaginatedMenu {

    public FoundShopsMenu(PlayerMenuUtility playerMenuUtility, List<Shop> searchResult) {
        super(playerMenuUtility, searchResult);
    }

    @Override
    public String getMenuName() {
        return "Shops";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        if(event.getCurrentItem().getType().equals(super.backButton.getType())) {
            if(page == 0) {
                event.getWhoClicked().sendMessage(
                        CommonUtils.parseColors(FindItemAddOn.configProvider.PLUGIN_PREFIX + FindItemAddOn.configProvider.SHOP_NAV_FIRST_PAGE_ALERT_MSG));
            }
            else {
                page = page - 1;
                super.open(super.playerMenuUtility.getPlayerShopSearchResult());
            }
        }
        else if(event.getCurrentItem().getType().equals(super.nextButton.getType())) {
            if(!((index + 1) >= super.playerMenuUtility.getPlayerShopSearchResult().size())) {
                page = page + 1;
                super.open(super.playerMenuUtility.getPlayerShopSearchResult());
            }
            else {
                event.getWhoClicked().sendMessage(
                        CommonUtils.parseColors(FindItemAddOn.configProvider.PLUGIN_PREFIX + FindItemAddOn.configProvider.SHOP_NAV_LAST_PAGE_ALERT_MSG));
            }
        }
        else if(event.getCurrentItem().getType().equals(Material.BARRIER)) {
            event.getWhoClicked().closeInventory();
        }
        else if(event.getCurrentItem().getType().equals(super.playerMenuUtility.getPlayerShopSearchResult().get(0).getItem().getType())) {
            if(FindItemAddOn.configProvider.ALLOW_DIRECT_SHOP_TP) {
                if(playerMenuUtility.getOwner().hasPermission("finditem.shoptp")) {
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
                            if(!StringUtils.isEmpty(FindItemAddOn.configProvider.UNSAFE_SHOP_AREA_MSG)) {
                                player.sendMessage(FindItemAddOn.configProvider.PLUGIN_PREFIX
                                        + CommonUtils.parseColors(FindItemAddOn.configProvider.UNSAFE_SHOP_AREA_MSG));
                            }
                        }
                        player.closeInventory();
                    }
                }
                else {
                    if(!StringUtils.isEmpty(FindItemAddOn.configProvider.SHOP_TP_NO_PERMISSION_MSG)) {
                        playerMenuUtility.getOwner()
                                .sendMessage(FindItemAddOn.configProvider.PLUGIN_PREFIX
                                        + CommonUtils.parseColors(FindItemAddOn.configProvider.SHOP_TP_NO_PERMISSION_MSG));
                    }
                }
            }
        }
    }

    @Override
    public void setMenuItems() {}

    @Override
    public void setMenuItems(List<Shop> foundShops) {
        addMenuBottomBar();
        if(foundShops != null && !foundShops.isEmpty()) {
            int guiSlotCounter = 0;
            while(guiSlotCounter < super.maxItemsPerPage) {
                index = super.maxItemsPerPage * page + guiSlotCounter;

                if(index >= foundShops.size())  break;

                if(foundShops.get(index) != null) {
                    // Place Search Results here
                    NamespacedKey key = new NamespacedKey(FindItemAddOn.getInstance(), "locationData");
                    ItemStack item = new ItemStack(foundShops.get(0).getItem().getType());
                    ItemMeta meta = item.getItemMeta();
                    List<String> lore;
                    OfflinePlayer offlinePlayer;
                    Shop shop = foundShops.get(index);
                    lore = new ArrayList<>();

                    List<String> shopItemLore = FindItemAddOn.configProvider.SHOP_GUI_ITEM_LORE;
                    for(String shopItemLore_i : shopItemLore) {
                        if(shopItemLore_i.contains("{ITEM_DISPLAY_NAME}")) {
                            String text = replaceLorePlaceholders(shopItemLore_i, shop);
                            if(!StringUtils.isEmpty(text)) {
                                lore.add(CommonUtils.parseColors(text));
                            }
                        }
                        else if(shopItemLore_i.contains("{ITEM_LORE}")) {
                            if(shop.getItem().hasItemMeta()) {
                                if(shop.getItem().getItemMeta().hasLore()) {
                                    for(String s : shop.getItem().getItemMeta().getLore()) {
                                        lore.add(CommonUtils.parseColors(s));
                                    }
                                }
                            }
                        }
                        else if(shopItemLore_i.contains("{ITEM_ENCHANTS}")) {
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
                        }
                        else if(shopItemLore_i.contains("{ITEM_POTION_EFFECTS}")) {
                            // checking if item is Potion and if Potion effects not hidden
                            LoggerUtils.logDebugInfo("Inside Potion Effects");
                            if(shop.getItem().hasItemMeta()) {
                                if(shop.getItem().getItemMeta() instanceof PotionMeta) {
                                    if(!shop.getItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_POTION_EFFECTS)) {
                                        PotionMeta potionMeta = (PotionMeta) shop.getItem().getItemMeta();
                                        // Base potion effect
                                        LoggerUtils.logDebugInfo("Total potions: " + potionMeta.getCustomEffects().size());
                                        PotionData potionData = potionMeta.getBasePotionData();
                                        lore.add(CommonUtils.parseColors("&7"
                                                + CommonUtils.capitalizeFirstLetters(
                                                StringUtils.replace(potionData.getType().name().toLowerCase(), "_", " "))));
                                        // Custom potion effects
                                        for(PotionEffect potionEffect : potionMeta.getCustomEffects()) {
                                            LoggerUtils.logDebugInfo("Potion name: " + potionEffect.getType().getName());
                                            LoggerUtils.logDebugInfo("Potion duration: " + potionEffect.getDuration());
                                            LoggerUtils.logDebugInfo("Potion amplifier: " + potionEffect.getAmplifier());
                                            lore.add(CommonUtils.parseColors("&7"
                                                + CommonUtils.capitalizeFirstLetters(potionEffect.getType().getName().toLowerCase())
                                                + " " + EnchantmentUtil.toRoman(potionEffect.getAmplifier() + 1)
                                                + " (" + potionEffect.getDuration() + "sec)"));
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            lore.add(CommonUtils.parseColors(replaceLorePlaceholders(shopItemLore_i, shop)));
                        }
                    }

                    if(FindItemAddOn.configProvider.ALLOW_DIRECT_SHOP_TP) {
                        if(playerMenuUtility.getOwner().hasPermission("finditem.shoptp")) {
                            lore.add(CommonUtils.parseColors(FindItemAddOn.configProvider.CLICK_TO_TELEPORT_MSG));
                        }
                    }
                    assert meta != null;
                    meta.setLore(lore);

                    // storing location data in item persistent storage
                    String locData = Objects.requireNonNull(shop.getLocation().getWorld()).getName() + ","
                            + shop.getLocation().getBlockX() + ","
                            + shop.getLocation().getBlockY() + ","
                            + shop.getLocation().getBlockZ();
                    meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, locData);

                    // handling custom model data
                    if(Objects.requireNonNull(shop.getItem().getItemMeta()).hasCustomModelData()) {
                        meta.setCustomModelData(shop.getItem().getItemMeta().getCustomModelData());
                    }
                    item.setItemMeta(meta);
                    inventory.addItem(item);
                }
                guiSlotCounter++;
            }
        }
    }

    private String replaceLorePlaceholders(String text, Shop shop) {

        if(text.contains("{ITEM_DISPLAY_NAME}")) {
            if(shop.getItem().hasItemMeta()) {
                if(Objects.requireNonNull(shop.getItem().getItemMeta()).hasDisplayName()) {
                    text = text.replace("{ITEM_DISPLAY_NAME}", shop.getItem().getItemMeta().getDisplayName());
                }
                else {
                    return "";
                }
            }
            else {
                return "";
            }
        }
        if(text.contains("{ITEM_PRICE}")) {
            text = text.replace("{ITEM_PRICE}", FindItemAddOn.essAPI.getSettings().getCurrencySymbol() + shop.getPrice());
        }
        if(text.contains("{ITEM_QTY}")) {
            text = text.replace("{ITEM_QTY}", String.valueOf(shop.getRemainingStock()));
        }
        if(text.contains("{SHOP_OWNER}")) {
            text = text.replace("{SHOP_OWNER}", Objects.requireNonNull(Bukkit.getOfflinePlayer(shop.getOwner()).getName()));
        }
        if(text.contains("{SHOP_LOC}")) {
            text = text.replace("{SHOP_LOC}",
                    shop.getLocation().getBlockX() + ", "
                    + shop.getLocation().getBlockY() + ", "
                    + shop.getLocation().getBlockZ());
        }
        if(text.contains("{SHOP_WORLD}")) {
            text = text.replace("{SHOP_WORLD}", Objects.requireNonNull(shop.getLocation().getWorld()).getName());
        }
        return text;
    }
}
