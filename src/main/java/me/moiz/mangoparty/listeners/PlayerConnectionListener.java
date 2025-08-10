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
        // Remove player from queues
        plugin.getQueueManager().removePlayer(event.getPlayer().getUniqueId());
        
        // Handle party leaving
        plugin.getPartyManager().handlePlayerDisconnect(event.getPlayer());
        
        // Handle match leaving
        plugin.getMatchManager().handlePlayerDisconnect(event.getPlayer());
    }
}
