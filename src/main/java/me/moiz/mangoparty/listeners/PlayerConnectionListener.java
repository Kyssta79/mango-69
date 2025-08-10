package me.moiz.mangoparty.listeners;

import me.moiz.mangoparty.MangoParty;
import me.moiz.mangoparty.models.Match;
import me.moiz.mangoparty.models.Party;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {
    private MangoParty plugin;
    
    public PlayerConnectionListener(MangoParty plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Remove from queues
        plugin.getQueueManager().removePlayer(player.getUniqueId());
        
        // Handle party leaving
        Party party = plugin.getPartyManager().getParty(player);
        if (party != null) {
            plugin.getPartyManager().leaveParty(player);
            
            // Notify remaining party members
            for (Player member : party.getOnlineMembers()) {
                if (!member.equals(player)) {
                    member.sendMessage("§c" + player.getName() + " §7left the server and was removed from the party!");
                }
            }
            
            // Disband party if empty
            if (party.getSize() == 0) {
                plugin.getPartyManager().disbandParty(party);
            }
        }
        
        // Handle match leaving
        Match match = plugin.getMatchManager().getPlayerMatch(player);
        if (match != null) {
            plugin.getMatchManager().eliminatePlayer(player, match);
            
            // Notify other players in match
            for (Player matchPlayer : match.getAllPlayers()) {
                if (!matchPlayer.equals(player) && matchPlayer.isOnline()) {
                    matchPlayer.sendMessage("§c" + player.getName() + " §7left the server and was eliminated!");
                }
            }
        }
    }
}
