package io.mysticbeans.finditemaddon.SAPICommands;

import io.mysticbeans.finditemaddon.FindItemAddOn;
import io.mysticbeans.finditemaddon.Handlers.CommandHandler.CmdExecutorHandler;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import me.kodysimpson.simpapi.command.SubCommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SellSubCmd extends SubCommand {

    private final String sellSubCommand;
    private final List<String> itemsList = new ArrayList<>();

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
            CmdExecutorHandler.handleShopSearch(sellSubCommand, commandSender, args[1]);
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

