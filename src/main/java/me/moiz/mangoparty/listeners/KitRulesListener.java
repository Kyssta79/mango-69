package me.moiz.mangoparty.listeners;

import me.moiz.mangoparty.MangoParty;
import me.moiz.mangoparty.models.Kit;
import me.moiz.mangoparty.models.KitRules;
import me.moiz.mangoparty.models.Match;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public class KitRulesListener implements Listener {
    private MangoParty plugin;
    
    public KitRulesListener(MangoParty plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        
        Player player = (Player) event.getEntity();
        Match match = plugin.getMatchManager().getPlayerMatch(player);
        
        if (match != null) {
            Kit kit = plugin.getKitManager().getKit(match.getKitName());
            if (kit != null && kit.getRules() != null) {
                if (!kit.getRules().isNaturalHealthRegen() && 
                    event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Match match = plugin.getMatchManager().getPlayerMatch(player);
        
        if (match != null) {
            Kit kit = plugin.getKitManager().getKit(match.getKitName());
            if (kit != null && kit.getRules() != null) {
                if (!kit.getRules().isBlockBreak()) {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Match match = plugin.getMatchManager().getPlayerMatch(player);
        
        if (match != null) {
            Kit kit = plugin.getKitManager().getKit(match.getKitName());
            if (kit != null && kit.getRules() != null) {
                if (!kit.getRules().isBlockPlace()) {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof TNTPrimed) {
            TNTPrimed tnt = (TNTPrimed) event.getEntity();
            
            // Check if TNT is in a match area
            Match match = plugin.getMatchManager().getMatchAtLocation(event.getLocation());
            if (match != null) {
                Kit kit = plugin.getKitManager().getKit(match.getKitName());
                if (kit != null && kit.getRules() != null && kit.getRules().isInstantTnt()) {
                    tnt.setFuseTicks(1); // Instant explosion
                }
            }
        }
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Player)) return;
        
        Player damaged = (Player) event.getEntity();
        Match match = plugin.getMatchManager().getPlayerMatch(damaged);
        
        if (match != null) {
            Kit kit = plugin.getKitManager().getKit(match.getKitName());
            if (kit != null && kit.getRules() != null) {
                double multiplier = kit.getRules().getDamageMultiplier();
                if (multiplier != 1.0) {
                    event.setDamage(event.getDamage() * multiplier);
                }
            }
        }
    }
}
