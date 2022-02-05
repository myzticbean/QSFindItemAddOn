package me.ronsane.finditemaddon.finditemaddon.Commands;

import me.ronsane.finditemaddon.finditemaddon.FindItemAddOn;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FindItemCmdTabCompleter implements TabCompleter {
    List<String> itemsList = new ArrayList<>();
    List<String> buyOrSellList = new ArrayList<>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(itemsList.isEmpty()) {
            for(Material mat : Material.values()) {
                itemsList.add(mat.name());
            }
        }
        if(buyOrSellList.isEmpty()) {
            // to-buy
            if(StringUtils.isEmpty(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_BUY_AUTOCOMPLETE)
                || StringUtils.containsIgnoreCase(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_BUY_AUTOCOMPLETE, " ")) {
                buyOrSellList.add("TO-BUY");
            }
            else {
                buyOrSellList.add(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_BUY_AUTOCOMPLETE);
            }
            // to-sell
            if(StringUtils.isEmpty(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_SELL_AUTOCOMPLETE)
                    || StringUtils.containsIgnoreCase(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_SELL_AUTOCOMPLETE, " ")) {
                buyOrSellList.add("TO-SELL");
            }
            else {
                buyOrSellList.add(FindItemAddOn.getConfigProvider().FIND_ITEM_TO_SELL_AUTOCOMPLETE);
            }
            // hide
            if(sender instanceof Player && sender.hasPermission("finditem.hideshop")) {
                buyOrSellList.add(FindItemAddOn.getConfigProvider().FIND_ITEM_HIDESHOP_AUTOCOMPLETE);
                buyOrSellList.add(FindItemAddOn.getConfigProvider().FIND_ITEM_REVEALSHOP_AUTOCOMPLETE);
            }
            // reload
            if(sender instanceof Player && (sender.hasPermission("finditem.admin") || sender.hasPermission("finditem.reload"))) {
                buyOrSellList.add("reload");
            }
            // updatelist
//            if(sender instanceof Player && (sender.hasPermission("finditem.admin") || sender.hasPermission("finditem.updatelist"))) {
//                buyOrSellList.add("updatelist");
//            }
        }

        List<String> result = new ArrayList<>();

        if(args.length == 1) {
            for(String a : buyOrSellList) {
                if(a.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(a);
                }
            }
            return result;
        }
        else if(args.length == 2) {
            for(String a : itemsList) {
                if(a.toLowerCase().startsWith(args[1].toLowerCase())) {
                    result.add(a);
                }
            }
            return result;
        }
        else {
            return null;
        }
    }
}
