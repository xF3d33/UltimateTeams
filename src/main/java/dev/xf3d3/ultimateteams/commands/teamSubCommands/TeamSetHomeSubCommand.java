package dev.xf3d3.ultimateteams.commands.teamSubCommands;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.TeamHomeCreateEvent;
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

public class TeamSetHomeSubCommand {
    FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();

    private final UltimateTeams plugin;

    public TeamSetHomeSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void setTeamHomeSubCommand(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (plugin.getSettings().teamHomeEnabled()) {
            if (plugin.getTeamStorageUtil().isTeamOwner(player)) {
                if (plugin.getTeamStorageUtil().findTeamByOwner(player) != null) {
                    Team team = plugin.getTeamStorageUtil().findTeamByOwner(player);
                    Location location = player.getLocation();
                    fireTeamHomeSetEvent(player, team, location);

                    if (plugin.getSettings().debugModeEnabled()) {
                        plugin.log(Level.INFO, Utils.Color("&6UltimateTeams-Debug: &aFired TeamHomeSetEvent"));
                    }

                    team.setTeamHomeWorld(Objects.requireNonNull(player.getLocation().getWorld()).getName());
                    team.setTeamHomeX(player.getLocation().getX());
                    team.setTeamHomeY(player.getLocation().getY());
                    team.setTeamHomeZ(player.getLocation().getZ());
                    team.setTeamHomeYaw(player.getLocation().getYaw());
                    team.setTeamHomePitch(player.getLocation().getPitch());

                    plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));

                    player.sendMessage(Utils.Color(messagesConfig.getString("successfully-set-team-home")));
                }
            } else {
                player.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            }
        } else {
            player.sendMessage(Utils.Color(messagesConfig.getString("function-disabled")));
        }
    }

    private void fireTeamHomeSetEvent(Player player, Team team, Location homeLocation) {
        TeamHomeCreateEvent teamHomeCreateEvent = new TeamHomeCreateEvent(player, team, homeLocation);
        Bukkit.getPluginManager().callEvent(teamHomeCreateEvent);
    }
}
