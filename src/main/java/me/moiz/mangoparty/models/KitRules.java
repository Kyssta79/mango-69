package me.moiz.mangoparty.models;

public class KitRules {
    private boolean blockBreaking;
    private boolean blockPlacing;
    private boolean naturalHealthRegen;
    private boolean instantTnt;
    private double damageMultiplier;

    public KitRules() {
        this.blockBreaking = false;
        this.blockPlacing = false;
        this.naturalHealthRegen = true;
        this.instantTnt = false;
        this.damageMultiplier = 1.0;
    }

    public boolean isBlockBreaking() {
        return blockBreaking;
    }

    public void setBlockBreaking(boolean blockBreaking) {
        this.blockBreaking = blockBreaking;
    }

    public boolean isBlockPlacing() {
        return blockPlacing;
    }

    public void setBlockPlacing(boolean blockPlacing) {
        this.blockPlacing = blockPlacing;
    }

    public boolean isNaturalHealthRegen() {
        return naturalHealthRegen;
    }

    public void setNaturalHealthRegen(boolean naturalHealthRegen) {
        this.naturalHealthRegen = naturalHealthRegen;
    }

    public boolean isInstantTnt() {
        return instantTnt;
    }

    public void setInstantTnt(boolean instantTnt) {
        this.instantTnt = instantTnt;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public void setDamageMultiplier(double damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
    }

    // Legacy method names for compatibility
    public boolean isBlockBreak() {
        return blockBreaking;
    }

    public boolean isBlockPlace() {
        return blockPlacing;
    }
}
