package me.ronsane.finditemaddon.finditemaddon.ScheduledTasks;

import me.ronsane.finditemaddon.finditemaddon.Utils.WarpUtils.WarpUtils;

public class Task15MinInterval implements Runnable {
    @Override
    public void run() {
        WarpUtils.updateWarps();
    }
}
