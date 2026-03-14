package dev.xf3d3.ultimateteams.commands.subCommands.home;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamHomeDeleteEvent;
import dev.xf3d3.ultimateteams.models.Team;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamDelHomeSubCommand {
    private final UltimateTeams plugin;

    public TeamDelHomeSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void deleteTeamHomeSubCommand(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getPlayerOnlyCommand()));
            return;
        }


        if (!plugin.getSettings().getTeam().getHome().isEnabled()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getFunctionDisabled()));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.HOME)))) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getNoPermission()));
                        return;
                    }


                    if (plugin.getTeamStorageUtil().isHomeSet(team)) {
                        if (!(new TeamHomeDeleteEvent(player, team).callEvent())) return;

                        plugin.getTeamStorageUtil().deleteHome(player, team);
                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getHome().getDeleteSuccessful()));
                    } else {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getHome().getNoHomeSet()));
                    }
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getMustBeOwner()))
        );
    }

}
