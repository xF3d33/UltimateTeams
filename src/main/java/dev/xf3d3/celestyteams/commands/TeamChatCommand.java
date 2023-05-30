package dev.xf3d3.celestyteams.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.api.ClanChatMessageSendEvent;
import dev.xf3d3.celestyteams.models.Team;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

@CommandAlias("tc|teamchat")
public class TeamChatCommand extends BaseCommand {

    private final FileConfiguration teamsConfig;
    private final FileConfiguration messagesConfig;
    private final Logger logger;
    private final CelestyTeams plugin;

    public TeamChatCommand(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.messagesFileManager.getMessagesConfig();
        this.teamsConfig = plugin.getConfig();
        this.logger = plugin.getLogger();
    }

    @Default
    @CommandCompletion("<message>")
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof final Player player)){
            logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("player-only-command")));
            return;
        }

        // Check if enabled
        if (!(teamsConfig.getBoolean("team-chat.enabled"))){
            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("function-disabled")));
            return;
        }

        // Check args
        if (args.length < 1) {
            player.sendMessage(ColorUtils.translateColorCodes(
                    "&6CelestyTeams team chat usage:&3" +
                            "\n/cc <message>"
            ));
            return;
        }

        Team teamByMember = plugin.getTeamStorageUtil().findClanByPlayer(player);
        Team teamByOwner = plugin.getTeamStorageUtil().findTeamByOwner(player);

        String chatSpyPrefix = teamsConfig.getString("team-chat.chat-spy.chat-spy-prefix");
        StringBuilder messageString = new StringBuilder();
        messageString.append(teamsConfig.getString("team-chat.chat-prefix")).append(" ");
        messageString.append("&d").append(player.getName()).append(":&r").append(" ");
        for (String arg : args) {
            messageString.append(arg).append(" ");
        }


        if (teamByMember != null) {
            ArrayList<String> playerClanMembers = teamByMember.getClanMembers();

            fireClanChatMessageSendEvent(
                    player,
                    teamByMember,
                    teamsConfig.getString("team-chat.chat-prefix"),
                    messageString.toString(),
                    playerClanMembers
            );

            // Send message to team owner
            final UUID ownerUUID = UUID.fromString(teamByMember.getTeamOwner());
            final Player playerTeamOwner = Bukkit.getPlayer(ownerUUID);
            if (playerTeamOwner != null) {
                playerTeamOwner.sendMessage(ColorUtils.translateColorCodes(messageString.toString()));
            }

            // Send message to team members
            assert playerClanMembers != null;
            for (String playerTeamMember : playerClanMembers) {
                if (playerTeamMember != null) {
                    UUID memberUUID = UUID.fromString(playerTeamMember);
                    Player TeamPlayer = Bukkit.getPlayer(memberUUID);

                    if (TeamPlayer != null) {
                        TeamPlayer.sendMessage(ColorUtils.translateColorCodes(messageString.toString()));
                    }
                }
            }

            // Send spy message
            if (teamsConfig.getBoolean("team-chat.chat-spy.enabled")) {
                Bukkit.broadcast(ColorUtils.translateColorCodes(chatSpyPrefix + " " + messageString), "celestyteams.chat.spy");
            }
        }

        if (teamByOwner != null) {
            ArrayList<String> ownerTeamMembers = teamByOwner.getClanMembers();

            fireClanChatMessageSendEvent(
                    player,
                    teamByOwner,
                    teamsConfig.getString("team-chat.chat-prefix"),
                    messageString.toString(),
                    ownerTeamMembers
            );

            // Send message to team owner
            player.sendMessage(ColorUtils.translateColorCodes(messageString.toString()));

            assert ownerTeamMembers != null;
            for (String ownerTeamMember : ownerTeamMembers) {
                if (ownerTeamMember != null) {
                    UUID memberUUID = UUID.fromString(ownerTeamMember);
                    Player ownerTeamPlayer = Bukkit.getPlayer(memberUUID);

                    if (ownerTeamPlayer != null) {
                        ownerTeamPlayer.sendMessage(ColorUtils.translateColorCodes(messageString.toString()));
                        //player.sendMessage(ColorUtils.translateColorCodes(messageString.toString()));
                    }
                }
            }

            // Send spy message
            if (teamsConfig.getBoolean("team-chat.chat-spy.enabled")) {
                Bukkit.broadcast(ColorUtils.translateColorCodes(chatSpyPrefix + " " + messageString), "celestyteams.chat.spy");
            }
        } else {
            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-must-be-in-team")));
        }

    }

    private void fireClanChatMessageSendEvent(Player player, Team team, String prefix, String message, ArrayList<String> recipients) {
        ClanChatMessageSendEvent teamChatMessageSendEvent = new ClanChatMessageSendEvent(player, team, prefix, message, recipients);
        Bukkit.getPluginManager().callEvent(teamChatMessageSendEvent);
    }
}
