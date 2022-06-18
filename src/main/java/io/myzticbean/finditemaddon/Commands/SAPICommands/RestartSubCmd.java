package io.myzticbean.finditemaddon.Commands.SAPICommands;

import io.myzticbean.finditemaddon.Handlers.CommandHandler.CmdExecutorHandler;
import me.kodysimpson.simpapi.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Sub Command Handler for /finditem restart
 * @deprecated No longer used, will be removed in future versions
 * @author ronsane
 */
@Deprecated
public class RestartSubCmd extends SubCommand {

    private final CmdExecutorHandler cmdExecutor;

    public RestartSubCmd() {
        cmdExecutor = new CmdExecutorHandler();
    }
    @Override
    public String getName() {
        return "restart";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Restarts the plugin (NOT recommended in most cases, restart server if necessary)";
    }

    @Override
    public String getSyntax() {
        return "/finditemadmin restart";
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        cmdExecutor.handlePluginRestart(commandSender);
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] strings) {
        return null;
    }
}

