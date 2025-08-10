package me.moiz.mangoparty.models;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
    
    // Getters
    public UUID getPlayerUUID() { return playerUUID; }
    public String getKitName() { return kitName; }
    public long getJoinTime() { return joinTime; }
    
    public Player getPlayer() {
        return Bukkit.getPlayer(playerUUID);
    }
    
    // Setters
    public void setPlayerUUID(UUID playerUUID) { this.playerUUID = playerUUID; }
    public void setKitName(String kitName) { this.kitName = kitName; }
    public void setJoinTime(long joinTime) { this.joinTime = joinTime; }
}
