package me.moiz.mangoparty.gui;

import me.moiz.mangoparty.MangoParty;
import me.moiz.mangoparty.models.Kit;
import me.moiz.mangoparty.models.KitRules;
import me.moiz.mangoparty.utils.HexUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class KitEditorGui implements Listener {
    private MangoParty plugin;
    private Map<UUID, String> awaitingInput = new HashMap<>();
    private Map<UUID, Kit> editingKit = new HashMap<>();

    public KitEditorGui(MangoParty plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openKitListGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, "§6Kit Manager");
        
        // Add create new kit button
        ItemStack createButton = new ItemStack(Material.EMERALD);
        ItemMeta createMeta = createButton.getItemMeta();
        createMeta.setDisplayName("§a§lCreate New Kit");
        List<String> createLore = new ArrayList<>();
        createLore.add("§7Click to create a new kit");
        createMeta.setLore(createLore);
        createButton.setItemMeta(createMeta);
        gui.setItem(0, createButton);
        
        // Add existing kits
        int slot = 9; // Start from second row
        Map<String, Kit> kits = plugin.getKitManager().getKits();
        
        for (Map.Entry<String, Kit> entry : kits.entrySet()) {
            if (slot >= 54) break;
            
            String kitName = entry.getKey();
            Kit kit = entry.getValue();
            
            ItemStack kitItem = kit.getIcon() != null ? kit.getIcon().clone() : new ItemStack(Material.IRON_SWORD);
            ItemMeta meta = kitItem.getItemMeta();
            meta.setDisplayName("§e" + kitName);
            
            List<String> lore = new ArrayList<>();
            lore.add("§7Items: §f" + kit.getItems().size());
            lore.add("§7Armor: §f" + kit.getArmor().size());
            lore.add("");
            lore.add("§aLeft-click to edit");
            lore.add("§cRight-click to delete");
            
            meta.setLore(lore);
            kitItem.setItemMeta(meta);
            gui.setItem(slot, kitItem);
            slot++;
        }
        
        player.openInventory(gui);
    }

    public void openKitEditorGui(Player player, String kitName) {
        Kit kit = plugin.getKitManager().getKit(kitName);
        if (kit == null) {
            player.sendMessage("§cKit not found!");
            return;
        }
        
        openKitEditor(player, kit);
    }

    private void openKitEditor(Player player, Kit kit) {
        Inventory inventory = Bukkit.createInventory(null, 27, "Kit Editor: " + kit.getName());
        
        // Kit rules (slot 10)
        ItemStack rulesItem = createKitRulesItem(kit);
        inventory.setItem(10, rulesItem);
        
        // Save kit (slot 13)
        ItemStack saveItem = new ItemStack(Material.EMERALD);
        ItemMeta saveMeta = saveItem.getItemMeta();
        saveMeta.setDisplayName("§aSave Kit");
        List<String> saveLore = new ArrayList<>();
        saveLore.add("§7Click to save changes");
        saveMeta.setLore(saveLore);
        saveItem.setItemMeta(saveMeta);
        inventory.setItem(13, saveItem);
        
        // Back button (slot 16)
        ItemStack backItem = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.setDisplayName("§cBack");
        List<String> backLore = new ArrayList<>();
        backLore.add("§7Return to kit list");
        backMeta.setLore(backLore);
        backItem.setItemMeta(backMeta);
        inventory.setItem(16, backItem);
        
        player.openInventory(inventory);
    }

    private ItemStack createKitRulesItem(Kit kit) {
        KitRules rules = kit.getRules();
        
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Kit Rules");
        
        List<String> lore = new ArrayList<>();
        lore.add("§7Health Regen: " + (rules.isNaturalHealthRegen() ? "§aEnabled" : "§cDisabled"));
        lore.add("§7Block Breaking: " + (rules.isBlockBreak() ? "§aEnabled" : "§cDisabled"));
        lore.add("§7Block Placing: " + (rules.isBlockPlace() ? "§aEnabled" : "§cDisabled"));
        lore.add("§7Damage Multiplier: §f" + rules.getDamageMultiplier());
        lore.add("§7Instant TNT: " + (rules.isInstantTnt() ? "§aEnabled" : "§cDisabled"));
        lore.add("");
        lore.add("§7Click to edit rules");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        
        String title = event.getView().getTitle();
        
        // Handle kit list GUI
        if (title.equals("§6Kit Manager")) {
            event.setCancelled(true);
            handleKitListClick(player, event);
            return;
        }
        
        // Handle kit editor GUI
        if (title.startsWith("Kit Editor:")) {
            event.setCancelled(true);
            handleKitEditorClick(player, event);
            return;
        }
        
        // Handle kit rules GUI
        if (title.startsWith("Kit Rules:")) {
            event.setCancelled(true);
            handleKitRulesClick(player, event);
            return;
        }
    }

    private void handleKitListClick(Player player, InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        
        if (clicked.getType() == Material.EMERALD) {
            // Create new kit
            player.closeInventory();
            player.sendMessage("§aType the name of the new kit in chat:");
            awaitingInput.put(player.getUniqueId(), "create_kit");
            return;
        }
        
        String kitName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
        
        if (event.isLeftClick()) {
            // Edit kit
            openKitEditorGui(player, kitName);
        } else if (event.isRightClick()) {
            // Delete kit
            plugin.getKitManager().deleteKit(kitName);
            player.sendMessage("§cDeleted kit: " + kitName);
            openKitListGui(player); // Refresh
        }
    }

    private void handleKitEditorClick(Player player, InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        
        String displayName = clicked.getItemMeta().getDisplayName();
        String kitName = getKitNameFromTitle(event.getView().getTitle());
        Kit kit = plugin.getKitManager().getKit(kitName);
        
        if (kit == null) {
            player.sendMessage("§cKit not found!");
            player.closeInventory();
            return;
        }
        
        if (displayName.contains("Kit Rules")) {
            // Open rules editor
            openKitRulesEditor(player, kit);
        } else if (displayName.contains("Save Kit")) {
            // Save kit
            plugin.getKitManager().saveKit(kit);
            player.sendMessage("§aKit saved successfully!");
        } else if (displayName.contains("Back")) {
            // Back to kit list
            openKitListGui(player);
        }
    }

    private void openKitRulesEditor(Player player, Kit kit) {
        Inventory inventory = Bukkit.createInventory(null, 27, "Kit Rules: " + kit.getName());
        
        KitRules rules = kit.getRules();
        
        // Health regeneration (slot 10)
        ItemStack healthRegenItem = new ItemStack(rules.isNaturalHealthRegen() ? Material.GOLDEN_APPLE : Material.ROTTEN_FLESH);
        ItemMeta healthMeta = healthRegenItem.getItemMeta();
        healthMeta.setDisplayName("§6Natural Health Regeneration");
        List<String> healthLore = new ArrayList<>();
        healthLore.add("§7Status: " + (rules.isNaturalHealthRegen() ? "§aEnabled" : "§cDisabled"));
        healthLore.add("");
        healthLore.add("§7Click to toggle");
        healthMeta.setLore(healthLore);
        healthRegenItem.setItemMeta(healthMeta);
        inventory.setItem(10, healthRegenItem);
        
        // Block breaking (slot 12)
        ItemStack blockBreakItem = new ItemStack(rules.isBlockBreak() ? Material.DIAMOND_PICKAXE : Material.BARRIER);
        ItemMeta breakMeta = blockBreakItem.getItemMeta();
        breakMeta.setDisplayName("§6Block Breaking");
        List<String> breakLore = new ArrayList<>();
        breakLore.add("§7Status: " + (rules.isBlockBreak() ? "§aEnabled" : "§cDisabled"));
        breakLore.add("");
        breakLore.add("§7Click to toggle");
        breakMeta.setLore(breakLore);
        blockBreakItem.setItemMeta(breakMeta);
        inventory.setItem(12, blockBreakItem);
        
        // Block placing (slot 14)
        ItemStack blockPlaceItem = new ItemStack(rules.isBlockPlace() ? Material.GRASS_BLOCK : Material.BARRIER);
        ItemMeta placeMeta = blockPlaceItem.getItemMeta();
        placeMeta.setDisplayName("§6Block Placing");
        List<String> placeLore = new ArrayList<>();
        placeLore.add("§7Status: " + (rules.isBlockPlace() ? "§aEnabled" : "§cDisabled"));
        placeLore.add("");
        placeLore.add("§7Click to toggle");
        placeMeta.setLore(placeLore);
        blockPlaceItem.setItemMeta(placeMeta);
        inventory.setItem(14, blockPlaceItem);
        
        // Damage multiplier (slot 16)
        ItemStack damageMultItem = new ItemStack(Material.IRON_SWORD);
        ItemMeta damageMeta = damageMultItem.getItemMeta();
        damageMeta.setDisplayName("§6Damage Multiplier");
        List<String> damageLore = new ArrayList<>();
        damageLore.add("§7Current: §f" + rules.getDamageMultiplier());
        damageLore.add("");
        damageLore.add("§7Click to change");
        damageMeta.setLore(damageLore);
        damageMultItem.setItemMeta(damageMeta);
        inventory.setItem(16, damageMultItem);
        
        // Back button (slot 22)
        ItemStack backItem = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.setDisplayName("§cBack");
        List<String> backLore = new ArrayList<>();
        backLore.add("§7Return to kit editor");
        backMeta.setLore(backLore);
        backItem.setItemMeta(backMeta);
        inventory.setItem(22, backItem);
        
        player.openInventory(inventory);
    }

    private void handleKitRulesClick(Player player, InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        
        String displayName = clicked.getItemMeta().getDisplayName();
        String kitName = getKitNameFromTitle(event.getView().getTitle());
        Kit kit = plugin.getKitManager().getKit(kitName);
        
        if (kit == null) {
            player.sendMessage("§cKit not found!");
            player.closeInventory();
            return;
        }
        
        KitRules rules = kit.getRules();
        
        if (displayName.contains("Natural Health Regeneration")) {
            rules.setNaturalHealthRegen(!rules.isNaturalHealthRegen());
            player.sendMessage("§aHealth regeneration " + 
                (rules.isNaturalHealthRegen() ? "enabled" : "disabled"));
            openKitRulesEditor(player, kit);
        } else if (displayName.contains("Block Breaking")) {
            rules.setBlockBreak(!rules.isBlockBreak());
            player.sendMessage("§aBlock breaking " + 
                (rules.isBlockBreak() ? "enabled" : "disabled"));
            openKitRulesEditor(player, kit);
        } else if (displayName.contains("Block Placing")) {
            rules.setBlockPlace(!rules.isBlockPlace());
            player.sendMessage("§aBlock placing " + 
                (rules.isBlockPlace() ? "enabled" : "disabled"));
            openKitRulesEditor(player, kit);
        } else if (displayName.contains("Damage Multiplier")) {
            player.closeInventory();
            player.sendMessage("§aEnter the new damage multiplier (e.g., 1.0, 1.5, 2.0):");
            awaitingInput.put(player.getUniqueId(), "damage_multiplier");
            editingKit.put(player.getUniqueId(), kit);
        } else if (displayName.contains("Back")) {
            openKitEditor(player, kit);
        }
        
        // Save the kit after any changes
        plugin.getKitManager().saveKit(kit);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        
        if (!awaitingInput.containsKey(playerId)) return;
        
        event.setCancelled(true);
        String input = event.getMessage();
        String inputType = awaitingInput.remove(playerId);
        
        Bukkit.getScheduler().runTask(plugin, () -> {
            switch (inputType) {
                case "create_kit":
                    if (plugin.getKitManager().getKit(input) != null) {
                        player.sendMessage("§cA kit with that name already exists!");
                        return;
                    }
                    
                    plugin.getKitManager().createKit(input, player);
                    player.sendMessage("§aCreated new kit: " + input);
                    openKitEditorGui(player, input);
                    break;
                    
                case "damage_multiplier":
                    Kit kit = editingKit.remove(playerId);
                    if (kit == null) {
                        player.sendMessage("§cError: Kit not found!");
                        return;
                    }
                    
                    try {
                        double multiplier = Double.parseDouble(input);
                        if (multiplier < 0.1 || multiplier > 10.0) {
                            player.sendMessage("§cDamage multiplier must be between 0.1 and 10.0!");
                            return;
                        }
                        
                        kit.getRules().setDamageMultiplier(multiplier);
                        plugin.getKitManager().saveKit(kit);
                        player.sendMessage("§aDamage multiplier set to: " + multiplier);
                        openKitRulesEditor(player, kit);
                    } catch (NumberFormatException e) {
                        player.sendMessage("§cInvalid number! Please enter a decimal number.");
                    }
                    break;
            }
        });
    }

    private String getKitNameFromTitle(String title) {
        // Extract kit name from title
        if (title.startsWith("Kit Editor: ")) {
            return title.substring("Kit Editor: ".length());
        }
        
        // For rules editor
        if (title.startsWith("Kit Rules: ")) {
            return title.substring("Kit Rules: ".length());
        }
        
        return "";
    }

    public void reloadConfigs() {
        plugin.getLogger().info("Kit editor configs reloaded.");
    }
}
