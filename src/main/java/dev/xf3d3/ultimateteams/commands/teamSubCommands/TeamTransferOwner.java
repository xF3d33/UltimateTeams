package dev.xf3d3.ultimateteams.commands.teamSubCommands;

import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamTransferOwner {

    FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();
    private final UltimateTeams plugin;

    public TeamTransferOwner(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void transferTeamOwnerSubCommand(CommandSender sender, OnlinePlayer onlinePlayer){
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        final String PLAYER_PLACEHOLDER = "%PLAYER%";
        final Player newTeamOwner = onlinePlayer.getPlayer();

        if (newTeamOwner == player) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-ownership-transfer-failed-cannot-transfer-to-self")
                    .replace(PLAYER_PLACEHOLDER, player.getName())));
            return;
        }

        if (plugin.getManager().teams().isTeamOwner(player)) {
            plugin.getManager().teams().findTeamByOwner(player).ifPresent(team -> {
                if (plugin.getManager().teams().transferTeamOwner(team, player, newTeamOwner)) {

                    plugin.getManager().teams().findTeamByOwner(newTeamOwner).ifPresent(newTeam -> {
                        String OLD_OWNER_PLACEHOLDER = "%OLDOWNER%";
                        String NEW_Team_NAME = "%TEAM%";

                        player.sendMessage(Utils.Color(messagesConfig.getString("team-ownership-transfer-successful")
                                .replace(PLAYER_PLACEHOLDER, newTeamOwner.getName())));
                        newTeamOwner.sendMessage(Utils.Color(messagesConfig.getString("team-ownership-transfer-new-owner")
                                .replace(OLD_OWNER_PLACEHOLDER, player.getName()).replace(NEW_Team_NAME, newTeam.getName())));
                    });
                }
            });
        } else {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
        }
    }
}
