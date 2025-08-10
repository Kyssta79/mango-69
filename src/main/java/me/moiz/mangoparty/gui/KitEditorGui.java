package me.moiz.mangoparty.gui;

import me.moiz.mangoparty.MangoParty;
import me.moiz.mangoparty.models.Kit;
import me.moiz.mangoparty.models.KitRules;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KitEditorGui implements Listener {
    private MangoParty plugin;

    public KitEditorGui(MangoParty plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openKitListGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', "&6Kit Manager"));
        
        // Add create new kit button
        ItemStack createButton = new ItemStack(Material.EMERALD);
        ItemMeta createMeta = createButton.getItemMeta();
        createMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a&lCreate New Kit"));
        List<String> createLore = new ArrayList<>();
        createLore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to create a new kit"));
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
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e" + kitName));
            
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.translateAlternateColorCodes('&', "&aLeft-click to edit"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&cRight-click to delete"));
            
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
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cKit not found!"));
            return;
        }
        
        KitEditorInstance instance = new KitEditorInstance(plugin, player, kit);
        instance.open();
    }

    public void openCreateKitGui(Player player) {
        KitEditorInstance instance = new KitEditorInstance(plugin, player, null);
        instance.open();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        if (title.equals(ChatColor.translateAlternateColorCodes('&', "&6Kit Manager"))) {
            event.setCancelled(true);
            handleKitListClick(player, event);
        }
    }
    
    private void handleKitListClick(Player player, InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        
        if (clicked.getType() == Material.EMERALD) {
            // Create new kit
            openCreateKitGui(player);
            return;
        }
        
        String kitName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
        
        if (event.isLeftClick()) {
            // Edit kit
            openKitEditorGui(player, kitName);
        } else if (event.isRightClick()) {
            // Delete kit
            plugin.getKitManager().deleteKit(kitName);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cDeleted kit: " + kitName));
            openKitListGui(player); // Refresh the GUI
        }
    }

    public void reloadConfigs() {
        plugin.getLogger().info("Kit editor configs reloaded.");
    }

    // Inner class for individual kit editor instances
    private static class KitEditorInstance implements Listener {
        private MangoParty plugin;
        private Player player;
        private Kit kit;
        private Inventory inventory;
        private boolean isNewKit;
        private String tempKitName;

        public KitEditorInstance(MangoParty plugin, Player player, Kit kit) {
            this.plugin = plugin;
            this.player = player;
            this.kit = kit;
            this.isNewKit = (kit == null);
            this.tempKitName = isNewKit ? "NewKit" : kit.getName();
            
            createInventory();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }

        private void createInventory() {
            String title = ChatColor.translateAlternateColorCodes('&', "&6Kit Editor: " + tempKitName);
            inventory = Bukkit.createInventory(null, 54, title);
            updateInventory();
        }

        private void updateInventory() {
            inventory.clear();
            
            // Kit Icon button
            ItemStack iconItem = new ItemStack(Material.ITEM_FRAME);
            ItemMeta iconMeta = iconItem.getItemMeta();
            iconMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eKit Icon"));
            List<String> iconLore = new ArrayList<>();
            iconLore.add(ChatColor.translateAlternateColorCodes('&', "&7Hold an item in your main hand"));
            iconLore.add(ChatColor.translateAlternateColorCodes('&', "&7and click to set as kit icon"));
            if (kit != null && kit.getIcon() != null) {
                iconLore.add("");
                iconLore.add(ChatColor.translateAlternateColorCodes('&', "&aCurrent icon set"));
            }
            iconMeta.setLore(iconLore);
            iconItem.setItemMeta(iconMeta);
            inventory.setItem(10, iconItem);

            // Kit Rules button
            ItemStack rulesItem = new ItemStack(Material.BOOK);
            ItemMeta rulesMeta = rulesItem.getItemMeta();
            rulesMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&bKit Rules"));
            List<String> rulesLore = new ArrayList<>();
            rulesLore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to configure kit rules"));
            rulesMeta.setLore(rulesLore);
            rulesItem.setItemMeta(rulesMeta);
            inventory.setItem(12, rulesItem);

            // Save Kit button
            ItemStack saveItem = new ItemStack(Material.EMERALD);
            ItemMeta saveMeta = saveItem.getItemMeta();
            saveMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aSave Kit"));
            List<String> saveLore = new ArrayList<>();
            saveLore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to save kit from"));
            saveLore.add(ChatColor.translateAlternateColorCodes('&', "&7your current inventory"));
            saveMeta.setLore(saveLore);
            saveItem.setItemMeta(saveMeta);
            inventory.setItem(14, saveItem);

            // Back button
            ItemStack backItem = new ItemStack(Material.ARROW);
            ItemMeta backMeta = backItem.getItemMeta();
            backMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cBack"));
            backItem.setItemMeta(backMeta);
            inventory.setItem(49, backItem);
        }

        public void open() {
            player.openInventory(inventory);
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            if (!event.getInventory().equals(inventory)) return;
            if (!(event.getWhoClicked() instanceof Player)) return;
            
            event.setCancelled(true);
            Player clicker = (Player) event.getWhoClicked();
            
            if (!clicker.equals(player)) return;
            
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;
            
            String displayName = clicked.getItemMeta().getDisplayName();
            
            if (displayName.contains("Kit Icon")) {
                handleKitIconClick();
            } else if (displayName.contains("Kit Rules")) {
                handleKitRulesClick();
            } else if (displayName.contains("Save Kit")) {
                handleSaveKitClick();
            } else if (displayName.contains("Back")) {
                cleanup();
                plugin.getKitEditorGui().openKitListGui(player);
            }
        }

        private void handleKitIconClick() {
            ItemStack heldItem = player.getInventory().getItemInMainHand();
            if (heldItem == null || heldItem.getType() == Material.AIR) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou must hold an item in your main hand!"));
                return;
            }
            
            if (kit == null) {
                // Create temporary kit for new kit
                kit = new Kit(tempKitName);
            }
            
            kit.setIcon(heldItem.clone());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aKit icon set to: " + heldItem.getType().name()));
            updateInventory();
        }

        private void handleKitRulesClick() {
            if (kit == null) {
                kit = new Kit(tempKitName);
            }
            
            // Open kit rules GUI
            openKitRulesGui();
        }

        private void handleSaveKitClick() {
            // Get player's current inventory
            ItemStack[] contents = player.getInventory().getContents();
            ItemStack[] armor = player.getInventory().getArmorContents();
            
            // Filter out null items
            List<ItemStack> itemsList = new ArrayList<>();
            for (ItemStack item : contents) {
                if (item != null && item.getType() != Material.AIR) {
                    itemsList.add(item.clone());
                }
            }
            
            List<ItemStack> armorList = new ArrayList<>();
            for (ItemStack item : armor) {
                if (item != null && item.getType() != Material.AIR) {
                    armorList.add(item.clone());
                }
            }
            
            if (isNewKit) {
                // Create new kit
                Kit newKit = new Kit(tempKitName);
                newKit.setItems(itemsList.toArray(new ItemStack[0]));
                newKit.setArmor(armorList.toArray(new ItemStack[0]));
                
                if (kit != null && kit.getIcon() != null) {
                    newKit.setIcon(kit.getIcon());
                }
                if (kit != null && kit.getRules() != null) {
                    newKit.setRules(kit.getRules());
                }
                
                plugin.getKitManager().addKit(newKit);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aCreated new kit: " + tempKitName));
            } else {
                // Update existing kit
                kit.setItems(itemsList.toArray(new ItemStack[0]));
                kit.setArmor(armorList.toArray(new ItemStack[0]));
                plugin.getKitManager().saveKit(kit);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aUpdated kit: " + kit.getName()));
            }
            
            cleanup();
            plugin.getKitEditorGui().openKitListGui(player);
        }

        private void openKitRulesGui() {
            Inventory rulesGui = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', "&6Kit Rules: " + tempKitName));
            
            KitRules rules = kit.getRules();
            if (rules == null) {
                rules = new KitRules();
                kit.setRules(rules);
            }
            
            // Block Breaking toggle
            ItemStack breakingItem = new ItemStack(rules.isBlockBreaking() ? Material.LIME_DYE : Material.GRAY_DYE);
            ItemMeta breakingMeta = breakingItem.getItemMeta();
            breakingMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eBlock Breaking"));
            List<String> breakingLore = new ArrayList<>();
            breakingLore.add(ChatColor.translateAlternateColorCodes('&', "&7Status: " + (rules.isBlockBreaking() ? "&aEnabled" : "&cDisabled")));
            breakingLore.add("");
            breakingLore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to toggle"));
            breakingMeta.setLore(breakingLore);
            breakingItem.setItemMeta(breakingMeta);
            rulesGui.setItem(10, breakingItem);
            
            // Block Placing toggle
            ItemStack placingItem = new ItemStack(rules.isBlockPlacing() ? Material.LIME_DYE : Material.GRAY_DYE);
            ItemMeta placingMeta = placingItem.getItemMeta();
            placingMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eBlock Placing"));
            List<String> placingLore = new ArrayList<>();
            placingLore.add(ChatColor.translateAlternateColorCodes('&', "&7Status: " + (rules.isBlockPlacing() ? "&aEnabled" : "&cDisabled")));
            placingLore.add("");
            placingLore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to toggle"));
            placingMeta.setLore(placingLore);
            placingItem.setItemMeta(placingMeta);
            rulesGui.setItem(12, placingItem);
            
            // Back button
            ItemStack backItem = new ItemStack(Material.ARROW);
            ItemMeta backMeta = backItem.getItemMeta();
            backMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cBack to Kit Editor"));
            backItem.setItemMeta(backMeta);
            rulesGui.setItem(22, backItem);
            
            // Register temporary listener for rules GUI
            Listener rulesListener = new Listener() {
                @EventHandler
                public void onRulesClick(InventoryClickEvent e) {
                    if (!e.getInventory().equals(rulesGui)) return;
                    if (!(e.getWhoClicked() instanceof Player)) return;
                    
                    e.setCancelled(true);
                    Player clicker = (Player) e.getWhoClicked();
                    
                    if (!clicker.equals(player)) return;
                    
                    ItemStack clicked = e.getCurrentItem();
                    if (clicked == null || clicked.getType() == Material.AIR) return;
                    
                    String displayName = clicked.getItemMeta().getDisplayName();
                    
                    if (displayName.contains("Block Breaking")) {
                        rules.setBlockBreaking(!rules.isBlockBreaking());
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eBlock breaking " + (rules.isBlockBreaking() ? "&aenabled" : "&cdisabled")));
                        openKitRulesGui(); // Refresh
                    } else if (displayName.contains("Block Placing")) {
                        rules.setBlockPlacing(!rules.isBlockPlacing());
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eBlock placing " + (rules.isBlockPlacing() ? "&aenabled" : "&cdisabled")));
                        openKitRulesGui(); // Refresh
                    } else if (displayName.contains("Back to Kit Editor")) {
                        HandlerList.unregisterAll(this);
                        updateInventory();
                        open();
                    }
                }
            };
            
            plugin.getServer().getPluginManager().registerEvents(rulesListener, plugin);
            
            // Auto-unregister after 5 minutes
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                HandlerList.unregisterAll(rulesListener);
            }, 6000L);
            
            player.openInventory(rulesGui);
        }

        public void cleanup() {
            HandlerList.unregisterAll(this);
        }
    }
}
