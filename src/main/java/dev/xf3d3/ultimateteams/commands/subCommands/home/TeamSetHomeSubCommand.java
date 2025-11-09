package dev.xf3d3.ultimateteams.commands.subCommands.home;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamHomeCreateEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamHome;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class TeamSetHomeSubCommand {
    private final UltimateTeams plugin;

    public TeamSetHomeSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void setTeamHomeSubCommand(CommandSender sender) {
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

                    Location location = player.getLocation();
                    fireTeamHomeSetEvent(player, team, location);

                    if (plugin.getSettings().debugModeEnabled()) {
                        plugin.log(Level.INFO, Utils.Color("&6UltimateTeams-Debug: &aFired TeamHomeSetEvent"));
                    }

                    final TeamHome home = TeamHome.of(location, plugin.getSettings().getServerName());
                    team.setHome(home);

                    plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));
                    player.sendMessage(MineDown.parse(plugin.getMessages().getSuccessfullySetTeamHome()));
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getFailedNotInTeam()))
        );
    }

    private void fireTeamHomeSetEvent(Player player, Team team, Location homeLocation) {
        TeamHomeCreateEvent teamHomeCreateEvent = new TeamHomeCreateEvent(player, team, homeLocation);
        Bukkit.getPluginManager().callEvent(teamHomeCreateEvent);
    }
}
