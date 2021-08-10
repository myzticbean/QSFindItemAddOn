package me.ronsane.finditemaddon.finditemaddon.GUIHandler;

import me.ronsane.finditemaddon.finditemaddon.Utils.CommonUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.maxgamer.quickshop.shop.Shop;

import java.util.List;

public abstract class PaginatedMenu extends Menu {
    protected int page = 0;
    protected int maxItemsPerPage = 45;
    protected int index = 0;

    protected ItemStack backButton = new ItemStack(Material.RED_CONCRETE, 1);
    protected ItemStack nextButton = new ItemStack(Material.GREEN_CONCRETE, 1);
    protected ItemStack closeInv = new ItemStack(Material.BARRIER, 1);

    public PaginatedMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);

        ItemMeta backButton_meta = backButton.getItemMeta();
        backButton_meta.setDisplayName(CommonUtils.parseColors("&cBack"));
        backButton.setItemMeta(backButton_meta);

        ItemMeta nextButton_meta = nextButton.getItemMeta();
        nextButton_meta.setDisplayName(CommonUtils.parseColors("&aNext"));
        nextButton.setItemMeta(nextButton_meta);

        ItemMeta closeInv_meta = closeInv.getItemMeta();
        closeInv_meta.setDisplayName(CommonUtils.parseColors("&fClose Search"));
        closeInv.setItemMeta(closeInv_meta);
    }

    public PaginatedMenu(PlayerMenuUtility playerMenuUtility, List<Shop> searchResult) {
        super(playerMenuUtility);

        ItemMeta backButton_meta = backButton.getItemMeta();
        backButton_meta.setDisplayName(CommonUtils.parseColors("&cBack"));
        backButton.setItemMeta(backButton_meta);

        ItemMeta nextButton_meta = nextButton.getItemMeta();
        nextButton_meta.setDisplayName(CommonUtils.parseColors("&aNext"));
        nextButton.setItemMeta(nextButton_meta);

        ItemMeta closeInv_meta = closeInv.getItemMeta();
        closeInv_meta.setDisplayName(CommonUtils.parseColors("&fClose Search"));
        closeInv.setItemMeta(closeInv_meta);

        super.playerMenuUtility.setPlayerShopSearchResult(searchResult);
    }

    public void addMenuBottomBar() {
        inventory.setItem(45, backButton);
        inventory.setItem(53, nextButton);
        inventory.setItem(49, closeInv);

        inventory.setItem(46, super.FILLER_GLASS);
        inventory.setItem(47, super.FILLER_GLASS);
        inventory.setItem(48, super.FILLER_GLASS);
        inventory.setItem(50, super.FILLER_GLASS);
        inventory.setItem(51, super.FILLER_GLASS);
        inventory.setItem(52, super.FILLER_GLASS);
    }
}
