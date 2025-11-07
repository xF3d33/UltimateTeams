package dev.xf3d3.ultimateteams.commands.subCommands.echest;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamEnderChestBackup;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Handles ender chest rollback commands
 */
public class TeamEnderChestRollbackSubCommand {
    
    private final UltimateTeams plugin;
    private final FileConfiguration messagesConfig;
    
    public TeamEnderChestRollbackSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
    }
    
    /**
     * List available backups for a chest
     */
    /*public void listBackups(@NotNull CommandSender sender, int chestNumber) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }
        
        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    if (!team.hasEnderChest(chestNumber)) {
                        player.sendMessage(Utils.Color(messagesConfig.getString("team-echest-not-exist")
                                .replace("%CHEST%", String.valueOf(chestNumber))));
                        return;
                    }
                    
                    List<TeamEnderChestBackup> backups = plugin.getBackupManager().getBackups(team.getId(), chestNumber);
                    
                    if (backups.isEmpty()) {
                        player.sendMessage(Utils.Color("&cNo backups available for chest #" + chestNumber));
                        return;
                    }
                    
                    player.sendMessage(Utils.Color("&6&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
                    player.sendMessage(Utils.Color("&e&lEnder Chest #" + chestNumber + " Backups"));
                    player.sendMessage(Utils.Color("&7Team: &f" + team.getName()));
                    player.sendMessage(Utils.Color("&6&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
                    
                    for (int i = 0; i < backups.size(); i++) {
                        TeamEnderChestBackup backup = backups.get(i);
                        player.sendMessage(Utils.Color(
                                "&e#" + (i + 1) + " &7- &f" + backup.getFormattedTimestamp() + 
                                " &7(" + backup.getTimeAgo() + ")"
                        ));
                    }
                    
                    player.sendMessage(Utils.Color("&6&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
                    player.sendMessage(Utils.Color("&7Use &e/team echest rollback " + chestNumber + " <backup#> &7to restore"));
                },
                () -> player.sendMessage(Utils.Color(messagesConfig.getString("not-in-team")))
        );
    }
    

    public void rollbackChest(@NotNull CommandSender sender, int chestNumber, int backupNumber) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }
        
        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    
                    if (!team.hasEnderChest(chestNumber)) {
                        player.sendMessage(Utils.Color(messagesConfig.getString("team-echest-not-exist")
                                .replace("%CHEST%", String.valueOf(chestNumber))));
                        return;
                    }
                    
                    // Backup number is 1-indexed, but array is 0-indexed
                    int backupIndex = backupNumber - 1;
                    
                    if (backupIndex < 0) {
                        player.sendMessage(Utils.Color("&cInvalid backup number! Use a positive number."));
                        return;
                    }
                    
                    List<TeamEnderChestBackup> backups = plugin.getBackupManager().getBackups(team.getId(), chestNumber);
                    
                    if (backups.isEmpty()) {
                        player.sendMessage(Utils.Color("&cNo backups available for chest #" + chestNumber));
                        return;
                    }
                    
                    if (backupIndex >= backups.size()) {
                        player.sendMessage(Utils.Color("&cBackup #" + backupNumber + " does not exist! " +
                                "Only " + backups.size() + " backup(s) available."));
                        player.sendMessage(Utils.Color("&7Use &e/team echest backups " + chestNumber + " &7to see available backups."));
                        return;
                    }
                    
                    TeamEnderChestBackup backup = backups.get(backupIndex);
                    
                    // Perform rollback
                    boolean success = plugin.getBackupManager().restoreBackup(team.getId(), chestNumber, backupIndex);
                    
                    if (success) {
                        player.sendMessage(Utils.Color("&a✓ Successfully rolled back Ender Chest #" + chestNumber + "!"));
                        player.sendMessage(Utils.Color("&7Restored from: &f" + backup.getFormattedTimestamp() + 
                                " &7(" + backup.getTimeAgo() + ")"));
                        
                        // Notify all online team members
                        team.getMembers().keySet().forEach(uuid -> {
                            Player member = plugin.getServer().getPlayer(uuid);
                            if (member != null && !member.equals(player)) {
                                member.sendMessage(Utils.Color("&e&l[!] &eTeam Ender Chest #" + chestNumber + 
                                        " has been rolled back by " + player.getName()));
                            }
                        });
                    } else {
                        player.sendMessage(Utils.Color("&c✗ Failed to rollback Ender Chest #" + chestNumber + "!"));
                        player.sendMessage(Utils.Color("&7Please contact an administrator."));
                    }
                },
                () -> player.sendMessage(Utils.Color(messagesConfig.getString("not-in-team")))
        );
    }
    */

    /**
     * List available backups for a chest (Admin version)
     */
    public void listBackupsAdmin(@NotNull CommandSender sender, @NotNull String teamName, int chestNumber) {
        plugin.getTeamStorageUtil().findTeamByName(teamName).ifPresentOrElse(
                team -> {
                    if (!team.hasEnderChest(chestNumber)) {
                        sender.sendMessage(Utils.Color(messagesConfig.getString("team-echest-not-exist")
                                .replace("%CHEST%", String.valueOf(chestNumber))
                                .replace("%NUMBER%", String.valueOf(chestNumber))));
                        return;
                    }
                    
                    List<TeamEnderChestBackup> backups = plugin.getBackupManager().getBackups(team.getId(), chestNumber);
                    
                    if (backups.isEmpty()) {
                        sender.sendMessage(Utils.Color("&cNo backups available for chest #" + chestNumber));
                        return;
                    }
                    
                    sender.sendMessage(Utils.Color("&6&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
                    sender.sendMessage(Utils.Color("&e&lEnder Chest #" + chestNumber + " Backups"));
                    sender.sendMessage(Utils.Color("&7Team: &f" + team.getName()));
                    sender.sendMessage(Utils.Color("&6&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
                    
                    for (int i = 0; i < backups.size(); i++) {
                        TeamEnderChestBackup backup = backups.get(i);
                        sender.sendMessage(Utils.Color(
                                "&e#" + (i + 1) + " &7- &f" + backup.getFormattedTimestamp() + 
                                " &7(" + backup.getTimeAgo() + ")"
                        ));
                    }
                    
                    sender.sendMessage(Utils.Color("&6&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
                    sender.sendMessage(Utils.Color("&7Use &e/ta echest rollback " + teamName + " " + chestNumber + " <backup#> &7to restore"));
                    sender.sendMessage(Utils.Color("&7Use &e/ta echest forcerollback " + teamName + " " + chestNumber + " <backup#> &7to force restore"));
                },
                () -> sender.sendMessage(Utils.Color("&cTeam '" + teamName + "' not found!"))
        );
    }
    
    /**
     * Rollback a chest to a specific backup (Admin version)
     */
    public void rollbackChestAdmin(@NotNull CommandSender sender, @NotNull String teamName, int chestNumber, int backupNumber, boolean force) {
        plugin.getTeamStorageUtil().findTeamByName(teamName).ifPresentOrElse(
                team -> {
                    if (!team.hasEnderChest(chestNumber)) {
                        sender.sendMessage(Utils.Color(messagesConfig.getString("team-echest-not-exist")
                                .replace("%CHEST%", String.valueOf(chestNumber))
                                .replace("%NUMBER%", String.valueOf(chestNumber))));
                        return;
                    }
                    
                    // Backup number is 1-indexed, but array is 0-indexed
                    int backupIndex = backupNumber - 1;
                    
                    if (backupIndex < 0) {
                        sender.sendMessage(Utils.Color("&cInvalid backup number! Use a positive number."));
                        return;
                    }
                    
                    List<TeamEnderChestBackup> backups = plugin.getBackupManager().getBackups(team.getId(), chestNumber);
                    
                    if (backups.isEmpty()) {
                        sender.sendMessage(Utils.Color("&cNo backups available for chest #" + chestNumber));
                        return;
                    }
                    
                    if (backupIndex >= backups.size()) {
                        sender.sendMessage(Utils.Color("&cBackup #" + backupNumber + " does not exist! " +
                                "Only " + backups.size() + " backup(s) available."));
                        sender.sendMessage(Utils.Color("&7Use &e/ta echest backups " + teamName + " " + chestNumber + " &7to see available backups."));
                        return;
                    }
                    
                    TeamEnderChestBackup backup = backups.get(backupIndex);
                    
                    // Perform rollback
                    boolean success = plugin.getBackupManager().restoreBackup(team.getId(), chestNumber, backupIndex);
                    
                    if (success) {
                        String rollbackType = force ? "&c&lFORCE ROLLBACK" : "&aROLLBACK";
                        sender.sendMessage(Utils.Color("&a✓ Successfully rolled back Ender Chest #" + chestNumber + " for team " + team.getName() + "!"));
                        sender.sendMessage(Utils.Color("&7Restored from: &f" + backup.getFormattedTimestamp() + 
                                " &7(" + backup.getTimeAgo() + ")"));
                        
                        if (force) {
                            sender.sendMessage(Utils.Color("&c&l⚠ FORCE ROLLBACK - Team was not notified"));
                        }
                        
                        // Notify all online team members (unless force rollback)
                        if (!force) {
                            team.getMembers().keySet().forEach(uuid -> {
                                Player member = plugin.getServer().getPlayer(uuid);
                                if (member != null) {
                                    member.sendMessage(Utils.Color("&c&l[!] &cADMIN ALERT: &eTeam Ender Chest #" + chestNumber + 
                                            " has been rolled back by an administrator"));
                                    member.sendMessage(Utils.Color("&7Restored from: &f" + backup.getFormattedTimestamp()));
                                }
                            });
                        }
                    } else {
                        sender.sendMessage(Utils.Color("&c✗ Failed to rollback Ender Chest #" + chestNumber + "!"));
                    }
                },
                () -> sender.sendMessage(Utils.Color("&cTeam '" + teamName + "' not found!"))
        );
    }
}
