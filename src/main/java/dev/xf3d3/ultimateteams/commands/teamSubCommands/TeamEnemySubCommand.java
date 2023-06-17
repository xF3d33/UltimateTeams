package dev.xf3d3.ultimateteams.commands.teamSubCommands;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.TeamEnemyAddEvent;
import dev.xf3d3.ultimateteams.api.TeamEnemyRemoveEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.TeamStorageUtil;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class TeamEnemySubCommand {

    private static final String ENEMY_Team = "%ENEMYTEAM%";
    private static final String ENEMY_OWNER = "%ENEMYOWNER%";
    private static final String Team_OWNER = "%TEAMOWNER%";

    private final FileConfiguration teamsConfig;
    private final FileConfiguration messagesConfig;
    private final UltimateTeams plugin;

    public TeamEnemySubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
        this.teamsConfig = plugin.getConfig();
    }

    public void teamEnemySubAddCommand(CommandSender sender, String teamName) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        final TeamStorageUtil storageUtil = plugin.getTeamStorageUtil();

        if (!plugin.getTeamStorageUtil().isTeamOwner(player)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            return;
        }

        if (storageUtil.findTeamByName(teamName) != null) {
            Team team = storageUtil.findTeamByName(teamName);
            Player enemyTeamOwner = Bukkit.getPlayer(UUID.fromString(team.getTeamOwner()));

            if (enemyTeamOwner == null) {
                player.sendMessage(Utils.Color(messagesConfig.getString("ally-team-add-owner-offline").replaceAll("%ALLYOWNER%", player.getName())));
                return;
            }

            if (storageUtil.findTeamByOwner(player) != team) {
                String enemyOwnerUUIDString = team.getTeamOwner();

                if (team.getTeamEnemies().size() >= teamsConfig.getInt("max-team-enemies")) {
                    int maxSize = teamsConfig.getInt("max-team-enemies");
                    player.sendMessage(Utils.Color(messagesConfig.getString("team-enemy-max-amount-reached")).replace("%LIMIT%", String.valueOf(maxSize)));
                    return;
                }

                if (team.getTeamAllies().contains(enemyOwnerUUIDString)) {
                    player.sendMessage(Utils.Color(messagesConfig.getString("failed-cannot-enemy-allied-team")));
                    return;
                }

                if (team.getTeamEnemies().contains(enemyOwnerUUIDString)) {
                    player.sendMessage(Utils.Color(messagesConfig.getString("failed-team-already-your-enemy")));
                    return;
                }

                storageUtil.addTeamEnemy(player, enemyTeamOwner);
                fireTeamEnemyAddEvent(player, team, enemyTeamOwner, team);

                // send message to player
                player.sendMessage(Utils.Color(messagesConfig.getString("added-team-to-your-enemies").replace(ENEMY_Team, team.getTeamFinalName())));

                // send message to player team
                ArrayList<String> playerTeamMembers = storageUtil.findTeamByOwner(player).getTeamMembers();
                for (String playerTeamMember : playerTeamMembers) {
                    if (playerTeamMember != null) {
                        UUID memberUUID = UUID.fromString(playerTeamMember);
                        Player playerTeamPlayer = Bukkit.getPlayer(memberUUID);
                        if (playerTeamPlayer != null) {
                            playerTeamPlayer.sendMessage(Utils.Color(messagesConfig.getString("added-team-to-your-enemies").replace(ENEMY_Team, team.getTeamFinalName())));
                        }
                    }
                }

                // send message to enemy team
                ArrayList<String> enemyTeamMembers = storageUtil.findTeamByOwner(enemyTeamOwner).getTeamMembers();
                for (String playerTeamMember : enemyTeamMembers) {
                    if (playerTeamMember != null) {
                        UUID memberUUID = UUID.fromString(playerTeamMember);
                        Player playerTeamPlayer = Bukkit.getPlayer(memberUUID);
                        if (playerTeamPlayer != null) {
                            playerTeamPlayer.sendMessage(Utils.Color(messagesConfig.getString("added-team-to-your-enemies").replace(ENEMY_Team, team.getTeamFinalName())));
                        }
                    }
                }

                // send message to enemy team owner
                if (enemyTeamOwner.isOnline()) {
                    enemyTeamOwner.sendMessage(Utils.Color(messagesConfig.getString("team-added-to-other-enemies").replace(Team_OWNER, player.getName())));
                }

            } else {
                player.sendMessage(Utils.Color(messagesConfig.getString("failed-cannot-enemy-your-own-team")));
            }
        }
    }

    public void teamEnemySubRemoveCommand(CommandSender sender, String teamName) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }
        final TeamStorageUtil storageUtil = plugin.getTeamStorageUtil();

        if (!plugin.getTeamStorageUtil().isTeamOwner(player)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            return;
        }

        if (storageUtil.findTeamByName(teamName) != null) {
            Team team = storageUtil.findTeamByName(teamName);
            Player enemyTeamOwner = Bukkit.getPlayer(UUID.fromString(team.getTeamOwner()));

            if (enemyTeamOwner == null) {
                player.sendMessage(Utils.Color(messagesConfig.getString("enemy-team-remove-owner-offline").replaceAll("%ENEMYTEAM%", team.getTeamFinalName())));
                return;
            }

            if (storageUtil.findTeamByOwner(player) != team) {
                String enemyOwnerUUIDString = team.getTeamOwner();

                if (team.getTeamEnemies().contains(enemyOwnerUUIDString)){
                    fireTeamEnemyRemoveEvent(storageUtil, player, enemyTeamOwner, team);
                    storageUtil.removeTeamEnemy(player, enemyTeamOwner);

                    // message to team owner
                    player.sendMessage(Utils.Color(messagesConfig.getString("removed-team-from-your-enemies").replace(ENEMY_Team, team.getTeamFinalName())));

                    // message to team players
                    ArrayList<String> playerTeamMembers = storageUtil.findTeamByOwner(player).getTeamMembers();
                    for (String playerTeamMember : playerTeamMembers){
                        if (playerTeamMember != null){
                            UUID memberUUID = UUID.fromString(playerTeamMember);
                            Player playerTeamPlayer = Bukkit.getPlayer(memberUUID);
                            if (playerTeamPlayer != null){
                                playerTeamPlayer.sendMessage(Utils.Color(messagesConfig.getString("removed-team-from-your-enemies").replace(ENEMY_Team, team.getTeamFinalName())));
                            }
                        }
                    }

                    // message to enemy team players
                    ArrayList<String> enemyTeamMembers = team.getTeamMembers();
                    for (String playerTeamMember : playerTeamMembers){
                        if (playerTeamMember != null){
                            UUID memberUUID = UUID.fromString(playerTeamMember);
                            Player playerTeamPlayer = Bukkit.getPlayer(memberUUID);
                            if (playerTeamPlayer != null){
                                playerTeamPlayer.sendMessage(Utils.Color(messagesConfig.getString("removed-team-from-your-enemies").replace(ENEMY_Team, team.getTeamFinalName())));
                            }
                        }
                    }

                    // message to enemy team owner
                    if (enemyTeamOwner.isOnline()){
                        enemyTeamOwner.sendMessage(Utils.Color(messagesConfig.getString("team-removed-from-other-enemies").replace(ENEMY_OWNER, player.getName())));
                    }
                } else {
                    player.sendMessage(Utils.Color(messagesConfig.getString("failed-to-remove-team-from-enemies").replace("%ENEMYTEAM%", teamName)));
                }
            } else {
                player.sendMessage(Utils.Color(messagesConfig.getString("failed-cannot-enemy-your-own-team")));
            }
        }
    }

    private void fireTeamEnemyRemoveEvent(TeamStorageUtil storageUtil, Player player, Player enemyTeamOwner, Team enemyTeam) {
        TeamEnemyRemoveEvent teamEnemyRemoveEvent = new TeamEnemyRemoveEvent(player, storageUtil.findTeamByPlayer(player), enemyTeam, enemyTeamOwner);
        Bukkit.getPluginManager().callEvent(teamEnemyRemoveEvent);
    }
    private void fireTeamEnemyAddEvent(Player player, Team team, Player enemyTeamOwner, Team enemyTeam) {
        TeamEnemyAddEvent teamEnemyAddEvent = new TeamEnemyAddEvent(player, team, enemyTeam, enemyTeamOwner);
        Bukkit.getPluginManager().callEvent(teamEnemyAddEvent);
    }
}
