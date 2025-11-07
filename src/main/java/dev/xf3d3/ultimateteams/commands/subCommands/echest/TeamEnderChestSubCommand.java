package dev.xf3d3.ultimateteams.commands.subCommands.echest;

import com.google.common.collect.Maps;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamEnderChest;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
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

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class TeamEnderChestSubCommand implements Listener {
    private final UltimateTeams plugin;
    private final FileConfiguration messagesConfig;
    
    // Track which player is viewing which team chest
    private static final Map<UUID, TeamChestView> activeViews = Maps.newConcurrentMap();
    
    // Shared inventories for each team chest (key: teamId-chestNumber)
    private static final Map<String, Inventory> sharedInventories = Maps.newConcurrentMap();
    
    // Track viewers for each shared inventory
    private static final Map<String, Set<UUID>> inventoryViewers = Maps.newConcurrentMap();
    
    // Track pending saves to prevent concurrent database writes (key: teamId-chestNumber)
    private static final Map<String, Integer> pendingSaveTasks = Maps.newConcurrentMap();
    private static final long SAVE_DELAY_TICKS = 20L; // 1 second delay before saving
    
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
     * Made public to allow admin commands to use the same shared inventory system
     */
    public Inventory getOrCreateSharedInventory(@NotNull Team team, @NotNull TeamEnderChest chest, int chestNumber) {
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
     * Track an admin viewer for a team chest
     * This allows admins to see the same real-time inventory as players
     */
    public void trackAdminViewer(@NotNull UUID adminUUID, int teamId, int chestNumber) {
        String key = getInventoryKey(teamId, chestNumber);
        activeViews.put(adminUUID, new TeamChestView(teamId, chestNumber));
        inventoryViewers.computeIfAbsent(key, k -> new HashSet<>()).add(adminUUID);
        
        if (plugin.getSettings().debugModeEnabled()) {
            plugin.log(java.util.logging.Level.INFO, 
                "Admin viewer added to chest " + key + " (total viewers: " + inventoryViewers.get(key).size() + ")");
        }
    }
    
    /**
     * Save the inventory contents to the database asynchronously with debouncing
     * This prevents concurrent database writes when multiple players interact rapidly
     */
    private void saveInventoryAsync(@NotNull Team team, int chestNumber, @NotNull Inventory inventory) {
        String key = getInventoryKey(team.getId(), chestNumber);
        
        // Cancel any pending save task for this chest (synchronized to prevent race conditions)
        synchronized (pendingSaveTasks) {
            Integer existingTaskId = pendingSaveTasks.get(key);
            if (existingTaskId != null) {
                Bukkit.getScheduler().cancelTask(existingTaskId);
                pendingSaveTasks.remove(key);
            }
            
            // Schedule a new save task with delay (debouncing)
            int taskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                // Remove from pending tasks (synchronized)
                synchronized (pendingSaveTasks) {
                    pendingSaveTasks.remove(key);
                }
                
                // Perform the actual save asynchronously
                plugin.runAsync(task -> {
                    Optional<TeamEnderChest> chestOpt = team.getEnderChest(chestNumber);
                    if (chestOpt.isPresent()) {
                        TeamEnderChest chest = chestOpt.get();
                        ItemStack[] contents = inventory.getContents();
                        chest.setContents(contents);

                        Player randomPlayer = Bukkit.getOnlinePlayers().stream().findAny().orElse(null);

                        // Save to database
                        if (randomPlayer != null)
                            plugin.runAsync(task1 -> plugin.getTeamStorageUtil().updateTeamData(randomPlayer, team));
                        
                        if (plugin.getSettings().debugModeEnabled()) {
                            plugin.log(java.util.logging.Level.INFO, 
                                "Auto-saved team ender chest #" + chestNumber + " for team " + team.getName());
                        }
                    }
                });
            }, SAVE_DELAY_TICKS).getTaskId();
            
            // Track this pending save
            pendingSaveTasks.put(key, taskId);
        }
    }

    
    public void openEnderChest(@NotNull CommandSender sender, int chestNumber) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (!plugin.getSettings().isTeamEnderChestEnabled()) {
            player.sendMessage(Utils.Color(messagesConfig.getString("function-disabled")));
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
            
            // If no more viewers, save immediately and remove the shared inventory
            if (viewers.isEmpty()) {
                // Cancel any pending save task since we're doing an immediate save (synchronized)
                synchronized (pendingSaveTasks) {
                    Integer pendingTaskId = pendingSaveTasks.remove(key);
                    if (pendingTaskId != null) {
                        Bukkit.getScheduler().cancelTask(pendingTaskId);
                    }
                }
                
                // Find the team
                Optional<Team> teamOpt = plugin.getTeamStorageUtil().findTeam(view.teamId);
                if (teamOpt.isPresent()) {
                    Team team = teamOpt.get();
                    
                    // Get the chest
                    Optional<TeamEnderChest> chestOpt = team.getEnderChest(view.chestNumber);
                    if (chestOpt.isPresent()) {
                        TeamEnderChest chest = chestOpt.get();
                        
                        // Save the contents immediately (not debounced since chest is closing)
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
        private record TeamChestView(int teamId, int chestNumber) {
    }

    /**
         * Custom inventory holder to identify team chest inventories
         */
        private record TeamChestHolder(int teamId, int chestNumber) implements InventoryHolder {

        @Override
            public @NotNull Inventory getInventory() {
                // This method is required by the interface but not used in our implementation
                throw new UnsupportedOperationException("Not implemented");
            }
        }
}
