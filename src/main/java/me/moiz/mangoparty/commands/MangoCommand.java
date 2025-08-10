package me.moiz.mangoparty.commands;

import me.moiz.mangoparty.MangoParty;
import me.moiz.mangoparty.models.Arena;
import me.moiz.mangoparty.models.Kit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MangoCommand implements CommandExecutor {
    private MangoParty plugin;

    public MangoCommand(MangoParty plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis command can only be used by players!"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "gui":
                plugin.getGuiManager().openMainGui(player);
                break;
            case "arena":
                handleArenaCommand(player, args);
                break;
            case "kit":
                handleKitCommand(player, args);
                break;
            case "reload":
                if (!player.hasPermission("mangoparty.admin")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission to use this command!"));
                    return true;
                }
                handleReloadCommand(player);
                break;
            case "debug":
                if (!player.hasPermission("mangoparty.admin")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission to use this command!"));
                    return true;
                }
                handleDebugCommand(player, args);
                break;
            default:
                sendHelpMessage(player);
                break;
        }

        return true;
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l=== MangoParty Commands ==="));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/mango gui &7- Open the main GUI"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/mango arena editor &7- Open arena editor"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/mango arena list &7- List all arenas"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/mango kit editor &7- Open kit editor"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/mango kit list &7- List all kits"));
        if (player.hasPermission("mangoparty.admin")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/mango reload &7- Reload plugin"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/mango debug &7- Toggle debug mode"));
        }
    }

    private void handleArenaCommand(Player player, String[] args) {
        if (!player.hasPermission("mangoparty.admin")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission to use this command!"));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /mango arena <editor|list|create|delete>"));
            return;
        }

        switch (args[1].toLowerCase()) {
            case "editor":
                plugin.getArenaEditorGui().openArenaListGui(player);
                break;
            case "list":
                listArenas(player);
                break;
            case "create":
                if (args.length < 3) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /mango arena create <name>"));
                    return;
                }
                createArena(player, args[2]);
                break;
            case "delete":
                if (args.length < 3) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /mango arena delete <name>"));
                    return;
                }
                deleteArena(player, args[2]);
                break;
            default:
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /mango arena <editor|list|create|delete>"));
                break;
        }
    }

    private void handleKitCommand(Player player, String[] args) {
        if (!player.hasPermission("mangoparty.admin")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission to use this command!"));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /mango kit <editor|list|give>"));
            return;
        }

        switch (args[1].toLowerCase()) {
            case "editor":
                plugin.getKitEditorGui().openKitListGui(player);
                break;
            case "list":
                listKits(player);
                break;
            case "give":
                if (args.length < 3) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /mango kit give <kitName>"));
                    return;
                }
                giveKit(player, args[2]);
                break;
            default:
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /mango kit <editor|list|give>"));
                break;
        }
    }

    private void handleReloadCommand(Player player) {
        try {
            plugin.reloadConfig();
            plugin.getArenaManager().saveArenas();
            plugin.getKitManager().saveKits();
            plugin.getArenaEditorGui().reloadConfigs();
            plugin.getKitEditorGui().reloadConfigs();
            
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aPlugin reloaded successfully!"));
            plugin.getLogger().info("Plugin reloaded by " + player.getName());
        } catch (Exception e) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cFailed to reload plugin: " + e.getMessage()));
            plugin.getLogger().severe("Failed to reload plugin: " + e.getMessage());
        }
    }

    private void handleDebugCommand(Player player, String[] args) {
        boolean currentDebug = plugin.getConfig().getBoolean("debug", false);
        boolean newDebug = !currentDebug;
        
        plugin.getConfig().set("debug", newDebug);
        plugin.saveConfig();
        
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eDebug mode " + (newDebug ? "&aenabled" : "&cdisabled")));
        plugin.getLogger().info("Debug mode " + (newDebug ? "enabled" : "disabled") + " by " + player.getName());
    }

    private void listArenas(Player player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l=== Arenas ==="));
        
        if (plugin.getArenaManager().getAllArenas().isEmpty()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7No arenas found."));
            return;
        }
        
        for (Arena arena : plugin.getArenaManager().getAllArenas()) {
            String status = arena.isComplete() ? "&aComplete" : "&cIncomplete";
            String reserved = plugin.getArenaManager().isArenaReserved(arena.getName()) ? "&c[RESERVED]" : "";
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e" + arena.getName() + " &7- " + status + " " + reserved));
        }
    }

    private void listKits(Player player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l=== Kits ==="));
        
        if (plugin.getKitManager().getKits().isEmpty()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7No kits found."));
            return;
        }
        
        for (Kit kit : plugin.getKitManager().getKits().values()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e" + kit.getName() + " &7- " + kit.getDisplayName()));
        }
    }

    private void createArena(Player player, String name) {
        Arena arena = plugin.getArenaManager().createArena(name, player.getWorld().getName());
        if (arena != null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aCreated arena: " + name));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cArena already exists: " + name));
        }
    }

    private void deleteArena(Player player, String name) {
        Arena arena = plugin.getArenaManager().getArena(name);
        if (arena == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cArena not found: " + name));
            return;
        }
        
        plugin.getArenaManager().deleteArena(name);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cDeleted arena: " + name));
    }

    private void giveKit(Player player, String kitName) {
        Kit kit = plugin.getKitManager().getKit(kitName);
        if (kit == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cKit not found: " + kitName));
            return;
        }
        
        plugin.getKitManager().giveKit(player, kitName);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aGiven kit: " + kitName));
    }
}
