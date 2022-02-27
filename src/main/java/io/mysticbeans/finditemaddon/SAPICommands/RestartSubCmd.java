package io.mysticbeans.finditemaddon.SAPICommands;

import io.mysticbeans.finditemaddon.Handlers.CommandHandler.CmdExecutorHandler;
import me.kodysimpson.simpapi.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RestartSubCmd extends SubCommand {
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
        return "Restarts the plugin";
    }

    @Override
    public String getSyntax() {
        return "/finditem restart";
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        CmdExecutorHandler.handlePluginRestart(commandSender);
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] strings) {
        return null;
    }
}

