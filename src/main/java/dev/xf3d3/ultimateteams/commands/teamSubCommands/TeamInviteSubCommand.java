package dev.xf3d3.ultimateteams.commands.teamSubCommands;

import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamInviteSubCommand {

    private final FileConfiguration messagesConfig;
    private static final String INVITED_PLAYER = "%INVITED%";

    private final UltimateTeams plugin;

    public TeamInviteSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
    }

    public void teamInviteSubCommand(CommandSender sender, OnlinePlayer onlinePlayer) {
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
            } else {
                if (invitedPlayer.getName().equalsIgnoreCase(player.getName())) {
                    sender.sendMessage(Utils.Color(messagesConfig.getString("team-invite-self-error")));
                } else {
                    if (plugin.getTeamStorageUtil().findTeamByPlayer(invitedPlayer) != null) {
                        String playerAlreadyInTeam = Utils.Color(messagesConfig.getString("team-invite-invited-already-in-team")).replace(INVITED_PLAYER, invitedPlayer.getName());
                        sender.sendMessage(playerAlreadyInTeam);
                    } else {
                        Team team = plugin.getTeamStorageUtil().findTeamByOwner(player);
                        /*if (!(player.hasPermission("ultimateteams.maxteamsize.*") || player.hasPermission("ultimateteams.*") || player.isOp())) {
                            if (!teamsConfig.getBoolean("team-size.tiered-team-system.enabled")) {
                                if (team.getTeamMembers().size() >= teamsConfig.getInt("team-size.default-max-team-size")) {
                                    int maxSize = teamsConfig.getInt("team-size.default-max-team-size");
                                    player.sendMessage(Utils.Color(messagesConfig.getString("team-invite-max-size-reached")).replace("%LIMIT%", String.valueOf(maxSize)));
                                    return;
                                }
                            } else {
                                if (player.hasPermission("ultimateteams.maxteamsize.group6")) {
                                    if (team.getTeamMembers().size() >= teamsConfig.getInt("team-size.tiered-team-system.permission-group-list.group-6")) {
                                        int g6MaxSize = teamsConfig.getInt("team-size.tiered-team-system.permission-group-list.group-6");
                                        player.sendMessage(Utils.Color(messagesConfig.getString("team-invite-max-size-reached")).replace("%LIMIT%", String.valueOf(g6MaxSize)));
                                        return;
                                    }
                                } else if (player.hasPermission("ultimateteams.maxteamsize.group5")) {
                                    if (team.getTeamMembers().size() >= teamsConfig.getInt("team-size.tiered-team-system.permission-group-list.group-2")) {
                                        int g5MaxSize = teamsConfig.getInt("team-size.tiered-team-system.permission-group-list.group-5");
                                        player.sendMessage(Utils.Color(messagesConfig.getString("team-invite-max-size-reached")).replace("%LIMIT%", String.valueOf(g5MaxSize)));
                                        return;
                                    }
                                } else if (player.hasPermission("ultimateteams.maxteamsize.group4")) {
                                    if (team.getTeamMembers().size() >= teamsConfig.getInt("team-size.tiered-team-system.permission-group-list.group-4")) {
                                        int g4MaxSize = teamsConfig.getInt("team-size.tiered-team-system.permission-group-list.group-4");
                                        player.sendMessage(Utils.Color(messagesConfig.getString("team-invite-max-size-reached")).replace("%LIMIT%", String.valueOf(g4MaxSize)));
                                        return;
                                    }
                                } else if (player.hasPermission("ultimateteams.maxteamsize.group3")) {
                                    if (team.getTeamMembers().size() >= teamsConfig.getInt("team-size.tiered-team-system.permission-group-list.group-4")) {
                                        int g3MaxSize = teamsConfig.getInt("team-size.tiered-team-system.permission-group-list.group-3");
                                        player.sendMessage(Utils.Color(messagesConfig.getString("team-invite-max-size-reached")).replace("%LIMIT%", String.valueOf(g3MaxSize)));
                                        return;
                                    }
                                } else if (player.hasPermission("ultimateteams.maxteamsize.group2")) {
                                    if (team.getTeamMembers().size() >= teamsConfig.getInt("team-size.tiered-team-system.permission-group-list.group-2")) {
                                        int g2MaxSize = teamsConfig.getInt("team-size.tiered-team-system.permission-group-list.group-2");
                                        player.sendMessage(Utils.Color(messagesConfig.getString("team-invite-max-size-reached")).replace("%LIMIT%", String.valueOf(g2MaxSize)));
                                        return;
                                    }
                                } else if (player.hasPermission("ultimateteams.maxteamsize.group1")) {
                                    if (team.getTeamMembers().size() >= teamsConfig.getInt("team-size.tiered-team-system.permission-group-list.group-1")) {
                                        int g1MaxSize = teamsConfig.getInt("team-size.tiered-team-system.permission-group-list.group-1");
                                        player.sendMessage(Utils.Color(messagesConfig.getString("team-invite-max-size-reached")).replace("%LIMIT%", String.valueOf(g1MaxSize)));
                                        return;
                                    }
                                }
                            }
                        }*/

                        if (team.getTeamMembers().size() >= plugin.getSettings().getTeamMaxSize()) {
                            int maxSize = plugin.getSettings().getTeamMaxSize();
                            player.sendMessage(Utils.Color(messagesConfig.getString("team-invite-max-size-reached")).replace("%LIMIT%", String.valueOf(maxSize)));
                            return;
                        }


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
            }
    }
}
