package dev.xf3d3.ultimateteams.commands.subCommands.relations;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamAllyAddEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.TeamsStorage;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.logging.Logger;

public class TeamAllySubCommand {

    private final FileConfiguration messagesConfig;
    private final Logger logger;
    private static final String ALLY_TEAM = "%ALLYTEAM%";
    private static final String ALLY_OWNER = "%ALLYTEAM%";
    private static final String TEAM_OWNER = "%TEAMOWNER%";

    private final UltimateTeams plugin;

    public TeamAllySubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
        this.logger = plugin.getLogger();
    }

    public void teamAllyAddSubCommand(CommandSender sender, String teamName) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (!plugin.getTeamStorageUtil().isTeamOwner(player)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByOwner(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    if (team.getRelations(plugin).size() >= plugin.getSettings().getMaxTeamAllies()) {
                        player.sendMessage(Utils.Color(messagesConfig.getString("team-ally-max-amount-reached")).replaceAll("%LIMIT%", String.valueOf(plugin.getSettings().getMaxTeamAllies())));
                        return;
                    }

                    final Optional<Team> optionalOtherTeam = plugin.getTeamStorageUtil().findTeamByName(teamName);
                    if (optionalOtherTeam.isEmpty() || optionalOtherTeam.get().equals(team)) {
                        player.sendMessage(Utils.Color(messagesConfig.getString("team-not-found")));
                        return;
                    }

                    final Team otherTeam = optionalOtherTeam.get();
                    if (team.getRelations(plugin).containsKey(otherTeam) && team.getRelations(plugin).get(otherTeam).equals(Team.Relation.ENEMY)) {
                        player.sendMessage(Utils.Color(messagesConfig.getString("failed-cannot-ally-enemy-team")));
                        return;
                    }

                    if (team.getRelations(plugin).containsKey(otherTeam) && team.getRelations(plugin).get(otherTeam).equals(Team.Relation.ALLY)) {
                        player.sendMessage(Utils.Color(messagesConfig.getString("failed-team-already-your-ally")));
                        return;
                    }


                    plugin.getTeamStorageUtil().addTeamAlly(team, otherTeam, player);
                    //fireTeamAllyAddEvent(player, team, allyTeamOwner, team);

                    // send message to team members
                    team.sendTeamMessage(Utils.Color(messagesConfig.getString("added-team-to-your-allies").replaceAll(ALLY_TEAM, team.getName())));

                    // send message to ally team members
                    otherTeam.sendTeamMessage(Utils.Color(messagesConfig.getString("team-added-to-other-allies").replaceAll("%TEAMOWNER%", team.getName())));
                },
                () -> player.sendMessage(Utils.Color(messagesConfig.getString("not-in-team")))
        );
    }

    public void teamAllyRemoveSubCommand(CommandSender sender, String teamName) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (!plugin.getTeamStorageUtil().isTeamOwner(player)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByOwner(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    final Optional<Team> optionalOtherTeam = plugin.getTeamStorageUtil().findTeamByName(teamName);
                    if (optionalOtherTeam.isEmpty() || optionalOtherTeam.get().equals(team)) {
                        player.sendMessage(Utils.Color(messagesConfig.getString("team-not-found")));
                        return;
                    }

                    final Team otherTeam = optionalOtherTeam.get();
                    if (team.getRelations(plugin).containsKey(otherTeam) && team.getRelations(plugin).get(otherTeam).equals(Team.Relation.ALLY)) {

                        plugin.getTeamStorageUtil().removeTeamAlly(team, otherTeam, player);


                        team.sendTeamMessage(Utils.Color(messagesConfig.getString("removed-team-from-your-allies").replace(ALLY_TEAM, team.getName())));

                        otherTeam.sendTeamMessage(Utils.Color(messagesConfig.getString("team-removed-from-other-allies").replace(ALLY_TEAM, team.getName())));
                    } else {
                        player.sendMessage(Utils.Color(messagesConfig.getString("failed-to-remove-team-from-allies").replace("%ALLYTEAM%", teamName)));
                    }
                },
                () -> player.sendMessage(Utils.Color(messagesConfig.getString("not-in-team")))
        );
    }

    private void fireTeamAllyRemoveEvent(TeamsStorage storageUtil, Player player, Player allyTeamOwner, Team allyTeam) {
        //TeamAllyRemoveEvent teamAllyRemoveEvent = new TeamAllyRemoveEvent(player, storageUtil.findTeamByOwner(player), allyTeam, allyTeamOwner);
        //Bukkit.getPluginManager().callEvent(teamAllyRemoveEvent);
    }

    private void fireTeamAllyAddEvent(Player player, Team team, Player allyTeamOwner, Team allyTeam) {
        TeamAllyAddEvent teamAllyAddEvent = new TeamAllyAddEvent(player, team, allyTeam, allyTeamOwner);
        Bukkit.getPluginManager().callEvent(teamAllyAddEvent);
    }
}
