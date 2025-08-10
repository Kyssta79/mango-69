package me.moiz.mangoparty.gui;

import me.moiz.mangoparty.MangoParty;
import me.moiz.mangoparty.models.Kit;
import me.moiz.mangoparty.models.KitRules;
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
    private Map<UUID, String> editingKit = new HashMap<>();
    private Map<UUID, String> awaitingInput = new HashMap<>();
    
    public KitEditorGui(MangoParty plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    // Helper method to translate color codes
    private String translateColors(String text) {
        if (text == null) return null;
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    
    // Helper method to translate color codes in a list
    private List<String> translateColors(List<String> list) {
        if (list == null) return null;
        List<String> translated = new ArrayList<>();
        for (String line : list) {
            translated.add(translateColors(line));
        }
        return translated;
    }
    
    public void openKitListGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, translateColors("&6Kit Manager"));
        
        // Create New Kit button
        ItemStack createKit = new ItemStack(Material.EMERALD);
        ItemMeta createMeta = createKit.getItemMeta();
        createMeta.setDisplayName(translateColors("&aCreate New Kit"));
        List<String> createLore = new ArrayList<>();
        createLore.add(translateColors("&7Click to create a new kit"));
        createLore.add(translateColors("&7from your current inventory"));
        createMeta.setLore(createLore);
        createKit.setItemMeta(createMeta);
        gui.setItem(49, createKit);
        
        // Add existing kits
        int slot = 0;
        for (Kit kit : plugin.getKitManager().getKits().values()) {
            if (slot >= 45) break; // Leave space for create button
            
            ItemStack kitItem = kit.getIcon() != null ? kit.getIcon().clone() : new ItemStack(Material.IRON_SWORD);
            ItemMeta meta = kitItem.getItemMeta();
            meta.setDisplayName(translateColors("&e" + kit.getDisplayName()));
            
            List<String> lore = new ArrayList<>();
            lore.add(translateColors("&7Click to edit this kit"));
            lore.add("");
            lore.add(translateColors("&7Rules:"));
            KitRules rules = kit.getRules();
            lore.add(translateColors("&8• Health Regen: " + (rules.isNaturalHealthRegen() ? "&aEnabled" : "&cDisabled")));
            lore.add(translateColors("&8• Block Breaking: " + (rules.isBlockBreaking() ? "&aEnabled" : "&cDisabled")));
            lore.add(translateColors("&8• Block Placing: " + (rules.isBlockPlacing() ? "&aEnabled" : "&cDisabled")));
            lore.add(translateColors("&8• Damage Multiplier: &f" + rules.getDamageMultiplier()));
            
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
            player.sendMessage(translateColors("&cKit not found!"));
            return;
        }
        
        editingKit.put(player.getUniqueId(), kitName);
        openKitEditor(player, kit);
    }
    
    private void openKitEditor(Player player, Kit kit) {
        Inventory gui = Bukkit.createInventory(null, 27, translateColors("&6Editing: " + kit.getDisplayName()));
        
        // Kit Icon button
        ItemStack iconItem = new ItemStack(Material.ITEM_FRAME);
        ItemMeta iconMeta = iconItem.getItemMeta();
        iconMeta.setDisplayName(translateColors("&eKit Icon"));
        List<String> iconLore = new ArrayList<>();
        iconLore.add(translateColors("&7Hold an item in your main hand"));
        iconLore.add(translateColors("&7and click to set as kit icon"));
        iconMeta.setLore(iconLore);
        iconItem.setItemMeta(iconMeta);
        gui.setItem(10, iconItem);
        
        // Kit Rules button
        ItemStack rulesItem = new ItemStack(Material.BOOK);
        ItemMeta rulesMeta = rulesItem.getItemMeta();
        rulesMeta.setDisplayName(translateColors("&bKit Rules"));
        List<String> rulesLore = new ArrayList<>();
        rulesLore.add(translateColors("&7Click to edit kit rules"));
        rulesLore.add(translateColors("&7such as health regen, blocks, etc."));
        rulesMeta.setLore(rulesLore);
        rulesItem.setItemMeta(rulesMeta);
        gui.setItem(12, rulesItem);
        
        // Save button
        ItemStack saveItem = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta saveMeta = saveItem.getItemMeta();
        saveMeta.setDisplayName(translateColors("&aSave Kit"));
        List<String> saveLore = new ArrayList<>();
        saveLore.add(translateColors("&7Click to save all changes"));
        saveMeta.setLore(saveLore);
        saveItem.setItemMeta(saveMeta);
        gui.setItem(14, saveItem);
        
        // Back button
        ItemStack backItem = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.setDisplayName(translateColors("&cBack"));
        List<String> backLore = new ArrayList<>();
        backLore.add(translateColors("&7Return to kit list"));
        backMeta.setLore(backLore);
        backItem.setItemMeta(backMeta);
        gui.setItem(16, backItem);
        
        player.openInventory(gui);
    }
    
    public void openKitRulesGui(Player player, String kitName) {
        Kit kit = plugin.getKitManager().getKit(kitName);
        if (kit == null) {
            player.sendMessage(translateColors("&cKit not found!"));
            return;
        }
        
        Inventory gui = Bukkit.createInventory(null, 27, translateColors("&6Rules: " + kit.getDisplayName()));
        KitRules rules = kit.getRules();
        
        // Health Regen toggle
        ItemStack healthRegen = new ItemStack(rules.isNaturalHealthRegen() ? Material.GOLDEN_APPLE : Material.ROTTEN_FLESH);
        ItemMeta healthMeta = healthRegen.getItemMeta();
        healthMeta.setDisplayName(translateColors("&cNatural Health Regeneration"));
        List<String> healthLore = new ArrayList<>();
        healthLore.add(translateColors("&7Status: " + (rules.isNaturalHealthRegen() ? "&aEnabled" : "&cDisabled")));
        healthLore.add(translateColors("&7Click to toggle"));
        healthMeta.setLore(healthLore);
        healthRegen.setItemMeta(healthMeta);
        gui.setItem(10, healthRegen);
        
        // Block Breaking toggle
        ItemStack blockBreaking = new ItemStack(rules.isBlockBreaking() ? Material.DIAMOND_PICKAXE : Material.BARRIER);
        ItemMeta breakMeta = blockBreaking.getItemMeta();
        breakMeta.setDisplayName(translateColors("&6Block Breaking"));
        List<String> breakLore = new ArrayList<>();
        breakLore.add(translateColors("&7Status: " + (rules.isBlockBreaking() ? "&aEnabled" : "&cDisabled")));
        breakLore.add(translateColors("&7Click to toggle"));
        breakMeta.setLore(breakLore);
        blockBreaking.setItemMeta(breakMeta);
        gui.setItem(12, blockBreaking);
        
        // Block Placing toggle
        ItemStack blockPlacing = new ItemStack(rules.isBlockPlacing() ? Material.GRASS_BLOCK : Material.BARRIER);
        ItemMeta placeMeta = blockPlacing.getItemMeta();
        placeMeta.setDisplayName(translateColors("&aBlock Placing"));
        List<String> placeLore = new ArrayList<>();
        placeLore.add(translateColors("&7Status: " + (rules.isBlockPlacing() ? "&aEnabled" : "&cDisabled")));
        placeLore.add(translateColors("&7Click to toggle"));
        placeMeta.setLore(placeLore);
        blockPlacing.setItemMeta(placeMeta);
        gui.setItem(14, blockPlacing);
        
        // Damage Multiplier
        ItemStack damageMultiplier = new ItemStack(Material.IRON_SWORD);
        ItemMeta damageMeta = damageMultiplier.getItemMeta();
        damageMeta.setDisplayName(translateColors("&4Damage Multiplier"));
        List<String> damageLore = new ArrayList<>();
        damageLore.add(translateColors("&7Current: &f" + rules.getDamageMultiplier()));
        damageLore.add(translateColors("&7Click to change"));
        damageMeta.setLore(damageLore);
        damageMultiplier.setItemMeta(damageMeta);
        gui.setItem(16, damageMultiplier);
        
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
        
        if (title.equals(translateColors("&6Kit Manager"))) {
            event.setCancelled(true);
            handleKitListClick(player, event);
        } else if (title.startsWith(translateColors("&6Editing:"))) {
            event.setCancelled(true);
            handleKitEditorClick(player, event);
        } else if (title.startsWith(translateColors("&6Rules:"))) {
            event.setCancelled(true);
            handleKitRulesClick(player, event);
        }
    }
    
    private void handleKitListClick(Player player, InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        
        if (clicked.getType() == Material.EMERALD) {
            // Create new kit
            createKitFromInventory(player);
        } else {
            // Edit existing kit
            ItemMeta meta = clicked.getItemMeta();
            if (meta != null && meta.getDisplayName() != null) {
                String kitName = ChatColor.stripColor(meta.getDisplayName());
                openKitEditorGui(player, kitName);
            }
        }
    }
    
    private void handleKitEditorClick(Player player, InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        
        String kitName = editingKit.get(player.getUniqueId());
        if (kitName == null) return;
        
        if (clicked.getType() == Material.ITEM_FRAME) {
            // Set kit icon
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            if (mainHand != null && mainHand.getType() != Material.AIR) {
                Kit kit = plugin.getKitManager().getKit(kitName);
                if (kit != null) {
                    kit.setIcon(mainHand.clone());
                    plugin.getKitManager().saveKit(kit);
                    player.sendMessage(translateColors("&aKit icon updated!"));
                    openKitEditor(player, kit);
                }
            } else {
                player.sendMessage(translateColors("&cHold an item in your main hand first!"));
            }
        } else if (clicked.getType() == Material.BOOK) {
            // Edit kit rules
            openKitRulesGui(player, kitName);
        } else if (clicked.getType() == Material.EMERALD_BLOCK) {
            // Save kit
            Kit kit = plugin.getKitManager().getKit(kitName);
            if (kit != null) {
                plugin.getKitManager().saveKit(kit);
                player.sendMessage(translateColors("&aKit saved successfully!"));
            }
        } else if (clicked.getType() == Material.ARROW) {
            // Back to kit list
            editingKit.remove(player.getUniqueId());
            openKitListGui(player);
        }
    }
    
    private void handleKitRulesClick(Player player, InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        
        String kitName = editingKit.get(player.getUniqueId());
        if (kitName == null) return;
        
        Kit kit = plugin.getKitManager().getKit(kitName);
        if (kit == null) return;
        
        KitRules rules = kit.getRules();
        
        if (clicked.getType() == Material.GOLDEN_APPLE || clicked.getType() == Material.ROTTEN_FLESH) {
            // Toggle health regen
            rules.setNaturalHealthRegen(!rules.isNaturalHealthRegen());
            plugin.getKitManager().saveKit(kit);
            openKitRulesGui(player, kitName);
        } else if (clicked.getType() == Material.DIAMOND_PICKAXE || clicked.getType() == Material.BARRIER) {
            // Toggle block breaking
            if (event.getSlot() == 12) {
                rules.setBlockBreaking(!rules.isBlockBreaking());
                plugin.getKitManager().saveKit(kit);
                openKitRulesGui(player, kitName);
            } else if (event.getSlot() == 14) {
                // Toggle block placing
                rules.setBlockPlacing(!rules.isBlockPlacing());
                plugin.getKitManager().saveKit(kit);
                openKitRulesGui(player, kitName);
            }
        } else if (clicked.getType() == Material.GRASS_BLOCK) {
            // Toggle block placing
            rules.setBlockPlacing(!rules.isBlockPlacing());
            plugin.getKitManager().saveKit(kit);
            openKitRulesGui(player, kitName);
        } else if (clicked.getType() == Material.IRON_SWORD) {
            // Change damage multiplier
            player.closeInventory();
            player.sendMessage(translateColors("&eEnter new damage multiplier (e.g., 1.0, 1.5, 2.0):"));
            awaitingInput.put(player.getUniqueId(), "damage_multiplier:" + kitName);
        } else if (clicked.getType() == Material.ARROW) {
            // Back to kit editor
            Kit editKit = plugin.getKitManager().getKit(kitName);
            if (editKit != null) {
                openKitEditor(player, editKit);
            }
        }
    }
    
    private void createKitFromInventory(Player player) {
        player.closeInventory();
        player.sendMessage(translateColors("&eEnter a name for the new kit:"));
        awaitingInput.put(player.getUniqueId(), "create_kit");
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String input = awaitingInput.get(player.getUniqueId());
        
        if (input != null) {
            event.setCancelled(true);
            awaitingInput.remove(player.getUniqueId());
            
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (input.equals("create_kit")) {
                    String kitName = event.getMessage().trim();
                    if (plugin.getKitManager().getKit(kitName) != null) {
                        player.sendMessage(translateColors("&cA kit with that name already exists!"));
                        return;
                    }
                    
                    // Create kit from player's inventory
                    ItemStack[] armor = player.getInventory().getArmorContents();
                    ItemStack[] items = player.getInventory().getContents();
                    
                    Kit newKit = new Kit(kitName, kitName, items, armor);
                    
                    // Set icon to first non-null item or default sword
                    ItemStack icon = null;
                    for (ItemStack item : items) {
                        if (item != null && item.getType() != Material.AIR) {
                            icon = item.clone();
                            icon.setAmount(1);
                            break;
                        }
                    }
                    if (icon == null) {
                        icon = new ItemStack(Material.IRON_SWORD);
                    }
                    newKit.setIcon(icon);
                    
                    plugin.getKitManager().addKit(newKit);
                    plugin.getKitManager().saveKit(newKit);
                    
                    player.sendMessage(translateColors("&aKit '" + kitName + "' created successfully!"));
                    openKitListGui(player);
                    
                } else if (input.startsWith("damage_multiplier:")) {
                    String kitName = input.substring("damage_multiplier:".length());
                    try {
                        double multiplier = Double.parseDouble(event.getMessage().trim());
                        if (multiplier <= 0) {
                            player.sendMessage(translateColors("&cDamage multiplier must be greater than 0!"));
                            return;
                        }
                        
                        Kit kit = plugin.getKitManager().getKit(kitName);
                        if (kit != null) {
                            kit.getRules().setDamageMultiplier(multiplier);
                            plugin.getKitManager().saveKit(kit);
                            player.sendMessage(translateColors("&aDamage multiplier set to " + multiplier));
                            openKitRulesGui(player, kitName);
                        }
                    } catch (NumberFormatException e) {
                        player.sendMessage(translateColors("&cInvalid number! Please enter a valid decimal number."));
                    }
                }
            });
        }
    }
    
    // Add this method for compatibility with MangoCommand
    public void reloadConfigs() {
        plugin.getLogger().info("Kit editor configs reloaded.");
    }
}
