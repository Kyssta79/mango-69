package me.moiz.mangoparty.listeners;

import me.moiz.mangoparty.MangoParty;
import me.moiz.mangoparty.models.Kit;
import me.moiz.mangoparty.models.KitRules;
import me.moiz.mangoparty.models.Match;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

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
        
        if (match != null && event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            Kit kit = match.getKit();
            if (kit != null && kit.getRules() != null) {
                KitRules rules = kit.getRules();
                if (!rules.isNaturalHealthRegen()) {
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
            Kit kit = match.getKit();
            if (kit != null && kit.getRules() != null) {
                KitRules rules = kit.getRules();
                if (!rules.isBlockBreak()) {
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
            Kit kit = match.getKit();
            if (kit != null && kit.getRules() != null) {
                KitRules rules = kit.getRules();
                if (!rules.isBlockPlace()) {
                    event.setCancelled(true);
                } else if (rules.isInstantTnt() && event.getBlock().getType().name().contains("TNT")) {
                    // Handle instant TNT logic here if needed
                }
            }
        }
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;
        
        Player damaged = (Player) event.getEntity();
        Match match = plugin.getMatchManager().getPlayerMatch(damaged);
        
        if (match != null) {
            Kit kit = match.getKit();
            if (kit != null && kit.getRules() != null) {
                KitRules rules = kit.getRules();
                double multiplier = rules.getDamageMultiplier();
                if (multiplier != 1.0) {
                    event.setDamage(event.getDamage() * multiplier);
                }
            }
        }
    }
}
