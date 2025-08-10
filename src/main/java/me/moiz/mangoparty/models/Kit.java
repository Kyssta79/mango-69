package me.moiz.mangoparty.models;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class Kit {
    private String name;
    private String displayName;
    private ItemStack[] items;
    private ItemStack[] armor;
    private ItemStack icon;
    private List<PotionEffect> effects;
    private KitRules rules;

    public Kit(String name) {
        this.name = name;
        this.displayName = name;
        this.items = new ItemStack[36];
        this.armor = new ItemStack[4];
        this.effects = new ArrayList<>();
        this.rules = new KitRules();
    }

    public Kit(String name, String displayName, ItemStack[] items, ItemStack[] armor) {
        this.name = name;
        this.displayName = displayName;
        this.items = items != null ? items : new ItemStack[36];
        this.armor = armor != null ? armor : new ItemStack[4];
        this.effects = new ArrayList<>();
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
        return displayName != null ? displayName : name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ItemStack[] getItems() {
        return items;
    }

    public void setItems(ItemStack[] items) {
        this.items = items;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public List<PotionEffect> getEffects() {
        return effects;
    }

    public void setEffects(List<PotionEffect> effects) {
        this.effects = effects != null ? effects : new ArrayList<>();
    }

    public void addEffect(PotionEffect effect) {
        if (effects == null) {
            effects = new ArrayList<>();
        }
        effects.add(effect);
    }

    public KitRules getRules() {
        return rules;
    }

    public void setRules(KitRules rules) {
        this.rules = rules;
    }

    // Individual item methods
    public ItemStack getItem(int slot) {
        if (slot >= 0 && slot < items.length) {
            return items[slot];
        }
        return null;
    }

    public void setItem(int slot, ItemStack item) {
        if (slot >= 0 && slot < items.length) {
            items[slot] = item;
        }
    }

    // Armor methods
    public ItemStack getHelmet() {
        return armor[3];
    }

    public void setHelmet(ItemStack helmet) {
        armor[3] = helmet;
    }

    public ItemStack getChestplate() {
        return armor[2];
    }

    public void setChestplate(ItemStack chestplate) {
        armor[2] = chestplate;
    }

    public ItemStack getLeggings() {
        return armor[1];
    }

    public void setLeggings(ItemStack leggings) {
        armor[1] = leggings;
    }

    public ItemStack getBoots() {
        return armor[0];
    }

    public void setBoots(ItemStack boots) {
        armor[0] = boots;
    }
}
