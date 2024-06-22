package io.myzticbean.finditemaddon.Commands.impl;

import io.myzticbean.finditemaddon.Commands.CmdExecutorHandler;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class ReloadCommand extends AbstractCommand {

    private final CmdExecutorHandler cmdExecutor;

    public ReloadCommand(CmdExecutorHandler cmdExecutor) {
        this.cmdExecutor = cmdExecutor;
    }

    @Command("finditem|shopsearch|searchshop reload")
    @Permission("finditem.reload")
    private void onReload(CommandSender sender) {
        this.cmdExecutor.handlePluginReload(sender);
    }

}
