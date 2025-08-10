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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitManager {
    private MangoParty plugin;
    private Map<String, Kit> kits;
    private File kitsFile;
    private YamlConfiguration kitsConfig;
    
    public KitManager(MangoParty plugin) {
        this.plugin = plugin;
        this.kits = new HashMap<>();
        loadKits();
    }
    
    private void loadKits() {
        kitsFile = new File(plugin.getDataFolder(), "kits.yml");
        if (!kitsFile.exists()) {
            plugin.saveResource("kits.yml", false);
        }
        
        kitsConfig = YamlConfiguration.loadConfiguration(kitsFile);
        
        ConfigurationSection kitsSection = kitsConfig.getConfigurationSection("kits");
        if (kitsSection != null) {
            for (String kitName : kitsSection.getKeys(false)) {
                ConfigurationSection kitSection = kitsSection.getConfigurationSection(kitName);
                if (kitSection != null) {
                    Kit kit = loadKit(kitName, kitSection);
                    if (kit != null) {
                        kits.put(kitName, kit);
                    }
                }
            }
        }
        
        plugin.getLogger().info("Loaded " + kits.size() + " kits");
    }
    
    private Kit loadKit(String name, ConfigurationSection section) {
        try {
            String displayName = section.getString("displayName", name);
            
            // Load icon
            ItemStack icon = null;
            if (section.contains("icon")) {
                String iconMaterial = section.getString("icon.material", "IRON_SWORD");
                icon = new ItemStack(Material.valueOf(iconMaterial));
                
                if (section.contains("icon.name")) {
                    ItemMeta meta = icon.getItemMeta();
                    meta.setDisplayName(section.getString("icon.name"));
                    icon.setItemMeta(meta);
                }
            }
            
            // Load items
            List<ItemStack> items = new ArrayList<>();
            if (section.contains("items")) {
                List<Map<?, ?>> itemsList = section.getMapList("items");
                for (Map<?, ?> itemMap : itemsList) {
                    ItemStack item = deserializeItem(itemMap);
                    if (item != null) {
                        items.add(item);
                    }
                }
            }
            
            // Load armor
            List<ItemStack> armor = new ArrayList<>();
            if (section.contains("armor")) {
                List<Map<?, ?>> armorList = section.getMapList("armor");
                for (Map<?, ?> armorMap : armorList) {
                    ItemStack armorPiece = deserializeItem(armorMap);
                    if (armorPiece != null) {
                        armor.add(armorPiece);
                    }
                }
            }
            
            Kit kit = new Kit(name, displayName, icon, items, armor);
            
            // Load rules
            if (section.contains("rules")) {
                ConfigurationSection rulesSection = section.getConfigurationSection("rules");
                KitRules rules = new KitRules(
                    rulesSection.getBoolean("naturalHealthRegen", true),
                    rulesSection.getBoolean("blockBreak", false),
                    rulesSection.getBoolean("blockPlace", false),
                    rulesSection.getBoolean("instantTnt", false),
                    rulesSection.getDouble("damageMultiplier", 1.0)
                );
                kit.setRules(rules);
            }
            
            return kit;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load kit '" + name + "': " + e.getMessage());
            return null;
        }
    }
    
    private ItemStack deserializeItem(Map<?, ?> itemMap) {
        try {
            String materialName = (String) itemMap.get("material");
            if (materialName == null) return null;
            
            Material material = Material.valueOf(materialName);
            int amount = itemMap.containsKey("amount") ? (Integer) itemMap.get("amount") : 1;
            
            ItemStack item = new ItemStack(material, amount);
            
            if (itemMap.containsKey("name")) {
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName((String) itemMap.get("name"));
                item.setItemMeta(meta);
            }
            
            return item;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to deserialize item: " + e.getMessage());
            return null;
        }
    }
    
    public void saveKits() {
        try {
            kitsConfig.save(kitsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save kits: " + e.getMessage());
        }
    }
    
    public Kit getKit(String name) {
        return kits.get(name);
    }
    
    public Map<String, Kit> getKits() {
        return new HashMap<>(kits);
    }
    
    public void addKit(Kit kit) {
        kits.put(kit.getName(), kit);
        saveKitToConfig(kit);
        saveKits();
    }
    
    public void removeKit(String name) {
        kits.remove(name);
        kitsConfig.set("kits." + name, null);
        saveKits();
    }
    
    private void saveKitToConfig(Kit kit) {
        String path = "kits." + kit.getName();
        kitsConfig.set(path + ".displayName", kit.getDisplayName());
        
        if (kit.getIcon() != null) {
            kitsConfig.set(path + ".icon.material", kit.getIcon().getType().name());
            if (kit.getIcon().hasItemMeta() && kit.getIcon().getItemMeta().hasDisplayName()) {
                kitsConfig.set(path + ".icon.name", kit.getIcon().getItemMeta().getDisplayName());
            }
        }
        
        // Save items and armor as serialized maps
        if (kit.getItems() != null && !kit.getItems().isEmpty()) {
            List<Map<String, Object>> itemsList = new ArrayList<>();
            for (ItemStack item : kit.getItems()) {
                itemsList.add(item.serialize());
            }
            kitsConfig.set(path + ".items", itemsList);
        }
        
        if (kit.getArmor() != null && !kit.getArmor().isEmpty()) {
            List<Map<String, Object>> armorList = new ArrayList<>();
            for (ItemStack armor : kit.getArmor()) {
                armorList.add(armor.serialize());
            }
            kitsConfig.set(path + ".armor", armorList);
        }
        
        // Save rules
        if (kit.getRules() != null) {
            KitRules rules = kit.getRules();
            kitsConfig.set(path + ".rules.naturalHealthRegen", rules.isNaturalHealthRegen());
            kitsConfig.set(path + ".rules.blockBreak", rules.isBlockBreak());
            kitsConfig.set(path + ".rules.blockPlace", rules.isBlockPlace());
            kitsConfig.set(path + ".rules.instantTnt", rules.isInstantTnt());
            kitsConfig.set(path + ".rules.damageMultiplier", rules.getDamageMultiplier());
        }
    }
    
    public void giveKit(Player player, String kitName) {
        Kit kit = getKit(kitName);
        if (kit == null) {
            player.sendMessage("§cKit not found: " + kitName);
            return;
        }
        
        // Clear inventory
        player.getInventory().clear();
        
        // Give items
        if (kit.getItems() != null) {
            for (ItemStack item : kit.getItems()) {
                if (item != null) {
                    player.getInventory().addItem(item.clone());
                }
            }
        }
        
        // Give armor
        if (kit.getArmor() != null) {
            ItemStack[] armorContents = new ItemStack[4];
            for (int i = 0; i < Math.min(kit.getArmor().size(), 4); i++) {
                armorContents[i] = kit.getArmor().get(i).clone();
            }
            player.getInventory().setArmorContents(armorContents);
        }
        
        player.updateInventory();
    }
}
