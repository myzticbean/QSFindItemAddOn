package me.ronsane.finditemaddon.finditemaddon.Commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

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
            buyOrSellList.add("TO-BUY");
            buyOrSellList.add("TO-SELL");
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
