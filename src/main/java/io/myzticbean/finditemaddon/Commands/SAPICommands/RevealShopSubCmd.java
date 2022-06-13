package io.myzticbean.finditemaddon.Commands.SAPICommands;

import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.Handlers.CommandHandler.CmdExecutorHandler;
import me.kodysimpson.simpapi.command.SubCommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Sub Command Handler for /finditemadmin revealshop
 * @author ronsane
 */
public class RevealShopSubCmd extends SubCommand {

    private final String revealShopSubCommand;
    private final CmdExecutorHandler cmdExecutor;

    public RevealShopSubCmd() {
        if(StringUtils.isEmpty(FindItemAddOn.getConfigProvider().FIND_ITEM_REVEALSHOP_AUTOCOMPLETE)
                || StringUtils.containsIgnoreCase(
                        FindItemAddOn.getConfigProvider().FIND_ITEM_REVEALSHOP_AUTOCOMPLETE, " ")) {
            revealShopSubCommand = "revealshop";
        }
        else {
            revealShopSubCommand = FindItemAddOn.getConfigProvider().FIND_ITEM_REVEALSHOP_AUTOCOMPLETE;
        }
        cmdExecutor = new CmdExecutorHandler();
    }

    @Override
    public String getName() {
        return revealShopSubCommand;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Run this command while looking at a hidden shop to make it public again";
    }

    @Override
    public String getSyntax() {
        return "/finditem " + revealShopSubCommand;
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        cmdExecutor.handleRevealShop(commandSender);
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] strings) {
        return null;
    }
}

