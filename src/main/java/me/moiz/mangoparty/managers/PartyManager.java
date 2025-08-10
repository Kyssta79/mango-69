package me.moiz.mangoparty.managers;

import me.moiz.mangoparty.models.Party;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PartyManager {
    private Map<UUID, Party> parties; // Leader UUID -> Party
    private Map<UUID, UUID> playerToParty; // Player UUID -> Leader UUID
    
    public PartyManager() {
        this.parties = new ConcurrentHashMap<>();
        this.playerToParty = new ConcurrentHashMap<>();
    }
    
    public Party createParty(Player leader) {
        if (isInParty(leader)) {
            return null; // Player already in a party
        }
        
        Party party = new Party(leader.getUniqueId());
        parties.put(leader.getUniqueId(), party);
        playerToParty.put(leader.getUniqueId(), leader.getUniqueId());
        
        return party;
    }
    
    public boolean disbandParty(Player leader) {
        Party party = parties.get(leader.getUniqueId());
        if (party == null) {
            return false;
        }
        
        return disbandParty(party);
    }
    
    public boolean disbandParty(Party party) {
        if (party == null) return false;
        
        // Remove all members from playerToParty map
        for (UUID memberId : party.getMembers()) {
            playerToParty.remove(memberId);
            
            Player member = Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cParty has been disbanded!"));
            }
        }
        
        // Remove party from parties map
        parties.remove(party.getLeader());
        
        return true;
    }
    
    public boolean invitePlayer(Player leader, Player target) {
        Party party = parties.get(leader.getUniqueId());
        if (party == null) {
            return false;
        }
        
        if (isInParty(target)) {
            leader.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c" + target.getName() + " is already in a party!"));
            return false;
        }
        
        if (party.getSize() >= 8) { // Max party size
            leader.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cParty is full!"));
            return false;
        }
        
        party.addInvite(target.getUniqueId());
        
        leader.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aInvited " + target.getName() + " to the party!"));
        target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a" + leader.getName() + " invited you to their party!"));
        target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eType /party accept to join or /party deny to decline"));
        
        return true;
    }
    
    public boolean acceptInvite(Player player, Player leader) {
        Party party = parties.get(leader.getUniqueId());
        if (party == null) {
            return false;
        }
        
        if (!party.hasInvite(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have an invite from " + leader.getName() + "!"));
            return false;
        }
        
        if (isInParty(player)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou are already in a party!"));
            return false;
        }
        
        party.removeInvite(player.getUniqueId());
        party.addMember(player.getUniqueId());
        playerToParty.put(player.getUniqueId(), leader.getUniqueId());
        
        // Notify all party members
        for (Player member : party.getOnlineMembers()) {
            member.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a" + player.getName() + " joined the party!"));
        }
        
        return true;
    }
    
    public boolean denyInvite(Player player, Player leader) {
        Party party = parties.get(leader.getUniqueId());
        if (party == null) {
            return false;
        }
        
        if (!party.hasInvite(player.getUniqueId())) {
            return false;
        }
        
        party.removeInvite(player.getUniqueId());
        
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou declined the party invite from " + leader.getName()));
        leader.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + " declined your party invite"));
        
        return true;
    }
    
    public boolean leaveParty(Player player) {
        UUID leaderUUID = playerToParty.get(player.getUniqueId());
        if (leaderUUID == null) {
            return false;
        }
        
        Party party = parties.get(leaderUUID);
        if (party == null) {
            return false;
        }
        
        // If player is the leader, disband the party
        if (party.isLeader(player.getUniqueId())) {
            return disbandParty(party);
        }
        
        // Remove player from party
        party.removeMember(player.getUniqueId());
        playerToParty.remove(player.getUniqueId());
        
        // Notify remaining party members
        for (Player member : party.getOnlineMembers()) {
            member.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + " left the party!"));
        }
        
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou left the party!"));
        
        return true;
    }
    
    public boolean kickPlayer(Player leader, Player target) {
        Party party = parties.get(leader.getUniqueId());
        if (party == null) {
            return false;
        }
        
        if (!party.isMember(target.getUniqueId())) {
            leader.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c" + target.getName() + " is not in your party!"));
            return false;
        }
        
        if (party.isLeader(target.getUniqueId())) {
            leader.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou cannot kick yourself!"));
            return false;
        }
        
        party.removeMember(target.getUniqueId());
        playerToParty.remove(target.getUniqueId());
        
        // Notify all party members
        for (Player member : party.getOnlineMembers()) {
            member.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c" + target.getName() + " was kicked from the party!"));
        }
        
        target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou were kicked from the party!"));
        
        return true;
    }
    
    public Party getParty(Player player) {
        UUID leaderUUID = playerToParty.get(player.getUniqueId());
        if (leaderUUID == null) {
            return null;
        }
        
        return parties.get(leaderUUID);
    }
    
    public boolean isInParty(Player player) {
        return playerToParty.containsKey(player.getUniqueId());
    }
    
    public boolean isPartyLeader(Player player) {
        Party party = getParty(player);
        return party != null && party.isLeader(player.getUniqueId());
    }
    
    public List<Party> getAllParties() {
        return new ArrayList<>(parties.values());
    }
    
    public void handlePlayerDisconnect(Player player) {
        Party party = getParty(player);
        if (party != null) {
            leaveParty(player);
            
            // Notify remaining party members
            for (Player member : party.getOnlineMembers()) {
                if (!member.equals(player)) {
                    member.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + " left the server and was removed from the party!"));
                }
            }
            
            // Disband party if empty
            if (party.getSize() == 0) {
                disbandParty(party);
            }
        }
    }
    
    public void cleanup() {
        parties.clear();
        playerToParty.clear();
    }
}
