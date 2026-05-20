package io.myzticbean.finditemaddon.models.enums;

/**
 * Sort modes available in the in-GUI sort control.
 * Ordered for intuitive cycling: DEFAULT → cheapest → most expensive → nearest.
 */
public enum SortMode {
    DEFAULT,
    PRICE_ASC,
    PRICE_DESC,
    DISTANCE_ASC;

    /** Returns the next mode in the cycle, wrapping around. */
    public SortMode next() {
        SortMode[] values = values();
        return values[(ordinal() + 1) % values.length];
    }
}
