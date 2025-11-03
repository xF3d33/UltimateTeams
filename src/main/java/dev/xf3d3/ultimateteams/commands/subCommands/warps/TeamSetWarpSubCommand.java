package dev.xf3d3.ultimateteams.commands.subCommands.warps;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamWarp;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamSetWarpSubCommand {
    private final FileConfiguration messagesConfig;
    private final UltimateTeams plugin;

    public TeamSetWarpSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
    }

    public void setWarpCommand(CommandSender sender, String name) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }


        if (!plugin.getSettings().teamWarpEnabled()) {
            player.sendMessage(Utils.Color(messagesConfig.getString("function-disabled")));
            return;
        }

        if (name.contains(" ")) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("incorrect-command-usage")));

            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.WARPS)))) {
                        sender.sendMessage(Utils.Color(messagesConfig.getString("no-permission")));
                        return;
                    }

                    plugin.getUsersStorageUtil().getPlayer(player.getUniqueId()).thenAccept(teamPlayer -> {
                        // Check if team has reached max warps limit
                        if (team.hasReachedMaxWarps()) {
                            player.sendMessage(Utils.Color("&cYour team has reached the maximum warp limit of &e" + team.getMaxWarps() + "&c!"));
                            player.sendMessage(Utils.Color("&7Use &e/team upgrade warps &7to increase this limit."));
                            return;
                        }

                        if (team.getTeamWarp(name).isPresent()) {
                            player.sendMessage(Utils.Color(messagesConfig.getString("team-warp-name-used")));
                            return;
                        }

                        final TeamWarp warp = TeamWarp.of(name, player.getLocation(), plugin.getSettings().getServerName());

                        team.addTeamWarp(warp);
                        plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                        player.sendMessage(Utils.Color(messagesConfig.getString("team-warp-successful").replaceAll("%WARP_NAME%", warp.getName())));
                    });
                },
                () -> player.sendMessage(Utils.Color(messagesConfig.getString("not-in-team")))
        );
    }
}
