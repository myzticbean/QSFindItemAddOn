package io.mysticbeans.finditemaddon.ScheduledTasks;

import io.mysticbeans.finditemaddon.Utils.HiddenShopStorageUtil;
import io.mysticbeans.finditemaddon.Utils.WarpUtils.WarpUtils;

public class Task15MinInterval implements Runnable {
    @Override
    public void run() {
        WarpUtils.updateWarps();
        HiddenShopStorageUtil.saveHiddenShopsToFile();
    }
}
