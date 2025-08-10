package me.moiz.mangoparty.models;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class Kit {
    private String name;
    private String displayName;
    private ItemStack icon;
    private List<ItemStack> items;
    private List<ItemStack> armor;
    private KitRules rules;

    public Kit(String name) {
        this.name = name;
        this.displayName = name;
        this.icon = null;
        this.items = new ArrayList<>(36);
        this.armor = new ArrayList<>(4);
        this.rules = new KitRules();
    }

    public Kit(String name, String displayName, ItemStack icon, List<ItemStack> items, List<ItemStack> armor) {
        this.name = name;
        this.displayName = displayName;
        this.icon = icon;
        this.items = items;
        this.armor = armor;
        this.rules = new KitRules();
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public List<ItemStack> getArmor() {
        return armor;
    }

    public KitRules getRules() {
        return rules;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public void setItems(List<ItemStack> items) {
        this.items = items;
    }

    public void setArmor(List<ItemStack> armor) {
        this.armor = armor;
    }

    public void setRules(KitRules rules) {
        this.rules = rules;
    }

    // Individual item methods
    public ItemStack getItem(int slot) {
        if (slot >= 0 && slot < items.size()) {
            return items.get(slot);
        }
        return null;
    }

    public void setItem(int slot, ItemStack item) {
        if (slot >= 0 && slot < items.size()) {
            items.set(slot, item);
        } else if (slot >= 0 && slot < 36) {
            items.add(slot, item);
        }
    }

    // Armor methods
    public ItemStack getHelmet() {
        return armor.size() > 3 ? armor.get(3) : null;
    }

    public void setHelmet(ItemStack helmet) {
        if (armor.size() > 3) {
            armor.set(3, helmet);
        } else {
            armor.add(3, helmet);
        }
    }

    public ItemStack getChestplate() {
        return armor.size() > 2 ? armor.get(2) : null;
    }

    public void setChestplate(ItemStack chestplate) {
        if (armor.size() > 2) {
            armor.set(2, chestplate);
        } else {
            armor.add(2, chestplate);
        }
    }

    public ItemStack getLeggings() {
        return armor.size() > 1 ? armor.get(1) : null;
    }

    public void setLeggings(ItemStack leggings) {
        if (armor.size() > 1) {
            armor.set(1, leggings);
        } else {
            armor.add(1, leggings);
        }
    }

    public ItemStack getBoots() {
        return armor.size() > 0 ? armor.get(0) : null;
    }

    public void setBoots(ItemStack boots) {
        if (armor.size() > 0) {
            armor.set(0, boots);
        } else {
            armor.add(0, boots);
        }
    }
}
