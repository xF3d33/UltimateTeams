package dev.xf3d3.ultimateteams.commands.teamSubCommands;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamWarp;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamDelWarpSubCommand {

    private final FileConfiguration messagesConfig;
    private final UltimateTeams plugin;

    public TeamDelWarpSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
    }

    public void delWarpCommand(CommandSender sender, String name) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (!plugin.getTeamStorageUtil().isTeamOwner(player)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            return;
        }

        if (!plugin.getSettings().teamWarpEnabled()) {
            player.sendMessage(Utils.Color(messagesConfig.getString("function-disabled")));
            return;
        }

        if (plugin.getTeamStorageUtil().findTeamByOwner(player) != null) {
            final Team team = plugin.getTeamStorageUtil().findTeamByOwner(player);
            final TeamWarp warp = team.getTeamWarp(name);

            team.removeTeamWarp(name);
            plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));

            player.sendMessage(Utils.Color(messagesConfig.getString("team-warp-deleted-successful").replaceAll("%WARP_NAME%", warp.getName())));
        } else {
            player.sendMessage(Utils.Color(messagesConfig.getString("failed-not-in-team")));
        }
    }
}
