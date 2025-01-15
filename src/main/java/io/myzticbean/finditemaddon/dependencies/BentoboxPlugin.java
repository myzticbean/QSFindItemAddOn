package io.myzticbean.finditemaddon.dependencies;

import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.lists.Flags;

public class BentoboxPlugin {
    
    private boolean isBentoBoxEnabled = false;

    public BentoboxPlugin() {
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
        return getInstance()
                .getIslands()
                .getIslandAt(loc)
                .filter(island -> !island.isAllowed(Flags.LOCK))
                .isPresent();
    }

    private BentoBox getInstance() {
        return BentoBox.getInstance();
    }

    public boolean isBentoBoxEnabled() {
        return isBentoBoxEnabled;
    }
}
