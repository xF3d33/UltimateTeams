package dev.xf3d3.ultimateteams.commands.teamSubCommands;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.TeamStorageUtil;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

public class TeamInfoSubCommand {

    FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();
    private static final String TEAM_PLACEHOLDER = "%TEAM%";
    private static final String OWNER = "%OWNER%";
    private static final String TEAM_MEMBER = "%MEMBER%";
    private static final String ALLY_TEAM = "%ALLYTEAM%";
    private static final String ENEMY_TEAM = "%ENEMYTEAM%";

    private final UltimateTeams plugin;

    public TeamInfoSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void teamInfoSubCommand(CommandSender sender, @Nullable String teamName) {

        if (sender instanceof final Player player) {
            Team team;
            if (teamName != null) {
                team = plugin.getTeamStorageUtil().findTeamByName(teamName);
            } else {
                if (plugin.getTeamStorageUtil().findTeamByOwner(player) != null) {
                    team = plugin.getTeamStorageUtil().findTeamByOwner(player);
                } else {
                    team = plugin.getTeamStorageUtil().findTeamByPlayer(player);
                }
            }


            if (team != null) {
                player.sendMessage(Utils.Color(getInfo(team)));
            } else {
                player.sendMessage(Utils.Color(messagesConfig.getString("not-in-team")));
            }
        }

        if (!(sender instanceof Player)) {
            final Team team = plugin.getTeamStorageUtil().findTeamByName(teamName);

            if (team != null) {
                sender.sendMessage(Utils.Color(getInfo(team)));
            } else {
                sender.sendMessage(Utils.Color(messagesConfig.getString("not-in-team")));
            }
        }
    }

    private String getInfo(@NotNull Team team) {
        ArrayList<String> teamMembers = team.getTeamMembers();
        ArrayList<String> teamAllies = team.getTeamAllies();
        ArrayList<String> teamEnemies = team.getTeamEnemies();
        StringBuilder teamInfo = new StringBuilder(Utils.Color(messagesConfig.getString("team-info-header"))
                .replace(TEAM_PLACEHOLDER, Utils.Color(team.getTeamFinalName()))
                .replace("%TEAMPREFIX%", Utils.Color(team.getTeamPrefix())));
        UUID teamOwnerUUID = UUID.fromString(team.getTeamOwner());
        Player teamOwner = Bukkit.getPlayer(teamOwnerUUID);
        if (teamOwner != null) {
            teamInfo.append(Utils.Color(messagesConfig.getString("team-info-owner-online")).replace(OWNER, teamOwner.getName()));
        } else {
            UUID uuid = UUID.fromString(team.getTeamOwner());

            String offlineOwner = Bukkit.getOfflinePlayer(uuid).getName();
            offlineOwner = offlineOwner != null ? offlineOwner : "player not found";

            teamInfo.append(Utils.Color(messagesConfig.getString("team-info-owner-offline")).replace(OWNER, offlineOwner));
        }
        if (teamMembers.size() > 0) {
            int teamMembersSize = teamMembers.size();
            teamInfo.append(Utils.Color(messagesConfig.getString("team-info-members-header")
                    .replace("%NUMBER%", Utils.Color(String.valueOf(teamMembersSize)))));
            for (String teamMember : teamMembers) {
                if (teamMember != null) {
                    UUID memberUUID = UUID.fromString(teamMember);
                    Player teamPlayer = Bukkit.getPlayer(memberUUID);
                    if (teamPlayer != null) {
                        teamInfo.append(Utils.Color(messagesConfig.getString("team-info-members-online") + "\n").replace(TEAM_MEMBER, teamPlayer.getName()));
                    } else {
                        UUID uuid = UUID.fromString(teamMember);

                        String offlinePlayer = Bukkit.getOfflinePlayer(uuid).getName();
                        offlinePlayer = offlinePlayer != null ? offlinePlayer : "player not found";

                        teamInfo.append(Utils.Color(messagesConfig.getString("team-info-members-offline") + "\n").replace(TEAM_MEMBER, offlinePlayer));
                    }
                }

            }
        }
        if (teamAllies.size() > 0) {
            teamInfo.append(" ");
            teamInfo.append(Utils.Color(messagesConfig.getString("team-info-allies-header")));
            for (String teamAlly : teamAllies) {
                if (teamAlly != null) {
                    Player allyOwner = Bukkit.getPlayer(teamAlly);
                    if (allyOwner != null) {
                        Team allyTeam = plugin.getTeamStorageUtil().findTeamByOwner(allyOwner);
                        String teamAllyName = allyTeam.getTeamFinalName();
                        teamInfo.append(Utils.Color(messagesConfig.getString("team-ally-members").replace(ALLY_TEAM, teamAllyName)));
                    } else {
                        UUID uuid = UUID.fromString(teamAlly);
                        OfflinePlayer offlineOwnerPlayer = Bukkit.getOfflinePlayer(uuid);
                        Team offlineAllyTeam = plugin.getTeamStorageUtil().findTeamByOfflineOwner(offlineOwnerPlayer);
                        String offlineAllyName = offlineAllyTeam.getTeamFinalName();
                        if (offlineAllyName != null) {
                            teamInfo.append(Utils.Color(messagesConfig.getString("team-ally-members").replace(ALLY_TEAM, offlineAllyName)));
                        } else {
                            teamInfo.append(Utils.Color(messagesConfig.getString("team-ally-members-not-found")));
                        }
                    }
                }
            }
        }
        if (teamEnemies.size() > 0) {
            teamInfo.append(" ");
            teamInfo.append(Utils.Color(messagesConfig.getString("team-info-enemies-header")));
            for (String teamEnemy : teamEnemies) {
                if (teamEnemy != null) {
                    Player enemyOwner = Bukkit.getPlayer(teamEnemy);
                    if (enemyOwner != null) {
                        Team enemyTeam = plugin.getTeamStorageUtil().findTeamByOwner(enemyOwner);
                        String teamEnemyName = enemyTeam.getTeamFinalName();
                        teamInfo.append(Utils.Color(messagesConfig.getString("team-enemy-members").replace(ENEMY_TEAM, teamEnemyName)));
                    } else {
                        UUID uuid = UUID.fromString(teamEnemy);
                        OfflinePlayer offlineOwnerPlayer = Bukkit.getOfflinePlayer(uuid);
                        Team offlineEnemyTeam = plugin.getTeamStorageUtil().findTeamByOfflineOwner(offlineOwnerPlayer);
                        String offlineEnemyName = offlineEnemyTeam.getTeamFinalName();
                        if (offlineEnemyName != null) {
                            teamInfo.append(Utils.Color(messagesConfig.getString("team-enemy-members").replace(ENEMY_TEAM, offlineEnemyName)));
                        } else {
                            teamInfo.append(Utils.Color(messagesConfig.getString("team-enemy-members-not-found")));
                        }
                    }
                }
            }
        }
        teamInfo.append(" ");
        if (team.isFriendlyFireAllowed()) {
            teamInfo.append(Utils.Color(messagesConfig.getString("team-pvp-status-enabled")));
        } else {
            teamInfo.append(Utils.Color(messagesConfig.getString("team-pvp-status-disabled")));
        }
        if (plugin.getTeamStorageUtil().isHomeSet(team)) {
            teamInfo.append(Utils.Color(messagesConfig.getString("team-home-set-true")));
        } else {
            teamInfo.append(Utils.Color(messagesConfig.getString("team-home-set-false")));
        }
        teamInfo.append(" ");
        teamInfo.append(Utils.Color(messagesConfig.getString("team-info-footer")));

        return teamInfo.toString();
    }
}
