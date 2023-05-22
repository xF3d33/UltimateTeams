package dev.xf3d3.celestyteams.commands.teamSubCommands;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.models.Team;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamLeaveSubCommand {

    FileConfiguration messagesConfig = CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig();
    private static final String CLAN_PLACEHOLDER = "%CLAN%";

    private final CelestyTeams plugin;

    public TeamLeaveSubCommand(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
    }

    public boolean teamLeaveSubCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            if (plugin.getTeamStorageUtil().findTeamByOwner(player) != null) {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-team-owner")));
                return true;
            }
            Team targetTeam = plugin.getTeamStorageUtil().findClanByPlayer(player);
            if (targetTeam != null) {
                if (targetTeam.removeClanMember(player.getUniqueId().toString())) {
                    String leaveMessage = ColorUtils.translateColorCodes(messagesConfig.getString("team-leave-successful")).replace(CLAN_PLACEHOLDER, targetTeam.getTeamFinalName());
                    player.sendMessage(leaveMessage);
                } else {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-leave-failed")));
                }
            }
            return true;

        }
        return false;
    }
}
