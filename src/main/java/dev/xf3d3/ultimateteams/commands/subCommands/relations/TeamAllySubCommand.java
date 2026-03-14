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

            sender.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getPlayerOnlyCommand()));
            return;
        }

        if (!plugin.getSettings().getTeam().getAllies().isEnabled()) {

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

                    if (team.getRelations(plugin).size() >= plugin.getSettings().getTeam().getAllies().getMaxAllies()) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getAlly().getMaxAmountReached().replaceAll("%LIMIT%", String.valueOf(plugin.getSettings().getTeam().getAllies().getMaxAllies()))));
                        return;
                    }

                    final Optional<Team> optionalOtherTeam = plugin.getTeamStorageUtil().findTeamByName(teamName);
                    if (optionalOtherTeam.isEmpty() || optionalOtherTeam.get().equals(team)) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getTeamNotFound()));
                        return;
                    }

                    final Team otherTeam = optionalOtherTeam.get();
                    if (team.getRelations(plugin).containsKey(otherTeam) && team.getRelations(plugin).get(otherTeam).equals(Team.Relation.ENEMY)) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getAlly().getFailedCannotAllyEnemy()));
                        return;
                    }

                    if (team.getRelations(plugin).containsKey(otherTeam) && team.getRelations(plugin).get(otherTeam).equals(Team.Relation.ALLY)) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getAlly().getFailedAlreadyAlly()));
                        return;
                    }

                    if (!(new TeamAllyAddEvent(player, team, otherTeam, otherTeam.getOwner())).callEvent()) return;


                    plugin.getTeamStorageUtil().addTeamAlly(team, otherTeam, player);
                    //fireTeamAllyAddEvent(player, team, allyTeamOwner, team);

                    // send message to team members
                    team.sendTeamMessage(MineDown.parse(plugin.getMessages().getTeam().getAlly().getAddedSuccessful().replaceAll(ALLY_TEAM, otherTeam.getName())));

                    // send message to allie team members
                    otherTeam.sendTeamMessage(MineDown.parse(plugin.getMessages().getTeam().getAlly().getAddedNotification().replaceAll("%TEAMOWNER%", team.getName())));
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getPvp().getFailedNotInTeam()))
        );
    }

    public void teamAllyRemoveSubCommand(CommandSender sender, String teamName) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getPlayerOnlyCommand()));
            return;
        }

        if (!plugin.getSettings().getTeam().getAllies().isEnabled()) {
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
                    if (team.getRelations(plugin).containsKey(otherTeam) && team.getRelations(plugin).get(otherTeam).equals(Team.Relation.ALLY)) {

                        if (!(new TeamAllyRemoveEvent(player, team, otherTeam, otherTeam.getOwner()).callEvent())) return;

                        plugin.getTeamStorageUtil().removeTeamAlly(team, otherTeam, player);


                        team.sendTeamMessage(MineDown.parse(plugin.getMessages().getTeam().getAlly().getRemovedSuccessful().replace(ALLY_TEAM, otherTeam.getName())));

                        otherTeam.sendTeamMessage(MineDown.parse(plugin.getMessages().getTeam().getAlly().getRemovedOtherNotification().replace(ALLY_TEAM, team.getName())));
                    } else {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getAlly().getFailedToRemove().replace("%ALLYTEAM%", teamName)));
                    }
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getInfo().getNotInTeam()))
        );
    }
}
