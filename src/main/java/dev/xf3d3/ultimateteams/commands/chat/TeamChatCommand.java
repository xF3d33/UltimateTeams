package dev.xf3d3.ultimateteams.commands.chat;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import de.themoep.minedown.adventure.MineDown;
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
    private final Logger logger;
    private final UltimateTeams plugin;

    public TeamChatCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    @Default
    @CommandCompletion("<message>")
    @Syntax("/tc <message>")
    @CommandPermission("ultimateteams.teamchat")
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof final Player player)){
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        // Check if enabled
        if (!plugin.getSettings().teamChatEnabled()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getFunctionDisabled()));
            return;
        }

        // Check args
        if (args.length < 1) {
            if (plugin.getUsersStorageUtil().getChatPlayers().containsKey(player.getUniqueId())) {
                plugin.getUsersStorageUtil().getChatPlayers().remove(player.getUniqueId());
                player.sendMessage(MineDown.parse(plugin.getMessages().getChatToggleOff()));

                plugin.getUsersStorageUtil().getPlayer(player.getUniqueId()).thenAcceptAsync(teamPlayer -> {
                    teamPlayer.getPreferences().setTeamChatTalking(false);
                    plugin.getDatabase().updatePlayer(teamPlayer);
                });
            } else {
                plugin.getUsersStorageUtil().getChatPlayers().put(player.getUniqueId(), true);
                player.sendMessage(MineDown.parse(plugin.getMessages().getChatToggleOn()));

                plugin.getUsersStorageUtil().getPlayer(player.getUniqueId()).thenAcceptAsync(teamPlayer -> {
                    teamPlayer.getPreferences().setTeamChatTalking(true);
                    plugin.getDatabase().updatePlayer(teamPlayer);
                });
            }

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

                    // Send spy message directly to players with permission (hidden from Discord)
                    if (plugin.getSettings().teamChatSpyEnabled()) {
                        String spyMessage = Utils.Color(chatSpyPrefix + " " + msg);
                        Bukkit.getOnlinePlayers().stream()
                                .filter(p -> p.hasPermission("ultimateteams.chat.spy"))
                                .forEach(p -> p.sendMessage(spyMessage));
                    }

                    // Send globally via a message
                    plugin.getMessageBroker().ifPresent(broker -> Message.builder()
                            .type(Message.Type.TEAM_CHAT_MESSAGE)
                            .payload(Payload.string(msg))
                            .target(Message.TARGET_ALL, Message.TargetType.SERVER)
                            .build()
                            .send(broker, player));
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()))
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
