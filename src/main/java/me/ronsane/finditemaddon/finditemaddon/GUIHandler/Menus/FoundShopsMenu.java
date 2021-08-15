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
                if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().SHOP_NAV_FIRST_PAGE_ALERT_MSG)) {
                    event.getWhoClicked().sendMessage(
                            CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX
                                    + FindItemAddOn.getConfigProvider().SHOP_NAV_FIRST_PAGE_ALERT_MSG));
                }
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
                if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().SHOP_NAV_LAST_PAGE_ALERT_MSG)) {
                    event.getWhoClicked().sendMessage(
                            CommonUtils.parseColors(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + FindItemAddOn.getConfigProvider().SHOP_NAV_LAST_PAGE_ALERT_MSG));
                }
            }
        }
        else if(event.getCurrentItem().getType().equals(Material.BARRIER)) {
            event.getWhoClicked().closeInventory();
        }
        else if(event.getCurrentItem().getType().equals(super.playerMenuUtility.getPlayerShopSearchResult().get(0).getItem().getType())) {
            if(FindItemAddOn.getConfigProvider().ALLOW_DIRECT_SHOP_TP) {
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
                            if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().UNSAFE_SHOP_AREA_MSG)) {
                                player.sendMessage(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX
                                        + CommonUtils.parseColors(FindItemAddOn.getConfigProvider().UNSAFE_SHOP_AREA_MSG));
                            }
                        }
                        player.closeInventory();
                    }
                }
                else {
                    if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().SHOP_TP_NO_PERMISSION_MSG)) {
                        playerMenuUtility.getOwner()
                                .sendMessage(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX
                                        + CommonUtils.parseColors(FindItemAddOn.getConfigProvider().SHOP_TP_NO_PERMISSION_MSG));
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
                    Shop shop = foundShops.get(index);
                    lore = new ArrayList<>();

                    if(shop.getItem().hasItemMeta()) {
                        meta = shop.getItem().getItemMeta();
                        if(shop.getItem().getItemMeta().hasLore()) {
                            for(String s : shop.getItem().getItemMeta().getLore()) {
                                lore.add(CommonUtils.parseColors(s));
                            }
                        }
                    }
                    List<String> shopItemLore = FindItemAddOn.getConfigProvider().SHOP_GUI_ITEM_LORE;
                    for(String shopItemLore_i : shopItemLore) {
                        lore.add(CommonUtils.parseColors(replaceLorePlaceholders(shopItemLore_i, shop)));
                    }

                    if(FindItemAddOn.getConfigProvider().ALLOW_DIRECT_SHOP_TP) {
                        if(playerMenuUtility.getOwner().hasPermission("finditem.shoptp")) {
                            lore.add(CommonUtils.parseColors(FindItemAddOn.getConfigProvider().CLICK_TO_TELEPORT_MSG));
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

        if(text.contains("{ITEM_PRICE}")) {
            text = text.replace("{ITEM_PRICE}", FindItemAddOn.essAPI.getSettings().getCurrencySymbol() + shop.getPrice());
        }
        if(text.contains("{SHOP_STOCK}")) {
            text = text.replace("{SHOP_STOCK}", String.valueOf(shop.getRemainingStock()));
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
