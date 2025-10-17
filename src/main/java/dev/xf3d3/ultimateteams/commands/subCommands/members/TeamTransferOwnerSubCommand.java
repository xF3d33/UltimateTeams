package dev.xf3d3.ultimateteams.commands.subCommands.members;

import co.aikar.commands.annotation.Values;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamTransferOwnerSubCommand {

    FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();
    private final UltimateTeams plugin;

    public TeamTransferOwnerSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void transferTeamOwnerSubCommand(CommandSender sender, @Values("teamPlayers") String memberName) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        final String PLAYER_PLACEHOLDER = "%PLAYER%";
        final OfflinePlayer newTeamOwner = Bukkit.getOfflinePlayer(memberName);

        if (!plugin.getTeamStorageUtil().isTeamOwner(player)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            return;
        }

        if (newTeamOwner.getName() == null) {
            player.sendMessage(Utils.Color(messagesConfig.getString("player-not-found")));
            return;
        }

        if (player.getUniqueId().equals(newTeamOwner.getUniqueId())) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-ownership-transfer-failed-cannot-transfer-to-self")
                    .replace(PLAYER_PLACEHOLDER, player.getName())));

            return;
        }

        plugin.getTeamStorageUtil().findTeamByOwner(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    if (!team.getMembers().containsKey(newTeamOwner.getUniqueId())) {
                        player.sendMessage(Utils.Color(messagesConfig.getString("team-ownership-transfer-failure-not-same-team")));
                        return;
                    }

                    plugin.getTeamStorageUtil().transferTeamOwner(team, newTeamOwner.getUniqueId());

                    String OLD_OWNER_PLACEHOLDER = "%OLDOWNER%";
                    String NEW_Team_NAME = "%Team%";

                    player.sendMessage(Utils.Color(messagesConfig.getString("team-ownership-transfer-successful")
                            .replace(PLAYER_PLACEHOLDER, newTeamOwner.getName())));

                    if (newTeamOwner.isOnline() && newTeamOwner.getPlayer() != null) {
                        newTeamOwner.getPlayer().sendMessage(Utils.Color(messagesConfig.getString("team-ownership-transfer-new-owner")
                                .replace(OLD_OWNER_PLACEHOLDER, player.getName()).replace(NEW_Team_NAME, team.getName())));
                    }
                },
                () -> player.sendMessage(Utils.Color(messagesConfig.getString("not-in-team")))
        );

    }
}
