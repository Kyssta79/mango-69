package me.moiz.mangoparty.models;

import org.bukkit.inventory.ItemStack;

public class Kit {
    private String name;
    private String displayName;
    private ItemStack[] items;
    private ItemStack[] armor;
    private ItemStack icon;
    private KitRules rules;

    public Kit(String name) {
        this.name = name;
        this.displayName = name;
        this.items = new ItemStack[0];
        this.armor = new ItemStack[0];
        this.rules = new KitRules();
    }

    public Kit(String name, String displayName, ItemStack[] items, ItemStack[] armor) {
        this.name = name;
        this.displayName = displayName;
        this.items = items != null ? items : new ItemStack[0];
        this.armor = armor != null ? armor : new ItemStack[0];
        this.rules = new KitRules();
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ItemStack[] getItems() {
        return items;
    }

    public void setItems(ItemStack[] items) {
        this.items = items != null ? items : new ItemStack[0];
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor != null ? armor : new ItemStack[0];
    }

    public ItemStack getIcon() {
        return icon;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public KitRules getRules() {
        return rules;
    }

    public void setRules(KitRules rules) {
        this.rules = rules != null ? rules : new KitRules();
    }
}
