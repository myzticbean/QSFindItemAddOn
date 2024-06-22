package io.myzticbean.finditemaddon.Commands.impl;

import io.myzticbean.finditemaddon.Commands.CmdExecutorHandler;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

public class BuyCommand extends AbstractCommand {

    private final CmdExecutorHandler cmdExecutor;

    public BuyCommand(CmdExecutorHandler cmdExecutor) {
        this.cmdExecutor = cmdExecutor;
    }

    @Command("finditem|shopsearch|searchshop to-buy <search>")
    private void onBuy(Player player, @Argument("search") @Greedy String search) {
        this.cmdExecutor.handleShopSearch(true, player, search);
    }

}
