package dev.xf3d3.ultimateteams.commands.subCommands;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
            if (teamName != null) {
                plugin.getTeamStorageUtil().findTeamByName(teamName).ifPresentOrElse(
                        team -> player.sendMessage(Utils.Color(getInfo(team))),
                        () -> player.sendMessage(Utils.Color(messagesConfig.getString("team-not-found")))
                );
                return;
            }

            plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                    team -> player.sendMessage(Utils.Color(getInfo(team))),
                    () -> player.sendMessage(Utils.Color(messagesConfig.getString("not-in-team")))
            );

        } else {
            plugin.getTeamStorageUtil().findTeamByName(teamName).ifPresentOrElse(
                    team -> sender.sendMessage(Utils.Color(getInfo(team))),
                    () -> sender.sendMessage(Utils.Color(messagesConfig.getString("team-not-found")))
            );
        }
    }


    private String getInfo(@NotNull Team team) {
        final Map<Team, Team.Relation> relations = team.getRelations(plugin);

        final Map<Team, Team.Relation> allies = relations.entrySet().stream()
                .filter(entry -> entry.getValue() == Team.Relation.ALLY)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        final Map<Team, Team.Relation> enemies = relations.entrySet().stream()
                .filter(entry -> entry.getValue() == Team.Relation.ENEMY)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        StringBuilder teamInfo = new StringBuilder(Utils.Color(messagesConfig.getString("team-info-header"))
                .replace(TEAM_PLACEHOLDER, Utils.Color(team.getName()))
                .replace("%TEAMPREFIX%", Utils.Color(team.getPrefix() != null ? team.getPrefix() : "")));


        Player teamOwner = Bukkit.getPlayer(team.getOwner());
        if (teamOwner != null) {
            teamInfo.append(Utils.Color(messagesConfig.getString("team-info-owner-online")).replace(OWNER, teamOwner.getName()));
        } else {
            String offlineOwner = Bukkit.getOfflinePlayer(team.getOwner()).getName();
            offlineOwner = offlineOwner != null ? offlineOwner : "player not found";

            teamInfo.append(Utils.Color(messagesConfig.getString("team-info-owner-offline")).replace(OWNER, offlineOwner));
        }

        if (!team.getMembers().isEmpty()) {
            Set<UUID> members = team.getMembers().keySet().stream().filter(member -> !member.equals(team.getOwner())).collect(Collectors.toSet());

            teamInfo.append(Utils.Color(messagesConfig.getString("team-info-members-header")
                    .replace("%NUMBER%", Utils.Color(String.valueOf(members.size())))));

            for (UUID teamMember : members) {
                Player teamPlayer = Bukkit.getPlayer(teamMember);
                if (teamPlayer != null) {
                    teamInfo.append(Utils.Color(messagesConfig.getString("team-info-members-online") + "\n").replace(TEAM_MEMBER, teamPlayer.getName()));

                } else {
                    String offlinePlayer = Bukkit.getOfflinePlayer(teamMember).getName();
                    teamInfo.append(Utils.Color(messagesConfig.getString("team-info-members-offline") + "\n").replace(TEAM_MEMBER, offlinePlayer != null ? offlinePlayer : "player not found"));
                }
            }
        }

        if (!allies.isEmpty()) {

            teamInfo.append(" ");
            teamInfo.append(Utils.Color(messagesConfig.getString("team-info-allies-header")));

            allies.keySet().forEach(
                    t -> teamInfo.append(Utils.Color(messagesConfig.getString("team-ally-members").replace(ALLY_TEAM, t.getName())))
            );

        }

        if (!enemies.isEmpty()) {
            teamInfo.append(" ");
            teamInfo.append(Utils.Color(messagesConfig.getString("team-info-enemies-header")));

            enemies.keySet().forEach(
                    t -> teamInfo.append(Utils.Color(messagesConfig.getString("team-enemy-members").replace(ENEMY_TEAM, t.getName())))
            );
        }

        teamInfo.append(" ");
        if (team.isFriendlyFire()) {
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
