package me.moiz.mangoparty.gui;

import me.moiz.mangoparty.MangoParty;
import me.moiz.mangoparty.models.Kit;
import me.moiz.mangoparty.models.KitRules;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class KitEditorGui implements Listener {
    private MangoParty plugin;
    private YamlConfiguration kitListConfig;
    private YamlConfiguration kitEditorConfig;
    private Map<UUID, String> editingKit = new HashMap<>();
    private Map<UUID, Inventory> playerInventories = new HashMap<>();
    
    public KitEditorGui(MangoParty plugin) {
        this.plugin = plugin;
        loadConfigs();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    private void loadConfigs() {
        File guiDir = new File(plugin.getDataFolder(), "gui");
        if (!guiDir.exists()) {
            guiDir.mkdirs();
        }
        
        File kitListFile = new File(guiDir, "kit_list.yml");
        File kitEditorFile = new File(guiDir, "kit_editor.yml");
        
        if (!kitListFile.exists()) {
            plugin.saveResource("gui/kit_list.yml", false);
        }
        if (!kitEditorFile.exists()) {
            plugin.saveResource("gui/kit_editor.yml", false);
        }
        
        kitListConfig = YamlConfiguration.loadConfiguration(kitListFile);
        kitEditorConfig = YamlConfiguration.loadConfiguration(kitEditorFile);
    }
    
    public void reloadConfigs() {
        loadConfigs();
    }
    
    private String translateColors(String text) {
        if (text == null) return null;
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    
    private List<String> translateColors(List<String> list) {
        if (list == null) return null;
        List<String> translated = new ArrayList<>();
        for (String line : list) {
            translated.add(translateColors(line));
        }
        return translated;
    }
    
    public void openKitListGui(Player player) {
        String title = translateColors(kitListConfig.getString("title", "&6Kit List"));
        int size = kitListConfig.getInt("size", 54);
        
        Inventory gui = Bukkit.createInventory(null, size, title);
        
        // Add existing kits
        Map<String, Kit> kits = plugin.getKitManager().getKits();
        int slot = 0;
        
        for (Map.Entry<String, Kit> entry : kits.entrySet()) {
            if (slot >= size - 9) break; // Leave space for navigation
            
            Kit kit = entry.getValue();
            ItemStack item = kit.getIcon() != null ? kit.getIcon().clone() : new ItemStack(Material.IRON_SWORD);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(translateColors("&e" + kit.getName()));
            
            List<String> lore = new ArrayList<>();
            lore.add(translateColors("&7Display Name: &f" + kit.getDisplayName()));
            lore.add("");
            lore.add(translateColors("&aLeft Click: Edit Kit"));
            lore.add(translateColors("&cRight Click: Delete Kit"));
            
            meta.setLore(lore);
            item.setItemMeta(meta);
            gui.setItem(slot, item);
            slot++;
        }
        
        // Add create new kit button
        ItemStack createItem = new ItemStack(Material.EMERALD);
        ItemMeta createMeta = createItem.getItemMeta();
        createMeta.setDisplayName(translateColors("&aCreate New Kit"));
        List<String> createLore = new ArrayList<>();
        createLore.add(translateColors("&7Click to create a new kit"));
        createMeta.setLore(createLore);
        createItem.setItemMeta(createMeta);
        gui.setItem(size - 5, createItem);
        
        player.openInventory(gui);
    }
    
    public void openKitEditorGui(Player player, String kitName) {
        String title = translateColors("&6Editing: " + kitName);
        int size = 54;
        
        Inventory gui = Bukkit.createInventory(null, size, title);
        editingKit.put(player.getUniqueId(), kitName);
        
        Kit kit = plugin.getKitManager().getKit(kitName);
        if (kit == null) {
            // Create new kit
            kit = new Kit(kitName, kitName);
            plugin.getKitManager().addKit(kit);
        }
        
        // Add kit items to inventory slots 0-35
        if (kit.getItems() != null) {
            for (int i = 0; i < Math.min(kit.getItems().size(), 36); i++) {
                gui.setItem(i, kit.getItems().get(i));
            }
        }
        
        // Add armor slots (36-39)
        if (kit.getArmor() != null) {
            for (int i = 0; i < Math.min(kit.getArmor().size(), 4); i++) {
                gui.setItem(36 + i, kit.getArmor().get(i));
            }
        }
        
        // Add control buttons
        addControlButtons(gui, kit);
        
        player.openInventory(gui);
    }
    
    private void addControlButtons(Inventory gui, Kit kit) {
        // Save button
        ItemStack saveItem = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta saveMeta = saveItem.getItemMeta();
        saveMeta.setDisplayName(translateColors("&aSave Kit"));
        List<String> saveLore = new ArrayList<>();
        saveLore.add(translateColors("&7Click to save changes"));
        saveMeta.setLore(saveLore);
        saveItem.setItemMeta(saveMeta);
        gui.setItem(45, saveItem);
        
        // Icon selector
        ItemStack iconItem = kit.getIcon() != null ? kit.getIcon().clone() : new ItemStack(Material.IRON_SWORD);
        ItemMeta iconMeta = iconItem.getItemMeta();
        iconMeta.setDisplayName(translateColors("&eKit Icon"));
        List<String> iconLore = new ArrayList<>();
        iconLore.add(translateColors("&7Current icon for the kit"));
        iconLore.add(translateColors("&7Place item here to change"));
        iconMeta.setLore(iconLore);
        iconItem.setItemMeta(iconMeta);
        gui.setItem(46, iconItem);
        
        // Rules editor
        ItemStack rulesItem = new ItemStack(Material.BOOK);
        ItemMeta rulesMeta = rulesItem.getItemMeta();
        rulesMeta.setDisplayName(translateColors("&6Kit Rules"));
        List<String> rulesLore = new ArrayList<>();
        rulesLore.add(translateColors("&7Click to edit kit rules"));
        
        final KitRules rules = kit.getRules();
        if (rules != null) {
            rulesLore.add(translateColors("&7Health Regen: " + (rules.isNaturalHealthRegen() ? "&aEnabled" : "&cDisabled")));
            rulesLore.add(translateColors("&7Block Break: " + (rules.isBlockBreak() ? "&aEnabled" : "&cDisabled")));
            rulesLore.add(translateColors("&7Block Place: " + (rules.isBlockPlace() ? "&aEnabled" : "&cDisabled")));
            rulesLore.add(translateColors("&7Instant TNT: " + (rules.isInstantTnt() ? "&aEnabled" : "&cDisabled")));
            rulesLore.add(translateColors("&7Damage Multiplier: &f" + rules.getDamageMultiplier()));
        }
        
        rulesMeta.setLore(rulesLore);
        rulesItem.setItemMeta(rulesMeta);
        gui.setItem(47, rulesItem);
        
        // Back button
        ItemStack backItem = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.setDisplayName(translateColors("&cBack"));
        List<String> backLore = new ArrayList<>();
        backLore.add(translateColors("&7Return to kit list"));
        backMeta.setLore(backLore);
        backItem.setItemMeta(backMeta);
        gui.setItem(53, backItem);
    }
    
    public void openRulesEditorGui(Player player, String kitName) {
        Kit kit = plugin.getKitManager().getKit(kitName);
        if (kit == null) return;
        
        String title = translateColors("&6Rules: " + kitName);
        Inventory gui = Bukkit.createInventory(null, 27, title);
        
        final KitRules rules = kit.getRules();
        
        // Health Regen toggle
        ItemStack healthItem = new ItemStack(rules.isNaturalHealthRegen() ? Material.GOLDEN_APPLE : Material.ROTTEN_FLESH);
        ItemMeta healthMeta = healthItem.getItemMeta();
        healthMeta.setDisplayName(translateColors("&eNatural Health Regeneration"));
        List<String> healthLore = new ArrayList<>();
        healthLore.add(translateColors("&7Status: " + (rules.isNaturalHealthRegen() ? "&aEnabled" : "&cDisabled")));
        healthLore.add(translateColors("&7Click to toggle"));
        healthMeta.setLore(healthLore);
        healthItem.setItemMeta(healthMeta);
        gui.setItem(10, healthItem);
        
        // Block Break toggle
        ItemStack breakItem = new ItemStack(rules.isBlockBreak() ? Material.DIAMOND_PICKAXE : Material.BARRIER);
        ItemMeta breakMeta = breakItem.getItemMeta();
        breakMeta.setDisplayName(translateColors("&eBlock Breaking"));
        List<String> breakLore = new ArrayList<>();
        breakLore.add(translateColors("&7Status: " + (rules.isBlockBreak() ? "&aEnabled" : "&cDisabled")));
        breakLore.add(translateColors("&7Click to toggle"));
        breakMeta.setLore(breakLore);
        breakItem.setItemMeta(breakMeta);
        gui.setItem(11, breakItem);
        
        // Block Place toggle
        ItemStack placeItem = new ItemStack(rules.isBlockPlace() ? Material.GRASS_BLOCK : Material.BARRIER);
        ItemMeta placeMeta = placeItem.getItemMeta();
        placeMeta.setDisplayName(translateColors("&eBlock Placing"));
        List<String> placeLore = new ArrayList<>();
        placeLore.add(translateColors("&7Status: " + (rules.isBlockPlace() ? "&aEnabled" : "&cDisabled")));
        placeLore.add(translateColors("&7Click to toggle"));
        placeMeta.setLore(placeLore);
        placeItem.setItemMeta(placeMeta);
        gui.setItem(12, placeItem);
        
        // Instant TNT toggle
        ItemStack tntItem = new ItemStack(rules.isInstantTnt() ? Material.TNT : Material.BARRIER);
        ItemMeta tntMeta = tntItem.getItemMeta();
        tntMeta.setDisplayName(translateColors("&eInstant TNT"));
        List<String> tntLore = new ArrayList<>();
        tntLore.add(translateColors("&7Status: " + (rules.isInstantTnt() ? "&aEnabled" : "&cDisabled")));
        tntLore.add(translateColors("&7Click to toggle"));
        tntMeta.setLore(tntLore);
        tntItem.setItemMeta(tntMeta);
        gui.setItem(13, tntItem);
        
        // Damage Multiplier
        ItemStack damageItem = new ItemStack(Material.IRON_SWORD);
        ItemMeta damageMeta = damageItem.getItemMeta();
        damageMeta.setDisplayName(translateColors("&eDamage Multiplier"));
        List<String> damageLore = new ArrayList<>();
        damageLore.add(translateColors("&7Current: &f" + rules.getDamageMultiplier()));
        damageLore.add(translateColors("&7Left Click: +0.1"));
        damageLore.add(translateColors("&7Right Click: -0.1"));
        damageMeta.setLore(damageLore);
        damageItem.setItemMeta(damageMeta);
        gui.setItem(14, damageItem);
        
        // Back button
        ItemStack backItem = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.setDisplayName(translateColors("&cBack"));
        List<String> backLore = new ArrayList<>();
        backLore.add(translateColors("&7Return to kit editor"));
        backMeta.setLore(backLore);
        backItem.setItemMeta(backMeta);
        gui.setItem(22, backItem);
        
        player.openInventory(gui);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        if (title.contains("Kit List")) {
            event.setCancelled(true);
            handleKitListClick(player, event);
        } else if (title.contains("Editing:")) {
            handleKitEditorClick(player, event);
        } else if (title.contains("Rules:")) {
            event.setCancelled(true);
            handleRulesEditorClick(player, event);
        }
    }
    
    private void handleKitListClick(Player player, InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        
        if (clicked.getType() == Material.EMERALD) {
            // Create new kit
            player.closeInventory();
            player.sendMessage(translateColors("&eType the name of the new kit in chat:"));
            // TODO: Implement chat listener for kit name input
            return;
        }
        
        String displayName = clicked.getItemMeta().getDisplayName();
        String kitName = ChatColor.stripColor(displayName);
        
        if (event.isLeftClick()) {
            // Edit kit
            openKitEditorGui(player, kitName);
        } else if (event.isRightClick()) {
            // Delete kit
            plugin.getKitManager().removeKit(kitName);
            player.sendMessage(translateColors("&cDeleted kit: " + kitName));
            openKitListGui(player);
        }
    }
    
    private void handleKitEditorClick(Player player, InventoryClickEvent event) {
        int slot = event.getSlot();
        
        if (slot == 45) {
            // Save button
            event.setCancelled(true);
            saveKit(player);
        } else if (slot == 46) {
            // Icon slot - allow placing items
            // Don't cancel event
        } else if (slot == 47) {
            // Rules editor
            event.setCancelled(true);
            String kitName = editingKit.get(player.getUniqueId());
            if (kitName != null) {
                openRulesEditorGui(player, kitName);
            }
        } else if (slot == 53) {
            // Back button
            event.setCancelled(true);
            openKitListGui(player);
        } else if (slot >= 40 && slot <= 44) {
            // Control area - cancel clicks
            event.setCancelled(true);
        }
        // Slots 0-39 are for kit items and armor - allow normal interaction
    }
    
    private void handleRulesEditorClick(Player player, InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        
        String kitName = editingKit.get(player.getUniqueId());
        if (kitName == null) return;
        
        Kit kit = plugin.getKitManager().getKit(kitName);
        if (kit == null) return;
        
        final KitRules rules = kit.getRules();
        int slot = event.getSlot();
        
        switch (slot) {
            case 10: // Health Regen
                rules.setNaturalHealthRegen(!rules.isNaturalHealthRegen());
                break;
            case 11: // Block Break
                rules.setBlockBreak(!rules.isBlockBreak());
                break;
            case 12: // Block Place
                rules.setBlockPlace(!rules.isBlockPlace());
                break;
            case 13: // Instant TNT
                rules.setInstantTnt(!rules.isInstantTnt());
                break;
            case 14: // Damage Multiplier
                double currentMultiplier = rules.getDamageMultiplier();
                if (event.isLeftClick()) {
                    rules.setDamageMultiplier(Math.round((currentMultiplier + 0.1) * 10.0) / 10.0);
                } else if (event.isRightClick()) {
                    rules.setDamageMultiplier(Math.max(0.1, Math.round((currentMultiplier - 0.1) * 10.0) / 10.0));
                }
                break;
            case 22: // Back button
                openKitEditorGui(player, kitName);
                return;
        }
        
        // Refresh the GUI
        openRulesEditorGui(player, kitName);
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        
        Player player = (Player) event.getPlayer();
        String title = event.getView().getTitle();
        
        if (title.contains("Editing:")) {
            // Auto-save when closing kit editor
            saveKit(player);
        }
    }
    
    private void saveKit(Player player) {
        String kitName = editingKit.get(player.getUniqueId());
        if (kitName == null) return;
        
        Kit kit = plugin.getKitManager().getKit(kitName);
        if (kit == null) return;
        
        Inventory gui = player.getOpenInventory().getTopInventory();
        
        // Save items (slots 0-35)
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 36; i++) {
            ItemStack item = gui.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                items.add(item.clone());
            }
        }
        kit.setItems(items);
        
        // Save armor (slots 36-39)
        List<ItemStack> armor = new ArrayList<>();
        for (int i = 36; i < 40; i++) {
            ItemStack armorPiece = gui.getItem(i);
            if (armorPiece != null && armorPiece.getType() != Material.AIR) {
                armor.add(armorPiece.clone());
            }
        }
        kit.setArmor(armor);
        
        // Save icon (slot 46)
        ItemStack icon = gui.getItem(46);
        if (icon != null && icon.getType() != Material.AIR) {
            kit.setIcon(icon.clone());
        }
        
        // Save to file
        plugin.getKitManager().addKit(kit);
        player.sendMessage(translateColors("&aSaved kit: " + kitName));
    }
}
