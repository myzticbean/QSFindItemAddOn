package io.myzticbean.finditemaddon.SAPICommands;

import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.Handlers.CommandHandler.CmdExecutorHandler;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import me.kodysimpson.simpapi.command.SubCommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Sub Command Handler for /finditem TO_SELL
 * @author ronsane
 */
public class SellSubCmd extends SubCommand {

    private final String sellSubCommand;
    private final List<String> itemsList = new ArrayList<>();
    private final CmdExecutorHandler cmdExecutor;

    public SellSubCmd() {
        // to-sell
        if(StringUtils.isBlank(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_SELL_AUTOCOMPLETE)) {
            sellSubCommand = "TO_SELL";
        }
        else {
            sellSubCommand = FindItemAddOn.getConfigProvider().FIND_ITEM_TO_SELL_AUTOCOMPLETE;
        }
        if(itemsList.isEmpty()) {
            for(Material mat : Material.values()) {
                itemsList.add(mat.name());
            }
        }
        cmdExecutor = new CmdExecutorHandler();
    }

    @Override
    public String getName() {
        return sellSubCommand;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Find shops that sell a specific item";
    }

    @Override
    public String getSyntax() {
        return "/finditem " + sellSubCommand + " {item type | item name}";
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        if(args.length != 2)
            commandSender.sendMessage(ColorTranslator.translateColorCodes(
                    FindItemAddOn.getConfigProvider().PLUGIN_PREFIX
                            + FindItemAddOn.getConfigProvider().FIND_ITEM_CMD_INCORRECT_USAGE_MSG));
        else
            cmdExecutor.handleShopSearch(sellSubCommand, commandSender, args[1]);
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        List<String> result = new ArrayList<>();
        for(String a : itemsList) {
            if(a.toLowerCase().startsWith(args[1].toLowerCase())) {
                result.add(a);
            }
        }
        return result;
    }
}

