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

            sender.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getPlayerOnlyCommand()));
            return;
        }

        if (!plugin.getSettings().getTeam().getEnemies().isEnabled()) {

            player.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getFunctionDisabled()));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.RELATIONS)))) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getNoPermission()));
                        return;
                    }


                    if (team.getRelations(plugin).size() >= plugin.getSettings().getTeam().getEnemies().getMaxEnemies()) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getEnemy().getMaxAmountReached().replaceAll("%LIMIT%", String.valueOf(plugin.getSettings().getTeam().getEnemies().getMaxEnemies()))));
                        return;
                    }

                    final Optional<Team> optionalOtherTeam = plugin.getTeamStorageUtil().findTeamByName(teamName);
                    if (optionalOtherTeam.isEmpty() || optionalOtherTeam.get().equals(team)) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getTeamNotFound()));
                        return;
                    }

                    final Team otherTeam = optionalOtherTeam.get();
                    if (team.getRelations(plugin).containsKey(otherTeam) && team.getRelations(plugin).get(otherTeam).equals(Team.Relation.ALLY)) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getEnemy().getFailedCannotEnemyAlly()));
                        return;
                    }

                    if (team.getRelations(plugin).containsKey(otherTeam) && team.getRelations(plugin).get(otherTeam).equals(Team.Relation.ENEMY)) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getEnemy().getFailedAlreadyEnemy()));
                        return;
                    }

                    if (!(new TeamEnemyAddEvent(player, team, otherTeam, otherTeam.getOwner()).callEvent())) return;


                    plugin.getTeamStorageUtil().addTeamEnemy(team, otherTeam, player);
                    //fireTeamAllyAddEvent(player, team, allyTeamOwner, team);

                    // send message to team members
                    team.sendTeamMessage(MineDown.parse(plugin.getMessages().getTeam().getEnemy().getAddedSuccessful().replaceAll(ENEMY_Team, otherTeam.getName())));

                    // send message to ally team members
                    otherTeam.sendTeamMessage(MineDown.parse(plugin.getMessages().getTeam().getEnemy().getAddedNotification().replaceAll("%TEAMOWNER%", team.getName())));
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getInfo().getNotInTeam()))
        );
    }

    public void teamEnemySubRemoveCommand(CommandSender sender, String teamName) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getPlayerOnlyCommand()));
            return;
        }

        if (!plugin.getSettings().getTeam().getEnemies().isEnabled()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getFunctionDisabled()));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.RELATIONS)))) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getNoPermission()));
                        return;
                    }


                    final Optional<Team> optionalOtherTeam = plugin.getTeamStorageUtil().findTeamByName(teamName);
                    if (optionalOtherTeam.isEmpty() || optionalOtherTeam.get().equals(team)) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getTeamNotFound()));
                        return;
                    }

                    final Team otherTeam = optionalOtherTeam.get();
                    if (team.getRelations(plugin).containsKey(otherTeam) && team.getRelations(plugin).get(otherTeam).equals(Team.Relation.ENEMY)) {

                        if (!(new TeamEnemyRemoveEvent(player, team, otherTeam, otherTeam.getOwner()).callEvent())) return;

                        plugin.getTeamStorageUtil().removeTeamEnemy(team, otherTeam, player);


                        team.sendTeamMessage(MineDown.parse(plugin.getMessages().getTeam().getEnemy().getRemovedSuccessful().replace(ENEMY_Team, otherTeam.getName())));

                        otherTeam.sendTeamMessage(MineDown.parse(plugin.getMessages().getTeam().getEnemy().getRemovedNotification().replace("%TEAMOWNER%", team.getName())));
                    } else {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getEnemy().getFailedToRemove().replace("%ENEMYTEAM%", teamName)));
                    }
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getInfo().getNotInTeam()))
        );
    }
}
