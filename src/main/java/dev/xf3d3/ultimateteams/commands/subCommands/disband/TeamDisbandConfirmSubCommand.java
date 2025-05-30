package dev.xf3d3.ultimateteams.commands.subCommands.disband;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamDisbandConfirmSubCommand {

    private final FileConfiguration messagesConfig;
    private final UltimateTeams plugin;

    public TeamDisbandConfirmSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
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

        plugin.getTeamStorageUtil().findTeamByOwner(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    plugin.runAsync(task -> plugin.getTeamStorageUtil().deleteTeamData(player, team));
                    player.sendMessage(Utils.Color(messagesConfig.getString("team-successfully-disbanded")));
                },
                () -> sender.sendMessage(Utils.Color(messagesConfig.getString("team-disband-failure")))
        );
    }
}
