package dev.xf3d3.ultimateteams.commands.chat;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamChatMessageSendEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.network.Message;
import dev.xf3d3.ultimateteams.network.Payload;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@SuppressWarnings("unused")
@CommandAlias("tc|teamchat|tchat")
public class TeamChatCommand extends BaseCommand {
    private final FileConfiguration messagesConfig;
    private final Logger logger;
    private final UltimateTeams plugin;

    public TeamChatCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
        this.logger = plugin.getLogger();
    }

    @Default
    @CommandCompletion("<message>")
    @Syntax("/tc <message>")
    @CommandPermission("ultimateteams.teamchat")
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof final Player player)){
            logger.warning(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        // Check if enabled
        if (!plugin.getSettings().teamChatEnabled()) {
            player.sendMessage(Utils.Color(messagesConfig.getString("function-disabled")));
            return;
        }

        // Check args
        if (args.length < 1) {
            player.sendMessage(Utils.Color(
                    "&6UltimateTeams team chat usage:&3" +
                            "\n/tc <message>"
            ));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    String chatSpyPrefix = plugin.getSettings().getTeamChatSpyPrefix();
                    StringBuilder messageString = new StringBuilder();
                    messageString.append(plugin.getSettings().getTeamChatPrefix()).append(" ");
                    messageString.append("&d").append(player.getName()).append(":&r").append(" ");
                    for (String arg : args) {
                        messageString.append(arg).append(" ");
                    }

                    final String msg = messageString.toString()
                            .replace("%TEAM%", team.getName())
                            .replace("%PLAYER%", player.getName());

                    // Send message to team members
                    team.sendTeamMessage(Utils.Color(msg));

                    // Send spy message
                    if (plugin.getSettings().teamChatSpyEnabled()) {
                        Bukkit.broadcast(Utils.Color(chatSpyPrefix + " " + msg), "ultimateteams.chat.spy");
                    }

                    // Send globally via a message
                    plugin.getMessageBroker().ifPresent(broker -> Message.builder()
                            .type(Message.Type.TEAM_CHAT_MESSAGE)
                            .payload(Payload.string(msg))
                            .target(Message.TARGET_ALL, Message.TargetType.SERVER)
                            .build()
                            .send(broker, player));
                },
                () -> player.sendMessage(Utils.Color(messagesConfig.getString("not-in-team")))
        );


        /*List<UUID> playerTeamMembers = team.getMembers().keySet().stream().toList();

        fireTeamChatMessageSendEvent(
                player,
                team,
                plugin.getSettings().getTeamChatPrefix(),
                messageString.toString(),
                playerTeamMembers
        );*/
    }

    private void fireTeamChatMessageSendEvent(Player player, Team team, String prefix, String message, List<UUID> recipients) {
        TeamChatMessageSendEvent teamChatMessageSendEvent = new TeamChatMessageSendEvent(player, team, prefix, message, recipients);
        Bukkit.getPluginManager().callEvent(teamChatMessageSendEvent);
    }
}
