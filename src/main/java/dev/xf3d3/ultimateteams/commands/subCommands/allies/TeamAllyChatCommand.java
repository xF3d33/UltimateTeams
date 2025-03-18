package dev.xf3d3.ultimateteams.commands.subCommands.allies;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

@SuppressWarnings("unused")
@CommandAlias("ac|allychat|achat")
public class TeamAllyChatCommand extends BaseCommand {
    private final FileConfiguration messagesConfig;
    private final Logger logger;
    private final UltimateTeams plugin;

    public TeamAllyChatCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
        this.logger = plugin.getLogger();
    }

    @Default
    @CommandCompletion("<message>")
    @Syntax("/allychat <message>")
    @CommandPermission("ultimateteams.allychat")
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof final Player player)){
            logger.warning(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        // Check if enabled
        if (!plugin.getSettings().teamAllyChatEnabled()){
            player.sendMessage(Utils.Color(messagesConfig.getString("function-disabled")));
            return;
        }

        // Check args
        if (args.length < 1) {
            player.sendMessage(Utils.Color(
                    "&6UltimateTeams team chat usage:&3" +
                            "\n/allychat <message>"
            ));
            return;
        }

        Team team;
        if (plugin.getTeamStorageUtil().findTeamByOwner(player) != null) {
            team = plugin.getTeamStorageUtil().findTeamByOwner(player);
        } else {
            team = plugin.getTeamStorageUtil().findTeamByPlayer(player);
        }

        String chatSpyPrefix = plugin.getSettings().getTeamChatSpyPrefix();
        StringBuilder messageString = new StringBuilder();
        messageString.append(plugin.getSettings().getTeamAllyChatPrefix()).append(" ");
        messageString.append("&d").append(player.getName()).append(":&r").append(" ");
        for (String arg : args) {
            messageString.append(arg).append(" ");
        }


        if (team != null) {
            ArrayList<String> playerClanMembers = team.getTeamMembers();

            // Send message to team owner
            final UUID ownerUUID = UUID.fromString(team.getTeamOwner());
            final Player playerTeamOwner = Bukkit.getPlayer(ownerUUID);
            if (playerTeamOwner != null) {
                playerTeamOwner.sendMessage(Utils.Color(messageString.toString()));
            }

            // Send message to team members
            for (String playerTeamMember : playerClanMembers) {
                if (playerTeamMember != null) {
                    UUID memberUUID = UUID.fromString(playerTeamMember);
                    Player TeamPlayer = Bukkit.getPlayer(memberUUID);

                    if (TeamPlayer != null) {
                        TeamPlayer.sendMessage(Utils.Color(messageString.toString()));
                    }
                }
            }

            // Send message to team allies
            for (String allyTeam : team.getTeamAllies()) {
                final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(allyTeam));
                final Team ally = plugin.getTeamStorageUtil().findTeamByOfflineOwner(offlinePlayer);

                final Player teamOwner = Bukkit.getPlayer(UUID.fromString(allyTeam));

                if (teamOwner != null) {
                    teamOwner.sendMessage(Utils.Color(messageString.toString()));
                }

                for (String playerUUID : ally.getTeamMembers()) {
                    final Player allyPlayer = Bukkit.getPlayer(UUID.fromString(playerUUID));

                    if (allyPlayer != null) {
                        allyPlayer.sendMessage(Utils.Color(messageString.toString()));
                    }
                }
            }

            // Send spy message
            if (plugin.getSettings().teamChatSpyEnabled()) {
                Bukkit.broadcast(Utils.Color(chatSpyPrefix + " " + messageString), "ultimateteams.chat.spy");
            }
        }
    }
}
