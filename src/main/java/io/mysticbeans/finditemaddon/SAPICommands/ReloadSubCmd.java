package io.mysticbeans.finditemaddon.SAPICommands;

import io.mysticbeans.finditemaddon.Handlers.CommandHandler.CmdExecutorHandler;
import me.kodysimpson.simpapi.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ReloadSubCmd extends SubCommand {
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
        CmdExecutorHandler.handlePluginReload(commandSender);
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] strings) {
        return null;
    }
}

