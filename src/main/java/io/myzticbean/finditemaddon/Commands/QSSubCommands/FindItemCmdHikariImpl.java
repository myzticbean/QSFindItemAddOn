package io.myzticbean.finditemaddon.Commands.QSSubCommands;

import com.ghostchu.quickshop.api.command.CommandHandler;
import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.Handlers.CommandHandler.CmdExecutorHandler;
import io.myzticbean.finditemaddon.Utils.Defaults.PlayerPerms;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ronsane
 */
public class FindItemCmdHikariImpl implements CommandHandler<Player> {

    private final String hideSubCommand;
    private final String revealShopSubCommand;
    private final CmdExecutorHandler cmdExecutor;
    private final List<String> itemsList = new ArrayList<>();
    private final List<String> buyOrSellList = new ArrayList<>();

    public FindItemCmdHikariImpl() {
        if(StringUtils.isBlank(FindItemAddOn.getConfigProvider().FIND_ITEM_HIDESHOP_AUTOCOMPLETE)) {
            this.hideSubCommand = "hideshop";
        }
        else {
            this.hideSubCommand = FindItemAddOn.getConfigProvider().FIND_ITEM_HIDESHOP_AUTOCOMPLETE;
        }

        if(StringUtils.isEmpty(FindItemAddOn.getConfigProvider().FIND_ITEM_REVEALSHOP_AUTOCOMPLETE)
                || StringUtils.containsIgnoreCase(
                FindItemAddOn.getConfigProvider().FIND_ITEM_REVEALSHOP_AUTOCOMPLETE, " ")) {
            this.revealShopSubCommand = "revealshop";
        }
        else {
            this.revealShopSubCommand = FindItemAddOn.getConfigProvider().FIND_ITEM_REVEALSHOP_AUTOCOMPLETE;
        }
        cmdExecutor = new CmdExecutorHandler();
    }

    @Override
    public void onCommand(Player commandSender, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            commandSender.sendMessage(ColorTranslator.translateColorCodes(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&cIncorrect usage!"));
        }
        else if(args.length == 1) {
            if(commandSender.hasPermission(PlayerPerms.FINDITEM_HIDESHOP.value())) {
                if(args[0].equalsIgnoreCase(hideSubCommand)) {
                    cmdExecutor.handleHideShop(commandSender);
                } else if(args[0].equalsIgnoreCase(revealShopSubCommand)) {
                    cmdExecutor.handleRevealShop(commandSender);
                } else {
                    commandSender.sendMessage(ColorTranslator.translateColorCodes(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&cIncorrect usage!"));
                }
            } else {
                commandSender.sendMessage(ColorTranslator.translateColorCodes(FindItemAddOn.getConfigProvider().PLUGIN_PREFIX + "&cYou don't have permission to use that!"));
            }
        } else {
            cmdExecutor.handleShopSearch(args[0], commandSender, args[1]);
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player sender, @NotNull String commandLabel, @NotNull String[] args) {
        if(itemsList.isEmpty()) {
            for(Material mat : Material.values()) {
                itemsList.add(mat.name());
            }
        }
        if(buyOrSellList.isEmpty()) {
            // to-buy
            if(StringUtils.isEmpty(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_BUY_AUTOCOMPLETE)
                    || StringUtils.containsIgnoreCase(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_BUY_AUTOCOMPLETE, " ")) {
                buyOrSellList.add("TO_BUY");
            }
            else {
                buyOrSellList.add(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_BUY_AUTOCOMPLETE);
            }
            // to-sell
            if(StringUtils.isEmpty(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_SELL_AUTOCOMPLETE)
                    || StringUtils.containsIgnoreCase(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_SELL_AUTOCOMPLETE, " ")) {
                buyOrSellList.add("TO_SELL");
            }
            else {
                buyOrSellList.add(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_SELL_AUTOCOMPLETE);
            }
            // hide
            if(sender.hasPermission(PlayerPerms.FINDITEM_HIDESHOP.value())) {
                buyOrSellList.add(FindItemAddOn.getConfigProvider().FIND_ITEM_HIDESHOP_AUTOCOMPLETE);
                buyOrSellList.add(FindItemAddOn.getConfigProvider().FIND_ITEM_REVEALSHOP_AUTOCOMPLETE);
            }
        }
        List<String> result = new ArrayList<>();
        if(args.length == 1) {
            for(String a : buyOrSellList) {
                if(a.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(a);
                }
            }
            return result;
        }
        else if(args.length == 2) {
            for(String a : itemsList) {
                if(a.toLowerCase().startsWith(args[1].toLowerCase())) {
                    result.add(a);
                }
            }
            return result;
        }
        else {
            return null;
        }

    }
}
