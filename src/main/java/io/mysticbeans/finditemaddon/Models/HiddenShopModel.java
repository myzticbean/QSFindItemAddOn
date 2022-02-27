package io.mysticbeans.finditemaddon.Models;

public class HiddenShopModel {

    private String worldName;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;
    private String shopOwnerUUID;
//    public Location shopLocation;
//    public UUID shopOwner;

    public HiddenShopModel(String worldName, double x, double y, double z, float pitch, float yaw, String shopOwnerUUID) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        this.shopOwnerUUID = shopOwnerUUID;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public String getShopOwnerUUID() {
        return shopOwnerUUID;
    }

    public void setShopOwnerUUID(String shopOwnerUUID) {
        this.shopOwnerUUID = shopOwnerUUID;
    }

//    public HiddenShopModel(Location shopLocation, UUID shopOwner) {
//        this.shopLocation = shopLocation;
//        this.shopOwner = shopOwner;
//    }
//
//    public Location getShopLocation() {
//        return shopLocation;
//    }
//
//    public void setShopLocation(Location shopLocation) {
//        this.shopLocation = shopLocation;
//    }
//
//    public UUID getShopOwner() {
//        return shopOwner;
//    }
//
//    public void setShopOwner(UUID shopOwner) {
//        this.shopOwner = shopOwner;
//    }
}
