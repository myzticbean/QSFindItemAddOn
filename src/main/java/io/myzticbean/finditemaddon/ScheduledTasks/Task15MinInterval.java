package io.myzticbean.finditemaddon.ScheduledTasks;

import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.Utils.JsonStorageUtils.ShopSearchActivityStorageUtil;
import io.myzticbean.finditemaddon.Utils.WarpUtils.WarpUtils;
import org.bukkit.Bukkit;

/**
 * @author myzticbean
 */
public class Task15MinInterval implements Runnable {
    @Override
    public void run() {
        // v2.0.6.0 - Changed tasks to run in async thread
        Bukkit.getScheduler().runTaskAsynchronously(FindItemAddOn.getInstance(), () -> {
            WarpUtils.updateWarps();
            ShopSearchActivityStorageUtil.syncShops();
        });
    }
}
