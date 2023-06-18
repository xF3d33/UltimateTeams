package dev.xf3d3.ultimateteams.commands.teamSubCommands;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamDisbandSubCommand {

    FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();

    //TODO: ask for confirmation

    private final UltimateTeams plugin;

    public TeamDisbandSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void disbandTeamSubCommand(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (!plugin.getTeamStorageUtil().isTeamOwner(player)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            return;
        }

        if (plugin.getTeamStorageUtil().deleteTeam(player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("team-successfully-disbanded")));
        } else {
            sender.sendMessage(Utils.Color(messagesConfig.getString("team-disband-failure")));
        }
    }
}
