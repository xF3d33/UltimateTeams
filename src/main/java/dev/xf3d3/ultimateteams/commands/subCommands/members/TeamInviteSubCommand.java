package dev.xf3d3.ultimateteams.commands.subCommands.members;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamMemberJoinEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TeamInviteSubCommand {

    private static final String INVITED_PLAYER = "%INVITED%";
    public static final String PLAYER_PLACEHOLDER = "%PLAYER%";
    public static final String TEAM_PLACEHOLDER = "%TEAM%";

    private final UltimateTeams plugin;

    public TeamInviteSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void teamInviteSendSubCommand(CommandSender sender, String inviteeName) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(inviteeName);

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {

                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.INVITE)))) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getNoPermission()));
                        return;
                    }

                    if (offlinePlayer.getUniqueId().toString().equals(player.getUniqueId().toString())) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getTeamInviteSelfError()));
                        return;
                    }

                    // Check if the player is already in a team
                    if (plugin.getTeamStorageUtil().findTeamByMember(offlinePlayer.getUniqueId()).isPresent()) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getTeamInviteInvitedAlreadyInTeam().replace(INVITED_PLAYER, inviteeName)));
                        return;
                    }

                    // Check if the player has another active invite
                    if (plugin.getTeamInviteUtil().hasInvitee(offlinePlayer.getUniqueId())) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeamInviteFailed().replaceAll("%INVITED%", inviteeName)));
                        return;
                    }


                    plugin.getUsersStorageUtil().getPlayer(player.getUniqueId()).thenAccept(teamPlayer -> {
                        final int maxMembers = teamPlayer.getMaxMembers(player, plugin.getSettings().getTeamMaxSize(), plugin.getSettings().getStackedTeamSize());

                        if (team.getMembers().size() >= maxMembers) {
                            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamInviteMaxSizeReached().replace("%LIMIT%", String.valueOf(maxMembers))));
                            return;
                        }

                        plugin.getUsersStorageUtil().getPlayer(offlinePlayer.getUniqueId()).thenAccept(invitedTeamPlayer -> {
                            if (!invitedTeamPlayer.getPreferences().isAcceptInvitations()) {
                                player.sendMessage(MineDown.parse(plugin.getMessages().getTeamInviteFailInvitesOff().replace("%NAME%", inviteeName)));

                                return;
                            }

                            boolean invited = plugin.getTeamInviteUtil().createInvite(team.getId(), player, offlinePlayer.getUniqueId());

                            if (invited) {
                                player.sendMessage(MineDown.parse(plugin.getMessages().getTeamInviteSuccessful().replace(INVITED_PLAYER, inviteeName)));

                                Optional.ofNullable(offlinePlayer.getPlayer())
                                        .ifPresent(invitedPlayer -> invitedPlayer.sendMessage(MineDown.parse(String.join("\n", plugin.getMessages().getTeamInviteInvitedMessage())
                                                .replace("%TEAM%", team.getName())
                                                .replace("%INVITER%", player.getName())
                                        )));
                            } else {
                                player.sendMessage(MineDown.parse(plugin.getMessages().getTeamInviteFailed().replace(INVITED_PLAYER, inviteeName)));
                            }
                        });
                    });
                },
                () -> sender.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()))
        );


            /*if (plugin.getSettings().FloodGateHook()) {
                if (plugin.getFloodgateApi() != null) {
                    if (plugin.getBedrockPlayers().containsKey(invitedPlayer)) {
                        String bedrockInvitedPlayerUUIDString = plugin.getBedrockPlayers().get(String.valueOf(invitedPlayer.getUniqueId()));


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
            }*/
    }

    public void teamInviteDenySubCommand(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        if (!plugin.getTeamInviteUtil().hasInvitee(player.getUniqueId())) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamInviteDenyFail()));
            return;
        }

        if (plugin.getTeamInviteUtil().declineInvite(player)) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamInviteDenied()));
        } else {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamInviteDenyFail()));
        }
    }


    // ACCEPT
    public void teamInviteAcceptSubCommand(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        if (!plugin.getTeamInviteUtil().hasInvitee(player.getUniqueId())) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamJoinFailedNoInvite()));
            return;
        }

        plugin.getTeamInviteUtil().getInvite(player.getUniqueId()).ifPresentOrElse(
                invite -> plugin.getTeamStorageUtil().findTeamByMember(invite.getInviter()).ifPresentOrElse(
                        team -> {
                            if (plugin.getEconomyHook() != null && team.getJoin_fee() > 0) {
                                if (!plugin.getEconomyHook().takeMoney(player, team.getJoin_fee())) {
                                    player.sendMessage(MineDown.parse(plugin.getMessages().getTeamFeeCantJoin()
                                            .replace("%TEAM%",  team.getName())
                                            .replace("%AMOUNT%", String.valueOf(team.getJoin_fee()))
                                    ));

                                    return;
                                }

                                team.addBalance(team.getJoin_fee());
                                team.sendTeamMessage(MineDown.parse(plugin.getMessages().getTeamFeeDeposited()
                                        .replace("%PLAYER%", player.getName())
                                        .replace("%AMOUNT%", String.valueOf(team.getJoin_fee()))
                                ));
                            }

                            if (!(new TeamMemberJoinEvent(player, team, TeamMemberJoinEvent.JoinReason.ACCEPT_INVITE).callEvent())) return;

                            plugin.getTeamStorageUtil().addTeamMember(team, player);
                            plugin.getTeamInviteUtil().acceptInvite(invite, player);

                            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamJoinSuccessful().replace(TEAM_PLACEHOLDER, team.getName())));

                            if (!plugin.getSettings().teamJoinAnnounce())
                                return;

                            // Send message to team members
                            team.sendTeamMessage(MineDown.parse(plugin.getMessages().getTeamJoinBroadcastChat()
                                    .replace(PLAYER_PLACEHOLDER, player.getName())
                                    .replace(TEAM_PLACEHOLDER, Utils.Color(team.getName()))));
                        },
                        () -> player.sendMessage(MineDown.parse(plugin.getMessages().getTeamJoinFailedNoValidTeam()))
                ),
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getTeamJoinFailedNoInvite()))
        );
    }
}
