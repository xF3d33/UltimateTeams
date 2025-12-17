package dev.xf3d3.ultimateteams.commands.subCommands.echest;

import com.google.common.collect.Maps;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamEnderChest;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TeamEnderChestSubCommand implements Listener {
    private final UltimateTeams plugin;
    
    // Track which player is viewing which team chest
    private static final Map<UUID, TeamChestView> activeViews = Maps.newConcurrentMap();
    
    // Shared inventories for each team chest (key: teamId-chestNumber)
    private static final Map<String, Inventory> sharedInventories = Maps.newConcurrentMap();
    
    // Track viewers for each shared inventory
    private static final Map<String, Set<UUID>> inventoryViewers = Maps.newConcurrentMap();
    
    // Track pending saves to prevent concurrent database writes (key: teamId-chestNumber)
    private static final Map<String, WrappedTask> pendingSaveTasks = Maps.newConcurrentMap();
    private static final long SAVE_DELAY_TICKS = 20L; // 1s delay before saving

    private static final Object INVENTORY_LOCK = new Object();
    
    public TeamEnderChestSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        
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
            inventory = Bukkit.createInventory(new TeamChestHolder(team.getId(), chestNumber), chest.getSize(), MineDown.parse(title));
            
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

    public void openDirectEnderChest(@NotNull Player viewer, @NotNull Team team, int chestNumber) {
        if (!team.hasEnderChest(chestNumber)) {
            viewer.sendMessage(MineDown.parse(plugin.getMessages().getTeamEchestNotExist()
                    .replace("%NUMBER%", String.valueOf(chestNumber))));
            return;
        }

        assert team.getEnderChest(chestNumber).isPresent();
        TeamEnderChest chest = team.getEnderChest(chestNumber).get();
        String key = getInventoryKey(team.getId(), chestNumber);
        Inventory inventory;

        // --- SYNCHRONIZED SECTION ---
        synchronized (INVENTORY_LOCK) {
            // Get or create the inventory
            inventory = getOrCreateSharedInventory(team, chest, chestNumber);

            // Track the viewer
            activeViews.put(viewer.getUniqueId(), new TeamChestView(team.getId(), chestNumber));

            // Add to viewer set
            inventoryViewers.computeIfAbsent(key, k -> new HashSet<>()).add(viewer.getUniqueId());
        }

        // Open the inventory
        viewer.openInventory(inventory);

        // Debug logging
        if (plugin.getSettings().debugModeEnabled()) {
            plugin.log(java.util.logging.Level.INFO,
                    "Player/Admin " + viewer.getName() + " opened chest #" + chestNumber + " for team " + team.getName());
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
            WrappedTask existingTaskId = pendingSaveTasks.get(key);
            if (existingTaskId != null) {

                plugin.getScheduler().cancelTask(existingTaskId);
                pendingSaveTasks.remove(key);
            }

            WrappedTask task = plugin.getScheduler().runLater(() -> {
                // Remove from pending tasks (synchronized)
                synchronized (pendingSaveTasks) {
                    pendingSaveTasks.remove(key);
                }

                // Perform the actual save asynchronously
                Optional<TeamEnderChest> chestOpt = team.getEnderChest(chestNumber);
                if (chestOpt.isPresent()) {
                    TeamEnderChest chest = chestOpt.get();
                    ItemStack[] contents = inventory.getContents();
                    chest.setContents(contents);

                    Player randomPlayer = Bukkit.getOnlinePlayers().stream().findAny().orElse(null);
                    plugin.runAsync(task2 -> plugin.getTeamStorageUtil().updateTeamData(randomPlayer, team));

                    if (plugin.getSettings().debugModeEnabled()) {
                        plugin.log(java.util.logging.Level.INFO,
                                "Auto-saved team ender chest #" + chestNumber + " for team " + team.getName());
                    }
                }
            }, SAVE_DELAY_TICKS);

            
            // Track this pending save
            pendingSaveTasks.put(key, task);
        }
    }

    
    public void openEnderChest(@NotNull CommandSender sender, int chestNumber) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));

            return;
        }

        if (!plugin.getSettings().isTeamEnderChestEnabled()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getFunctionDisabled()));

            return;
        }
        
        // Check if player is in a team
        Optional<Team> teamOpt = plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId());
        if (teamOpt.isEmpty()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()));

            return;
        }
        
        Team team = teamOpt.get();

        openDirectEnderChest(player, team, chestNumber);

        sender.sendMessage(MineDown.parse(plugin.getMessages().getTeamEchestOpened()
                .replace("%NUMBER%", String.valueOf(chestNumber))
        ));
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        
        // Check if this is a team chest inventory
        if (!(event.getInventory().getHolder() instanceof TeamChestHolder(int teamId, int chestNumber))) {
            return;
        }
        
        // If the event was cancelled by another plugin, don't save
        if (event.isCancelled()) {
            return;
        }
        
        // Schedule a save after the click is processed
        String key = getInventoryKey(teamId, chestNumber);
        plugin.getScheduler().runLater(() -> {
            Optional<Team> teamOpt = plugin.getTeamStorageUtil().findTeam(teamId);

            teamOpt.ifPresent(team -> saveInventoryAsync(team, chestNumber, event.getInventory()));
        }, 1L); // 1 tick delay to ensure the inventory has been updated
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        
        // Check if this is a team chest inventory
        if (!(event.getInventory().getHolder() instanceof TeamChestHolder(int teamId, int chestNumber))) {
            return;
        }
        
        // If the event was cancelled by another plugin, don't save
        if (event.isCancelled()) {
            return;
        }
        
        // Schedule a save after the drag is processed
        String key = getInventoryKey(teamId, chestNumber);
        plugin.getScheduler().runLater(() -> {
            Optional<Team> teamOpt = plugin.getTeamStorageUtil().findTeam(teamId);

            teamOpt.ifPresent(team -> saveInventoryAsync(team, chestNumber, event.getInventory()));
        }, 1L); // 1 tick delay to ensure the inventory has been updated
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }
        
        UUID playerUUID = player.getUniqueId();
        TeamChestView view = activeViews.remove(playerUUID);
        
        if (view == null) {
            return;
        }
        
        // Check if this is a team chest inventory
        if (!(event.getInventory().getHolder() instanceof TeamChestHolder holder)) {
            return;
        }
        
        String key = getInventoryKey(view.teamId, view.chestNumber);

        synchronized (INVENTORY_LOCK) {
            Set<UUID> viewers = inventoryViewers.get(key);
            if (viewers != null) {
                viewers.remove(playerUUID);

                // If no more viewers, save immediately and remove the shared inventory
                if (viewers.isEmpty()) {
                    // Cancel any pending save task since we're doing an immediate save (synchronized)
                    synchronized (pendingSaveTasks) {
                        WrappedTask pendingTaskId = pendingSaveTasks.remove(key);

                        if (pendingTaskId != null) {
                            plugin.getScheduler().cancelTask(pendingTaskId);
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
                return null;
            }
        }
}
