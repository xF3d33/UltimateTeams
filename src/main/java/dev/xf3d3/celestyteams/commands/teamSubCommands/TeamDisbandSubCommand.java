package dev.xf3d3.celestyteams.commands.teamSubCommands;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class TeamDisbandSubCommand {

    FileConfiguration messagesConfig = CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig();

    //TODO: ask for confirmation

    private final CelestyTeams plugin;

    public TeamDisbandSubCommand(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
    }

    public boolean disbandClanSubCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
                try {
                    if (plugin.getTeamStorageUtil().deleteTeam(player)) {
                        sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-successfully-disbanded")));
                    } else {
                        sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-disband-failure")));
                    }
                } catch (IOException e) {
                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("teams-update-error-1")));
                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("teams-update-error-2")));
                    e.printStackTrace();
                }
                return true;
        }
        return false;
    }
}
