package io.myzticbean.finditemaddon.Commands.SAPICommands;

import io.myzticbean.finditemaddon.Handlers.CommandHandler.CmdExecutorHandler;
import me.kodysimpson.simpapi.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Sub Command Handler for /finditemadmin reload
 * @author myzticbean
 */
public class ReloadSubCmd extends SubCommand {

    private final CmdExecutorHandler cmdExecutor;

    public ReloadSubCmd() {
        cmdExecutor = new CmdExecutorHandler();
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Reloads config.yml";
    }

    @Override
    public String getSyntax() {
        return "/finditemadmin reload";
    }

    @Override
    public void perform(CommandSender commandSender, String[] strings) {
        cmdExecutor.handlePluginReload(commandSender);
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] strings) {
        return null;
    }
}

