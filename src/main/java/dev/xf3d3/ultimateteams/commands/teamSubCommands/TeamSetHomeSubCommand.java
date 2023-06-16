package dev.xf3d3.ultimateteams.commands.teamSubCommands;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.TeamHomeCreateEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TeamSetHomeSubCommand {

    FileConfiguration teamsConfig = UltimateTeams.getPlugin().getConfig();
    FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();
    Logger logger = UltimateTeams.getPlugin().getLogger();

    private final UltimateTeams plugin;

    public TeamSetHomeSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public boolean setClanHomeSubCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            if (teamsConfig.getBoolean("team-home.enabled")){
                if (plugin.getTeamStorageUtil().isClanOwner(player)){
                    if (plugin.getTeamStorageUtil().findTeamByOwner(player) != null){
                        Team team = plugin.getTeamStorageUtil().findTeamByOwner(player);
                        Location location = player.getLocation();
                        fireClanHomeSetEvent(player, team, location);

                        if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                            plugin.log(Level.INFO, Utils.Color("&6UltimateTeams-Debug: &aFired ClanHomeSetEvent"));
                        }

                        team.setTeamHomeWorld(player.getLocation().getWorld().getName());
                        team.setTeamHomeX(player.getLocation().getX());
                        team.setTeamHomeY(player.getLocation().getY());
                        team.setTeamHomeZ(player.getLocation().getZ());
                        team.setTeamHomeYaw(player.getLocation().getYaw());
                        team.setTeamHomePitch(player.getLocation().getPitch());

                        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));

                        player.sendMessage(Utils.Color(messagesConfig.getString("successfully-set-team-home")));
                    }
                }else {
                    player.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
                }
            }else {
                player.sendMessage(Utils.Color(messagesConfig.getString("function-disabled")));
            }
            return true;

        }
        return false;
    }

    private static void fireClanHomeSetEvent(Player player, Team team, Location homeLocation) {
        TeamHomeCreateEvent teamHomeCreateEvent = new TeamHomeCreateEvent(player, team, homeLocation);
        Bukkit.getPluginManager().callEvent(teamHomeCreateEvent);
    }
}
