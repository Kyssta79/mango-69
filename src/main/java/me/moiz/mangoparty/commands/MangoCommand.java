package me.moiz.mangoparty.commands;

import me.moiz.mangoparty.MangoParty;
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
            // Open main GUI
            plugin.getGuiManager().openMainGui(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "gui":
                plugin.getGuiManager().openMainGui(player);
                break;
            case "kits":
                if (player.hasPermission("mangoparty.admin")) {
                    plugin.getKitEditorGui().openKitListGui(player);
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission to use this command!"));
                }
                break;
            case "arenas":
                if (player.hasPermission("mangoparty.admin")) {
                    plugin.getArenaEditorGui().openArenaListGui(player);
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission to use this command!"));
                }
                break;
            case "reload":
                if (player.hasPermission("mangoparty.admin")) {
                    plugin.getConfigManager().reloadConfigs();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aConfigs reloaded!"));
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission to use this command!"));
                }
                break;
            default:
                sendHelpMessage(player);
                break;
        }
        
        return true;
    }
    
    private void sendHelpMessage(Player player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6=== MangoParty Commands ==="));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/mango gui &7- Open the main GUI"));
        if (player.hasPermission("mangoparty.admin")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/mango kits &7- Manage kits"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/mango arenas &7- Manage arenas"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/mango reload &7- Reload configs"));
        }
    }
}
