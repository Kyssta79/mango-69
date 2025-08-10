package me.moiz.mangoparty.gui;

import me.moiz.mangoparty.MangoParty;
import me.moiz.mangoparty.models.Kit;
import me.moiz.mangoparty.models.QueueEntry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GuiManager implements Listener {
  private MangoParty plugin;

  public GuiManager(MangoParty plugin) {
      this.plugin = plugin;
      Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  public void openMainGui(Player player) {
      Inventory gui = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', "&6&lMangoParty"));
      
      // 1v1 Queue
      ItemStack oneVsOne = new ItemStack(Material.IRON_SWORD);
      ItemMeta oneVsOneMeta = oneVsOne.getItemMeta();
      oneVsOneMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a&l1v1 Queue"));
      List<String> oneVsOneLore = new ArrayList<>();
      oneVsOneLore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to join 1v1 queue"));
      oneVsOneMeta.setLore(oneVsOneLore);
      oneVsOne.setItemMeta(oneVsOneMeta);
      gui.setItem(10, oneVsOne);
      
      // 2v2 Queue
      ItemStack twoVsTwo = new ItemStack(Material.DIAMOND_SWORD);
      ItemMeta twoVsTwoMeta = twoVsTwo.getItemMeta();
      twoVsTwoMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b&l2v2 Queue"));
      List<String> twoVsTwoLore = new ArrayList<>();
      twoVsTwoLore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to join 2v2 queue"));
      twoVsTwoMeta.setLore(twoVsTwoLore);
      twoVsTwo.setItemMeta(twoVsTwoMeta);
      gui.setItem(12, twoVsTwo);
      
      // 3v3 Queue
      ItemStack threeVsThree = new ItemStack(Material.NETHERITE_SWORD);
      ItemMeta threeVsThreeMeta = threeVsThree.getItemMeta();
      threeVsThreeMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&l3v3 Queue"));
      List<String> threeVsThreeLore = new ArrayList<>();
      threeVsThreeLore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to join 3v3 queue"));
      threeVsThreeMeta.setLore(threeVsThreeLore);
      threeVsThree.setItemMeta(threeVsThreeMeta);
      gui.setItem(14, threeVsThree);
      
      // FFA Queue
      ItemStack ffa = new ItemStack(Material.GOLDEN_SWORD);
      ItemMeta ffaMeta = ffa.getItemMeta();
      ffaMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&lFFA Queue"));
      List<String> ffaLore = new ArrayList<>();
      ffaLore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to join FFA queue"));
      ffaMeta.setLore(ffaLore);
      ffa.setItemMeta(ffaMeta);
      gui.setItem(16, ffa);
      
      player.openInventory(gui);
  }

  public void openKitSelectionGui(Player player, String queueType) {
      String title = ChatColor.translateAlternateColorCodes('&', "&6" + queueType + " Kit Selection");
      Inventory gui = Bukkit.createInventory(null, 54, title);
      
      Map<String, Kit> kits = plugin.getKitManager().getKits();
      int slot = 0;
      
      // Get queue count for display
      int currentQueued = 0;
      int requiredPlayers = 0;
      
      switch (queueType.toLowerCase()) {
          case "1v1":
              currentQueued = plugin.getQueueManager().getQueueSize("1v1");
              requiredPlayers = 2;
              break;
          case "2v2":
              currentQueued = plugin.getQueueManager().getQueueSize("2v2");
              requiredPlayers = 4;
              break;
          case "3v3":
              currentQueued = plugin.getQueueManager().getQueueSize("3v3");
              requiredPlayers = 6;
              break;
          case "ffa":
              currentQueued = plugin.getQueueManager().getQueueSize("ffa");
              requiredPlayers = 8; // or whatever max FFA size is
              break;
      }
      
      for (Map.Entry<String, Kit> entry : kits.entrySet()) {
          if (slot >= 45) break; // Leave space for navigation
          
          String kitName = entry.getKey();
          Kit kit = entry.getValue();
          
          ItemStack kitItem = kit.getIcon() != null ? kit.getIcon().clone() : new ItemStack(Material.IRON_SWORD);
          ItemMeta meta = kitItem.getItemMeta();
          meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e" + kitName));
          
          List<String> lore = new ArrayList<>();
          lore.add("");
          lore.add(ChatColor.translateAlternateColorCodes('&', "&7Queued: &f" + currentQueued + "&7/&f" + requiredPlayers));
          lore.add("");
          lore.add(ChatColor.translateAlternateColorCodes('&', "&aClick to join queue"));
          
          meta.setLore(lore);
          kitItem.setItemMeta(meta);
          gui.setItem(slot, kitItem);
          slot++;
      }
      
      // Back button
      ItemStack backItem = new ItemStack(Material.ARROW);
      ItemMeta backMeta = backItem.getItemMeta();
      backMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cBack"));
      backItem.setItemMeta(backMeta);
      gui.setItem(49, backItem);
      
      player.openInventory(gui);
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
      if (!(event.getWhoClicked() instanceof Player)) return;
      
      Player player = (Player) event.getWhoClicked();
      String title = event.getView().getTitle();
      
      if (title.contains("MangoParty")) {
          event.setCancelled(true);
          handleMainGuiClick(player, event);
      } else if (title.contains("Kit Selection")) {
          event.setCancelled(true);
          handleKitSelectionClick(player, event, title);
      }
  }
  
  private void handleMainGuiClick(Player player, InventoryClickEvent event) {
      ItemStack clicked = event.getCurrentItem();
      if (clicked == null || clicked.getType() == Material.AIR) return;
      
      String displayName = clicked.getItemMeta().getDisplayName();
      
      if (displayName.contains("1v1")) {
          openKitSelectionGui(player, "1v1");
      } else if (displayName.contains("2v2")) {
          openKitSelectionGui(player, "2v2");
      } else if (displayName.contains("3v3")) {
          openKitSelectionGui(player, "3v3");
      } else if (displayName.contains("FFA")) {
          openKitSelectionGui(player, "FFA");
      }
  }
  
  private void handleKitSelectionClick(Player player, InventoryClickEvent event, String title) {
      ItemStack clicked = event.getCurrentItem();
      if (clicked == null || clicked.getType() == Material.AIR) return;
      
      String displayName = clicked.getItemMeta().getDisplayName();
      
      if (displayName.contains("Back")) {
          openMainGui(player);
          return;
      }
      
      // Extract queue type from title
      String queueType = title.split(" ")[0].toLowerCase();
      String kitName = ChatColor.stripColor(displayName);
      
      // Join queue with selected kit
      plugin.getQueueManager().joinQueue(player, queueType, kitName);
      player.closeInventory();
  }
}
