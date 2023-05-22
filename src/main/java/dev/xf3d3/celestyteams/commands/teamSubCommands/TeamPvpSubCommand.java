package dev.xf3d3.celestyteams.commands.teamSubCommands;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.models.Team;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamPvpSubCommand {

    FileConfiguration teamsConfig = CelestyTeams.getPlugin().getConfig();
    FileConfiguration messagesConfig = CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig();

    private final CelestyTeams plugin;

    public TeamPvpSubCommand(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
    }

    public boolean teamPvpSubCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            if (teamsConfig.getBoolean("protections.pvp.pvp-command-enabled")){
                if (plugin.getTeamStorageUtil().isClanOwner(player)){
                    if (plugin.getTeamStorageUtil().findTeamByOwner(player) != null){
                        Team team = plugin.getTeamStorageUtil().findTeamByOwner(player);
                        if (team.isFriendlyFireAllowed()){

                            team.setFriendlyFireAllowed(false);
                            plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));

                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("disabled-friendly-fire")));
                        }else {
                            team.setFriendlyFireAllowed(true);
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("enabled-friendly-fire")));
                        }
                    }else {
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-not-in-team")));
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
}
