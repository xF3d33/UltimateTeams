package dev.xf3d3.ultimateteams.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamEnderChest;
import dev.xf3d3.ultimateteams.models.TeamEnderChestBackup;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages automatic backups of team ender chests
 */
public class EnderChestBackupManager {
    
    private final UltimateTeams plugin;
    private final File backupsFile;
    private final Gson gson;
    private final Map<String, List<TeamEnderChestBackup>> backups; // Key: "teamId-chestNumber"
    private volatile WrappedTask backupTask;
    private volatile boolean running = true;
    
    private static final int BACKUP_INTERVAL_MINUTES = 30;
    private static final long BACKUP_INTERVAL_TICKS = BACKUP_INTERVAL_MINUTES * 60 * 20; // 30 minutes in ticks
    private static final int MAX_BACKUPS_PER_CHEST = 10; // Keep last 10 backups (5 hours worth)
    
    public EnderChestBackupManager(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.backupsFile = new File(plugin.getDataFolder(), "echest_backups.json");
        this.gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .create();
        this.backups = new ConcurrentHashMap<>();
        
        loadBackups();
        startAutoBackup();
    }
    
    /**
     * Start the automatic backup task
     */
    private void startAutoBackup() {
        running = true;
        if (backupTask != null) {
            backupTask.cancel();
        }
        
        // Schedule the first backup immediately
        scheduleNextBackup();
    }
    
    /**
     * Schedule the next backup task
     */
    private void scheduleNextBackup() {
        if (!running) {
            return;
        }
        
        // Schedule the backup task asynchronously
        plugin.runAsync(task -> {
            // Store task reference for cancellation
            backupTask = task;
            
            // Double-check running flag after task starts
            if (!running) {
                return;
            }
            
            try {
                plugin.getLogger().info("Creating automatic ender chest backups...");
                backupAllChests();
                saveBackups();
                plugin.getLogger().info("Automatic ender chest backups completed.");
            } catch (Exception e) {
                plugin.getLogger().log(java.util.logging.Level.SEVERE, 
                    "Error during automatic ender chest backup: {0}", e.getMessage());
                plugin.getLogger().log(java.util.logging.Level.SEVERE, 
                    "Stack trace:", e);
            }
            
            // Schedule the next backup after the interval (only if still running)
            if (running) {
                // Use runLater to schedule the next backup on the main thread, then call scheduleNextBackup
                // This ensures proper thread context and prevents stack overflow
                plugin.getScheduler().runLater(() -> {
                    if (running) {
                        scheduleNextBackup();
                    }
                }, BACKUP_INTERVAL_TICKS);
            }
        });
    }
    
    /**
     * Stop the automatic backup task
     */
    public void shutdown() {
        running = false;
        if (backupTask != null) {
            backupTask.cancel();
            backupTask = null;
        }
        saveBackups();
    }
    
    /**
     * Backup all team ender chests
     */
    public void backupAllChests() {
        for (Team team : plugin.getTeamStorageUtil().getTeams()) {
            for (int chestNumber = 1; chestNumber <= team.getEnderChestCount(); chestNumber++) {
                team.getEnderChest(chestNumber).ifPresent(chest -> 
                    backupChest(team.getId(), chest)
                );
            }
        }
    }
    
    /**
     * Backup all chests for a specific team
     * @param teamId The team ID
     * @return Number of chests backed up
     */
    public int backupTeamChests(int teamId) {
        int count = 0;
        Optional<Team> teamOpt = plugin.getTeamStorageUtil().findTeam(teamId);
        if (teamOpt.isPresent()) {
            Team team = teamOpt.get();
            for (int chestNumber = 1; chestNumber <= team.getEnderChestCount(); chestNumber++) {
                team.getEnderChest(chestNumber).ifPresent(chest -> {
                    backupChest(team.getId(), chest);
                });
                count++;
            }
        }
        return count;
    }
    
    /**
     * Backup a specific chest
     * @param teamId The team ID
     * @param chest The chest to backup
     */
    public void backupChest(int teamId, @NotNull TeamEnderChest chest) {
        String key = getKey(teamId, chest.getChestNumber());
        TeamEnderChestBackup backup = TeamEnderChestBackup.fromChest(teamId, chest);
        
        List<TeamEnderChestBackup> chestBackups = backups.computeIfAbsent(key, k -> new ArrayList<>());
        chestBackups.add(backup);
        
        // Keep only the last MAX_BACKUPS_PER_CHEST backups
        if (chestBackups.size() > MAX_BACKUPS_PER_CHEST) {
            chestBackups.sort(Comparator.comparingLong(TeamEnderChestBackup::getTimestamp));
            chestBackups.subList(0, chestBackups.size() - MAX_BACKUPS_PER_CHEST).clear();
        }
    }
    
    /**
     * Get all backups for a specific chest
     * @param teamId The team ID
     * @param chestNumber The chest number
     * @return List of backups, sorted by timestamp (newest first)
     */
    @NotNull
    public List<TeamEnderChestBackup> getBackups(int teamId, int chestNumber) {
        String key = getKey(teamId, chestNumber);
        List<TeamEnderChestBackup> chestBackups = backups.getOrDefault(key, new ArrayList<>());
        chestBackups.sort(Comparator.comparingLong(TeamEnderChestBackup::getTimestamp).reversed());
        return new ArrayList<>(chestBackups);
    }
    
    /**
     * Get a specific backup by index (0 = newest, 1 = second newest, etc.)
     * @param teamId The team ID
     * @param chestNumber The chest number
     * @param index The backup index (0-based)
     * @return Optional containing the backup if found
     */
    @NotNull
    public Optional<TeamEnderChestBackup> getBackup(int teamId, int chestNumber, int index) {
        List<TeamEnderChestBackup> chestBackups = getBackups(teamId, chestNumber);
        if (index >= 0 && index < chestBackups.size()) {
            return Optional.of(chestBackups.get(index));
        }
        return Optional.empty();
    }
    
    /**
     * Restore a chest from a backup
     * @param teamId The team ID
     * @param chestNumber The chest number
     * @param backupIndex The backup index (0 = newest)
     * @return true if restore was successful
     */
    public boolean restoreBackup(int teamId, int chestNumber, int backupIndex) {
        Optional<Team> teamOpt = plugin.getTeamStorageUtil().findTeam(teamId);
        if (teamOpt.isEmpty()) {
            return false;
        }
        
        Team team = teamOpt.get();
        Optional<TeamEnderChest> chestOpt = team.getEnderChest(chestNumber);
        if (chestOpt.isEmpty()) {
            return false;
        }
        
        Optional<TeamEnderChestBackup> backupOpt = getBackup(teamId, chestNumber, backupIndex);
        if (backupOpt.isEmpty()) {
            return false;
        }
        
        TeamEnderChestBackup backup = backupOpt.get();
        TeamEnderChest chest = chestOpt.get();
        
        // Restore the contents
        chest.setSerializedContents(backup.getSerializedContents());
        chest.setRows(backup.getRows());
        
        // Save to database
        plugin.getDatabase().updateTeam(team);
        
        return true;
    }
    
    /**
     * Delete all backups for a specific chest
     * @param teamId The team ID
     * @param chestNumber The chest number
     */
    public void deleteBackups(int teamId, int chestNumber) {
        String key = getKey(teamId, chestNumber);
        backups.remove(key);
        saveBackups();
    }
    
    /**
     * Delete all backups for a team
     * @param teamId The team ID
     */
    public void deleteTeamBackups(int teamId) {
        backups.keySet().removeIf(key -> key.startsWith(teamId + "-"));
        saveBackups();
    }
    
    /**
     * Get the key for a chest backup
     */
    private String getKey(int teamId, int chestNumber) {
        return teamId + "-" + chestNumber;
    }
    
    /**
     * Load backups from file
     */
    private void loadBackups() {
        if (!backupsFile.exists()) {
            return;
        }
        
        try (FileReader reader = new FileReader(backupsFile)) {
            Type type = new TypeToken<Map<String, List<TeamEnderChestBackup>>>(){}.getType();
            Map<String, List<TeamEnderChestBackup>> loaded = gson.fromJson(reader, type);
            if (loaded != null) {
                backups.putAll(loaded);
                plugin.getLogger().log(java.util.logging.Level.INFO, 
                    "Loaded {0} ender chest backup entries.", backups.size());
            }
        } catch (IOException e) {
            plugin.getLogger().log(java.util.logging.Level.SEVERE, 
                "Failed to load ender chest backups: {0}", e.getMessage());
        }
    }
    
    /**
     * Save backups to file
     */
    public void saveBackups() {
        try {
            if (!backupsFile.exists()) {
                backupsFile.getParentFile().mkdirs();
                backupsFile.createNewFile();
            }
            
            try (FileWriter writer = new FileWriter(backupsFile)) {
                gson.toJson(backups, writer);
            }
        } catch (IOException e) {
            plugin.getLogger().log(java.util.logging.Level.SEVERE, 
                "Failed to save ender chest backups: {0}", e.getMessage());
        }
    }
}
