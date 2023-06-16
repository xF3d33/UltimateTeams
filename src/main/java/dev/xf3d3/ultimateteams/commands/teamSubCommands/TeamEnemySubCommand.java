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

    private static final String ENEMY_CLAN = "%ENEMYTEAM%";
    private static final String ENEMY_OWNER = "%ENEMYOWNER%";
    private static final String CLAN_OWNER = "%TEAMOWNER%";

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

        if (!plugin.getTeamStorageUtil().isClanOwner(player)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            return;
        }

        if (storageUtil.findTeamByName(teamName) != null) {
            Team team = storageUtil.findTeamByName(teamName);
            Player enemyClanOwner = Bukkit.getPlayer(UUID.fromString(team.getTeamOwner()));

            if (enemyClanOwner == null) {
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

                storageUtil.addClanEnemy(player, enemyClanOwner);
                fireClanEnemyAddEvent(player, team, enemyClanOwner, team);

                // send message to player
                player.sendMessage(Utils.Color(messagesConfig.getString("added-team-to-your-enemies").replace(ENEMY_CLAN, team.getTeamFinalName())));

                // send message to player team
                ArrayList<String> playerClanMembers = storageUtil.findTeamByOwner(player).getTeamMembers();
                for (String playerClanMember : playerClanMembers) {
                    if (playerClanMember != null) {
                        UUID memberUUID = UUID.fromString(playerClanMember);
                        Player playerClanPlayer = Bukkit.getPlayer(memberUUID);
                        if (playerClanPlayer != null) {
                            playerClanPlayer.sendMessage(Utils.Color(messagesConfig.getString("added-team-to-your-enemies").replace(ENEMY_CLAN, team.getTeamFinalName())));
                        }
                    }
                }

                // send message to enemy team
                ArrayList<String> enemyClanMembers = storageUtil.findTeamByOwner(enemyClanOwner).getTeamMembers();
                for (String playerClanMember : enemyClanMembers) {
                    if (playerClanMember != null) {
                        UUID memberUUID = UUID.fromString(playerClanMember);
                        Player playerClanPlayer = Bukkit.getPlayer(memberUUID);
                        if (playerClanPlayer != null) {
                            playerClanPlayer.sendMessage(Utils.Color(messagesConfig.getString("added-team-to-your-enemies").replace(ENEMY_CLAN, team.getTeamFinalName())));
                        }
                    }
                }

                // send message to enemy team owner
                if (enemyClanOwner.isOnline()) {
                    enemyClanOwner.sendMessage(Utils.Color(messagesConfig.getString("team-added-to-other-enemies").replace(CLAN_OWNER, player.getName())));
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

        if (!plugin.getTeamStorageUtil().isClanOwner(player)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            return;
        }

        if (storageUtil.findTeamByName(teamName) != null) {
            Team team = storageUtil.findTeamByName(teamName);
            Player enemyClanOwner = Bukkit.getPlayer(UUID.fromString(team.getTeamOwner()));

            if (enemyClanOwner == null) {
                player.sendMessage(Utils.Color(messagesConfig.getString("enemy-team-remove-owner-offline").replaceAll("%ENEMYTEAM%", team.getTeamFinalName())));
                return;
            }

            if (storageUtil.findTeamByOwner(player) != team) {
                String enemyOwnerUUIDString = team.getTeamOwner();

                if (team.getTeamEnemies().contains(enemyOwnerUUIDString)){
                    fireClanEnemyRemoveEvent(storageUtil, player, enemyClanOwner, team);
                    storageUtil.removeClanEnemy(player, enemyClanOwner);

                    // message to team owner
                    player.sendMessage(Utils.Color(messagesConfig.getString("removed-team-from-your-enemies").replace(ENEMY_CLAN, team.getTeamFinalName())));

                    // message to team players
                    ArrayList<String> playerClanMembers = storageUtil.findTeamByOwner(player).getTeamMembers();
                    for (String playerClanMember : playerClanMembers){
                        if (playerClanMember != null){
                            UUID memberUUID = UUID.fromString(playerClanMember);
                            Player playerClanPlayer = Bukkit.getPlayer(memberUUID);
                            if (playerClanPlayer != null){
                                playerClanPlayer.sendMessage(Utils.Color(messagesConfig.getString("removed-team-from-your-enemies").replace(ENEMY_CLAN, team.getTeamFinalName())));
                            }
                        }
                    }

                    // message to enemy team players
                    ArrayList<String> enemyTeamMembers = team.getTeamMembers();
                    for (String playerClanMember : playerClanMembers){
                        if (playerClanMember != null){
                            UUID memberUUID = UUID.fromString(playerClanMember);
                            Player playerClanPlayer = Bukkit.getPlayer(memberUUID);
                            if (playerClanPlayer != null){
                                playerClanPlayer.sendMessage(Utils.Color(messagesConfig.getString("removed-team-from-your-enemies").replace(ENEMY_CLAN, team.getTeamFinalName())));
                            }
                        }
                    }

                    // message to enemy team owner
                    if (enemyClanOwner.isOnline()){
                        enemyClanOwner.sendMessage(Utils.Color(messagesConfig.getString("team-removed-from-other-enemies").replace(ENEMY_OWNER, player.getName())));
                    }
                } else {
                    player.sendMessage(Utils.Color(messagesConfig.getString("failed-to-remove-team-from-enemies").replace("%ENEMYTEAM%", teamName)));
                }
            } else {
                player.sendMessage(Utils.Color(messagesConfig.getString("failed-cannot-enemy-your-own-team")));
            }
        }
    }

    private void fireClanEnemyRemoveEvent(TeamStorageUtil storageUtil, Player player, Player enemyClanOwner, Team enemyTeam) {
        TeamEnemyRemoveEvent teamEnemyRemoveEvent = new TeamEnemyRemoveEvent(player, storageUtil.findTeamByPlayer(player), enemyTeam, enemyClanOwner);
        Bukkit.getPluginManager().callEvent(teamEnemyRemoveEvent);
    }
    private void fireClanEnemyAddEvent(Player player, Team team, Player enemyClanOwner, Team enemyTeam) {
        TeamEnemyAddEvent teamEnemyAddEvent = new TeamEnemyAddEvent(player, team, enemyTeam, enemyClanOwner);
        Bukkit.getPluginManager().callEvent(teamEnemyAddEvent);
    }
}
