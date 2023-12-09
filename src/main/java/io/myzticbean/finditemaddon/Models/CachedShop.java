package io.myzticbean.finditemaddon.Models;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter @Builder
public class CachedShop {
    private long shopId;
    private int remainingStock;
    private int remainingSpace;
    private Date lastFetched;
}
