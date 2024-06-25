package io.myzticbean.finditemaddon.Commands.impl;

import io.myzticbean.finditemaddon.Commands.CmdExecutorHandler;
import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.Utils.Colourify;
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
        if (search.length() > 3) player.sendMessage(Colourify.colour(FindItemAddOn.getConfigProvider().QUERY_TOO_SHORT_MSG));

        this.cmdExecutor.handleShopSearch(false, player, search);
    }
}
