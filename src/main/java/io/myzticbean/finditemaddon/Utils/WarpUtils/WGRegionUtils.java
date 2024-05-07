/**
 * QSFindItemAddOn: An Minecraft add-on plugin for the QuickShop Hikari
 * and Reremake Shop plugins for Spigot server platform.
 * Copyright (C) 2021  myzticbean
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.myzticbean.finditemaddon.Utils.WarpUtils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.myzticbean.finditemaddon.Dependencies.WGPlugin;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author myzticbean
 */
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
