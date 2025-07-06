package dev.xf3d3.ultimateteams.commands.subCommands.relations;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamEnemyAddEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.TeamsStorage;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TeamEnemySubCommand {

    private static final String ENEMY_Team = "%ENEMYTEAM%";
    private static final String ENEMY_OWNER = "%ENEMYOWNER%";
    private static final String Team_OWNER = "%TEAMOWNER%";
    private final FileConfiguration messagesConfig;
    private final UltimateTeams plugin;

    public TeamEnemySubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
    }

    public void teamEnemySubAddCommand(CommandSender sender, String teamName) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.RELATIONS)))) {
                        sender.sendMessage(Utils.Color(messagesConfig.getString("no-permission")));
                        return;
                    }


                    if (team.getRelations(plugin).size() >= plugin.getSettings().getMaxTeamEnemies()) {
                        player.sendMessage(Utils.Color(messagesConfig.getString("team-enemy-max-amount-reached")).replaceAll("%LIMIT%", String.valueOf(plugin.getSettings().getMaxTeamAllies())));
                        return;
                    }

                    final Optional<Team> optionalOtherTeam = plugin.getTeamStorageUtil().findTeamByName(teamName);
                    if (optionalOtherTeam.isEmpty() || optionalOtherTeam.get().equals(team)) {
                        player.sendMessage(Utils.Color(messagesConfig.getString("team-not-found")));
                        return;
                    }

                    final Team otherTeam = optionalOtherTeam.get();
                    if (team.getRelations(plugin).containsKey(otherTeam) && team.getRelations(plugin).get(otherTeam).equals(Team.Relation.ALLY)) {
                        player.sendMessage(Utils.Color(messagesConfig.getString("failed-cannot-enemy-allied-team")));
                        return;
                    }

                    if (team.getRelations(plugin).containsKey(otherTeam) && team.getRelations(plugin).get(otherTeam).equals(Team.Relation.ENEMY)) {
                        player.sendMessage(Utils.Color(messagesConfig.getString("failed-team-already-your-enemy")));
                        return;
                    }


                    plugin.getTeamStorageUtil().addTeamEnemy(team, otherTeam, player);
                    //fireTeamAllyAddEvent(player, team, allyTeamOwner, team);

                    // send message to team members
                    team.sendTeamMessage(Utils.Color(messagesConfig.getString("added-team-to-your-enemies").replaceAll(ENEMY_Team, otherTeam.getName())));

                    // send message to ally team members
                    otherTeam.sendTeamMessage(Utils.Color(messagesConfig.getString("team-added-to-other-enemies").replaceAll("%TEAMOWNER%", team.getName())));
                },
                () -> player.sendMessage(Utils.Color(messagesConfig.getString("not-in-team")))
        );
    }

    public void teamEnemySubRemoveCommand(CommandSender sender, String teamName) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.RELATIONS)))) {
                        sender.sendMessage(Utils.Color(messagesConfig.getString("no-permission")));
                        return;
                    }


                    final Optional<Team> optionalOtherTeam = plugin.getTeamStorageUtil().findTeamByName(teamName);
                    if (optionalOtherTeam.isEmpty() || optionalOtherTeam.get().equals(team)) {
                        player.sendMessage(Utils.Color(messagesConfig.getString("team-not-found")));
                        return;
                    }

                    final Team otherTeam = optionalOtherTeam.get();
                    if (team.getRelations(plugin).containsKey(otherTeam) && team.getRelations(plugin).get(otherTeam).equals(Team.Relation.ENEMY)) {

                        plugin.getTeamStorageUtil().removeTeamEnemy(team, otherTeam, player);


                        team.sendTeamMessage(Utils.Color(messagesConfig.getString("removed-team-from-your-enemies").replace(ENEMY_Team, otherTeam.getName())));

                        otherTeam.sendTeamMessage(Utils.Color(messagesConfig.getString("team-removed-from-other-enemies").replace("%TEAMOWNER%", team.getName())));
                    } else {
                        player.sendMessage(Utils.Color(messagesConfig.getString("failed-to-remove-team-from-enemies").replace("%ENEMYTEAM%", teamName)));
                    }
                },
                () -> player.sendMessage(Utils.Color(messagesConfig.getString("not-in-team")))
        );
    }

    private void fireTeamEnemyRemoveEvent(TeamsStorage storageUtil, Player player, Player enemyTeamOwner, Team enemyTeam) {
        //TeamEnemyRemoveEvent teamEnemyRemoveEvent = new TeamEnemyRemoveEvent(player, storageUtil.findTeamByMember(player), enemyTeam, enemyTeamOwner);
        //Bukkit.getPluginManager().callEvent(teamEnemyRemoveEvent);
    }
    private void fireTeamEnemyAddEvent(Player player, Team team, Player enemyTeamOwner, Team enemyTeam) {
        TeamEnemyAddEvent teamEnemyAddEvent = new TeamEnemyAddEvent(player, team, enemyTeam, enemyTeamOwner);
        Bukkit.getPluginManager().callEvent(teamEnemyAddEvent);
    }
}
