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
package io.myzticbean.finditemaddon.scheduledtasks;

import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.utils.async.VirtualThreadScheduler;
import io.myzticbean.finditemaddon.utils.json.ShopSearchActivityStorageUtil;
import io.myzticbean.finditemaddon.utils.warp.WarpUtils;
import org.bukkit.Bukkit;

/**
 * @author myzticbean
 */
public class Task15MinInterval implements Runnable {
    @Override
    public void run() {
        // v2.0.6.0 - Changed tasks to run in async thread
        // v2.0.7.7 - Switched to virtual thread
        VirtualThreadScheduler.runTaskAsync(() -> {
            WarpUtils.updateWarps();
            ShopSearchActivityStorageUtil.syncShops();
        });
    }
}
