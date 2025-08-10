package me.moiz.mangoparty.models;

import java.util.UUID;

public class QueueEntry {
    private UUID playerUUID;
    private String kitName;
    private long joinTime;

    public QueueEntry(UUID playerUUID, String kitName, long joinTime) {
        this.playerUUID = playerUUID;
        this.kitName = kitName;
        this.joinTime = joinTime;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public String getKitName() {
        return kitName;
    }

    public void setKitName(String kitName) {
        this.kitName = kitName;
    }

    public long getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(long joinTime) {
        this.joinTime = joinTime;
    }
}
