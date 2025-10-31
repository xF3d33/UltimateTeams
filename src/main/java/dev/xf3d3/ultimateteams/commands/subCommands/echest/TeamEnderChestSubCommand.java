package dev.xf3d3.ultimateteams.commands.subCommands.echest;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamEnderChest;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TeamEnderChestSubCommand implements Listener {
    private final UltimateTeams plugin;
    private final FileConfiguration messagesConfig;
    
    // Track which player is viewing which team chest
    private static final Map<UUID, TeamChestView> activeViews = new HashMap<>();
    
    public TeamEnderChestSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
        
        // Register the listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    public void openEnderChest(@NotNull CommandSender sender, int chestNumber) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }
        
        // Check if player is in a team
        Optional<Team> teamOpt = plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId());
        if (teamOpt.isEmpty()) {
            player.sendMessage(Utils.Color(messagesConfig.getString("not-in-team")));
            return;
        }
        
        Team team = teamOpt.get();
        
        // Check if the chest exists
        if (!team.hasEnderChest(chestNumber)) {
            String message = messagesConfig.getString("team-echest-not-exist");
            if (message != null) {
                message = message.replace("%NUMBER%", String.valueOf(chestNumber));
                player.sendMessage(Utils.Color(message));
            } else {
                player.sendMessage(Utils.Color("&cTeam ender chest #" + chestNumber + " does not exist!"));
            }
            return;
        }
        
        TeamEnderChest chest = team.getEnderChest(chestNumber).get();
        
        // Create and open the inventory
        String title = "Team Chest #" + chestNumber;
        Inventory inventory = Bukkit.createInventory(null, chest.getSize(), title);
        
        // Load the contents
        ItemStack[] contents = chest.getContents();
        for (int i = 0; i < contents.length && i < inventory.getSize(); i++) {
            inventory.setItem(i, contents[i]);
        }
        
        // Track this view
        activeViews.put(player.getUniqueId(), new TeamChestView(team.getId(), chestNumber));
        
        // Open the inventory
        player.openInventory(inventory);
        
        String message = messagesConfig.getString("team-echest-opened");
        if (message != null) {
            message = message.replace("%NUMBER%", String.valueOf(chestNumber));
            player.sendMessage(Utils.Color(message));
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }
        
        UUID playerUUID = player.getUniqueId();
        TeamChestView view = activeViews.get(playerUUID);
        
        if (view == null) {
            return;
        }
        
        // Remove from tracking
        activeViews.remove(playerUUID);
        
        // Find the team
        Optional<Team> teamOpt = plugin.getTeamStorageUtil().findTeam(view.teamId);
        if (teamOpt.isEmpty()) {
            return;
        }
        
        Team team = teamOpt.get();
        
        // Get the chest
        Optional<TeamEnderChest> chestOpt = team.getEnderChest(view.chestNumber);
        if (chestOpt.isEmpty()) {
            return;
        }
        
        TeamEnderChest chest = chestOpt.get();
        
        // Save the contents
        ItemStack[] contents = event.getInventory().getContents();
        chest.setContents(contents);
        
        // Update the team in storage
        plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));
        
        if (plugin.getSettings().debugModeEnabled()) {
            plugin.log(java.util.logging.Level.INFO, 
                "Saved team ender chest #" + view.chestNumber + " for team " + team.getName());
        }
    }
    
    /**
     * Helper class to track which team chest a player is viewing
     */
    private static class TeamChestView {
        final int teamId;
        final int chestNumber;
        
        TeamChestView(int teamId, int chestNumber) {
            this.teamId = teamId;
            this.chestNumber = chestNumber;
        }
    }
}
