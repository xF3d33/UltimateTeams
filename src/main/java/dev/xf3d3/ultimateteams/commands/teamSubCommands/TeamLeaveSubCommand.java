package dev.xf3d3.ultimateteams.commands.teamSubCommands;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

// todo: message when someone leave
public class TeamLeaveSubCommand {

    FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();
    private static final String Team_PLACEHOLDER = "%TEAM%";

    private final UltimateTeams plugin;

    public TeamLeaveSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public boolean teamLeaveSubCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            if (plugin.getTeamStorageUtil().findTeamByOwner(player) != null) {
                player.sendMessage(Utils.Color(messagesConfig.getString("failed-team-owner")));
                return true;
            }
            Team targetTeam = plugin.getTeamStorageUtil().findTeamByPlayer(player);
            if (targetTeam != null) {
                if (targetTeam.removeTeamMember(player.getUniqueId().toString())) {
                    String leaveMessage = Utils.Color(messagesConfig.getString("team-leave-successful")).replace(Team_PLACEHOLDER, targetTeam.getTeamFinalName());
                    player.sendMessage(leaveMessage);
                } else {
                    player.sendMessage(Utils.Color(messagesConfig.getString("team-leave-failed")));
                }
            }
            return true;

        }
        return false;
    }
}
