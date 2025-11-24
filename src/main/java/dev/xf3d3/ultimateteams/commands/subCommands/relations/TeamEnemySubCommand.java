package dev.xf3d3.ultimateteams.commands.subCommands.relations;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamEnemyAddEvent;
import dev.xf3d3.ultimateteams.api.events.TeamEnemyRemoveEvent;
import dev.xf3d3.ultimateteams.models.Team;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TeamEnemySubCommand {

    private static final String ENEMY_Team = "%ENEMYTEAM%";
    private final UltimateTeams plugin;

    public TeamEnemySubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void teamEnemySubAddCommand(CommandSender sender, String teamName) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.RELATIONS)))) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getNoPermission()));
                        return;
                    }


                    if (team.getRelations(plugin).size() >= plugin.getSettings().getMaxTeamEnemies()) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeamEnemyMaxAmountReached().replaceAll("%LIMIT%", String.valueOf(plugin.getSettings().getMaxTeamAllies()))));
                        return;
                    }

                    final Optional<Team> optionalOtherTeam = plugin.getTeamStorageUtil().findTeamByName(teamName);
                    if (optionalOtherTeam.isEmpty() || optionalOtherTeam.get().equals(team)) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeamNotFound()));
                        return;
                    }

                    final Team otherTeam = optionalOtherTeam.get();
                    if (team.getRelations(plugin).containsKey(otherTeam) && team.getRelations(plugin).get(otherTeam).equals(Team.Relation.ALLY)) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getFailedCannotEnemyAlliedTeam()));
                        return;
                    }

                    if (team.getRelations(plugin).containsKey(otherTeam) && team.getRelations(plugin).get(otherTeam).equals(Team.Relation.ENEMY)) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getFailedTeamAlreadyYourEnemy()));
                        return;
                    }

                    if (!(new TeamEnemyAddEvent(player, team, otherTeam, otherTeam.getOwner()).callEvent())) return;


                    plugin.getTeamStorageUtil().addTeamEnemy(team, otherTeam, player);
                    //fireTeamAllyAddEvent(player, team, allyTeamOwner, team);

                    // send message to team members
                    team.sendTeamMessage(MineDown.parse(plugin.getMessages().getAddedTeamToYourEnemies().replaceAll(ENEMY_Team, otherTeam.getName())));

                    // send message to ally team members
                    otherTeam.sendTeamMessage(MineDown.parse(plugin.getMessages().getTeamAddedToOtherEnemies().replaceAll("%TEAMOWNER%", team.getName())));
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()))
        );
    }

    public void teamEnemySubRemoveCommand(CommandSender sender, String teamName) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.RELATIONS)))) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getNoPermission()));
                        return;
                    }


                    final Optional<Team> optionalOtherTeam = plugin.getTeamStorageUtil().findTeamByName(teamName);
                    if (optionalOtherTeam.isEmpty() || optionalOtherTeam.get().equals(team)) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeamNotFound()));
                        return;
                    }

                    final Team otherTeam = optionalOtherTeam.get();
                    if (team.getRelations(plugin).containsKey(otherTeam) && team.getRelations(plugin).get(otherTeam).equals(Team.Relation.ENEMY)) {

                        if (!(new TeamEnemyRemoveEvent(player, team, otherTeam, otherTeam.getOwner()).callEvent())) return;

                        plugin.getTeamStorageUtil().removeTeamEnemy(team, otherTeam, player);


                        team.sendTeamMessage(MineDown.parse(plugin.getMessages().getRemovedTeamFromYourEnemies().replace(ENEMY_Team, otherTeam.getName())));

                        otherTeam.sendTeamMessage(MineDown.parse(plugin.getMessages().getTeamRemovedFromOtherEnemies().replace("%TEAMOWNER%", team.getName())));
                    } else {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getFailedToRemoveTeamFromEnemies().replace("%ENEMYTEAM%", teamName)));
                    }
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()))
        );
    }
}
