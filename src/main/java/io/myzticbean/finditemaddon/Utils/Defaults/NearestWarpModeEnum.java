package io.myzticbean.finditemaddon.Utils.Defaults;

public enum NearestWarpModeEnum {

    ESSENTIAL_WARPS(1),
    PLAYER_WARPS(2),
    WORLDGUARD_REGION(3);

    private final int mode;

    NearestWarpModeEnum(int mode) {
        this.mode = mode;
    }

    public int value() {
        return mode;
    }

}
