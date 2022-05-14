package io.myzticbean.finditemaddon.Utils.WarpUtils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.myzticbean.finditemaddon.Dependencies.WGPlugin;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WGRegionUtils {

    @Nullable
    public String findNearestWGRegion(Location shopLocation) {
        ApplicableRegionSet set = WGPlugin.getWgInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt(shopLocation));

        Set<ProtectedRegion> regions = set.getRegions();
        if(!regions.isEmpty()) {
            Iterator<ProtectedRegion> regionIterator = regions.iterator();
            Map<Integer, ProtectedRegion> regionPriorityMap = new TreeMap<>(Collections.reverseOrder());
            while(regionIterator.hasNext()) {
                ProtectedRegion region_i = regionIterator.next();
                regionPriorityMap.put(region_i.getPriority(), region_i);
            }
            return regionPriorityMap.entrySet().iterator().next().getValue().getId();
        }
        else {
            return null;
        }
    }
}
