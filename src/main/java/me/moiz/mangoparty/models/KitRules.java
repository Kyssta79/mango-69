package me.moiz.mangoparty.models;

public class KitRules {
    private boolean naturalHealthRegen;
    private boolean blockBreak;
    private boolean blockPlace;
    private boolean instantTnt;
    private double damageMultiplier;
    private boolean allowBowBoosting;
    private boolean allowPearling;
    private boolean allowSoup;
    private boolean allowGapples;
    private int maxGapples;

    public KitRules() {
        // Default values
        this.naturalHealthRegen = true;
        this.blockBreak = false;
        this.blockPlace = false;
        this.instantTnt = false;
        this.damageMultiplier = 1.0;
        this.allowBowBoosting = true;
        this.allowPearling = true;
        this.allowSoup = true;
        this.allowGapples = true;
        this.maxGapples = 64;
    }

    // Getters
    public boolean isNaturalHealthRegen() {
        return naturalHealthRegen;
    }

    public boolean isBlockBreak() {
        return blockBreak;
    }

    public boolean isBlockPlace() {
        return blockPlace;
    }

    public boolean isInstantTnt() {
        return instantTnt;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public boolean isAllowBowBoosting() {
        return allowBowBoosting;
    }

    public boolean isAllowPearling() {
        return allowPearling;
    }

    public boolean isAllowSoup() {
        return allowSoup;
    }

    public boolean isAllowGapples() {
        return allowGapples;
    }

    public int getMaxGapples() {
        return maxGapples;
    }

    // Setters
    public void setNaturalHealthRegen(boolean naturalHealthRegen) {
        this.naturalHealthRegen = naturalHealthRegen;
    }

    public void setBlockBreak(boolean blockBreak) {
        this.blockBreak = blockBreak;
    }

    public void setBlockPlace(boolean blockPlace) {
        this.blockPlace = blockPlace;
    }

    public void setInstantTnt(boolean instantTnt) {
        this.instantTnt = instantTnt;
    }

    public void setDamageMultiplier(double damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
    }

    public void setAllowBowBoosting(boolean allowBowBoosting) {
        this.allowBowBoosting = allowBowBoosting;
    }

    public void setAllowPearling(boolean allowPearling) {
        this.allowPearling = allowPearling;
    }

    public void setAllowSoup(boolean allowSoup) {
        this.allowSoup = allowSoup;
    }

    public void setAllowGapples(boolean allowGapples) {
        this.allowGapples = allowGapples;
    }

    public void setMaxGapples(int maxGapples) {
        this.maxGapples = maxGapples;
    }
}
