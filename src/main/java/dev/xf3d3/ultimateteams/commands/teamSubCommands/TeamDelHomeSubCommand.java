package dev.xf3d3.ultimateteams.commands.teamSubCommands;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.TeamHomeDeleteEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamDelHomeSubCommand {

    private final FileConfiguration teamsConfig;
    private final FileConfiguration messagesConfig;
    private final UltimateTeams plugin;

    public TeamDelHomeSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.teamsConfig = plugin.getConfig();
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
    }

    public void deleteTeamHomeSubCommand(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (teamsConfig.getBoolean("team-home.enabled")) {
            if (plugin.getTeamStorageUtil().findTeamByOwner(player) != null) {
                Team teamByOwner = plugin.getTeamStorageUtil().findTeamByOwner(player);

                if (plugin.getTeamStorageUtil().isHomeSet(teamByOwner)) {
                    fireTeamHomeDeleteEvent(player, teamByOwner);

                    plugin.getTeamStorageUtil().deleteHome(teamByOwner);
                    player.sendMessage(Utils.Color(messagesConfig.getString("successfully-deleted-team-home")));
                } else {
                    player.sendMessage(Utils.Color(messagesConfig.getString("failed-no-home-set")));
                }
            } else {
                player.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            }
        } else {
            player.sendMessage(Utils.Color(messagesConfig.getString("function-disabled")));
        }
    }

    private void fireTeamHomeDeleteEvent(Player player, Team team) {
        TeamHomeDeleteEvent teamHomeDeleteEvent = new TeamHomeDeleteEvent(player, team);
        Bukkit.getPluginManager().callEvent(teamHomeDeleteEvent);
    }
}
