package dev.xf3d3.ultimateteams.commands.teamSubCommands;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamWarp;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamWarpSubCommand {
    private final FileConfiguration messagesConfig;
    private final FileConfiguration teamsConfig;
    private final UltimateTeams plugin;

    public TeamWarpSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
        this.teamsConfig = plugin.getConfig();
    }

    public void WarpCommand(CommandSender sender, String name) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (!teamsConfig.getBoolean("team-warp.enabled")) {
            player.sendMessage(Utils.Color(messagesConfig.getString("function-disabled")));
            return;
        }

        Team team;
        if (plugin.getTeamStorageUtil().findTeamByOwner(player) != null) {
            team = plugin.getTeamStorageUtil().findTeamByOwner(player);
        } else {
            team = plugin.getTeamStorageUtil().findTeamByPlayer(player);
        }

        // todo: add cooldown check
        if (team != null) {
            final TeamWarp warp = team.getTeamWarp(name);

            if (warp == null) {
                player.sendMessage(Utils.Color(messagesConfig.getString("team-warp-not-found")));
                return;
            }

            if (teamsConfig.getLong("team-warp.tp-delay") > 0) {
                player.sendMessage(Utils.Color(messagesConfig.getString("team-warp-cooldown-start").replaceAll("%SECONDS%", teamsConfig.getString("team-warp.tp-delay"))));

                plugin.runLater(() -> {
                    plugin.getUtils().teleportPlayer(player, warp.getLocation());
                    player.sendMessage(Utils.Color(messagesConfig.getString("team-warp-teleported-successful").replaceAll("%WARP_NAME%", warp.getName())));
                }, teamsConfig.getLong("team-warp.tp-delay"));
            } else {
                plugin.getUtils().teleportPlayer(player, warp.getLocation());
                player.sendMessage(Utils.Color(messagesConfig.getString("team-warp-teleported-successful").replaceAll("%WARP_NAME%", warp.getName())));
            }


        } else {
            player.sendMessage(Utils.Color(messagesConfig.getString("failed-not-in-team")));
        }
    }
}
