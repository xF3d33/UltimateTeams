package dev.xf3d3.ultimateteams.commands.subCommands.members;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamPvpSubCommand {
    private final FileConfiguration messagesConfig;
    private final UltimateTeams plugin;

    public TeamPvpSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
    }

    public void teamPvpSubCommand(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (!plugin.getSettings().isPvpCommandEnabled()) {
            player.sendMessage(Utils.Color(messagesConfig.getString("function-disabled")));
            return;
        }

        if (plugin.getTeamStorageUtil().isTeamOwner(player)) {
            if (plugin.getTeamStorageUtil().findTeamByOwner(player) != null) {
                final Team team = plugin.getTeamStorageUtil().findTeamByOwner(player);

                if (team.isFriendlyFireAllowed()){

                    team.setFriendlyFireAllowed(false);
                    plugin.runAsync(task -> plugin.getDatabase().updateTeam(team));

                    player.sendMessage(Utils.Color(messagesConfig.getString("disabled-friendly-fire")));
                } else {
                    team.setFriendlyFireAllowed(true);
                    plugin.runAsync(task -> plugin.getDatabase().updateTeam(team));

                    player.sendMessage(Utils.Color(messagesConfig.getString("enabled-friendly-fire")));
                }
            } else {
                player.sendMessage(Utils.Color(messagesConfig.getString("failed-not-in-team")));
            }
        } else {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
        }
    }
}
