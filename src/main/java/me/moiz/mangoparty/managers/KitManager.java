package me.moiz.mangoparty.managers;

import me.moiz.mangoparty.MangoParty;
import me.moiz.mangoparty.models.Kit;
import me.moiz.mangoparty.models.KitRules;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class KitManager {
    private MangoParty plugin;
    private Map<String, Kit> kits;
    private File kitsFile;
    private YamlConfiguration kitsConfig;
    
    public KitManager(MangoParty plugin) {
        this.plugin = plugin;
        this.kits = new HashMap<>();
        this.kitsFile = new File(plugin.getDataFolder(), "kits.yml");
        loadKits();
    }
    
    private void loadKits() {
        if (!kitsFile.exists()) {
            plugin.saveResource("kits.yml", false);
        }
        
        kitsConfig = YamlConfiguration.loadConfiguration(kitsFile);
        
        ConfigurationSection kitsSection = kitsConfig.getConfigurationSection("kits");
        if (kitsSection != null) {
            for (String kitName : kitsSection.getKeys(false)) {
                Kit kit = loadKitFromConfig(kitName, kitsSection.getConfigurationSection(kitName));
                if (kit != null) {
                    kits.put(kitName, kit);
                }
            }
        }
        
        plugin.getLogger().info("Loaded " + kits.size() + " kits");
    }
    
    private Kit loadKitFromConfig(String name, ConfigurationSection section) {
        try {
            String displayName = section.getString("displayName", name);
            
            // Create kit with basic constructor
            Kit kit = new Kit(name);
            kit.setDisplayName(displayName);
            
            // Load icon
            if (section.contains("icon")) {
                ConfigurationSection iconSection = section.getConfigurationSection("icon");
                if (iconSection != null) {
                    Material material = Material.valueOf(iconSection.getString("material", "IRON_SWORD"));
                    ItemStack icon = new ItemStack(material);
                    
                    if (iconSection.contains("name")) {
                        ItemMeta meta = icon.getItemMeta();
                        meta.setDisplayName(iconSection.getString("name"));
                        icon.setItemMeta(meta);
                    }
                    
                    kit.setIcon(icon);
                }
            }
            
            // Load items
            if (section.contains("items")) {
                List<Map<?, ?>> itemsList = section.getMapList("items");
                for (Map<?, ?> itemMap : itemsList) {
                    int slot = (Integer) itemMap.get("slot");
                    String materialName = (String) itemMap.get("material");
                    int amount = itemMap.containsKey("amount") ? (Integer) itemMap.get("amount") : 1;
                    
                    Material material = Material.valueOf(materialName);
                    ItemStack item = new ItemStack(material, amount);
                    
                    if (itemMap.containsKey("name")) {
                        ItemMeta meta = item.getItemMeta();
                        meta.setDisplayName((String) itemMap.get("name"));
                        item.setItemMeta(meta);
                    }
                    
                    kit.setItem(slot, item);
                }
            }
            
            // Load armor
            if (section.contains("armor")) {
                ConfigurationSection armorSection = section.getConfigurationSection("armor");
                if (armorSection.contains("helmet")) {
                    kit.setHelmet(new ItemStack(Material.valueOf(armorSection.getString("helmet"))));
                }
                if (armorSection.contains("chestplate")) {
                    kit.setChestplate(new ItemStack(Material.valueOf(armorSection.getString("chestplate"))));
                }
                if (armorSection.contains("leggings")) {
                    kit.setLeggings(new ItemStack(Material.valueOf(armorSection.getString("leggings"))));
                }
                if (armorSection.contains("boots")) {
                    kit.setBoots(new ItemStack(Material.valueOf(armorSection.getString("boots"))));
                }
            }
            
            // Load effects
            if (section.contains("effects")) {
                List<Map<?, ?>> effectsList = section.getMapList("effects");
                for (Map<?, ?> effectMap : effectsList) {
                    String effectName = (String) effectMap.get("type");
                    int amplifier = effectMap.containsKey("amplifier") ? (Integer) effectMap.get("amplifier") : 0;
                    int duration = effectMap.containsKey("duration") ? (Integer) effectMap.get("duration") : Integer.MAX_VALUE;
                    
                    PotionEffectType effectType = PotionEffectType.getByName(effectName);
                    if (effectType != null) {
                        kit.addEffect(new PotionEffect(effectType, duration, amplifier));
                    }
                }
            }
            
            // Load rules
            if (section.contains("rules")) {
                ConfigurationSection rulesSection = section.getConfigurationSection("rules");
                KitRules rules = new KitRules();
                
                if (rulesSection.contains("naturalHealthRegen")) {
                    rules.setNaturalHealthRegen(rulesSection.getBoolean("naturalHealthRegen"));
                }
                if (rulesSection.contains("blockBreak")) {
                    rules.setBlockBreak(rulesSection.getBoolean("blockBreak"));
                }
                if (rulesSection.contains("blockPlace")) {
                    rules.setBlockPlace(rulesSection.getBoolean("blockPlace"));
                }
                if (rulesSection.contains("damageMultiplier")) {
                    rules.setDamageMultiplier(rulesSection.getDouble("damageMultiplier"));
                }
                if (rulesSection.contains("instantTnt")) {
                    rules.setInstantTnt(rulesSection.getBoolean("instantTnt"));
                }
                
                kit.setRules(rules);
            }
            
            return kit;
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load kit '" + name + "': " + e.getMessage());
            return null;
        }
    }
    
    public void saveKits() {
        kitsConfig.set("kits", null); // Clear existing data
        
        for (Kit kit : kits.values()) {
            saveKitToConfig(kit);
        }
        
        try {
            kitsConfig.save(kitsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save kits.yml: " + e.getMessage());
        }
    }
    
    private void saveKitToConfig(Kit kit) {
        String path = "kits." + kit.getName();
        
        kitsConfig.set(path + ".displayName", kit.getDisplayName());
        
        // Save icon
        if (kit.getIcon() != null) {
            kitsConfig.set(path + ".icon.material", kit.getIcon().getType().name());
            if (kit.getIcon().hasItemMeta() && kit.getIcon().getItemMeta().hasDisplayName()) {
                kitsConfig.set(path + ".icon.name", kit.getIcon().getItemMeta().getDisplayName());
            }
        }
        
        // Save items
        List<Map<String, Object>> itemsList = new ArrayList<>();
        for (int i = 0; i < 36; i++) {
            ItemStack item = kit.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("slot", i);
                itemMap.put("material", item.getType().name());
                itemMap.put("amount", item.getAmount());
                
                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                    itemMap.put("name", item.getItemMeta().getDisplayName());
                }
                
                itemsList.add(itemMap);
            }
        }
        kitsConfig.set(path + ".items", itemsList);
        
        // Save armor
        if (kit.getHelmet() != null) {
            kitsConfig.set(path + ".armor.helmet", kit.getHelmet().getType().name());
        }
        if (kit.getChestplate() != null) {
            kitsConfig.set(path + ".armor.chestplate", kit.getChestplate().getType().name());
        }
        if (kit.getLeggings() != null) {
            kitsConfig.set(path + ".armor.leggings", kit.getLeggings().getType().name());
        }
        if (kit.getBoots() != null) {
            kitsConfig.set(path + ".armor.boots", kit.getBoots().getType().name());
        }
        
        // Save effects
        if (!kit.getEffects().isEmpty()) {
            List<Map<String, Object>> effectsList = new ArrayList<>();
            for (PotionEffect effect : kit.getEffects()) {
                Map<String, Object> effectMap = new HashMap<>();
                effectMap.put("type", effect.getType().getName());
                effectMap.put("amplifier", effect.getAmplifier());
                effectMap.put("duration", effect.getDuration());
                effectsList.add(effectMap);
            }
            kitsConfig.set(path + ".effects", effectsList);
        }
        
        // Save rules
        if (kit.getRules() != null) {
            KitRules rules = kit.getRules();
            kitsConfig.set(path + ".rules.naturalHealthRegen", rules.isNaturalHealthRegen());
            kitsConfig.set(path + ".rules.blockBreak", rules.isBlockBreak());
            kitsConfig.set(path + ".rules.blockPlace", rules.isBlockPlace());
            kitsConfig.set(path + ".rules.damageMultiplier", rules.getDamageMultiplier());
            kitsConfig.set(path + ".rules.instantTnt", rules.isInstantTnt());
        }
    }
    
    public Kit createKit(String name, String displayName) {
        if (kits.containsKey(name)) {
            return null;
        }
        
        Kit kit = new Kit(name);
        kit.setDisplayName(displayName);
        kit.setIcon(new ItemStack(Material.IRON_SWORD));
        kits.put(name, kit);
        saveKit(kit);
        
        plugin.getLogger().info("Created new kit: " + name);
        return kit;
    }
    
    public void addKit(Kit kit) {
        kits.put(kit.getName(), kit);
        saveKit(kit);
    }
    
    public void saveKit(Kit kit) {
        saveKitToConfig(kit);
        try {
            kitsConfig.save(kitsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save kit '" + kit.getName() + "': " + e.getMessage());
        }
    }
    
    public Kit getKit(String name) {
        return kits.get(name);
    }
    
    public Map<String, Kit> getKits() {
        return new HashMap<>(kits);
    }
    
    public List<Kit> getAllKits() {
        return new ArrayList<>(kits.values());
    }
    
    public void deleteKit(String name) {
        Kit kit = kits.remove(name);
        if (kit != null) {
            kitsConfig.set("kits." + name, null);
            try {
                kitsConfig.save(kitsFile);
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to delete kit '" + name + "': " + e.getMessage());
            }
            
            plugin.getLogger().info("Deleted kit: " + name);
        }
    }
    
    public void giveKit(Player player, String kitName) {
        Kit kit = kits.get(kitName);
        if (kit == null) {
            plugin.getLogger().warning("Tried to give non-existent kit: " + kitName);
            return;
        }
        
        // Clear inventory
        player.getInventory().clear();
        
        // Give items
        for (int i = 0; i < 36; i++) {
            ItemStack item = kit.getItem(i);
            if (item != null) {
                player.getInventory().setItem(i, item.clone());
            }
        }
        
        // Give armor
        if (kit.getHelmet() != null) {
            player.getInventory().setHelmet(kit.getHelmet().clone());
        }
        if (kit.getChestplate() != null) {
            player.getInventory().setChestplate(kit.getChestplate().clone());
        }
        if (kit.getLeggings() != null) {
            player.getInventory().setLeggings(kit.getLeggings().clone());
        }
        if (kit.getBoots() != null) {
            player.getInventory().setBoots(kit.getBoots().clone());
        }
        
        // Apply effects
        for (PotionEffect effect : kit.getEffects()) {
            player.addPotionEffect(effect);
        }
        
        player.updateInventory();
    }
    
    public void reloadKits() {
        kits.clear();
        loadKits();
        plugin.getLogger().info("Reloaded kits configuration.");
    }
}
