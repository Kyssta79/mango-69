package me.moiz.mangoparty.models;

public class KitRules {
    private boolean naturalHealthRegen = true;
    private boolean blockBreak = false;
    private boolean blockPlace = false;
    private boolean instantTnt = false;
    private double damageMultiplier = 1.0;
    
    public KitRules() {}
    
    public KitRules(boolean naturalHealthRegen, boolean blockBreak, boolean blockPlace, boolean instantTnt, double damageMultiplier) {
        this.naturalHealthRegen = naturalHealthRegen;
        this.blockBreak = blockBreak;
        this.blockPlace = blockPlace;
        this.instantTnt = instantTnt;
        this.damageMultiplier = damageMultiplier;
    }
    
    // Getters
    public boolean isNaturalHealthRegen() { return naturalHealthRegen; }
    public boolean isBlockBreak() { return blockBreak; }
    public boolean isBlockPlace() { return blockPlace; }
    public boolean isInstantTnt() { return instantTnt; }
    public double getDamageMultiplier() { return damageMultiplier; }
    
    // Setters
    public void setNaturalHealthRegen(boolean naturalHealthRegen) { this.naturalHealthRegen = naturalHealthRegen; }
    public void setBlockBreak(boolean blockBreak) { this.blockBreak = blockBreak; }
    public void setBlockPlace(boolean blockPlace) { this.blockPlace = blockPlace; }
    public void setInstantTnt(boolean instantTnt) { this.instantTnt = instantTnt; }
    public void setDamageMultiplier(double damageMultiplier) { this.damageMultiplier = damageMultiplier; }
}
