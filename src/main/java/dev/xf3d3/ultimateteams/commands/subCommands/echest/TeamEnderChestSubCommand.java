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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

    // Anti-Dupe state lock to prevent macro spam
    private static final Set<UUID> openingChests = ConcurrentHashMap.newKeySet();

    public TeamEnderChestSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    /**
     * Get the shared inventory key for a team chest
     */
    private String getInventoryKey(int teamId, int chestNumber) {
        return teamId + "-" + chestNumber;
    }

    /**
     * Remove cached shared inventory and cancel pending saves for a chest.
     */
    public void invalidateChest(int teamId, int chestNumber) {
        String key = getInventoryKey(teamId, chestNumber);

        synchronized (INVENTORY_LOCK) {
            sharedInventories.remove(key);
            inventoryViewers.remove(key);
        }

        synchronized (pendingSaveTasks) {
            WrappedTask pendingTask = pendingSaveTasks.remove(key);
            if (pendingTask != null) {
                plugin.getScheduler().cancelTask(pendingTask);
            }
        }
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
            inventoryViewers.put(key, ConcurrentHashMap.newKeySet());

            if (plugin.getSettings().getGeneral().isDeveloperDebugMode()) {
                plugin.log(java.util.logging.Level.INFO,
                        "Created shared inventory for team " + team.getName() + " chest #" + chestNumber);
            }
        }

        return inventory;
    }

    public void openDirectEnderChest(@NotNull Player viewer, @NotNull Team team, int chestNumber) {
        if (!team.hasEnderChest(chestNumber)) {
            viewer.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getEchest().getNotExist()
                    .replace("%NUMBER%", String.valueOf(chestNumber))));
            return;
        }

        UUID playerUUID = viewer.getUniqueId();

        // Prevent macro spamming
        if (openingChests.contains(playerUUID)) {
            return; // ignore the spam
        }

        // Lock the player
        openingChests.add(playerUUID);

        // Ensure any existing GUI is closed (preventing ghost items)
        if (viewer.getOpenInventory().getType() != InventoryType.CRAFTING) {
            viewer.closeInventory();
        }

        // 1 tick on the player's entity scheduler to process pending packets and open safely on Folia
        plugin.getScheduler().runAtEntityLater(viewer, () -> {
            try {
                if (!viewer.isOnline()) return;

                Optional<TeamEnderChest> chestOpt = team.getEnderChest(chestNumber);
                if (chestOpt.isEmpty()) return;

                TeamEnderChest chest = chestOpt.get();
                String key = getInventoryKey(team.getId(), chestNumber);
                Inventory inventory;

                // --- SYNCHRONIZED SECTION ---
                synchronized (INVENTORY_LOCK) {
                    inventory = getOrCreateSharedInventory(team, chest, chestNumber);
                    activeViews.put(playerUUID, new TeamChestView(team.getId(), chestNumber));
                    inventoryViewers.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).add(playerUUID);
                }

                viewer.openInventory(inventory);
                viewer.updateInventory();

                if (plugin.getSettings().getGeneral().isDeveloperDebugMode()) {
                    plugin.log(java.util.logging.Level.INFO,
                            "Player/Admin " + viewer.getName() + " opened chest #" + chestNumber + " for team " + team.getName());
                }
            } finally {
                openingChests.remove(playerUUID);
            }
        }, () -> openingChests.remove(playerUUID), 1L);
    }

    /**
     * Schedule a debounced save using a snapshot captured on the entity thread.
     */
    private void scheduleDebouncedSave(@NotNull Team team, int chestNumber, @NotNull ItemStack[] snapshot) {
        String key = getInventoryKey(team.getId(), chestNumber);

        synchronized (pendingSaveTasks) {
            WrappedTask existingTask = pendingSaveTasks.get(key);
            if (existingTask != null) {
                plugin.getScheduler().cancelTask(existingTask);
                pendingSaveTasks.remove(key);
            }

            WrappedTask task = plugin.getScheduler().runLater(() -> {
                synchronized (pendingSaveTasks) {
                    pendingSaveTasks.remove(key);
                }

                team.getEnderChest(chestNumber).ifPresent(chest -> {
                    chest.setContents(snapshot);
                    plugin.runAsync(t -> plugin.getTeamStorageUtil().updateTeamData(null, team));

                    if (plugin.getSettings().getGeneral().isDeveloperDebugMode()) {
                        plugin.log(java.util.logging.Level.INFO,
                                "Auto-saved team ender chest #" + chestNumber + " for team " + team.getName());
                    }
                });
            }, SAVE_DELAY_TICKS);

            pendingSaveTasks.put(key, task);
        }
    }

    public void openEnderChest(@NotNull CommandSender sender, int chestNumber) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getPlayerOnlyCommand()));

            return;
        }

        if (!plugin.getSettings().getTeam().getEchest().isEnabled()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getFunctionDisabled()));

            return;
        }

        // Check if player is in a team
        Optional<Team> teamOpt = plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId());
        if (teamOpt.isEmpty()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getInfo().getNotInTeam()));

            return;
        }

        Team team = teamOpt.get();

        openDirectEnderChest(player, team, chestNumber);

        sender.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getEchest().getOpened()
                .replace("%NUMBER%", String.valueOf(chestNumber))
        ));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        if (!activeViews.containsKey(event.getWhoClicked().getUniqueId())) {
            return;
        }

        if (!(event.getInventory().getHolder() instanceof TeamChestHolder(int teamId, int chestNumber))) {
            return;
        }

        if (event.isCancelled()) {
            return;
        }

        ItemStack[] snapshot = event.getInventory().getContents();
        plugin.getTeamStorageUtil().findTeam(teamId)
                .ifPresent(team -> scheduleDebouncedSave(team, chestNumber, snapshot));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        if (!(event.getInventory().getHolder() instanceof TeamChestHolder(int teamId, int chestNumber))) {
            return;
        }

        if (event.isCancelled()) {
            return;
        }

        ItemStack[] snapshot = event.getInventory().getContents();
        plugin.getTeamStorageUtil().findTeam(teamId)
                .ifPresent(team -> scheduleDebouncedSave(team, chestNumber, snapshot));
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

        if (!(event.getInventory().getHolder() instanceof TeamChestHolder)) {
            return;
        }

        String key = getInventoryKey(view.teamId, view.chestNumber);

        synchronized (INVENTORY_LOCK) {
            Set<UUID> viewers = inventoryViewers.get(key);
            if (viewers != null) {
                viewers.remove(playerUUID);

                if (viewers.isEmpty()) {
                    synchronized (pendingSaveTasks) {
                        WrappedTask pendingTaskId = pendingSaveTasks.remove(key);

                        if (pendingTaskId != null) {
                            plugin.getScheduler().cancelTask(pendingTaskId);
                        }
                    }

                    Optional<Team> teamOpt = plugin.getTeamStorageUtil().findTeam(view.teamId);
                    if (teamOpt.isPresent()) {
                        Team team = teamOpt.get();
                        Optional<TeamEnderChest> chestOpt = team.getEnderChest(view.chestNumber);

                        if (chestOpt.isPresent()) {
                            TeamEnderChest chest = chestOpt.get();
                            ItemStack[] contents = event.getInventory().getContents();
                            chest.setContents(contents);

                            plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                            if (plugin.getSettings().getGeneral().isDeveloperDebugMode()) {
                                plugin.log(java.util.logging.Level.INFO,
                                        "Saved and unloaded team ender chest #" + view.chestNumber + " for team " + team.getName());
                            }
                        }
                    }

                    sharedInventories.remove(key);
                    inventoryViewers.remove(key);

                    if (plugin.getSettings().getGeneral().isDeveloperDebugMode()) {
                        plugin.log(java.util.logging.Level.INFO,
                                "Removed shared inventory " + key + " (no more viewers)");
                    }
                } else if (plugin.getSettings().getGeneral().isDeveloperDebugMode()) {
                    plugin.log(java.util.logging.Level.INFO,
                            "Player " + player.getName() + " closed chest, " + viewers.size() + " viewer(s) remaining");
                }
            }
        }
    }

    private record TeamChestView(int teamId, int chestNumber) {
    }

    private record TeamChestHolder(int teamId, int chestNumber) implements InventoryHolder {

        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }
}
