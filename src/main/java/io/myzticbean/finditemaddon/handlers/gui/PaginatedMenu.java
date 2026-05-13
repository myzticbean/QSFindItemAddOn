/**
 * QSFindItemAddOn: An Minecraft add-on plugin for the QuickShop Hikari
 * and Reremake Shop plugins for Spigot server platform.
 * Copyright (C) 2021  myzticbean
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.myzticbean.finditemaddon.handlers.gui;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.models.FoundShopItemModel;
import io.myzticbean.finditemaddon.utils.log.Logger;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * @author myzticbean
 */
public abstract class PaginatedMenu extends Menu {

    private static final Gson gson = new Gson();

    protected int page = 0;
    protected int index = 0;

    protected ItemStack backButton;
    protected ItemStack firstPageButton;
    protected ItemStack nextButton;
    protected ItemStack lastPageButton;
    protected ItemStack closeInvButton;

    protected static final int MAX_ITEMS_PER_PAGE = 45;
    private final String BACK_BUTTON_SKIN_ID = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjZkYWI3MjcxZjRmZjA0ZDU0NDAyMTkwNjdhMTA5YjVjMGMxZDFlMDFlYzYwMmMwMDIwNDc2ZjdlYjYxMjE4MCJ9fX0=";
    private final String FIRST_PAGE_BUTTON_SKIN_ID = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTI5M2E2MDcwNTAzMTcyMDcxZjM1ZjU4YzgyMjA0ZTgxOGNkMDY1MTg2OTAxY2ExOWY3ZGFkYmRhYzE2NWU0NCJ9fX0=";
    private final String NEXT_BUTTON_SKIN_ID = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGFhMTg3ZmVkZTg4ZGUwMDJjYmQ5MzA1NzVlYjdiYTQ4ZDNiMWEwNmQ5NjFiZGM1MzU4MDA3NTBhZjc2NDkyNiJ9fX0=";
    private final String LAST_PAGE_BUTTON_SKIN_ID = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWRhNDRjNzY3Y2NhMjU4NjFkM2E1MmZlMTdjMjY0MjhlNjYwZWUyM2RjMGQ3OTNiZjdiZDg2ZWEyMDJmNzAzZCJ9fX0=";

    protected PaginatedMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        initMaterialsForBottomBar();
    }

    protected PaginatedMenu(PlayerMenuUtility playerMenuUtility, List<FoundShopItemModel> searchResult) {
        super(playerMenuUtility);
        initMaterialsForBottomBar();
        super.playerMenuUtility.setPlayerShopSearchResult(searchResult);
    }

    private void initMaterialsForBottomBar() {
        createGUIBackButton();
        createGUIFirstPageButton();
        createGUINextButton();
        createGUILastPageButton();
        createGUICloseInvButton();
    }

    public void addMenuBottomBar() {
        inventory.setItem(45, backButton);
        inventory.setItem(46, firstPageButton);
        inventory.setItem(53, nextButton);
        inventory.setItem(52, lastPageButton);
        inventory.setItem(49, closeInvButton);

        inventory.setItem(47, super.GUI_FILLER_ITEM);
        inventory.setItem(48, super.GUI_FILLER_ITEM);
        inventory.setItem(50, super.GUI_FILLER_ITEM);
        inventory.setItem(51, super.GUI_FILLER_ITEM);
    }

    private void createGUIBackButton() {
        Material mat = Material.getMaterial(FindItemAddOn.getConfigProvider().SHOP_GUI_BACK_BUTTON_MATERIAL);
        backButton = buildNavButton(mat, BACK_BUTTON_SKIN_ID,
                FindItemAddOn.getConfigProvider().SHOP_GUI_BACK_BUTTON_TEXT,
                FindItemAddOn.getConfigProvider().SHOP_GUI_BACK_BUTTON_CMD,
                "Back Button");
    }

    private void createGUINextButton() {
        Material mat = Material.getMaterial(FindItemAddOn.getConfigProvider().SHOP_GUI_NEXT_BUTTON_MATERIAL);
        nextButton = buildNavButton(mat, NEXT_BUTTON_SKIN_ID,
                FindItemAddOn.getConfigProvider().SHOP_GUI_NEXT_BUTTON_TEXT,
                FindItemAddOn.getConfigProvider().SHOP_GUI_NEXT_BUTTON_CMD,
                "Next Button");
    }

    private void createGUIFirstPageButton() {
        Material mat = Material.getMaterial(FindItemAddOn.getConfigProvider().SHOP_GUI_GOTO_FIRST_PAGE_BUTTON_MATERIAL);
        firstPageButton = buildNavButton(mat, FIRST_PAGE_BUTTON_SKIN_ID,
                FindItemAddOn.getConfigProvider().SHOP_GUI_GOTO_FIRST_PAGE_BUTTON_TEXT,
                FindItemAddOn.getConfigProvider().SHOP_GUI_GOTO_FIRST_PAGE_BUTTON_CMD,
                "Goto First Page Button");
    }

    private void createGUILastPageButton() {
        Material mat = Material.getMaterial(FindItemAddOn.getConfigProvider().SHOP_GUI_GOTO_LAST_PAGE_BUTTON_MATERIAL);
        lastPageButton = buildNavButton(mat, LAST_PAGE_BUTTON_SKIN_ID,
                FindItemAddOn.getConfigProvider().SHOP_GUI_GOTO_LAST_PAGE_BUTTON_TEXT,
                FindItemAddOn.getConfigProvider().SHOP_GUI_GOTO_LAST_PAGE_BUTTON_CMD,
                "Goto Last Page Button");
    }

    private void createGUICloseInvButton() {
        Material mat = Material.getMaterial(FindItemAddOn.getConfigProvider().SHOP_GUI_CLOSE_BUTTON_MATERIAL);
        if (mat == null) mat = Material.BARRIER;
        closeInvButton = buildNavButton(mat, null,
                FindItemAddOn.getConfigProvider().SHOP_GUI_CLOSE_BUTTON_TEXT,
                FindItemAddOn.getConfigProvider().SHOP_GUI_CLOSE_BUTTON_CMD,
                "Close Button");
    }

    /**
     * Builds a navigation bar button. Falls back to a player-head texture when {@code material} is
     * null and {@code skinId} is provided; falls back to STONE when both are absent.
     */
    private ItemStack buildNavButton(Material material, String skinId, String displayText, String cmdValue, String buttonLabel) {
        ItemStack item;
        if (material == null && skinId != null) {
            item = createPlayerHead(skinId);
        } else {
            item = new ItemStack(material != null ? material : Material.STONE);
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) meta = Bukkit.getItemFactory().getItemMeta(item.getType());
        if (meta == null) return item;
        if (!StringUtils.isEmpty(displayText)) {
            meta.setDisplayName(ColorTranslator.translateColorCodes(displayText));
        }
        if (!StringUtils.isEmpty(cmdValue)) {
            try {
                meta.setCustomModelData(Integer.parseInt(cmdValue));
            } catch (NumberFormatException e) {
                Logger.logDebugInfo("Invalid Custom Model Data for " + buttonLabel + " in config.yml");
            }
        }
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createPlayerHead(String textureValue) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID(), null);
            PlayerTextures textures = profile.getTextures();
            try {
                String decodedValue = new String(Base64.getDecoder().decode(textureValue));
                Logger.logDebugInfo("Decoded Value: " + decodedValue);
                String textureUrl = extractTextureUrl(decodedValue);
                URL url = new URL(textureUrl);
                textures.setSkin(url);
            } catch (Exception e) {
                Logger.logError(e);
            }
            profile.setTextures(textures);
            meta.setOwnerProfile(profile);
            head.setItemMeta(meta);
        }
        return head;
    }

    private String extractTextureUrl(String decodedValue) {
        JsonObject jsonObject = gson.fromJson(decodedValue, JsonObject.class);
        return jsonObject
                .getAsJsonObject("textures")
                .getAsJsonObject("SKIN")
                .get("url")
                .getAsString();
    }
}
