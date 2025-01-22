package io.myzticbean.finditemaddon.dependencies;

import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.lists.Flags;

public class BentoBoxPlugin {
    
    private boolean isBentoBoxEnabled = false;

    public BentoBoxPlugin() {
        checkBentoBoxPlugin();
    }

    private void checkBentoBoxPlugin() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("BentoBox");
        isBentoBoxEnabled = plugin != null && plugin.isEnabled();
    }

    public boolean isIslandLocked(Location loc, Player searchingPlayer) {
        if (!isBentoBoxEnabled) {
            return false;
        }
        User bentoboxUser = User.getInstance(searchingPlayer);
        return BentoBox.getInstance()
                .getIslands()
                .getIslandAt(loc)
                .filter(island -> !island.isAllowed(bentoboxUser, Flags.LOCK))
                .isPresent();
    }
}
