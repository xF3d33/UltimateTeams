package dev.xf3d3.ultimateteams.commands.teamSubCommands;

import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class TeamTransferOwnerSubCommand {

    FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();
    private final UltimateTeams plugin;

    public TeamTransferOwnerSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void transferClanOwnerSubCommand(CommandSender sender, OnlinePlayer onlinePlayer){
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        final String PLAYER_PLACEHOLDER = "%PLAYER%";
        final Player newClanOwner = onlinePlayer.getPlayer();

        if (newClanOwner != player) {
            if (plugin.getTeamStorageUtil().isClanOwner(player)) {
                Team originalTeam = plugin.getTeamStorageUtil().findTeamByOwner(player);
                if (originalTeam != null) {
                    try {
                        Team newTeam = plugin.getTeamStorageUtil().transferClanOwner(originalTeam, player, newClanOwner);
                        if (newTeam != null){
                            String OLD_OWNER_PLACEHOLDER = "%OLDOWNER%";
                            String NEW_CLAN_NAME = "%CLAN%";
                            player.sendMessage(Utils.Color(messagesConfig.getString("team-ownership-transfer-successful")
                                    .replace(PLAYER_PLACEHOLDER, newClanOwner.getName())));
                            newClanOwner.sendMessage(Utils.Color(messagesConfig.getString("team-ownership-transfer-new-owner")
                                    .replace(OLD_OWNER_PLACEHOLDER, player.getName()).replace(NEW_CLAN_NAME, newTeam.getTeamFinalName())));
                        }
                    } catch (IOException e) {
                        sender.sendMessage(Utils.Color(messagesConfig.getString("teams-update-error-1")));
                        sender.sendMessage(Utils.Color(messagesConfig.getString("teams-update-error-2")));
                        e.printStackTrace();
                    }
                }
            } else {
                player.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            }
        } else {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-ownership-transfer-failed-cannot-transfer-to-self")
                    .replace(PLAYER_PLACEHOLDER, player.getName())));
        }
    }
}
