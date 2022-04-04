package io.mysticbeans.finditemaddon.SAPICommands;

import io.mysticbeans.finditemaddon.FindItemAddOn;
import io.mysticbeans.finditemaddon.Handlers.CommandHandler.CmdExecutorHandler;
import me.kodysimpson.simpapi.command.SubCommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HideShopSubCmd extends SubCommand {

    private final String hideSubCommand;
    private final CmdExecutorHandler cmdExecutor;

    public HideShopSubCmd() {
        if(StringUtils.isBlank(FindItemAddOn.getConfigProvider().FIND_ITEM_HIDESHOP_AUTOCOMPLETE)) {
            hideSubCommand = "hideshop";
        }
        else {
            hideSubCommand = FindItemAddOn.getConfigProvider().FIND_ITEM_HIDESHOP_AUTOCOMPLETE;
        }
        cmdExecutor = new CmdExecutorHandler();
    }

    @Override
    public String getName() {
        return hideSubCommand;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Run this command while looking at the shop (NOT the shop sign) you wish to hide and it will no" +
                " longer appear in searches";
    }

    @Override
    public String getSyntax() {
        return "/finditem " + hideSubCommand;
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        cmdExecutor.handleHideShop(commandSender);
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] strings) {
        return null;
    }
}

