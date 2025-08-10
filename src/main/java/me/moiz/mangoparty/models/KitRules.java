package me.moiz.mangoparty.models;

public class KitRules {
    private boolean naturalHealthRegen;
    private boolean blockBreak;
    private boolean blockPlace;
    private double damageMultiplier;
    private boolean instantTnt;
    
    public KitRules() {
        this.naturalHealthRegen = true;
        this.blockBreak = false;
        this.blockPlace = false;
        this.damageMultiplier = 1.0;
        this.instantTnt = false;
    }
    
    public boolean isNaturalHealthRegen() {
        return naturalHealthRegen;
    }
    
    public void setNaturalHealthRegen(boolean naturalHealthRegen) {
        this.naturalHealthRegen = naturalHealthRegen;
    }
    
    public boolean isBlockBreak() {
        return blockBreak;
    }
    
    public void setBlockBreak(boolean blockBreak) {
        this.blockBreak = blockBreak;
    }
    
    public boolean isBlockPlace() {
        return blockPlace;
    }
    
    public void setBlockPlace(boolean blockPlace) {
        this.blockPlace = blockPlace;
    }
    
    // Block Breaking
    public boolean isBlockBreaking() {
        return blockBreak;
    }
    
    public void setBlockBreaking(boolean blockBreaking) {
        this.blockBreak = blockBreaking;
    }
    
    // Block Placing
    public boolean isBlockPlacing() {
        return blockPlace;
    }
    
    public void setBlockPlacing(boolean blockPlacing) {
        this.blockPlace = blockPlacing;
    }
    
    // Damage Multiplier
    public double getDamageMultiplier() {
        return damageMultiplier;
    }
    
    public void setDamageMultiplier(double damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
    }
    
    // Instant TNT
    public boolean isInstantTnt() {
        return instantTnt;
    }
    
    public void setInstantTnt(boolean instantTnt) {
        this.instantTnt = instantTnt;
    }
}
