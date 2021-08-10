package me.ronsane.finditemaddon.finditemaddon.Commands;

import me.ronsane.finditemaddon.finditemaddon.FindItemAddOn;
import me.ronsane.finditemaddon.finditemaddon.GUIHandler.Menus.FoundShopsMenu;
import me.ronsane.finditemaddon.finditemaddon.QuickShopHandler.QuickShopAPIHandler;
import me.ronsane.finditemaddon.finditemaddon.Utils.CommonUtils;
import me.ronsane.finditemaddon.finditemaddon.Utils.LoggerUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.maxgamer.quickshop.shop.Shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FindItemCommand implements CommandExecutor {

    private final List<String> findItemCommandAliases;

    public FindItemCommand() {
        findItemCommandAliases = new ArrayList<>();
        findItemCommandAliases.add("finditem");
        findItemCommandAliases.add("searchitem");
        findItemCommandAliases.add("shopsearch");
        findItemCommandAliases.add("searchshop");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, String label, String[] args) {
        if(findItemCommandAliases.contains(label.toLowerCase())) {
            if (!(sender instanceof Player)) {
                if(args.length == 1){
                    if(args[0].equalsIgnoreCase("reload")) {
                        if(sender.hasPermission("finditem.reload")) {
                            FindItemAddOn.getInstance().reloadConfig();
                            sender.sendMessage(CommonUtils.parseColors(FindItemAddOn.PluginInGamePrefix + "&aConfig reloaded!"));
                        }
                        else {
                            sender.sendMessage(CommonUtils.parseColors(FindItemAddOn.PluginInGamePrefix + "&cNo permission!"));
                        }
                    }
                    else {
                        sender.sendMessage(CommonUtils.parseColors(FindItemAddOn.PluginInGamePrefix + "&CIncorrect usage! Try &e/finditem &freload"));
                    }
                }
                else {
                    sender.sendMessage(FindItemAddOn.PluginInGamePrefix + "This command can only be run from in game");
                }
            }
            else {
                Player player = (Player) sender;
                if(player.hasPermission("finditem.use")) {
                    if(args.length < 1 || args.length > 2) {
                        player.sendMessage(CommonUtils.parseColors(FindItemAddOn.PluginInGamePrefix + Objects.requireNonNull(FindItemAddOn.getInstance().getConfig().getString("FindItemCommand.IncorrectUsageMessage"))));
                    }
                    else if(args.length == 1){
                        if(args[0].equalsIgnoreCase("reload")) {
                            if(player.hasPermission("finditem.reload")) {
                                FindItemAddOn.getInstance().reloadConfig();
                                player.sendMessage(CommonUtils.parseColors(FindItemAddOn.PluginInGamePrefix + "&aConfig reloaded!"));
                            }
                            else {
                                player.sendMessage(CommonUtils.parseColors(FindItemAddOn.PluginInGamePrefix + "&cNo permission!"));
                            }
                        }
                        else {
                            player.sendMessage(CommonUtils.parseColors(FindItemAddOn.PluginInGamePrefix + "&CIncorrect usage! Try &e/finditem &freload"));
                        }
                    }
                    else {
                        player.sendMessage(FindItemAddOn.PluginInGamePrefix + CommonUtils.parseColors("&7Searching..."));
                        boolean isBuying = args[0].equalsIgnoreCase("to-buy");
                        Material mat = Material.getMaterial(args[1]);
                        if(mat != null) {
                            LoggerUtils.logDebugInfo("Material found: " + mat.toString());
                            List<Shop> searchResult = QuickShopAPIHandler.findItemBasedOnTypeFromAllShops(new ItemStack(mat), isBuying);
                            if(searchResult.size() > 0) {
                                FoundShopsMenu menu = new FoundShopsMenu(FindItemAddOn.getPlayerMenuUtility(player), searchResult);
                                menu.open(searchResult);
                            }
                            else {
                                player.sendMessage(FindItemAddOn.PluginInGamePrefix + CommonUtils.parseColors("&7No shops found!"));
                            }
                        }
                        else {
                            LoggerUtils.logDebugInfo("Material not found! Performing query based search..");
                            List<Shop> searchResult = QuickShopAPIHandler.findItemBasedOnDisplayNameFromAllShops(args[1], isBuying);
                            if(searchResult.size() > 0) {
                                FoundShopsMenu menu = new FoundShopsMenu(FindItemAddOn.getPlayerMenuUtility(player), searchResult);
                                menu.open(searchResult);
                            }
                            else {
                                // Invalid Material
                                player.sendMessage(CommonUtils.parseColors(FindItemAddOn.PluginInGamePrefix + Objects.requireNonNull(FindItemAddOn.getInstance().getConfig().getString("FindItemCommand.InvalidMaterialMessage"))));
                            }
                        }
                    }
                }
                else {
                    // No Permission
                    player.sendMessage(FindItemAddOn.PluginInGamePrefix + CommonUtils.parseColors("&cNo permission!"));
                }
            }
        }
        return true;
    }
}
