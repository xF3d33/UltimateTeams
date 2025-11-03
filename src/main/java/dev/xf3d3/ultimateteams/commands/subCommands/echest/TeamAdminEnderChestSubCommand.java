package dev.xf3d3.ultimateteams.commands.subCommands.echest;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamEnderChest;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TeamAdminEnderChestSubCommand {
    private final UltimateTeams plugin;
    private final FileConfiguration messagesConfig;
    private final TeamEnderChestSubCommand teamEnderChestSubCommand;
    
    public TeamAdminEnderChestSubCommand(@NotNull UltimateTeams plugin, @NotNull TeamEnderChestSubCommand teamEnderChestSubCommand) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
        this.teamEnderChestSubCommand = teamEnderChestSubCommand;
    }
    
    /**
     * Add rows to existing chest or create new chest page
     * @param sender The command sender
     * @param teamName The name of the team
     * @param rowsToAdd Number of rows to add (1-6)
     */
    public void addEnderChest(@NotNull CommandSender sender, @NotNull String teamName, @NotNull String rowsOrType) {
        // Find the team
        Optional<Team> teamOpt = plugin.getTeamStorageUtil().findTeamByName(teamName);
        if (teamOpt.isEmpty()) {
            String message = messagesConfig.getString("team-not-found");
            if (message != null) {
                message = message.replace("%TEAM%", teamName);
                sender.sendMessage(Utils.Color(message));
            } else {
                sender.sendMessage(Utils.Color("&cTeam not found: " + teamName));
            }
            return;
        }
        
        Team team = teamOpt.get();
        int rows;
        boolean isNewPage = false;
        
        // Parse the input - could be a number (rows to add), "chest", or "doublechest"
        if (rowsOrType.equalsIgnoreCase("chest")) {
            rows = 3; // Single chest = 3 rows = 27 slots
            isNewPage = true;
        } else if (rowsOrType.equalsIgnoreCase("doublechest")) {
            rows = 6; // Double chest = 6 rows = 54 slots
            isNewPage = true;
        } else {
            // Try to parse as number of rows
            try {
                int rowsToAdd = Integer.parseInt(rowsOrType);
                if (rowsToAdd < 1 || rowsToAdd > 6) {
                    sender.sendMessage(Utils.Color("&cRows must be between 1 and 6!"));
                    return;
                }
                
                // Get the last chest to add rows to it
                Optional<TeamEnderChest> lastChestOpt = team.getEnderChests().values().stream()
                        .max((c1, c2) -> Integer.compare(c1.getChestNumber(), c2.getChestNumber()));
                
                if (lastChestOpt.isPresent()) {
                    TeamEnderChest lastChest = lastChestOpt.get();
                    int newRows = lastChest.getRows() + rowsToAdd;
                    
                    if (newRows > 6) {
                        sender.sendMessage(Utils.Color("&cCannot add " + rowsToAdd + " rows! Current chest has " + 
                                lastChest.getRows() + " rows. Maximum is 6 rows (54 slots)."));
                        sender.sendMessage(Utils.Color("&eYou can add up to " + (6 - lastChest.getRows()) + 
                                " more rows, or use 'chest' or 'doublechest' to create a new page."));
                        return;
                    }
                    
                    // Update existing chest
                    lastChest.setRows(newRows);
                    team.setEnderChest(lastChest);
                    
                    // Save to database
                    plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(null, team));
                    
                    String message = messagesConfig.getString("team-echest-rows-added");
                    if (message != null) {
                        message = message.replace("%ROWS%", String.valueOf(rowsToAdd))
                                .replace("%TOTAL%", String.valueOf(newRows))
                                .replace("%SLOTS%", String.valueOf(lastChest.getSize()))
                                .replace("%NUMBER%", String.valueOf(lastChest.getChestNumber()))
                                .replace("%TEAM%", team.getName());
                        sender.sendMessage(Utils.Color(message));
                    } else {
                        sender.sendMessage(Utils.Color("&aAdded " + rowsToAdd + " rows to chest #" + 
                                lastChest.getChestNumber() + " (now " + newRows + " rows / " + 
                                lastChest.getSize() + " slots) for team " + team.getName()));
                    }
                    return;
                } else {
                    // No chests exist (shouldn't happen with default chest)
                    rows = rowsToAdd;
                    isNewPage = true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(Utils.Color("&cInvalid input! Use: <1-6> (rows to add), 'chest' (new single chest page), or 'doublechest' (new double chest page)"));
                return;
            }
        }
        
        // Create new chest page
        int chestNumber = 1;
        while (team.hasEnderChest(chestNumber)) {
            chestNumber++;
        }
        
        TeamEnderChest chest = TeamEnderChest.builder()
                .chestNumber(chestNumber)
                .rows(rows)
                .serializedContents("")
                .build();
        
        team.setEnderChest(chest);
        
        // Save to database
        plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(null, team));
        
        String chestType = rows == 6 ? "double chest" : "single chest";
        String message = messagesConfig.getString("team-echest-page-added");
        if (message != null) {
            message = message.replace("%NUMBER%", String.valueOf(chestNumber))
                    .replace("%TYPE%", chestType)
                    .replace("%ROWS%", String.valueOf(rows))
                    .replace("%SLOTS%", String.valueOf(chest.getSize()))
                    .replace("%TEAM%", team.getName());
            sender.sendMessage(Utils.Color(message));
        } else {
            sender.sendMessage(Utils.Color("&aAdded " + chestType + " page #" + chestNumber + 
                    " (" + rows + " rows / " + chest.getSize() + " slots) to team " + team.getName()));
        }
    }
    
    /**
     * Remove an ender chest from a team
     * @param sender The command sender
     * @param teamName The name of the team
     * @param chestNumber The chest number to remove
     */
    public void removeEnderChest(@NotNull CommandSender sender, @NotNull String teamName, int chestNumber) {
        // Find the team
        Optional<Team> teamOpt = plugin.getTeamStorageUtil().findTeamByName(teamName);
        if (teamOpt.isEmpty()) {
            String message = messagesConfig.getString("team-not-found");
            if (message != null) {
                message = message.replace("%TEAM%", teamName);
                sender.sendMessage(Utils.Color(message));
            } else {
                sender.sendMessage(Utils.Color("&cTeam not found: " + teamName));
            }
            return;
        }
        
        Team team = teamOpt.get();
        
        // Check if chest exists
        if (!team.hasEnderChest(chestNumber)) {
            String message = messagesConfig.getString("team-echest-not-exist");
            if (message != null) {
                message = message.replace("%NUMBER%", String.valueOf(chestNumber));
                sender.sendMessage(Utils.Color(message));
            } else {
                sender.sendMessage(Utils.Color("&cTeam ender chest #" + chestNumber + " does not exist!"));
            }
            return;
        }
        
        // Remove the chest
        team.removeEnderChest(chestNumber);
        
        // Save to database (use null for actor in admin commands)
        plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(null, team));
        
        String message = messagesConfig.getString("team-echest-removed");
        if (message != null) {
            message = message.replace("%NUMBER%", String.valueOf(chestNumber))
                    .replace("%TEAM%", team.getName());
            sender.sendMessage(Utils.Color(message));
        } else {
            sender.sendMessage(Utils.Color("&aRemoved ender chest #" + chestNumber + 
                    " from team " + team.getName()));
        }
    }
    
    /**
     * List all ender chests for a team
     * @param sender The command sender
     * @param teamName The name of the team
     */
    public void listEnderChests(@NotNull CommandSender sender, @NotNull String teamName) {
        // Find the team
        Optional<Team> teamOpt = plugin.getTeamStorageUtil().findTeamByName(teamName);
        if (teamOpt.isEmpty()) {
            String message = messagesConfig.getString("team-not-found");
            if (message != null) {
                message = message.replace("%TEAM%", teamName);
                sender.sendMessage(Utils.Color(message));
            } else {
                sender.sendMessage(Utils.Color("&cTeam not found: " + teamName));
            }
            return;
        }
        
        Team team = teamOpt.get();
        
        if (team.getEnderChestCount() == 0) {
            sender.sendMessage(Utils.Color("&eTeam " + team.getName() + " has no ender chests."));
            return;
        }
        
        sender.sendMessage(Utils.Color("&6=== Ender Chests for Team: " + team.getName() + " ==="));
        team.getEnderChests().values().forEach(chest -> {
            String chestType = chest.getRows() == 6 ? "Double Chest" : chest.getRows() + " rows";
            sender.sendMessage(Utils.Color("&e  #" + chest.getChestNumber() + " - " + chestType + 
                    " (" + chest.getSize() + " slots)"));
        });
    }
    
    /**
     * Open an ender chest for viewing/editing (admin only)
     * Uses the same shared inventory system as players for real-time synchronization
     * @param sender The command sender (must be a player)
     * @param teamName The name of the team
     * @param chestNumber The chest number to view
     */
    public void seeEnderChest(@NotNull CommandSender sender, @NotNull String teamName, int chestNumber) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }
        
        // Find the team
        Optional<Team> teamOpt = plugin.getTeamStorageUtil().findTeamByName(teamName);
        if (teamOpt.isEmpty()) {
            String message = messagesConfig.getString("team-not-found");
            if (message != null) {
                message = message.replace("%TEAM%", teamName);
                sender.sendMessage(Utils.Color(message));
            } else {
                sender.sendMessage(Utils.Color("&cTeam not found: " + teamName));
            }
            return;
        }
        
        Team team = teamOpt.get();
        
        // Check if the chest exists
        if (!team.hasEnderChest(chestNumber)) {
            String message = messagesConfig.getString("team-echest-not-exist");
            if (message != null) {
                message = message.replace("%NUMBER%", String.valueOf(chestNumber));
                sender.sendMessage(Utils.Color(message));
            } else {
                sender.sendMessage(Utils.Color("&cTeam ender chest #" + chestNumber + " does not exist!"));
            }
            return;
        }
        
        TeamEnderChest chest = team.getEnderChest(chestNumber).get();
        
        // Use the shared inventory system from TeamEnderChestSubCommand
        // This ensures admins see real-time changes alongside players
        Inventory sharedInventory = teamEnderChestSubCommand.getOrCreateSharedInventory(team, chest, chestNumber);
        
        // Track this admin viewer using the same system as regular players
        teamEnderChestSubCommand.trackAdminViewer(player.getUniqueId(), team.getId(), chestNumber);
        
        // Open the shared inventory
        player.openInventory(sharedInventory);
        
        sender.sendMessage(Utils.Color("&c[ADMIN] &aOpened team ender chest #" + chestNumber + 
                " for team &6" + team.getName() + "&a (Real-time View)"));
        sender.sendMessage(Utils.Color("&eChanges are synchronized in real-time with all viewers."));
        
        if (plugin.getSettings().debugModeEnabled()) {
            plugin.log(java.util.logging.Level.INFO, 
                "Admin " + player.getName() + " opened shared chest #" + chestNumber + " for team " + team.getName());
        }
    }
}
