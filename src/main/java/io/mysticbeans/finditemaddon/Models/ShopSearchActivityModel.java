package io.mysticbeans.finditemaddon.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ShopSearchActivityModel {

    private String worldName;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;
    private String shopOwnerUUID;
    private List<PlayerShopVisitModel> playerVisitList;
    private boolean isHiddenFromSearch;

}
