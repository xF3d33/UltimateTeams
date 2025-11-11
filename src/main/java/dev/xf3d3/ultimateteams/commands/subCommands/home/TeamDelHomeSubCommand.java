package dev.xf3d3.ultimateteams.commands.subCommands.home;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamHomeCreateEvent;
import dev.xf3d3.ultimateteams.api.events.TeamHomeDeleteEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamDelHomeSubCommand {
    private final UltimateTeams plugin;

    public TeamDelHomeSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void deleteTeamHomeSubCommand(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }


        if (!plugin.getSettings().teamHomeEnabled()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getFunctionDisabled()));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.HOME)))) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getNoPermission()));
                        return;
                    }


                    if (plugin.getTeamStorageUtil().isHomeSet(team)) {
                        if (new TeamHomeDeleteEvent(player, team).callEvent()) return;

                        plugin.getTeamStorageUtil().deleteHome(player, team);
                        player.sendMessage(MineDown.parse(plugin.getMessages().getSuccessfullyDeletedTeamHome()));
                    } else {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getFailedNoHomeSet()));
                    }
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getTeamMustBeOwner()))
        );
    }

}
