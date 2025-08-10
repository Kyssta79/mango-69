package me.moiz.mangoparty.managers;

import me.moiz.mangoparty.MangoParty;
import me.moiz.mangoparty.models.QueueEntry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QueueManager {
    private MangoParty plugin;
    private Map<String, List<QueueEntry>> queues;
    
    public QueueManager(MangoParty plugin) {
        this.plugin = plugin;
        this.queues = new ConcurrentHashMap<>();
        
        // Initialize queue types
        queues.put("1v1", new ArrayList<>());
        queues.put("2v2", new ArrayList<>());
        queues.put("3v3", new ArrayList<>());
        queues.put("ffa", new ArrayList<>());
        
        // Start queue processing task
        startQueueProcessor();
    }
    
    public void joinQueue(Player player, String queueType, String kitName) {
        if (!queues.containsKey(queueType)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid queue type!"));
            return;
        }
        
        // Check if player is already in any queue
        if (isPlayerInQueue(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou are already in a queue!"));
            return;
        }
        
        QueueEntry entry = new QueueEntry(player.getUniqueId(), kitName, System.currentTimeMillis());
        queues.get(queueType).add(entry);
        
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aJoined " + queueType + " queue with kit: " + kitName));
        
        // Try to find a match immediately
        tryFindMatch(queueType);
    }
    
    public void leaveQueue(Player player) {
        UUID playerUUID = player.getUniqueId();
        boolean removed = false;
        
        for (List<QueueEntry> queue : queues.values()) {
            queue.removeIf(entry -> {
                if (entry.getPlayerUUID().equals(playerUUID)) {
                    return true;
                }
                return false;
            });
            if (!removed) {
                removed = queue.removeIf(e -> e.getPlayerUUID().equals(playerUUID));
            }
        }
        
        if (removed) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aLeft the queue!"));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou are not in any queue!"));
        }
    }
    
    public boolean isPlayerInQueue(UUID playerUUID) {
        for (List<QueueEntry> queue : queues.values()) {
            for (QueueEntry entry : queue) {
                if (entry.getPlayerUUID().equals(playerUUID)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public int getQueueSize(String queueType) {
        List<QueueEntry> queue = queues.get(queueType);
        return queue != null ? queue.size() : 0;
    }
    
    public int getQueueCount(String queueType, String kitName) {
        List<QueueEntry> queue = queues.get(queueType);
        if (queue == null) return 0;
        
        int count = 0;
        for (QueueEntry entry : queue) {
            if (entry.getKitName().equals(kitName)) {
                count++;
            }
        }
        return count;
    }
    
    public void removePlayer(UUID playerUUID) {
        for (List<QueueEntry> queue : queues.values()) {
            queue.removeIf(entry -> entry.getPlayerUUID().equals(playerUUID));
        }
    }
    
    private void startQueueProcessor() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (String queueType : queues.keySet()) {
                tryFindMatch(queueType);
            }
        }, 20L, 20L); // Run every second
    }
    
    private void tryFindMatch(String queueType) {
        List<QueueEntry> queue = queues.get(queueType);
        if (queue == null || queue.isEmpty()) return;
        
        int requiredPlayers = getRequiredPlayers(queueType);
        if (queue.size() < requiredPlayers) return;
        
        // Remove offline players
        queue.removeIf(entry -> {
            Player player = Bukkit.getPlayer(entry.getPlayerUUID());
            return player == null || !player.isOnline();
        });
        
        if (queue.size() < requiredPlayers) return;
        
        // Find players with matching kits
        Map<String, List<QueueEntry>> kitGroups = new HashMap<>();
        for (QueueEntry entry : queue) {
            kitGroups.computeIfAbsent(entry.getKitName(), k -> new ArrayList<>()).add(entry);
        }
        
        // Try to create matches
        for (Map.Entry<String, List<QueueEntry>> kitGroup : kitGroups.entrySet()) {
            List<QueueEntry> entries = kitGroup.getValue();
            if (entries.size() >= requiredPlayers) {
                // Create match
                List<QueueEntry> matchPlayers = entries.subList(0, requiredPlayers);
                List<Player> players = new ArrayList<>();
                
                for (QueueEntry entry : matchPlayers) {
                    Player player = Bukkit.getPlayer(entry.getPlayerUUID());
                    if (player != null && player.isOnline()) {
                        players.add(player);
                    }
                }
                
                if (players.size() == requiredPlayers) {
                    // Remove players from queue
                    for (QueueEntry entry : matchPlayers) {
                        queue.remove(entry);
                    }
                    
                    // Start match
                    startMatch(players, kitGroup.getKey(), queueType);
                }
            }
        }
    }
    
    private int getRequiredPlayers(String queueType) {
        switch (queueType) {
            case "1v1": return 2;
            case "2v2": return 4;
            case "3v3": return 6;
            case "ffa": return 8;
            default: return 2;
        }
    }
    
    private void startMatch(List<Player> players, String kitName, String queueType) {
        // Get an available arena
        me.moiz.mangoparty.models.Arena arena = plugin.getArenaManager().getAvailableArena();
        if (arena == null) {
            // No available arenas, put players back in queue
            for (Player player : players) {
                QueueEntry entry = new QueueEntry(player.getUniqueId(), kitName, System.currentTimeMillis());
                queues.get(queueType).add(entry);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cNo available arenas! You have been put back in queue."));
            }
            return;
        }
        
        // Get the kit
        me.moiz.mangoparty.models.Kit kit = plugin.getKitManager().getKit(kitName);
        if (kit == null) {
            for (Player player : players) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cKit not found: " + kitName));
            }
            return;
        }
        
        // Notify players
        for (Player player : players) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aMatch found! Starting " + queueType + " match..."));
        }
        
        // Start the match
        plugin.getMatchManager().startQueueMatch(players, arena, kit, queueType);
    }
}
