package me.moiz.mangoparty.models;

public class KitRules {
    private boolean blockBreaking;
    private boolean blockPlacing;
    private boolean pvp;
    private boolean itemDropping;
    private boolean itemPickup;

    public KitRules() {
        this.blockBreaking = false;
        this.blockPlacing = false;
        this.pvp = true;
        this.itemDropping = true;
        this.itemPickup = true;
    }

    // Block breaking methods (both naming conventions for compatibility)
    public boolean isBlockBreaking() {
        return blockBreaking;
    }

    public boolean canBreakBlocks() {
        return blockBreaking;
    }

    public void setBlockBreaking(boolean blockBreaking) {
        this.blockBreaking = blockBreaking;
    }

    public void setCanBreakBlocks(boolean canBreakBlocks) {
        this.blockBreaking = canBreakBlocks;
    }

    // Block placing methods (both naming conventions for compatibility)
    public boolean isBlockPlacing() {
        return blockPlacing;
    }

    public boolean canPlaceBlocks() {
        return blockPlacing;
    }

    public void setBlockPlacing(boolean blockPlacing) {
        this.blockPlacing = blockPlacing;
    }

    public void setCanPlaceBlocks(boolean canPlaceBlocks) {
        this.blockPlacing = canPlaceBlocks;
    }

    // Other getters and setters
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
}
