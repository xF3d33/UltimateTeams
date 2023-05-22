package dev.xf3d3.celestyteams.commands.teamSubCommands;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.api.ClanHomeDeleteEvent;
import dev.xf3d3.celestyteams.models.Team;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class TeamDelHomeSubCommand {

    FileConfiguration teamsConfig = CelestyTeams.getPlugin().getConfig();
    FileConfiguration messagesConfig = CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig();
    Logger logger = CelestyTeams.getPlugin().getLogger();

    private final CelestyTeams plugin;

    public TeamDelHomeSubCommand(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
    }

    public boolean deleteClanHomeSubCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            if (teamsConfig.getBoolean("team-home.enabled")){
                if (plugin.getTeamStorageUtil().findTeamByOwner(player) != null){
                    Team teamByOwner = plugin.getTeamStorageUtil().findTeamByOwner(player);
                    if (plugin.getTeamStorageUtil().isHomeSet(teamByOwner)){
                        fireClanHomeDeleteEvent(player, teamByOwner);
                        if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                            logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanHomeDeleteEvent"));
                        }
                        plugin.getTeamStorageUtil().deleteHome(teamByOwner);
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("successfully-deleted-team-home")));
                    }else {
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-no-home-set")));
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

    private static void fireClanHomeDeleteEvent(Player player, Team team) {
        ClanHomeDeleteEvent teamHomeDeleteEvent = new ClanHomeDeleteEvent(player, team);
        Bukkit.getPluginManager().callEvent(teamHomeDeleteEvent);
    }
}
