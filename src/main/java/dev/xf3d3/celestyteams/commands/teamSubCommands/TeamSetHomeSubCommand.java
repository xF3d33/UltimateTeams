package dev.xf3d3.celestyteams.commands.teamSubCommands;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.api.ClanHomeCreateEvent;
import dev.xf3d3.celestyteams.models.Team;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class TeamSetHomeSubCommand {

    FileConfiguration teamsConfig = CelestyTeams.getPlugin().getConfig();
    FileConfiguration messagesConfig = CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig();
    Logger logger = CelestyTeams.getPlugin().getLogger();

    private final CelestyTeams plugin;

    public TeamSetHomeSubCommand(@NotNull CelestyTeams plugin) {
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
                            logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanHomeSetEvent"));
                        }

                        team.setClanHomeWorld(player.getLocation().getWorld().getName());
                        team.setClanHomeX(player.getLocation().getX());
                        team.setClanHomeY(player.getLocation().getY());
                        team.setClanHomeZ(player.getLocation().getZ());
                        team.setClanHomeYaw(player.getLocation().getYaw());
                        team.setClanHomePitch(player.getLocation().getPitch());

                        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));

                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("successfully-set-team-home")));
                    }
                }else {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-must-be-owner")));
                }
            }else {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("function-disabled")));
            }
            return true;

        }
        return false;
    }

    private static void fireClanHomeSetEvent(Player player, Team team, Location homeLocation) {
        ClanHomeCreateEvent teamHomeCreateEvent = new ClanHomeCreateEvent(player, team, homeLocation);
        Bukkit.getPluginManager().callEvent(teamHomeCreateEvent);
    }
}
