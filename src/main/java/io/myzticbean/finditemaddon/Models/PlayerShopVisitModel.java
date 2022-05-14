package io.myzticbean.finditemaddon.Models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class PlayerShopVisitModel {

    private String playerUUID;
    private String visitDateTime;

    public void setPlayerUUID(UUID uuid) {
        this.playerUUID = uuid.toString();
    }

    public UUID getPlayerUUID() {
        return UUID.fromString(this.playerUUID);
    }

    public void setVisitDateTime() {
        this.visitDateTime = Instant.now().toString();
    }

    public Instant getVisitDateTime() {
        return Instant.parse(visitDateTime);
    }
}
