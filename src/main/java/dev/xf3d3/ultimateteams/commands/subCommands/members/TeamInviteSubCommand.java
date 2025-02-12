package dev.xf3d3.ultimateteams.commands.subCommands.members;

import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamInvite;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TeamInviteSubCommand {

    private final FileConfiguration messagesConfig;
    private static final String INVITED_PLAYER = "%INVITED%";
    private static final String PLAYER_PLACEHOLDER = "%PLAYER%";
    private static final String TEAM_PLACEHOLDER = "%TEAM%";

    private final UltimateTeams plugin;

    public TeamInviteSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
    }

    public void teamInviteSendSubCommand(CommandSender sender, OnlinePlayer onlinePlayer) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (!plugin.getTeamStorageUtil().isTeamOwner(player)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            return;
        }

        Player invitedPlayer = onlinePlayer.getPlayer();

            if (plugin.getTeamStorageUtil().findTeamByOwner(player) == null) {
                sender.sendMessage(Utils.Color(messagesConfig.getString("team-invite-not-team-owner")));
                return;
            }

        if (invitedPlayer.getName().equalsIgnoreCase(player.getName())) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("team-invite-self-error")));
        } else {

            // Check if the player is already in a team
            if (plugin.getTeamStorageUtil().findTeamByPlayer(invitedPlayer) != null) {
                String playerAlreadyInTeam = Utils.Color(messagesConfig.getString("team-invite-invited-already-in-team")).replace(INVITED_PLAYER, invitedPlayer.getName());
                sender.sendMessage(playerAlreadyInTeam);
                return;
            }

            // Check if the player has already a team
            if (plugin.getTeamStorageUtil().findTeamByOwner(invitedPlayer) != null) {
                String playerAlreadyInTeam = Utils.Color(messagesConfig.getString("team-invite-invited-already-in-team")).replace(INVITED_PLAYER, invitedPlayer.getName());
                sender.sendMessage(playerAlreadyInTeam);
                return;
            }

            // Check if the player has another active invite
            if (plugin.getTeamInviteUtil().hasInvitee(invitedPlayer.getUniqueId().toString())) {
                player.sendMessage(Utils.Color(messagesConfig.getString("team-invite-failed").replaceAll("%INVITED%", invitedPlayer.getName())));
                return;
            }

            Team team = plugin.getTeamStorageUtil().findTeamByOwner(player);

            if (team.getTeamMembers().size() >= plugin.getSettings().getTeamMaxSize()) {
                int maxSize = plugin.getSettings().getTeamMaxSize();
                player.sendMessage(Utils.Color(messagesConfig.getString("team-invite-max-size-reached")).replace("%LIMIT%", String.valueOf(maxSize)));
                return;
            }

            if (plugin.getSettings().FloodGateHook()) {
                if (plugin.getFloodgateApi() != null) {
                    if (plugin.getBedrockPlayers().containsKey(invitedPlayer)) {
                        String bedrockInvitedPlayerUUIDString = plugin.getBedrockPlayers().get(invitedPlayer);
                        if (plugin.getTeamInviteUtil().createInvite(player.getUniqueId().toString(), bedrockInvitedPlayerUUIDString) != null) {
                            String confirmationString = Utils.Color(messagesConfig.getString("team-invite-successful")).replace(INVITED_PLAYER, invitedPlayer.getName());
                            player.sendMessage(confirmationString);
                            String invitationString = Utils.Color(messagesConfig.getString("team-invited-player-invite-pending")).replace("%TEAMOWNER%", player.getName());
                            invitedPlayer.sendMessage(invitationString);
                            return;
                        } else {
                            String failureString = Utils.Color(messagesConfig.getString("team-invite-failed")).replace(INVITED_PLAYER, invitedPlayer.getName());
                            player.sendMessage(failureString);
                            return;
                        }
                    } else {
                        if (plugin.getTeamInviteUtil().createInvite(player.getUniqueId().toString(), invitedPlayer.getUniqueId().toString()) != null) {
                            String confirmationString = Utils.Color(messagesConfig.getString("team-invite-successful")).replace(INVITED_PLAYER, invitedPlayer.getName());
                            player.sendMessage(confirmationString);
                            String invitationString = Utils.Color(messagesConfig.getString("team-invited-player-invite-pending")).replace("%TEAMOWNER%", player.getName());
                            invitedPlayer.sendMessage(invitationString);
                            return;
                        } else {
                            String failureString = Utils.Color(messagesConfig.getString("team-invite-failed")).replace(INVITED_PLAYER, invitedPlayer.getName());
                            player.sendMessage(failureString);
                            return;
                        }
                    }
                }
            }

            if (plugin.getTeamInviteUtil().createInvite(player.getUniqueId().toString(), invitedPlayer.getUniqueId().toString()) != null) {
                String confirmationString = Utils.Color(messagesConfig.getString("team-invite-successful")).replace(INVITED_PLAYER, invitedPlayer.getName());
                player.sendMessage(confirmationString);

                String invitationString = Utils.Color(messagesConfig.getString("team-invited-player-invite-pending")).replace("%TEAMOWNER%", player.getName());
                invitedPlayer.sendMessage(invitationString);
            } else {
                String failureString = Utils.Color(messagesConfig.getString("team-invite-failed")).replace(INVITED_PLAYER, invitedPlayer.getName());
                player.sendMessage(failureString);
            }
        }
    }

    public void teamInviteDenySubCommand(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (!plugin.getTeamInviteUtil().hasInvitee(player.getUniqueId().toString())) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-invite-deny-failed-no-invite")));
            return;
        }

        if (plugin.getTeamInviteUtil().removeInvitee(player.getUniqueId().toString())) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-invite-denied")));
        } else {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-invite-deny-fail")));
        }
    }

    public void teamInviteAcceptSubCommand(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (!plugin.getTeamInviteUtil().hasInvitee(player.getUniqueId().toString())) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-join-failed-no-invite")));
            return;
        }

        TeamInvite invite = plugin.getTeamInviteUtil().getInvitee(player.getUniqueId().toString());
        String inviterUUIDString = invite.getInviter();

        Player inviterPlayer = Bukkit.getPlayer(UUID.fromString(inviterUUIDString));
        Team team = plugin.getTeamStorageUtil().findTeamByOwner(inviterPlayer);

        if (team != null) {
            if (plugin.getTeamStorageUtil().addTeamMember(team, player)) {
                plugin.getTeamInviteUtil().removeInvite(inviterUUIDString);

                String joinMessage = Utils.Color(messagesConfig.getString("team-join-successful")).replace(TEAM_PLACEHOLDER, team.getTeamFinalName());
                player.sendMessage(joinMessage);

                // Send message to team owner
                inviterPlayer.sendMessage(Utils.Color(messagesConfig.getString("team-join-broadcast-chat")
                        .replace(PLAYER_PLACEHOLDER, player.getName())
                        .replace(TEAM_PLACEHOLDER, Utils.Color(team.getTeamFinalName()))));

                // Send message to team players
                if (plugin.getSettings().teamJoinAnnounce()) {
                    for (String playerUUID : team.getTeamMembers()) {
                        final Player teamPlayer = Bukkit.getPlayer(UUID.fromString(playerUUID));

                        if (teamPlayer != null) {
                            teamPlayer.sendMessage(Utils.Color(messagesConfig.getString("team-join-broadcast-chat")
                                    .replace(PLAYER_PLACEHOLDER, player.getName())
                                    .replace(TEAM_PLACEHOLDER, Utils.Color(team.getTeamFinalName()))));
                        }
                    }
                }
            } else {
                String failureMessage = Utils.Color(messagesConfig.getString("team-join-failed")).replace(TEAM_PLACEHOLDER, team.getTeamFinalName());
                player.sendMessage(failureMessage);
            }
        } else {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-join-failed-no-valid-team")));
        }
    }
}
