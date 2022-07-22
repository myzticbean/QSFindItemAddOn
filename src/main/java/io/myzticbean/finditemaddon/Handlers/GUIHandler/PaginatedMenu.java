package io.myzticbean.finditemaddon.Handlers.GUIHandler;

import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.Models.FoundShopItemModel;
import io.myzticbean.finditemaddon.Utils.LoggerUtils;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public abstract class PaginatedMenu extends Menu {
    protected int page = 0;
    protected int maxItemsPerPage = 45;
    protected int index = 0;

    protected ItemStack backButton;
    protected ItemStack nextButton;
    protected ItemStack closeInvButton;

    private final String BACK_BUTTON_SKIN_ID = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjZkYWI3MjcxZjRmZjA0ZDU0NDAyMTkwNjdhMTA5YjVjMGMxZDFlMDFlYzYwMmMwMDIwNDc2ZjdlYjYxMjE4MCJ9fX0=";
    private final String NEXT_BUTTON_SKIN_ID = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGFhMTg3ZmVkZTg4ZGUwMDJjYmQ5MzA1NzVlYjdiYTQ4ZDNiMWEwNmQ5NjFiZGM1MzU4MDA3NTBhZjc2NDkyNiJ9fX0=";

    public PaginatedMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        initMaterialsForBottomBar();
    }

    public PaginatedMenu(PlayerMenuUtility playerMenuUtility, List<FoundShopItemModel> searchResult) {
        super(playerMenuUtility);
        initMaterialsForBottomBar();
        super.playerMenuUtility.setPlayerShopSearchResult(searchResult);
    }

    private void initMaterialsForBottomBar() {
        createGUIBackButton();
        createGUINextButton();
        createGUICloseInvButton();
    }

    public void addMenuBottomBar() {
        inventory.setItem(45, backButton);
        inventory.setItem(53, nextButton);
        inventory.setItem(49, closeInvButton);

        inventory.setItem(46, super.GUI_FILLER_ITEM);
        inventory.setItem(47, super.GUI_FILLER_ITEM);
        inventory.setItem(48, super.GUI_FILLER_ITEM);
        inventory.setItem(50, super.GUI_FILLER_ITEM);
        inventory.setItem(51, super.GUI_FILLER_ITEM);
        inventory.setItem(52, super.GUI_FILLER_ITEM);
    }

    private void createGUIBackButton() {
        Material backButtonMaterial = Material.getMaterial(FindItemAddOn.getConfigProvider().SHOP_GUI_BACK_BUTTON_MATERIAL);
        if(backButtonMaterial == null) {
            backButton = createPlayerHead(BACK_BUTTON_SKIN_ID);
        }
        else {
            backButton = new ItemStack(backButtonMaterial);
        }
        ItemMeta backButton_meta = backButton.getItemMeta();
        assert backButton_meta != null;
        if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().SHOP_GUI_BACK_BUTTON_TEXT)) {
            backButton_meta.setDisplayName(ColorTranslator.translateColorCodes(FindItemAddOn.getConfigProvider().SHOP_GUI_BACK_BUTTON_TEXT));
        }
        int backButtonCMD;
        try {
            if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().SHOP_GUI_BACK_BUTTON_CMD)) {
                backButtonCMD = Integer.parseInt(FindItemAddOn.getConfigProvider().SHOP_GUI_BACK_BUTTON_CMD);
                backButton_meta.setCustomModelData(backButtonCMD);
            }
        }
        catch (NumberFormatException e) {
            LoggerUtils.logDebugInfo("Invalid Custom Model Data for Back Button in config.yml");
        }
        backButton.setItemMeta(backButton_meta);
    }

    private void createGUINextButton() {
        Material nextButtonMaterial = Material.getMaterial(FindItemAddOn.getConfigProvider().SHOP_GUI_NEXT_BUTTON_MATERIAL);
        if(nextButtonMaterial == null) {
            nextButton = createPlayerHead(NEXT_BUTTON_SKIN_ID);
        }
        else {
            nextButton = new ItemStack(nextButtonMaterial);
        }
        ItemMeta nextButton_meta = nextButton.getItemMeta();
        assert nextButton_meta != null;
        if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().SHOP_GUI_NEXT_BUTTON_TEXT)) {
            nextButton_meta.setDisplayName(ColorTranslator.translateColorCodes(FindItemAddOn.getConfigProvider().SHOP_GUI_NEXT_BUTTON_TEXT));
        }
        int nextButtonCMD;
        try {
            if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().SHOP_GUI_NEXT_BUTTON_CMD)) {
                nextButtonCMD = Integer.parseInt(FindItemAddOn.getConfigProvider().SHOP_GUI_NEXT_BUTTON_CMD);
                nextButton_meta.setCustomModelData(nextButtonCMD);
            }
        }
        catch (NumberFormatException e) {
            LoggerUtils.logDebugInfo("Invalid Custom Model Data for Next Button in config.yml");
        }
        nextButton.setItemMeta(nextButton_meta);
    }

    private void createGUICloseInvButton() {
        Material closeInvButtonMaterial = Material.getMaterial(FindItemAddOn.getConfigProvider().SHOP_GUI_CLOSE_BUTTON_MATERIAL);
        if(closeInvButtonMaterial == null) {
            closeInvButtonMaterial = Material.BARRIER;
        }
        closeInvButton = new ItemStack(closeInvButtonMaterial);
        ItemMeta closeInv_meta = closeInvButton.getItemMeta();
        assert closeInv_meta != null;
        if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().SHOP_GUI_CLOSE_BUTTON_TEXT)) {
            closeInv_meta.setDisplayName(ColorTranslator.translateColorCodes(FindItemAddOn.getConfigProvider().SHOP_GUI_CLOSE_BUTTON_TEXT));
        }
        int closeInvButtonCMD;
        try {
            if(!StringUtils.isEmpty(FindItemAddOn.getConfigProvider().SHOP_GUI_CLOSE_BUTTON_CMD)) {
                closeInvButtonCMD = Integer.parseInt(FindItemAddOn.getConfigProvider().SHOP_GUI_CLOSE_BUTTON_CMD);
                closeInv_meta.setCustomModelData(closeInvButtonCMD);
            }
        }
        catch (NumberFormatException e) {
            LoggerUtils.logDebugInfo("Invalid Custom Model Data for Close Button in config.yml");
        }
        closeInvButton.setItemMeta(closeInv_meta);
    }

    private ItemStack createPlayerHead(String value) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        UUID hashAsId = new UUID(value.hashCode(), value.hashCode());
        return Bukkit.getUnsafe().modifyItemStack(skull,
                "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + value + "\"}]}}}"
        );
    }
}
