package io.myzticbean.finditemaddon.Commands.impl;

import io.myzticbean.finditemaddon.Commands.CmdExecutorHandler;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

public class SellCommand extends AbstractCommand {

    private final CmdExecutorHandler cmdExecutor;

    public SellCommand(CmdExecutorHandler cmdExecutor) {
        this.cmdExecutor = cmdExecutor;
    }

    @Command("finditem|shopsearch|searchshop to-sell <search>")
    private void onSell(Player player, @Argument("search") @Greedy String search) {
        this.cmdExecutor.handleShopSearch(false, player, search);
    }
}
