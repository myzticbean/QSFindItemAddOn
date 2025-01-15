package io.myzticbean.finditemaddon.dependencies;

import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import world.bentobox.bentobox.BentoBox;
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

    public boolean isIslandLocked(Location loc) {
        if (!isBentoBoxEnabled) {
            return false;
        }
        return BentoBox.getInstance()
                .getIslands()
                .getIslandAt(loc)
                .filter(island -> !island.isAllowed(Flags.LOCK))
                .isPresent();
    }
}
