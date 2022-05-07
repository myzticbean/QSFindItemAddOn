package io.mysticbeans.finditemaddon.ScheduledTasks;

import io.mysticbeans.finditemaddon.Utils.JsonStorageUtils.HiddenShopStorageUtil;
import io.mysticbeans.finditemaddon.Utils.JsonStorageUtils.ShopSearchActivityStorageUtil;
import io.mysticbeans.finditemaddon.Utils.WarpUtils.WarpUtils;

public class Task15MinInterval implements Runnable {
    @Override
    public void run() {
        WarpUtils.updateWarps();
//        HiddenShopStorageUtil.saveHiddenShopsToFile();
        new ShopSearchActivityStorageUtil().syncShops();
    }
}
