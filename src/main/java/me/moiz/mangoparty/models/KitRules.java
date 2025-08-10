package me.moiz.mangoparty.models;

public class KitRules {
    private boolean blockBreaking;
    private boolean blockPlacing;
    private boolean pvp;
    private boolean itemDropping;
    private boolean itemPickup;
    private boolean naturalHealthRegen;
    private boolean blockBreak;
    private boolean blockPlace;
    private boolean instantTnt;
    private double damageMultiplier;

    public KitRules() {
        this.blockBreaking = false;
        this.blockPlacing = false;
        this.pvp = true;
        this.itemDropping = true;
        this.itemPickup = true;
        this.naturalHealthRegen = true;
        this.blockBreak = false;
        this.blockPlace = false;
        this.instantTnt = false;
        this.damageMultiplier = 1.0;
    }

    // Getters and setters
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

    public boolean isPvp() {
        return pvp;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    public boolean isItemDropping() {
        return itemDropping;
    }

    public void setItemDropping(boolean itemDropping) {
        this.itemDropping = itemDropping;
    }

    public boolean isItemPickup() {
        return itemPickup;
    }

    public void setItemPickup(boolean itemPickup) {
        this.itemPickup = itemPickup;
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
}
