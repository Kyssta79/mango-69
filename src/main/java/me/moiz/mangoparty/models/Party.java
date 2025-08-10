package me.moiz.mangoparty.models;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class Party {
    private UUID leader;
    private Set<UUID> members;
    private Set<UUID> invites;
    private boolean inMatch;
    private boolean open;

    public Party(UUID leader) {
        this.leader = leader;
        this.members = new HashSet<>();
        this.invites = new HashSet<>();
        this.members.add(leader); // Leader is also a member
        this.inMatch = false;
        this.open = false;
    }

    // Member management
    public void addMember(UUID playerId) {
        members.add(playerId);
    }

    public void removeMember(UUID playerId) {
        members.remove(playerId);
        invites.remove(playerId); // Remove any pending invites
    }

    public boolean isMember(UUID playerId) {
        return members.contains(playerId);
    }

    public boolean isLeader(UUID playerId) {
        return leader.equals(playerId);
    }

    // Invite management
    public void addInvite(UUID playerId) {
        invites.add(playerId);
    }

    public void removeInvite(UUID playerId) {
        invites.remove(playerId);
    }

    public boolean hasInvite(UUID playerId) {
        return invites.contains(playerId);
    }

    // Utility methods
    public int getSize() {
        return members.size();
    }

    public List<Player> getOnlineMembers() {
        List<Player> onlineMembers = new ArrayList<>();
        for (UUID memberId : members) {
            Player player = Bukkit.getPlayer(memberId);
            if (player != null && player.isOnline()) {
                onlineMembers.add(player);
            }
        }
        return onlineMembers;
    }

    public List<Player> getOnlineMembersExcept(UUID excludeId) {
        List<Player> onlineMembers = new ArrayList<>();
        for (UUID memberId : members) {
            if (!memberId.equals(excludeId)) {
                Player player = Bukkit.getPlayer(memberId);
                if (player != null && player.isOnline()) {
                    onlineMembers.add(player);
                }
            }
        }
        return onlineMembers;
    }

    public Player getLeaderPlayer() {
        return Bukkit.getPlayer(leader);
    }

    // Getters and setters
    public UUID getLeader() {
        return leader;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    public Set<UUID> getMembers() {
        return new HashSet<>(members);
    }

    public Set<UUID> getInvites() {
        return new HashSet<>(invites);
    }

    public boolean isInMatch() {
        return inMatch;
    }

    public void setInMatch(boolean inMatch) {
        this.inMatch = inMatch;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Party party = (Party) obj;
        return Objects.equals(leader, party.leader);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leader);
    }

    @Override
    public String toString() {
        return "Party{" +
                "leader=" + leader +
                ", members=" + members +
                ", invites=" + invites +
                ", inMatch=" + inMatch +
                ", open=" + open +
                '}';
    }
}
