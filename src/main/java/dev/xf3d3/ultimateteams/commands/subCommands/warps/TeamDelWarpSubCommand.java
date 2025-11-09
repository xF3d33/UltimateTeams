package dev.xf3d3.ultimateteams.commands.subCommands.warps;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamDelWarpSubCommand {

    private final UltimateTeams plugin;

    public TeamDelWarpSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void delWarpCommand(CommandSender sender, String name) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        if (!plugin.getSettings().teamWarpEnabled()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getFunctionDisabled()));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.WARPS)))) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getNoPermission()));
                        return;
                    }

                    team.getTeamWarp(name).ifPresentOrElse(
                            warp -> {
                                team.removeTeamWarp(name);
                                plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                                player.sendMessage(MineDown.parse(plugin.getMessages().getTeamWarpDeletedSuccessful().replaceAll("%WARP_NAME%", warp.getName())));
                            },
                            () -> player.sendMessage(MineDown.parse(plugin.getMessages().getTeamWarpNotFound()))
                    );
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()))
        );
    }
}