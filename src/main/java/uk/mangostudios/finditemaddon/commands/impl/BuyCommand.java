package uk.mangostudios.finditemaddon.commands.impl;

import uk.mangostudios.finditemaddon.commands.CmdExecutorHandler;
import uk.mangostudios.finditemaddon.FindItemAddOn;
import uk.mangostudios.finditemaddon.util.Colourify;
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
        if (search.length() <= 3)
            player.sendMessage(Colourify.colour(FindItemAddOn.getConfigProvider().QUERY_TOO_SHORT_MSG));

        this.cmdExecutor.handleShopSearch(true, player, search);
    }

}
