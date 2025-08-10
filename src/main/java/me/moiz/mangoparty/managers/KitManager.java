package me.moiz.mangoparty.managers;

import me.moiz.mangoparty.MangoParty;
import me.moiz.mangoparty.models.Kit;
import me.moiz.mangoparty.models.KitRules;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
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
            
            Kit kit = new Kit(name, displayName, new ItemStack[0], new ItemStack[0]);
            
            // Load items
            if (section.contains("items")) {
                List<?> itemsList = section.getList("items");
                if (itemsList != null) {
                    ItemStack[] items = new ItemStack[itemsList.size()];
                    for (int i = 0; i < itemsList.size(); i++) {
                        if (itemsList.get(i) instanceof ItemStack) {
                            items[i] = (ItemStack) itemsList.get(i);
                        }
                    }
                    kit.setItems(items);
                }
            }
            
            // Load armor
            if (section.contains("armor")) {
                List<?> armorList = section.getList("armor");
                if (armorList != null) {
                    ItemStack[] armor = new ItemStack[armorList.size()];
                    for (int i = 0; i < armorList.size(); i++) {
                        if (armorList.get(i) instanceof ItemStack) {
                            armor[i] = (ItemStack) armorList.get(i);
                        }
                    }
                    kit.setArmor(armor);
                }
            }
            
            // Load icon
            if (section.contains("icon")) {
                Object iconObj = section.get("icon");
                if (iconObj instanceof ItemStack) {
                    kit.setIcon((ItemStack) iconObj);
                }
            }
            
            // Load rules
            if (section.contains("rules")) {
                ConfigurationSection rulesSection = section.getConfigurationSection("rules");
                KitRules rules = new KitRules();
                rules.setBlockBreaking(rulesSection.getBoolean("blockBreaking", false));
                rules.setBlockPlacing(rulesSection.getBoolean("blockPlacing", false));
                rules.setPvp(rulesSection.getBoolean("pvp", true));
                rules.setItemDropping(rulesSection.getBoolean("itemDropping", true));
                rules.setItemPickup(rulesSection.getBoolean("itemPickup", true));
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
        kitsConfig.set(path + ".items", kit.getItems());
        kitsConfig.set(path + ".armor", kit.getArmor());
        
        if (kit.getIcon() != null) {
            kitsConfig.set(path + ".icon", kit.getIcon());
        }
        
        if (kit.getRules() != null) {
            KitRules rules = kit.getRules();
            kitsConfig.set(path + ".rules.blockBreaking", rules.isBlockBreaking());
            kitsConfig.set(path + ".rules.blockPlacing", rules.isBlockPlacing());
            kitsConfig.set(path + ".rules.pvp", rules.isPvp());
            kitsConfig.set(path + ".rules.itemDropping", rules.isItemDropping());
            kitsConfig.set(path + ".rules.itemPickup", rules.isItemPickup());
        }
    }

    public void addKit(Kit kit) {
        kits.put(kit.getName(), kit);
        saveKit(kit);
        plugin.getLogger().info("Added new kit: " + kit.getName());
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

    public void giveKit(org.bukkit.entity.Player player, String kitName) {
        Kit kit = getKit(kitName);
        if (kit == null) {
            player.sendMessage("§cKit not found: " + kitName);
            return;
        }
        
        // Clear player inventory
        player.getInventory().clear();
        
        // Give items
        for (ItemStack item : kit.getItems()) {
            if (item != null && item.getType() != Material.AIR) {
                player.getInventory().addItem(item.clone());
            }
        }
        
        // Give armor
        ItemStack[] armor = kit.getArmor();
        if (armor.length >= 4) {
            player.getInventory().setHelmet(armor[3] != null ? armor[3].clone() : null);
            player.getInventory().setChestplate(armor[2] != null ? armor[2].clone() : null);
            player.getInventory().setLeggings(armor[1] != null ? armor[1].clone() : null);
            player.getInventory().setBoots(armor[0] != null ? armor[0].clone() : null);
        }
        
        player.updateInventory();
    }

    public void cleanup() {
        saveKits();
    }
}
