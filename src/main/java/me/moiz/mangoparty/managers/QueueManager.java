package me.moiz.mangoparty.managers;

import me.moiz.mangoparty.MangoParty;
import me.moiz.mangoparty.models.Arena;
import me.moiz.mangoparty.models.Kit;
import me.moiz.mangoparty.models.Party;
import me.moiz.mangoparty.models.QueueEntry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QueueManager {
    private MangoParty plugin;
    private Map<String, Map<String, List<QueueEntry>>> queues; // mode -> kit -> entries
    private Map<UUID, QueueEntry> playerQueues; // player -> queue entry
    
    public QueueManager(MangoParty plugin) {
        this.plugin = plugin;
        this.queues = new ConcurrentHashMap<>();
        this.playerQueues = new ConcurrentHashMap<>();
        
        // Initialize queue modes
        initializeQueues();
        
        // Start queue matching task
        startQueueMatchingTask();
    }
    
    private void initializeQueues() {
        String[] modes = {"1v1", "2v2", "3v3"};
        for (String mode : modes) {
            queues.put(mode, new ConcurrentHashMap<>());
        }
    }
    
    public void joinQueue(Player player, String mode, String kitName) {
        // Check if player is already in queue
        if (playerQueues.containsKey(player.getUniqueId())) {
            player.sendMessage("§cYou are already in a queue!");
            return;
        }
        
        // Check if player is in a party and if party size matches mode requirements
        Party party = plugin.getPartyManager().getParty(player);
        int requiredPartySize = getRequiredPartySize(mode);
        
        if (party == null && requiredPartySize > 1) {
            player.sendMessage("§cYou need to be in a party of " + requiredPartySize + " to join " + mode + " queue!");
            return;
        }
        
        if (party != null) {
            if (!party.isLeader(player.getUniqueId())) {
                player.sendMessage("§cOnly the party leader can join queues!");
                return;
            }
            
            if (party.getSize() != requiredPartySize) {
                player.sendMessage("§cYour party size (" + party.getSize() + ") doesn't match the required size (" + requiredPartySize + ") for " + mode + "!");
                return;
            }
            
            if (party.isInMatch()) {
                player.sendMessage("§cYour party is already in a match!");
                return;
            }
        }
        
        // Create queue entry
        QueueEntry entry = new QueueEntry(player.getUniqueId(), kitName, System.currentTimeMillis());
        
        // Add to queue
        queues.computeIfAbsent(mode, k -> new ConcurrentHashMap<>())
               .computeIfAbsent(kitName, k -> new ArrayList<>())
               .add(entry);
        
        playerQueues.put(player.getUniqueId(), entry);
        
        player.sendMessage("§aJoined " + mode + " queue with kit: " + kitName);
        
        // Try to find a match immediately
        tryMatchPlayers(mode, kitName);
    }
    
    public void leaveQueue(Player player) {
        QueueEntry entry = playerQueues.remove(player.getUniqueId());
        if (entry != null) {
            // Remove from queue
            for (Map<String, List<QueueEntry>> modeQueues : queues.values()) {
                for (List<QueueEntry> kitQueue : modeQueues.values()) {
                    kitQueue.removeIf(e -> e.getPlayerUUID().equals(player.getUniqueId()));
                }
            }
            
            player.sendMessage("§aLeft the queue!");
        } else {
            player.sendMessage("§cYou are not in any queue!");
        }
    }
    
    public int getQueueCount(String mode, String kitName) {
        return queues.getOrDefault(mode, new HashMap<>())
                    .getOrDefault(kitName, new ArrayList<>())
                    .size();
    }
    
    public int getQueueSize(String mode) {
        return queues.getOrDefault(mode, new HashMap<>())
                    .values()
                    .stream()
                    .mapToInt(List::size)
                    .sum();
    }
    
    private int getRequiredPartySize(String mode) {
        switch (mode) {
            case "1v1": return 1;
            case "2v2": return 2;
            case "3v3": return 3;
            default: return 1;
        }
    }
    
    private int getRequiredPlayers(String mode) {
        switch (mode) {
            case "1v1": return 2;
            case "2v2": return 4;
            case "3v3": return 6;
            default: return 2;
        }
    }
    
    private void tryMatchPlayers(String mode, String kitName) {
        List<QueueEntry> kitQueue = queues.getOrDefault(mode, new HashMap<>())
                                          .getOrDefault(kitName, new ArrayList<>());
        
        int requiredPlayers = getRequiredPlayers(mode);
        
        if (kitQueue.size() >= requiredPlayers) {
            // Get the required number of players
            List<QueueEntry> matchPlayers = new ArrayList<>();
            for (int i = 0; i < requiredPlayers && i < kitQueue.size(); i++) {
                matchPlayers.add(kitQueue.get(i));
            }
            
            // Validate all players are still online and available
            List<Player> players = new ArrayList<>();
            for (QueueEntry entry : matchPlayers) {
                Player player = Bukkit.getPlayer(entry.getPlayerUUID());
                if (player == null || !player.isOnline()) {
                    // Remove offline player from queue
                    kitQueue.remove(entry);
                    playerQueues.remove(entry.getPlayerUUID());
                    return; // Try again later
                }
                
                Party party = plugin.getPartyManager().getParty(player);
                if (party != null && party.isInMatch()) {
                    // Remove player in match from queue
                    kitQueue.remove(entry);
                    playerQueues.remove(entry.getPlayerUUID());
                    return; // Try again later
                }
                
                players.add(player);
                
                // Add party members if applicable
                if (party != null) {
                    for (Player member : party.getOnlineMembers()) {
                        if (!member.equals(player)) {
                            players.add(member);
                        }
                    }
                }
            }
            
            // Remove matched players from queue
            for (QueueEntry entry : matchPlayers) {
                kitQueue.remove(entry);
                playerQueues.remove(entry.getPlayerUUID());
            }
            
            // Start the match
            startQueueMatch(mode, kitName, players);
        }
    }
    
    private void startQueueMatch(String mode, String kitName, List<Player> players) {
        // Get an available arena
        Arena arena = plugin.getArenaManager().getAvailableArena();
        if (arena == null) {
            // No available arenas, put players back in queue
            for (Player player : players) {
                QueueEntry entry = new QueueEntry(player.getUniqueId(), kitName, System.currentTimeMillis());
                queues.computeIfAbsent(mode, k -> new ConcurrentHashMap<>())
                       .computeIfAbsent(kitName, k -> new ArrayList<>())
                       .add(entry);
                playerQueues.put(player.getUniqueId(), entry);
                player.sendMessage("§cNo available arenas! You have been put back in queue.");
            }
            return;
        }
        
        // Get the kit
        Kit kit = plugin.getKitManager().getKit(kitName);
        if (kit == null) {
            for (Player player : players) {
                player.sendMessage("§cKit not found! Match cancelled.");
            }
            return;
        }
        
        // Create a temporary party for the match if needed
        Party matchParty = null;
        Player leader = players.get(0);
        Party existingParty = plugin.getPartyManager().getParty(leader);
        
        if (existingParty != null) {
            matchParty = existingParty;
        } else {
            // Create temporary party for solo players
            matchParty = plugin.getPartyManager().createParty(leader);
        }
        
        // Notify players
        for (Player player : players) {
            player.sendMessage("§aMatch found! Starting " + mode + " match with kit: " + kitName);
        }
        
        // Start the match
        plugin.getMatchManager().startQueueMatch(matchParty, arena, kit, mode, players);
    }
    
    private void startQueueMatchingTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            // Try to match players in all queues
            for (String mode : queues.keySet()) {
                Map<String, List<QueueEntry>> modeQueues = queues.get(mode);
                for (String kitName : modeQueues.keySet()) {
                    tryMatchPlayers(mode, kitName);
                }
            }
            
            // Clean up expired queue entries (older than 5 minutes)
            long currentTime = System.currentTimeMillis();
            long expireTime = 5 * 60 * 1000; // 5 minutes
            
            for (Map<String, List<QueueEntry>> modeQueues : queues.values()) {
                for (List<QueueEntry> kitQueue : modeQueues.values()) {
                    kitQueue.removeIf(entry -> {
                        if (currentTime - entry.getJoinTime() > expireTime) {
                            Player player = Bukkit.getPlayer(entry.getPlayerUUID());
                            if (player != null && player.isOnline()) {
                                player.sendMessage("§cQueue expired! Please rejoin the queue.");
                            }
                            playerQueues.remove(entry.getPlayerUUID());
                            return true;
                        }
                        return false;
                    });
                }
            }
        }, 20L, 20L); // Run every second
    }
    
    public boolean isInQueue(Player player) {
        return playerQueues.containsKey(player.getUniqueId());
    }
    
    public void cleanup() {
        queues.clear();
        playerQueues.clear();
    }
}
