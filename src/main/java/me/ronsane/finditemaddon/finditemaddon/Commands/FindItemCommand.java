package me.ronsane.finditemaddon.finditemaddon.Commands;

import me.ronsane.finditemaddon.finditemaddon.FindItemAddOn;
import me.ronsane.finditemaddon.finditemaddon.QuickShopHandler.SearchHandler;
import me.ronsane.finditemaddon.finditemaddon.Utils.CommonUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FindItemCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, String label, String[] args) {
        if(label.equalsIgnoreCase("finditem")) {
            if (!(sender instanceof Player)) {
                if(args.length == 1){
                    if(args[0].equalsIgnoreCase("reloadconfig")) {
                        if(sender.hasPermission("finditem.reload")) {
                            FindItemAddOn.getInstance().reloadConfig();
                            sender.sendMessage(CommonUtils.parseColors(FindItemAddOn.PluginPrefix + "&aConfig reloaded!"));
                        }
                        else {
                            sender.sendMessage(CommonUtils.parseColors(FindItemAddOn.PluginPrefix + "&cNo permission!"));
                        }
                    }
                    else {
                        sender.sendMessage(CommonUtils.parseColors(FindItemAddOn.PluginPrefix + "&CIncorrect usage! Try &e/finditem &freloadconfig"));
                    }
                }
                else {
                    sender.sendMessage(FindItemAddOn.PluginPrefix + "This command can only be run from in game");
                }
            }
            else {
                Player player = (Player) sender;
                if(player.hasPermission("finditem.use")) {
                    if(args.length < 1 || args.length > 2) {
                        player.sendMessage(CommonUtils.parseColors(FindItemAddOn.PluginPrefix + Objects.requireNonNull(FindItemAddOn.getInstance().getConfig().getString("FindItemCommand.IncorrectUsageMessage"))));
                    }
                    else if(args.length == 1){
                        if(args[0].equalsIgnoreCase("reload")) {
                            if(player.hasPermission("finditem.reload")) {
                                FindItemAddOn.getInstance().reloadConfig();
                                player.sendMessage(CommonUtils.parseColors(FindItemAddOn.PluginPrefix + "&aConfig reloaded!"));
                            }
                            else {
                                player.sendMessage(CommonUtils.parseColors(FindItemAddOn.PluginPrefix + "&cNo permission!"));
                            }
                        }
                        else {
                            player.sendMessage(CommonUtils.parseColors(FindItemAddOn.PluginPrefix + "&CIncorrect usage! Try &e/finditem &freloadconfig"));
                        }
                    }
                    else {
                        player.sendMessage(FindItemAddOn.PluginPrefix + CommonUtils.parseColors("&7Searching..."));
                        boolean isBuying = args[0].equalsIgnoreCase("to-buy");
                        Material mat = Material.getMaterial(args[1]);
                        if(mat != null) {
                            Inventory inv = SearchHandler.searchForShops(mat, isBuying);
                            if(inv != null) {
                                player.openInventory(inv);
                            }
                            else {
                                player.sendMessage(FindItemAddOn.PluginPrefix + CommonUtils.parseColors("&7No shops found!"));
                            }
                        }
                        else {
                            Inventory inv = SearchHandler.searchForShops(args[1], isBuying);
                            if(inv != null) {
                                player.openInventory(inv);
                            }
                            else {
                                // Invalid Material
                                player.sendMessage(CommonUtils.parseColors(FindItemAddOn.PluginPrefix + Objects.requireNonNull(FindItemAddOn.getInstance().getConfig().getString("FindItemCommand.InvalidMaterialMessage"))));
                            }
                        }
                    }
                }
                else {
                    player.sendMessage(FindItemAddOn.PluginPrefix + CommonUtils.parseColors("&cNo permission!"));
                }
            }
        }
        return true;
    }
}
