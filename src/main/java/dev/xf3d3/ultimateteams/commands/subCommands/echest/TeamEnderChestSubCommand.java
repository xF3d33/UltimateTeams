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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class TeamEnderChestSubCommand implements Listener {
    private final UltimateTeams plugin;
    private final FileConfiguration messagesConfig;
    
    // Track which player is viewing which team chest
    private static final Map<UUID, TeamChestView> activeViews = new HashMap<>();
    
    // Shared inventories for each team chest (key: teamId-chestNumber)
    private static final Map<String, Inventory> sharedInventories = new HashMap<>();
    
    // Track viewers for each shared inventory
    private static final Map<String, Set<UUID>> inventoryViewers = new HashMap<>();
    
    public TeamEnderChestSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
        
        // Register the listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    /**
     * Get the shared inventory key for a team chest
     */
    private String getInventoryKey(int teamId, int chestNumber) {
        return teamId + "-" + chestNumber;
    }
    
    /**
     * Get or create a shared inventory for a team chest
     */
    private Inventory getOrCreateSharedInventory(@NotNull Team team, @NotNull TeamEnderChest chest, int chestNumber) {
        String key = getInventoryKey(team.getId(), chestNumber);
        
        Inventory inventory = sharedInventories.get(key);
        if (inventory == null) {
            // Create new shared inventory
            String title = "Team Chest #" + chestNumber;
            inventory = Bukkit.createInventory(new TeamChestHolder(team.getId(), chestNumber), chest.getSize(), title);
            
            // Load the contents from database
            ItemStack[] contents = chest.getContents();
            for (int i = 0; i < contents.length && i < inventory.getSize(); i++) {
                inventory.setItem(i, contents[i]);
            }
            
            sharedInventories.put(key, inventory);
            inventoryViewers.put(key, new HashSet<>());
            
            if (plugin.getSettings().debugModeEnabled()) {
                plugin.log(java.util.logging.Level.INFO, 
                    "Created shared inventory for team " + team.getName() + " chest #" + chestNumber);
            }
        }
        
        return inventory;
    }
    
    /**
     * Save the inventory contents to the database asynchronously
     */
    private void saveInventoryAsync(@NotNull Team team, int chestNumber, @NotNull Inventory inventory) {
        plugin.runAsync(task -> {
            Optional<TeamEnderChest> chestOpt = team.getEnderChest(chestNumber);
            if (chestOpt.isPresent()) {
                TeamEnderChest chest = chestOpt.get();
                ItemStack[] contents = inventory.getContents();
                chest.setContents(contents);
                
                // Update the team in storage
                plugin.getTeamStorageUtil().updateTeamData(null, team);
                
                if (plugin.getSettings().debugModeEnabled()) {
                    plugin.log(java.util.logging.Level.INFO, 
                        "Auto-saved team ender chest #" + chestNumber + " for team " + team.getName());
                }
            }
        });
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
        
        // Get or create the shared inventory
        Inventory inventory = getOrCreateSharedInventory(team, chest, chestNumber);
        
        // Track this view
        String key = getInventoryKey(team.getId(), chestNumber);
        activeViews.put(player.getUniqueId(), new TeamChestView(team.getId(), chestNumber));
        inventoryViewers.get(key).add(player.getUniqueId());
        
        // Open the inventory
        player.openInventory(inventory);
        
        String message = messagesConfig.getString("team-echest-opened");
        if (message != null) {
            message = message.replace("%NUMBER%", String.valueOf(chestNumber));
            player.sendMessage(Utils.Color(message));
        }
        
        if (plugin.getSettings().debugModeEnabled()) {
            plugin.log(java.util.logging.Level.INFO, 
                "Player " + player.getName() + " opened shared chest #" + chestNumber + " for team " + team.getName() +
                " (viewers: " + inventoryViewers.get(key).size() + ")");
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        
        // Check if this is a team chest inventory
        if (!(event.getInventory().getHolder() instanceof TeamChestHolder holder)) {
            return;
        }
        
        // If the event was cancelled by another plugin, don't save
        if (event.isCancelled()) {
            return;
        }
        
        // Schedule a save after the click is processed
        String key = getInventoryKey(holder.teamId, holder.chestNumber);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Optional<Team> teamOpt = plugin.getTeamStorageUtil().findTeam(holder.teamId);
            if (teamOpt.isPresent()) {
                saveInventoryAsync(teamOpt.get(), holder.chestNumber, event.getInventory());
            }
        }, 1L); // 1 tick delay to ensure the inventory has been updated
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        
        // Check if this is a team chest inventory
        if (!(event.getInventory().getHolder() instanceof TeamChestHolder holder)) {
            return;
        }
        
        // If the event was cancelled by another plugin, don't save
        if (event.isCancelled()) {
            return;
        }
        
        // Schedule a save after the drag is processed
        String key = getInventoryKey(holder.teamId, holder.chestNumber);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Optional<Team> teamOpt = plugin.getTeamStorageUtil().findTeam(holder.teamId);
            if (teamOpt.isPresent()) {
                saveInventoryAsync(teamOpt.get(), holder.chestNumber, event.getInventory());
            }
        }, 1L); // 1 tick delay to ensure the inventory has been updated
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
        
        // Check if this is a team chest inventory
        if (!(event.getInventory().getHolder() instanceof TeamChestHolder holder)) {
            // Clean up tracking even if not a team chest
            activeViews.remove(playerUUID);
            return;
        }
        
        // Remove from tracking
        activeViews.remove(playerUUID);
        
        String key = getInventoryKey(view.teamId, view.chestNumber);
        Set<UUID> viewers = inventoryViewers.get(key);
        if (viewers != null) {
            viewers.remove(playerUUID);
            
            // If no more viewers, save and remove the shared inventory
            if (viewers.isEmpty()) {
                // Find the team
                Optional<Team> teamOpt = plugin.getTeamStorageUtil().findTeam(view.teamId);
                if (teamOpt.isPresent()) {
                    Team team = teamOpt.get();
                    
                    // Get the chest
                    Optional<TeamEnderChest> chestOpt = team.getEnderChest(view.chestNumber);
                    if (chestOpt.isPresent()) {
                        TeamEnderChest chest = chestOpt.get();
                        
                        // Save the contents
                        ItemStack[] contents = event.getInventory().getContents();
                        chest.setContents(contents);
                        
                        // Update the team in storage
                        plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));
                        
                        if (plugin.getSettings().debugModeEnabled()) {
                            plugin.log(java.util.logging.Level.INFO, 
                                "Saved and unloaded team ender chest #" + view.chestNumber + " for team " + team.getName());
                        }
                    }
                }
                
                // Remove the shared inventory since no one is viewing it
                sharedInventories.remove(key);
                inventoryViewers.remove(key);
                
                if (plugin.getSettings().debugModeEnabled()) {
                    plugin.log(java.util.logging.Level.INFO, 
                        "Removed shared inventory " + key + " (no more viewers)");
                }
            } else {
                if (plugin.getSettings().debugModeEnabled()) {
                    plugin.log(java.util.logging.Level.INFO, 
                        "Player " + player.getName() + " closed chest, " + viewers.size() + " viewer(s) remaining");
                }
            }
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
    
    /**
     * Custom inventory holder to identify team chest inventories
     */
    private static class TeamChestHolder implements InventoryHolder {
        final int teamId;
        final int chestNumber;
        
        TeamChestHolder(int teamId, int chestNumber) {
            this.teamId = teamId;
            this.chestNumber = chestNumber;
        }
        
        @Override
        public @NotNull Inventory getInventory() {
            // This method is required by the interface but not used in our implementation
            throw new UnsupportedOperationException("Not implemented");
        }
    }
}
