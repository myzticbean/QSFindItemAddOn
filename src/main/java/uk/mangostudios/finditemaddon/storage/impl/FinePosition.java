package uk.mangostudios.finditemaddon.storage.impl;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public record FinePosition(double x, double y, double z, String world) {

    public int blockX() {
        return (int) this.x;
    }

    public int blockY() {
        return (int) this.y;
    }

    public int blockZ() {
        return (int) this.z;
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z, -180, 0);
    }
}
