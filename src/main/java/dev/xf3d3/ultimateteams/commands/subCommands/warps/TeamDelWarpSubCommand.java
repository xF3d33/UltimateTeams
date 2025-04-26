package dev.xf3d3.ultimateteams.commands.subCommands.warps;

import dev.xf3d3.ultimateteams.UltimateTeams;
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

        plugin.getTeamStorageUtil().findTeamByOwner(player.getUniqueId()).ifPresentOrElse(
                team -> team.getTeamWarp(name).ifPresentOrElse(
                        warp -> {
                            team.removeTeamWarp(name);
                            plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                            player.sendMessage(Utils.Color(messagesConfig.getString("team-warp-deleted-successful").replaceAll("%WARP_NAME%", warp.getName())));
                        },
                        () -> player.sendMessage(Utils.Color(messagesConfig.getString("team-warp-not-found")))
                ),
                () -> player.sendMessage(Utils.Color(messagesConfig.getString("failed-not-in-team")))
        );
    }
}