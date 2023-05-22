package dev.xf3d3.celestyteams.commands.teamSubCommands;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.models.Team;
import dev.xf3d3.celestyteams.utils.TeamStorageUtil;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class TeamInfoSubCommand {

    FileConfiguration messagesConfig = CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig();
    private static final String CLAN_PLACEHOLDER = "%CLAN%";
    private static final String OWNER = "%OWNER%";
    private static final String CLAN_MEMBER = "%MEMBER%";
    private static final String ALLY_CLAN = "%ALLYCLAN%";
    private static final String ENEMY_CLAN = "%ENEMYCLAN%";
    private static final String POINTS_PLACEHOLDER = "%POINTS%";

    private final CelestyTeams plugin;

    public TeamInfoSubCommand(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
    }

    public boolean teamInfoSubCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            final TeamStorageUtil storageUtil = plugin.getTeamStorageUtil();

            Team teamByOwner = storageUtil.findTeamByOwner(player);
            Team teamByPlayer = storageUtil.findClanByPlayer(player);

            if (teamByOwner != null) {
                ArrayList<String> teamMembers = teamByOwner.getClanMembers();
                ArrayList<String> teamAllies = teamByOwner.getClanAllies();
                ArrayList<String> teamEnemies = teamByOwner.getClanEnemies();
                StringBuilder teamInfo = new StringBuilder(ColorUtils.translateColorCodes(messagesConfig.getString("team-info-header"))
                        .replace(CLAN_PLACEHOLDER, ColorUtils.translateColorCodes(teamByOwner.getTeamFinalName()))
                        .replace("%CLANPREFIX%", ColorUtils.translateColorCodes(teamByOwner.getTeamPrefix())));
                UUID teamOwnerUUID = UUID.fromString(teamByOwner.getTeamOwner());
                Player teamOwner = Bukkit.getPlayer(teamOwnerUUID);
                if (teamOwner != null) {
                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-info-owner-online")).replace(OWNER, teamOwner.getName()));
                }else {
                    UUID uuid = UUID.fromString(teamByOwner.getTeamOwner());
                    String offlineOwner = Bukkit.getOfflinePlayer(uuid).getName();
                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-info-owner-offline")).replace(OWNER, offlineOwner));
                }
                if (teamMembers.size() > 0) {
                    int teamMembersSize = teamMembers.size();
                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-info-members-header")
                            .replace("%NUMBER%", ColorUtils.translateColorCodes(String.valueOf(teamMembersSize)))));
                    for (String teamMember : teamMembers) {
                        if (teamMember != null) {
                            UUID memberUUID = UUID.fromString(teamMember);
                            Player teamPlayer = Bukkit.getPlayer(memberUUID);
                            if (teamPlayer != null) {
                                teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-info-members-online") + "\n").replace(CLAN_MEMBER, teamPlayer.getName()));
                            } else {
                                UUID uuid = UUID.fromString(teamMember);
                                String offlinePlayer = Bukkit.getOfflinePlayer(uuid).getName();
                                teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-info-members-offline") + "\n").replace(CLAN_MEMBER, offlinePlayer));
                            }
                        }

                    }
                }
                if (teamAllies.size() > 0){
                    teamInfo.append(" ");
                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-info-allies-header")));
                    for (String teamAlly : teamAllies){
                        if (teamAlly != null){
                            Player allyOwner = Bukkit.getPlayer(teamAlly);
                            if (allyOwner != null){
                                Team allyTeam = storageUtil.findTeamByOwner(allyOwner);
                                String teamAllyName = allyTeam.getTeamFinalName();
                                teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-ally-members").replace(ALLY_CLAN, teamAllyName)));
                            }else {
                                UUID uuid = UUID.fromString(teamAlly);
                                OfflinePlayer offlineOwnerPlayer = Bukkit.getOfflinePlayer(uuid);
                                Team offlineAllyTeam = storageUtil.findTeamByOfflineOwner(offlineOwnerPlayer);
                                String offlineAllyName = offlineAllyTeam.getTeamFinalName();
                                if (offlineAllyName != null){
                                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-ally-members").replace(ALLY_CLAN, offlineAllyName)));
                                }else {
                                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-ally-members-not-found")));
                                }
                            }
                        }
                    }
                }
                if (teamEnemies.size() > 0){
                    teamInfo.append(" ");
                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-info-enemies-header")));
                    for (String teamEnemy : teamEnemies){
                        if (teamEnemy != null){
                            Player enemyOwner = Bukkit.getPlayer(teamEnemy);
                            if (enemyOwner != null){
                                Team enemyTeam = storageUtil.findTeamByOwner(enemyOwner);
                                String teamEnemyName = enemyTeam.getTeamFinalName();
                                teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-enemy-members").replace(ENEMY_CLAN, teamEnemyName)));
                            }else {
                                UUID uuid = UUID.fromString(teamEnemy);
                                OfflinePlayer offlineOwnerPlayer = Bukkit.getOfflinePlayer(uuid);
                                Team offlineEnemyTeam = storageUtil.findTeamByOfflineOwner(offlineOwnerPlayer);
                                String offlineEnemyName = offlineEnemyTeam.getTeamFinalName();
                                if (offlineEnemyName != null){
                                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-enemy-members").replace(ENEMY_CLAN, offlineEnemyName)));
                                }else {
                                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-enemy-members-not-found")));
                                }
                            }
                        }
                    }
                }
                teamInfo.append(" ");
                if (teamByOwner.isFriendlyFireAllowed()){
                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-pvp-status-enabled")));
                }else {
                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-pvp-status-disabled")));
                }
                if (storageUtil.isHomeSet(teamByOwner)){
                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-home-set-true")));
                }else {
                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-home-set-false")));
                }
                teamInfo.append(" ");
                teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-info-footer")));
                player.sendMessage(teamInfo.toString());

            }else if (teamByPlayer != null){
                ArrayList<String> teamMembers = teamByPlayer.getClanMembers();
                ArrayList<String> teamAllies = teamByPlayer.getClanAllies();
                ArrayList<String> teamEnemies = teamByPlayer.getClanEnemies();
                StringBuilder teamInfo = new StringBuilder(ColorUtils.translateColorCodes(messagesConfig.getString("team-info-header"))
                        .replace(CLAN_PLACEHOLDER, ColorUtils.translateColorCodes(teamByPlayer.getTeamFinalName()))
                        .replace("%CLANPREFIX%", ColorUtils.translateColorCodes(teamByPlayer.getTeamPrefix())));
                UUID teamOwnerUUID = UUID.fromString(teamByPlayer.getTeamOwner());
                Player teamOwner = Bukkit.getPlayer(teamOwnerUUID);
                if (teamOwner != null) {
                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-info-owner-online")).replace(OWNER, teamOwner.getName()));
                } else {
                    UUID uuid = UUID.fromString(teamByPlayer.getTeamOwner());
                    String offlineOwner = Bukkit.getOfflinePlayer(uuid).getName();
                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-info-owner-offline")).replace(OWNER, offlineOwner));
                }
                if (teamMembers.size() > 0) {
                    int teamMembersSize = teamMembers.size();
                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-info-members-header")
                            .replace("%NUMBER%", ColorUtils.translateColorCodes(String.valueOf(teamMembersSize)))));
                    for (String teamMember : teamMembers) {
                        if (teamMember != null) {
                            UUID memberUUID = UUID.fromString(teamMember);
                            Player teamPlayer = Bukkit.getPlayer(memberUUID);
                            if (teamPlayer != null) {
                                teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-info-members-online") + "\n").replace(CLAN_MEMBER, teamPlayer.getName()));
                            } else {
                                UUID uuid = UUID.fromString(teamMember);
                                String offlinePlayer = Bukkit.getOfflinePlayer(uuid).getName();
                                teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-info-members-offline") + "\n").replace(CLAN_MEMBER, offlinePlayer));
                            }
                        }

                    }
                }
                if (teamAllies.size() > 0){
                    teamInfo.append(" ");
                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-info-allies-header")));
                    for (String teamAlly : teamAllies){
                        if (teamAlly != null){
                            Player allyOwner = Bukkit.getPlayer(teamAlly);
                            if (allyOwner != null){
                                Team allyTeam = storageUtil.findTeamByOwner(allyOwner);
                                String teamAllyName = allyTeam.getTeamFinalName();
                                teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-ally-members").replace(ALLY_CLAN, teamAllyName)));
                            }else {
                                UUID uuid = UUID.fromString(teamAlly);
                                OfflinePlayer offlineOwnerPlayer = Bukkit.getOfflinePlayer(uuid);
                                Team offlineAllyTeam = storageUtil.findTeamByOfflineOwner(offlineOwnerPlayer);
                                String offlineAllyName = offlineAllyTeam.getTeamFinalName();
                                if (offlineAllyName != null){
                                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-ally-members").replace(ALLY_CLAN, offlineAllyName)));
                                }else {
                                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-ally-members-not-found")));
                                }
                            }
                        }
                    }
                }
                if (teamEnemies.size() > 0){
                    teamInfo.append(" ");
                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-info-enemies-header")));
                    for (String teamEnemy : teamEnemies){
                        if (teamEnemy != null){
                            Player enemyOwner = Bukkit.getPlayer(teamEnemy);
                            if (enemyOwner != null){
                                Team enemyTeam = storageUtil.findTeamByOwner(enemyOwner);
                                String teamEnemyName = enemyTeam.getTeamFinalName();
                                teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-enemy-members").replace(ENEMY_CLAN, teamEnemyName)));
                            }else {
                                UUID uuid = UUID.fromString(teamEnemy);
                                OfflinePlayer offlineOwnerPlayer = Bukkit.getOfflinePlayer(uuid);
                                Team offlineEnemyTeam = storageUtil.findTeamByOfflineOwner(offlineOwnerPlayer);
                                String offlineEnemyName = offlineEnemyTeam.getTeamFinalName();
                                if (offlineEnemyName != null){
                                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-enemy-members").replace(ENEMY_CLAN, offlineEnemyName)));
                                }else {
                                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-enemy-members-not-found")));
                                }
                            }
                        }
                    }
                }
                teamInfo.append(" ");
                if (teamByPlayer.isFriendlyFireAllowed()){
                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-pvp-status-enabled")));
                }else {
                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-pvp-status-disabled")));
                }
                if (storageUtil.isHomeSet(teamByPlayer)){
                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-home-set-true")));
                }else {
                    teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-home-set-false")));
                }
                teamInfo.append(" ");
                teamInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("team-info-footer")));
                player.sendMessage(teamInfo.toString());
            }else {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("not-in-team")));
            }
            return true;

        }
        return false;
    }
}
