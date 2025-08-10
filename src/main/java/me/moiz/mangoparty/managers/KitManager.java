package me.moiz.mangoparty.managers;

import me.moiz.mangoparty.MangoParty;
import me.moiz.mangoparty.models.Kit;
import me.moiz.mangoparty.models.KitRules;
import org.bukkit.ChatColor;
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
                    Kit kit = new Kit(kitName);
                    
                    // Load display name
                    if (kitSection.contains("displayName")) {
                        kit.setDisplayName(kitSection.getString("displayName"));
                    }
                    
                    // Load icon
                    if (kitSection.contains("icon")) {
                        ConfigurationSection iconSection = kitSection.getConfigurationSection("icon");
                        if (iconSection != null) {
                            Material material = Material.valueOf(iconSection.getString("material", "IRON_SWORD"));
                            ItemStack icon = new ItemStack(material);
                            ItemMeta meta = icon.getItemMeta();
                            if (iconSection.contains("name")) {
                                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', iconSection.getString("name")));
                            }
                            if (iconSection.contains("lore")) {
                                List<String> lore = iconSection.getStringList("lore");
                                for (int i = 0; i < lore.size(); i++) {
                                    lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
                                }
                                meta.setLore(lore);
                            }
                            icon.setItemMeta(meta);
                            kit.setIcon(icon);
                        }
                    }
                    
                    // Load items
                    if (kitSection.contains("items")) {
                        ConfigurationSection itemsSection = kitSection.getConfigurationSection("items");
                        if (itemsSection != null) {
                            for (String slotStr : itemsSection.getKeys(false)) {
                                try {
                                    int slot = Integer.parseInt(slotStr);
                                    ConfigurationSection itemSection = itemsSection.getConfigurationSection(slotStr);
                                    if (itemSection != null) {
                                        ItemStack item = loadItemFromConfig(itemSection);
                                        kit.setItem(slot, item);
                                    }
                                } catch (NumberFormatException e) {
                                    plugin.getLogger().warning("Invalid slot number in kit " + kitName + ": " + slotStr);
                                }
                            }
                        }
                    }
                    
                    // Load armor
                    if (kitSection.contains("armor")) {
                        ConfigurationSection armorSection = kitSection.getConfigurationSection("armor");
                        if (armorSection != null) {
                            if (armorSection.contains("helmet")) {
                                ItemStack helmet = loadItemFromConfig(armorSection.getConfigurationSection("helmet"));
                                kit.setHelmet(helmet);
                            }
                            if (armorSection.contains("chestplate")) {
                                ItemStack chestplate = loadItemFromConfig(armorSection.getConfigurationSection("chestplate"));
                                kit.setChestplate(chestplate);
                            }
                            if (armorSection.contains("leggings")) {
                                ItemStack leggings = loadItemFromConfig(armorSection.getConfigurationSection("leggings"));
                                kit.setLeggings(leggings);
                            }
                            if (armorSection.contains("boots")) {
                                ItemStack boots = loadItemFromConfig(armorSection.getConfigurationSection("boots"));
                                kit.setBoots(boots);
                            }
                        }
                    }
                    
                    // Load effects
                    if (kitSection.contains("effects")) {
                        List<String> effectStrings = kitSection.getStringList("effects");
                        for (String effectString : effectStrings) {
                            try {
                                String[] parts = effectString.split(":");
                                PotionEffectType type = PotionEffectType.getByName(parts[0]);
                                int amplifier = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                                int duration = parts.length > 2 ? Integer.parseInt(parts[2]) : Integer.MAX_VALUE;
                                
                                if (type != null) {
                                    PotionEffect effect = new PotionEffect(type, duration, amplifier);
                                    kit.addEffect(effect);
                                }
                            } catch (Exception e) {
                                plugin.getLogger().warning("Invalid effect format in kit " + kitName + ": " + effectString);
                            }
                        }
                    }
                    
                    // Load rules
                    if (kitSection.contains("rules")) {
                        ConfigurationSection rulesSection = kitSection.getConfigurationSection("rules");
                        if (rulesSection != null) {
                            KitRules rules = new KitRules();
                            rules.setBlockBreaking(rulesSection.getBoolean("blockBreaking", false));
                            rules.setBlockPlacing(rulesSection.getBoolean("blockPlacing", false));
                            rules.setNaturalHealthRegen(rulesSection.getBoolean("naturalHealthRegen", true));
                            rules.setInstantTnt(rulesSection.getBoolean("instantTnt", false));
                            rules.setDamageMultiplier(rulesSection.getDouble("damageMultiplier", 1.0));
                            kit.setRules(rules);
                        }
                    }
                    
                    kits.put(kitName, kit);
                }
            }
        }
        
        plugin.getLogger().info("Loaded " + kits.size() + " kits");
    }

    private ItemStack loadItemFromConfig(ConfigurationSection section) {
        if (section == null) return null;
        
        Material material = Material.valueOf(section.getString("material", "STONE"));
        int amount = section.getInt("amount", 1);
        ItemStack item = new ItemStack(material, amount);
        
        if (section.contains("name") || section.contains("lore")) {
            ItemMeta meta = item.getItemMeta();
            if (section.contains("name")) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', section.getString("name")));
            }
            if (section.contains("lore")) {
                List<String> lore = section.getStringList("lore");
                for (int i = 0; i < lore.size(); i++) {
                    lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
                }
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
        
        return item;
    }

    public void giveKit(Player player, String kitName) {
        Kit kit = kits.get(kitName);
        if (kit == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cKit not found: " + kitName));
            return;
        }
        
        // Clear inventory
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        
        // Give items
        for (int i = 0; i < kit.getItems().length; i++) {
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
        if (kit.getEffects() != null) {
            for (PotionEffect effect : kit.getEffects()) {
                player.addPotionEffect(effect);
            }
        }
        
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aApplied kit: " + kit.getDisplayName()));
    }

    public void saveKit(Kit kit) {
        if (kit == null) return;
        
        ConfigurationSection kitsSection = kitsConfig.getConfigurationSection("kits");
        if (kitsSection == null) {
            kitsSection = kitsConfig.createSection("kits");
        }
        
        ConfigurationSection kitSection = kitsSection.createSection(kit.getName());
        kitSection.set("displayName", kit.getDisplayName());
        
        // Save icon
        if (kit.getIcon() != null) {
            ConfigurationSection iconSection = kitSection.createSection("icon");
            iconSection.set("material", kit.getIcon().getType().name());
            if (kit.getIcon().hasItemMeta()) {
                ItemMeta meta = kit.getIcon().getItemMeta();
                if (meta.hasDisplayName()) {
                    iconSection.set("name", meta.getDisplayName());
                }
                if (meta.hasLore()) {
                    iconSection.set("lore", meta.getLore());
                }
            }
        }
        
        // Save items
        ConfigurationSection itemsSection = kitSection.createSection("items");
        for (int i = 0; i < kit.getItems().length; i++) {
            ItemStack item = kit.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                saveItemToConfig(itemsSection.createSection(String.valueOf(i)), item);
            }
        }
        
        // Save armor
        ConfigurationSection armorSection = kitSection.createSection("armor");
        if (kit.getHelmet() != null) {
            saveItemToConfig(armorSection.createSection("helmet"), kit.getHelmet());
        }
        if (kit.getChestplate() != null) {
            saveItemToConfig(armorSection.createSection("chestplate"), kit.getChestplate());
        }
        if (kit.getLeggings() != null) {
            saveItemToConfig(armorSection.createSection("leggings"), kit.getLeggings());
        }
        if (kit.getBoots() != null) {
            saveItemToConfig(armorSection.createSection("boots"), kit.getBoots());
        }
        
        // Save effects
        if (kit.getEffects() != null && !kit.getEffects().isEmpty()) {
            List<String> effectStrings = new java.util.ArrayList<>();
            for (PotionEffect effect : kit.getEffects()) {
                effectStrings.add(effect.getType().getName() + ":" + effect.getAmplifier() + ":" + effect.getDuration());
            }
            kitSection.set("effects", effectStrings);
        }
        
        // Save rules
        if (kit.getRules() != null) {
            ConfigurationSection rulesSection = kitSection.createSection("rules");
            rulesSection.set("blockBreaking", kit.getRules().isBlockBreaking());
            rulesSection.set("blockPlacing", kit.getRules().isBlockPlacing());
            rulesSection.set("naturalHealthRegen", kit.getRules().isNaturalHealthRegen());
            rulesSection.set("instantTnt", kit.getRules().isInstantTnt());
            rulesSection.set("damageMultiplier", kit.getRules().getDamageMultiplier());
        }
        
        try {
            kitsConfig.save(kitsFile);
            kits.put(kit.getName(), kit);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save kit " + kit.getName() + ": " + e.getMessage());
        }
    }

    private void saveItemToConfig(ConfigurationSection section, ItemStack item) {
        section.set("material", item.getType().name());
        section.set("amount", item.getAmount());
        
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                section.set("name", meta.getDisplayName());
            }
            if (meta.hasLore()) {
                section.set("lore", meta.getLore());
            }
        }
    }

    public void addKit(Kit kit) {
        if (kit != null) {
            kits.put(kit.getName(), kit);
            saveKit(kit);
        }
    }

    public void deleteKit(String kitName) {
        kits.remove(kitName);
        
        ConfigurationSection kitsSection = kitsConfig.getConfigurationSection("kits");
        if (kitsSection != null && kitsSection.contains(kitName)) {
            kitsSection.set(kitName, null);
            try {
                kitsConfig.save(kitsFile);
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to delete kit " + kitName + ": " + e.getMessage());
            }
        }
    }

    public Kit getKit(String name) {
        return kits.get(name);
    }

    public Map<String, Kit> getKits() {
        return new HashMap<>(kits);
    }

    public void reloadKits() {
        kits.clear();
        loadKits();
    }
}
