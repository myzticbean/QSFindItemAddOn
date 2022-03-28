package io.mysticbeans.finditemaddon.Handlers.GUIHandler;

import io.mysticbeans.finditemaddon.FindItemAddOn;
import io.mysticbeans.finditemaddon.Models.FoundShopItemModel;
import io.mysticbeans.finditemaddon.Utils.LoggerUtils;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public abstract class PaginatedMenu extends Menu {
    protected int page = 0;
    protected int maxItemsPerPage = 45;
    protected int index = 0;

    protected ItemStack backButton;
    protected ItemStack nextButton;
    protected ItemStack closeInvButton;

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
        Material backButtonMaterial = Material.getMaterial(FindItemAddOn.getConfigProvider().SHOP_GUI_BACK_BUTTON_MATERIAL);
        if(backButtonMaterial == null) {
            backButtonMaterial = Material.RED_CONCRETE;
        }
        backButton = new ItemStack(backButtonMaterial);
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

        Material nextButtonMaterial = Material.getMaterial(FindItemAddOn.getConfigProvider().SHOP_GUI_NEXT_BUTTON_MATERIAL);
        if(nextButtonMaterial == null) {
            nextButtonMaterial = Material.GREEN_CONCRETE;
        }
        nextButton = new ItemStack(nextButtonMaterial);
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
}
