package dev.xf3d3.ultimateteams.commands.teamSubCommands;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.TeamHomeCreateEvent;
import dev.xf3d3.ultimateteams.team.Home;
import dev.xf3d3.ultimateteams.team.Position;
import dev.xf3d3.ultimateteams.team.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Level;

public class TeamSetHome {
    FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();

    private final UltimateTeams plugin;

    public TeamSetHome(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void setTeamHomeSubCommand(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (!plugin.getSettings().teamHomeEnabled()) {
            player.sendMessage(Utils.Color(messagesConfig.getString("function-disabled")));
        }

        if (!plugin.getManager().teams().isTeamOwner(player)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
        }

        plugin.getManager().teams().findTeamByOwner(player).ifPresentOrElse(
                team -> {
                    Location location = player.getLocation();
                    fireTeamHomeSetEvent(player, team, location);

                    Position position = Position.at(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(),
                            player.getWorld(), player.getLocation().getYaw(), player.getLocation().getPitch());
                    final Home home = Home.of(position, plugin.getServerName());

                    team.setHome(home);

                    plugin.runAsync(() -> plugin.getManager().updateTeamData(player, team));

                    player.sendMessage(Utils.Color(messagesConfig.getString("successfully-set-team-home")));
                },
                () -> player.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")))
        );


    }

    private void fireTeamHomeSetEvent(Player player, Team team, Location homeLocation) {
        TeamHomeCreateEvent teamHomeCreateEvent = new TeamHomeCreateEvent(player, team, homeLocation);
        Bukkit.getPluginManager().callEvent(teamHomeCreateEvent);
    }
}
