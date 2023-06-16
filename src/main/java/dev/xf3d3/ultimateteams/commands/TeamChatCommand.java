package dev.xf3d3.ultimateteams.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.TeamChatMessageSendEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

@SuppressWarnings("unused")
@CommandAlias("tc|teamchat")
public class TeamChatCommand extends BaseCommand {

    private final FileConfiguration teamsConfig;
    private final FileConfiguration messagesConfig;
    private final Logger logger;
    private final UltimateTeams plugin;

    public TeamChatCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
        this.teamsConfig = plugin.getConfig();
        this.logger = plugin.getLogger();
    }

    @Default
    @CommandCompletion("<message>")
    @Syntax("/tc <message>")
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof final Player player)){
            logger.warning(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        // Check if enabled
        if (!(teamsConfig.getBoolean("team-chat.enabled"))){
            player.sendMessage(Utils.Color(messagesConfig.getString("function-disabled")));
            return;
        }

        // Check args
        if (args.length < 1) {
            player.sendMessage(Utils.Color(
                    "&6UltimateTeams team chat usage:&3" +
                            "\n/cc <message>"
            ));
            return;
        }

        Team team;
        if (plugin.getTeamStorageUtil().findTeamByOwner(player) != null) {
            team = plugin.getTeamStorageUtil().findTeamByOwner(player);
        } else {
            team = plugin.getTeamStorageUtil().findTeamByPlayer(player);
        }

        String chatSpyPrefix = teamsConfig.getString("team-chat.chat-spy.chat-spy-prefix");
        StringBuilder messageString = new StringBuilder();
        messageString.append(teamsConfig.getString("team-chat.chat-prefix")).append(" ");
        messageString.append("&d").append(player.getName()).append(":&r").append(" ");
        for (String arg : args) {
            messageString.append(arg).append(" ");
        }


        if (team != null) {
            ArrayList<String> playerClanMembers = team.getTeamMembers();

            fireClanChatMessageSendEvent(
                    player,
                    team,
                    teamsConfig.getString("team-chat.chat-prefix"),
                    messageString.toString(),
                    playerClanMembers
            );

            // Send message to team owner
            final UUID ownerUUID = UUID.fromString(team.getTeamOwner());
            final Player playerTeamOwner = Bukkit.getPlayer(ownerUUID);
            if (playerTeamOwner != null) {
                playerTeamOwner.sendMessage(Utils.Color(messageString.toString()));
            }

            // Send message to team members
            assert playerClanMembers != null;
            for (String playerTeamMember : playerClanMembers) {
                if (playerTeamMember != null) {
                    UUID memberUUID = UUID.fromString(playerTeamMember);
                    Player TeamPlayer = Bukkit.getPlayer(memberUUID);

                    if (TeamPlayer != null) {
                        TeamPlayer.sendMessage(Utils.Color(messageString.toString()));
                    }
                }
            }

            // Send spy message
            if (teamsConfig.getBoolean("team-chat.chat-spy.enabled")) {
                Bukkit.broadcast(Utils.Color(chatSpyPrefix + " " + messageString), "ultimateteams.chat.spy");
            }
        }
    }

    private void fireClanChatMessageSendEvent(Player player, Team team, String prefix, String message, ArrayList<String> recipients) {
        TeamChatMessageSendEvent teamChatMessageSendEvent = new TeamChatMessageSendEvent(player, team, prefix, message, recipients);
        Bukkit.getPluginManager().callEvent(teamChatMessageSendEvent);
    }
}
