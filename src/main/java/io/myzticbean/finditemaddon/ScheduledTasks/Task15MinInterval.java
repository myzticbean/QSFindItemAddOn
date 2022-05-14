package io.myzticbean.finditemaddon.ScheduledTasks;

import io.myzticbean.finditemaddon.Utils.JsonStorageUtils.ShopSearchActivityStorageUtil;
import io.myzticbean.finditemaddon.Utils.WarpUtils.WarpUtils;

public class Task15MinInterval implements Runnable {
    @Override
    public void run() {
        WarpUtils.updateWarps();
//        HiddenShopStorageUtil.saveHiddenShopsToFile();
        ShopSearchActivityStorageUtil.syncShops();
    }
}
