package dev.xf3d3.ultimateteams.commands.subCommands.warps;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamWarpSetEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamWarp;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamSetWarpSubCommand {
    private final UltimateTeams plugin;

    public TeamSetWarpSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void setWarpCommand(CommandSender sender, String name) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }


        if (!plugin.getSettings().teamWarpEnabled()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getFunctionDisabled()));
            return;
        }

        if (name.contains(" ")) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getIncorrectCommandUsage()));

            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.WARPS)))) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getNoPermission()));
                        return;
                    }

                    plugin.getUsersStorageUtil().getPlayer(player.getUniqueId()).thenAccept(teamPlayer -> {
                        if (team.getWarps().size() >= teamPlayer.getMaxWarps(player, plugin.getSettings().getTeamWarpLimit(), plugin.getSettings().getTeamWarpStackEnabled())) {
                            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamWarpLimitReached()));
                            return;
                        }

                        if (team.getTeamWarp(name).isPresent()) {
                            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamWarpNameUsed()));
                            return;
                        }

                        final TeamWarp warp = TeamWarp.of(name, player.getLocation(), plugin.getSettings().getServerName());

                        if (new TeamWarpSetEvent(player, team, warp).callEvent())  return;

                        team.addTeamWarp(warp);
                        plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeamWarpSuccessful().replaceAll("%WARP_NAME%", warp.getName())));
                    });
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()))
        );
    }
}
