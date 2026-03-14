package dev.xf3d3.ultimateteams.commands.subCommands;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class TeamInfoSubCommand {

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
                        () -> player.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getTeamNotFound()))
                );
                return;
            }

            plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                    team -> player.sendMessage(Utils.Color(getInfo(team))),
                    () -> player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getInfo().getNotInTeam()))
            );

        } else {
            plugin.getTeamStorageUtil().findTeamByName(teamName).ifPresentOrElse(
                    team -> sender.sendMessage(Utils.Color(getInfo(team))),
                    () -> sender.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getTeamNotFound()))
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

        final Set<UUID> teamMembers = team.getMembers().entrySet().stream()
                .filter(entry -> entry.getValue() == 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        final Set<UUID> teamManagers = team.getMembers().entrySet().stream()
                .filter(entry -> entry.getValue() == 2)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        StringBuilder teamInfo = new StringBuilder(Utils.Color(plugin.getMessages().getTeam().getInfo().getHeader()
                .replace(TEAM_PLACEHOLDER, Utils.Color(team.getName())))
                .replace("%TEAMPREFIX%", Utils.Color(team.getPrefix() != null ? team.getPrefix() : "")));

        teamInfo.append("\n");


        Player teamOwner = Bukkit.getPlayer(team.getOwner());
        if (teamOwner != null) {
            teamInfo.append(Utils.Color(plugin.getMessages().getTeam().getInfo().getOwnerOnline()).replace(OWNER, teamOwner.getName())).append("\n");
        } else {
            String offlineOwner = Bukkit.getOfflinePlayer(team.getOwner()).getName();
            offlineOwner = offlineOwner != null ? offlineOwner : "player not found";

            teamInfo.append(Utils.Color(plugin.getMessages().getTeam().getInfo().getOwnerOffline()).replace(OWNER, offlineOwner)).append("\n");
        }

        if (!teamMembers.isEmpty()) {

            teamInfo.append(Utils.Color(plugin.getMessages().getTeam().getInfo().getMembersHeader())
                    .replace("%NUMBER%", Utils.Color(String.valueOf(teamMembers.size())))).append("\n");

            for (UUID teamMember : teamMembers) {
                Player teamPlayer = Bukkit.getPlayer(teamMember);
                if (teamPlayer != null) {
                    teamInfo.append((plugin.getMessages().getTeam().getInfo().getMembersOnline() + "\n").replace(TEAM_MEMBER, teamPlayer.getName()));

                } else {
                    String offlinePlayer = Bukkit.getOfflinePlayer(teamMember).getName();
                    teamInfo.append(Utils.Color(plugin.getMessages().getTeam().getInfo().getMembersOffline() + "\n").replace(TEAM_MEMBER, offlinePlayer != null ? offlinePlayer : "player not found"));
                }
            }
        }

        if (!teamManagers.isEmpty()) {
            teamInfo.append(Utils.Color(plugin.getMessages().getTeam().getInfo().getManagersHeader()
                    .replace("%NUMBER%", Utils.Color(String.valueOf(teamManagers.size()))))).append("\n");

            for (UUID teamMember : teamManagers) {
                Player teamPlayer = Bukkit.getPlayer(teamMember);
                if (teamPlayer != null) {
                    teamInfo.append(Utils.Color(plugin.getMessages().getTeam().getInfo().getMembersOnline() + "\n").replace(TEAM_MEMBER, teamPlayer.getName()));

                } else {
                    String offlinePlayer = Bukkit.getOfflinePlayer(teamMember).getName();
                    teamInfo.append(Utils.Color(plugin.getMessages().getTeam().getInfo().getMembersOffline() + "\n").replace(TEAM_MEMBER, offlinePlayer != null ? offlinePlayer : "player not found"));
                }
            }
        }

        if (!allies.isEmpty()) {

            teamInfo.append(" ");
            teamInfo.append(Utils.Color(plugin.getMessages().getTeam().getInfo().getAlliesHeader())).append("\n");

            allies.keySet().forEach(
                    t -> teamInfo.append(plugin.getMessages().getTeam().getInfo().getAllyListEntry().replace(ALLY_TEAM, t.getName())).append("\n")
            );

        }

        if (!enemies.isEmpty()) {
            teamInfo.append(" ");
            teamInfo.append(Utils.Color(plugin.getMessages().getTeam().getInfo().getEnemiesHeader())).append("\n");

            enemies.keySet().forEach(
                    t -> teamInfo.append(plugin.getMessages().getTeam().getInfo().getEnemyListEntry().replace(ENEMY_TEAM, t.getName())).append("\n")
            );
        }

        teamInfo.append(" ");
        if (plugin.getSettings().getEconomy().isEnable()) {
            teamInfo.append(Utils.Color(plugin.getMessages().getTeam().getInfo().getBankAmount()
                    .replace("%AMOUNT%", String.format("%.2f", team.getBalance()))
            )).append("\n");

            if (plugin.getSettings().getEconomy().getTeamJoinFee().isEnabled()) {
                teamInfo.append(Utils.Color(plugin.getMessages().getTeam().getInfo().getJoinFee()
                        .replace("%AMOUNT%", String.valueOf(team.getJoin_fee()))
                )).append("\n");
            }
        }

        if (plugin.getSettings().getTeam().getMotd().isEnable()) {
            teamInfo.append(plugin.getMessages().getTeam().getInfo().getMotd().replace("%MOTD%", Objects.requireNonNullElse(Utils.Color(team.getMotd()), plugin.getMessages().getTeam().getMotd().getNotSet()))).append("\n");
        }

        if (team.isFriendlyFire()) {
            teamInfo.append(Utils.Color(plugin.getMessages().getTeam().getInfo().getPvpEnabled())).append("\n");
        } else {
            teamInfo.append(Utils.Color(plugin.getMessages().getTeam().getInfo().getPvpDisabled())).append("\n");
        }
        if (plugin.getTeamStorageUtil().isHomeSet(team)) {
            teamInfo.append(Utils.Color(plugin.getMessages().getTeam().getInfo().getHomeSet())).append("\n");
        } else {
            teamInfo.append(Utils.Color(plugin.getMessages().getTeam().getInfo().getHomeNotSet())).append("\n");
        }
        teamInfo.append(" ");
        teamInfo.append(Utils.Color(plugin.getMessages().getTeam().getInfo().getFooter())).append("\n");

        return teamInfo.toString();
    }
}
