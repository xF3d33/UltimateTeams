package dev.xf3d3.ultimateteams.commands.subCommands.home;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamHomeDeleteEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamDelHomeSubCommand {
    private final FileConfiguration messagesConfig;
    private final UltimateTeams plugin;

    public TeamDelHomeSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
    }

    public void deleteTeamHomeSubCommand(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }


        if (!plugin.getSettings().teamHomeEnabled()) {
            player.sendMessage(Utils.Color(messagesConfig.getString("function-disabled")));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.HOME)))) {
                        sender.sendMessage(Utils.Color(messagesConfig.getString("no-permission")));
                        return;
                    }


                    if (plugin.getTeamStorageUtil().isHomeSet(team)) {
                        fireTeamHomeDeleteEvent(player, team);

                        plugin.getTeamStorageUtil().deleteHome(player, team);
                        player.sendMessage(Utils.Color(messagesConfig.getString("successfully-deleted-team-home")));
                    } else {
                        player.sendMessage(Utils.Color(messagesConfig.getString("failed-no-home-set")));
                    }
                },
                () -> player.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")))
        );
    }

    private void fireTeamHomeDeleteEvent(Player player, Team team) {
        TeamHomeDeleteEvent teamHomeDeleteEvent = new TeamHomeDeleteEvent(player, team);
        Bukkit.getPluginManager().callEvent(teamHomeDeleteEvent);
    }
}
