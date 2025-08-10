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

    public QueueEntry(Player player, String mode, String kitName) {
        this.playerUUID = player.getUniqueId();
        this.kitName = kitName;
        this.joinTime = System.currentTimeMillis();
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerUUID);
    }

    public String getKitName() {
        return kitName;
    }

    public long getJoinTime() {
        return joinTime;
    }

    public String getMode() {
        // This method is used by some parts of the code
        return "1v1"; // Default mode, can be enhanced later
    }
}
