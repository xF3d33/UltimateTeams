package dev.xf3d3.ultimateteams.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamChatMessageSendEvent;
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
        if (!plugin.getSettings().teamChatEnabled()){
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

        Team team;
        if (plugin.getTeamStorageUtil().findTeamByOwner(player) != null) {
            team = plugin.getTeamStorageUtil().findTeamByOwner(player);
        } else {
            team = plugin.getTeamStorageUtil().findTeamByPlayer(player);
        }

        if (team == null) {
            player.sendMessage(Utils.Color(messagesConfig.getString("not-in-team")));
            return;
        }

        String chatSpyPrefix = plugin.getSettings().getTeamChatSpyPrefix();
        StringBuilder messageString = new StringBuilder();
        messageString.append(plugin.getSettings().getTeamChatPrefix()).append(" ");
        messageString.append("&d").append(player.getName()).append(":&r").append(" ");
        for (String arg : args) {
            messageString.append(arg).append(" ");
        }


        ArrayList<String> playerTeamMembers = team.getTeamMembers();

        fireTeamChatMessageSendEvent(
                player,
                team,
                plugin.getSettings().getTeamChatPrefix(),
                messageString.toString(),
                playerTeamMembers
        );

        // Send message to team owner
        final UUID ownerUUID = UUID.fromString(team.getTeamOwner());
        final Player playerTeamOwner = Bukkit.getPlayer(ownerUUID);
        if (playerTeamOwner != null) {
            playerTeamOwner.sendPlainMessage(Utils.Color(messageString.toString()));
        }

        // Send message to team members
        for (Player teamMember : team.getOnlineMembers()) {

            teamMember.sendPlainMessage(Utils.Color(messageString.toString()));
        }

        // Send spy message
        if (plugin.getSettings().teamChatSpyEnabled()) {
            Bukkit.broadcast(Utils.Color(chatSpyPrefix + " " + messageString), "ultimateteams.chat.spy");
        }
    }

    private void fireTeamChatMessageSendEvent(Player player, Team team, String prefix, String message, ArrayList<String> recipients) {
        TeamChatMessageSendEvent teamChatMessageSendEvent = new TeamChatMessageSendEvent(player, team, prefix, message, recipients);
        Bukkit.getPluginManager().callEvent(teamChatMessageSendEvent);
    }
}
