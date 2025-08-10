package me.moiz.mangoparty.models;

import java.util.UUID;

public class PartyDuel {
    private UUID challengerId;
    private UUID challengedId;
    private String kitName;
    private long challengeTime;
    private boolean accepted;

    public PartyDuel(UUID challengerId, UUID challengedId, String kitName) {
        this.challengerId = challengerId;
        this.challengedId = challengedId;
        this.kitName = kitName;
        this.challengeTime = System.currentTimeMillis();
        this.accepted = false;
    }

    // Getters
    public UUID getChallengerId() {
        return challengerId;
    }

    public UUID getChallengedId() {
        return challengedId;
    }

    public String getKitName() {
        return kitName;
    }

    public long getChallengeTime() {
        return challengeTime;
    }

    public boolean isAccepted() {
        return accepted;
    }

    // Setters
    public void setChallengerId(UUID challengerId) {
        this.challengerId = challengerId;
    }

    public void setChallengedId(UUID challengedId) {
        this.challengedId = challengedId;
    }

    public void setKitName(String kitName) {
        this.kitName = kitName;
    }

    public void setChallengeTime(long challengeTime) {
        this.challengeTime = challengeTime;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - challengeTime > 60000; // 1 minute expiry
    }

    @Override
    public String toString() {
        return "PartyDuel{" +
                "challengerId=" + challengerId +
                ", challengedId=" + challengedId +
                ", kitName='" + kitName + '\'' +
                ", challengeTime=" + challengeTime +
                ", accepted=" + accepted +
                '}';
    }
}
