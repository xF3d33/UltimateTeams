package dev.xf3d3.ultimateteams.commands.subCommands.relations;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamAllyAddEvent;
import dev.xf3d3.ultimateteams.api.events.TeamAllyRemoveEvent;
import dev.xf3d3.ultimateteams.models.Team;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.logging.Logger;

public class TeamAllySubCommand {

    private final Logger logger;
    private static final String ALLY_TEAM = "%ALLYTEAM%";
    private static final String ALLY_OWNER = "%ALLYTEAM%";
    private static final String TEAM_OWNER = "%TEAMOWNER%";

    private final UltimateTeams plugin;

    public TeamAllySubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    public void teamAllyAddSubCommand(CommandSender sender, String teamName) {
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

                    if (team.getRelations(plugin).size() >= plugin.getSettings().getMaxTeamAllies()) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeamAllyMaxAmountReached().replaceAll("%LIMIT%", String.valueOf(plugin.getSettings().getMaxTeamAllies()))));
                        return;
                    }

                    final Optional<Team> optionalOtherTeam = plugin.getTeamStorageUtil().findTeamByName(teamName);
                    if (optionalOtherTeam.isEmpty() || optionalOtherTeam.get().equals(team)) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeamNotFound()));
                        return;
                    }

                    final Team otherTeam = optionalOtherTeam.get();
                    if (team.getRelations(plugin).containsKey(otherTeam) && team.getRelations(plugin).get(otherTeam).equals(Team.Relation.ENEMY)) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getFailedCannotAllyEnemyTeam()));
                        return;
                    }

                    if (team.getRelations(plugin).containsKey(otherTeam) && team.getRelations(plugin).get(otherTeam).equals(Team.Relation.ALLY)) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getFailedTeamAlreadyYourAlly()));
                        return;
                    }

                    if (new TeamAllyAddEvent(player, team, otherTeam, otherTeam.getOwner()).callEvent()) return;


                    plugin.getTeamStorageUtil().addTeamAlly(team, otherTeam, player);
                    //fireTeamAllyAddEvent(player, team, allyTeamOwner, team);

                    // send message to team members
                    team.sendTeamMessage(MineDown.parse(plugin.getMessages().getAddedTeamToYourAllies().replaceAll(ALLY_TEAM, otherTeam.getName())));

                    // send message to allie team members
                    otherTeam.sendTeamMessage(MineDown.parse(plugin.getMessages().getTeamAddedToOtherAllies().replaceAll("%TEAMOWNER%", team.getName())));
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getFailedNotInTeam()))
        );
    }

    public void teamAllyRemoveSubCommand(CommandSender sender, String teamName) {
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
                    if (team.getRelations(plugin).containsKey(otherTeam) && team.getRelations(plugin).get(otherTeam).equals(Team.Relation.ALLY)) {

                        if (new TeamAllyRemoveEvent(player, team, otherTeam, otherTeam.getOwner()).callEvent()) return;

                        plugin.getTeamStorageUtil().removeTeamAlly(team, otherTeam, player);


                        team.sendTeamMessage(MineDown.parse(plugin.getMessages().getRemovedTeamFromYourAllies().replace(ALLY_TEAM, otherTeam.getName())));

                        otherTeam.sendTeamMessage(MineDown.parse(plugin.getMessages().getTeamRemovedFromOtherAllies().replace(ALLY_TEAM, team.getName())));
                    } else {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getFailedToRemoveTeamFromAllies().replace("%ALLYTEAM%", teamName)));
                    }
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()))
        );
    }
}
