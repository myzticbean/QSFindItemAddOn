package io.myzticbean.finditemaddon.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
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

    public boolean compareWith(String targetWorldName, double targetX, double targetY, double targetZ, String targetShopOwnerUUID) {
        return this.getWorldName().equalsIgnoreCase(targetWorldName)
                && this.getX() == targetX
                && this.getY() == targetY
                && this.getZ() == targetZ
                && this.getShopOwnerUUID().equalsIgnoreCase(targetShopOwnerUUID);
    }

    public boolean compareWith(String targetWorldName, double targetX, double targetY, double targetZ) {
        return this.getWorldName().equalsIgnoreCase(targetWorldName)
                && this.getX() == targetX
                && this.getY() == targetY
                && this.getZ() == targetZ;
    }

}
