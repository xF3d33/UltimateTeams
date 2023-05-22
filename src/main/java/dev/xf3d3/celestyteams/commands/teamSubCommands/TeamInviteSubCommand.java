package dev.xf3d3.celestyteams.commands.teamSubCommands;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.models.Team;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import dev.xf3d3.celestyteams.utils.TeamInviteUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamInviteSubCommand {

    FileConfiguration teamsConfig = CelestyTeams.getPlugin().getConfig();
    FileConfiguration messagesConfig = CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig();
    private static final String INVITED_PLAYER = "%INVITED%";

    private final CelestyTeams plugin;

    public TeamInviteSubCommand(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
    }

    public boolean teamInviteSubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            if (args.length == 2) {
                if (args[1].length() < 1) {
                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-invite-no-valid-player")));
                    return true;
                }
                if (plugin.getTeamStorageUtil().findTeamByOwner(player) == null) {
                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-invite-not-team-owner")));
                    return true;
                } else {
                    String invitedPlayerStr = args[1];
                    if (invitedPlayerStr.equalsIgnoreCase(player.getName())) {
                        sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-invite-self-error")));
                    } else {
                        Player invitedPlayer = Bukkit.getPlayer(invitedPlayerStr);
                        if (invitedPlayer == null) {
                            String playerNotFound = ColorUtils.translateColorCodes(messagesConfig.getString("team-invitee-not-found")).replace(INVITED_PLAYER, invitedPlayerStr);
                            sender.sendMessage(playerNotFound);
                        } else if (plugin.getTeamStorageUtil().findClanByPlayer(invitedPlayer) != null) {
                            String playerAlreadyInClan = ColorUtils.translateColorCodes(messagesConfig.getString("team-invite-invited-already-in-team")).replace(INVITED_PLAYER, invitedPlayerStr);
                            sender.sendMessage(playerAlreadyInClan);
                        } else {
                            Team team = plugin.getTeamStorageUtil().findTeamByOwner(player);
                            if (!(player.hasPermission("celestyteams.maxteamsize.*")||player.hasPermission("celestyteams.*")||player.isOp())){
                                if (!teamsConfig.getBoolean("team-size.tiered-team-system.enabled")){
                                    if (team.getClanMembers().size() >= teamsConfig.getInt("team-size.default-max-team-size")) {
                                        int maxSize = teamsConfig.getInt("team-size.default-max-team-size");
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-invite-max-size-reached")).replace("%LIMIT%", String.valueOf(maxSize)));
                                        return true;
                                    }
                                }else {
                                    if (player.hasPermission("celestyteams.maxteamsize.group6")){
                                        if (team.getClanMembers().size() >= teamsConfig.getInt("team-size.tiered-team-system.permission-group-list.group-6")){
                                            int g6MaxSize = teamsConfig.getInt("team-size.tiered-team-system.permission-group-list.group-6");
                                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-invite-max-size-reached")).replace("%LIMIT%", String.valueOf(g6MaxSize)));
                                            return true;
                                        }
                                    }else if (player.hasPermission("celestyteams.maxteamsize.group5")){
                                        if (team.getClanMembers().size() >= teamsConfig.getInt("team-size.tiered-team-system.permission-group-list.group-2")){
                                            int g5MaxSize = teamsConfig.getInt("team-size.tiered-team-system.permission-group-list.group-5");
                                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-invite-max-size-reached")).replace("%LIMIT%", String.valueOf(g5MaxSize)));
                                            return true;
                                        }
                                    }else if (player.hasPermission("celestyteams.maxteamsize.group4")) {
                                        if (team.getClanMembers().size() >= teamsConfig.getInt("team-size.tiered-team-system.permission-group-list.group-4")) {
                                            int g4MaxSize = teamsConfig.getInt("team-size.tiered-team-system.permission-group-list.group-4");
                                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-invite-max-size-reached")).replace("%LIMIT%", String.valueOf(g4MaxSize)));
                                            return true;
                                        }
                                    }else if (player.hasPermission("celestyteams.maxteamsize.group3")) {
                                        if (team.getClanMembers().size() >= teamsConfig.getInt("team-size.tiered-team-system.permission-group-list.group-4")) {
                                            int g3MaxSize = teamsConfig.getInt("team-size.tiered-team-system.permission-group-list.group-3");
                                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-invite-max-size-reached")).replace("%LIMIT%", String.valueOf(g3MaxSize)));
                                            return true;
                                        }
                                    }else if (player.hasPermission("celestyteams.maxteamsize.group2")) {
                                        if (team.getClanMembers().size() >= teamsConfig.getInt("team-size.tiered-team-system.permission-group-list.group-2")) {
                                            int g2MaxSize = teamsConfig.getInt("team-size.tiered-team-system.permission-group-list.group-2");
                                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-invite-max-size-reached")).replace("%LIMIT%", String.valueOf(g2MaxSize)));
                                            return true;
                                        }
                                    }else if (player.hasPermission("celestyteams.maxteamsize.group1")) {
                                        if (team.getClanMembers().size() >= teamsConfig.getInt("team-size.tiered-team-system.permission-group-list.group-1")) {
                                            int g1MaxSize = teamsConfig.getInt("team-size.tiered-team-system.permission-group-list.group-1");
                                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-invite-max-size-reached")).replace("%LIMIT%", String.valueOf(g1MaxSize)));
                                            return true;
                                        }
                                    }
                                }
                            }


                            if (CelestyTeams.getFloodgateApi() != null){
                                if (CelestyTeams.bedrockPlayers.containsKey(invitedPlayer)){
                                    String bedrockInvitedPlayerUUIDString = CelestyTeams.bedrockPlayers.get(invitedPlayer);
                                    if (TeamInviteUtil.createInvite(player.getUniqueId().toString(), bedrockInvitedPlayerUUIDString) != null){
                                        String confirmationString = ColorUtils.translateColorCodes(messagesConfig.getString("team-invite-successful")).replace(INVITED_PLAYER, invitedPlayer.getName());
                                        player.sendMessage(confirmationString);
                                        String invitationString = ColorUtils.translateColorCodes(messagesConfig.getString("team-invited-player-invite-pending")).replace("%CLANOWNER%", player.getName());
                                        invitedPlayer.sendMessage(invitationString);
                                        return true;
                                    }else {
                                        String failureString = ColorUtils.translateColorCodes(messagesConfig.getString("team-invite-failed")).replace(INVITED_PLAYER, invitedPlayer.getName());
                                        player.sendMessage(failureString);
                                        return true;
                                    }
                                }else {
                                    if (TeamInviteUtil.createInvite(player.getUniqueId().toString(), invitedPlayer.getUniqueId().toString()) != null) {
                                        String confirmationString = ColorUtils.translateColorCodes(messagesConfig.getString("team-invite-successful")).replace(INVITED_PLAYER, invitedPlayer.getName());
                                        player.sendMessage(confirmationString);
                                        String invitationString = ColorUtils.translateColorCodes(messagesConfig.getString("team-invited-player-invite-pending")).replace("%CLANOWNER%", player.getName());
                                        invitedPlayer.sendMessage(invitationString);
                                        return true;
                                    }else {
                                        String failureString = ColorUtils.translateColorCodes(messagesConfig.getString("team-invite-failed")).replace(INVITED_PLAYER, invitedPlayer.getName());
                                        player.sendMessage(failureString);
                                        return true;
                                    }
                                }
                            }

                            if (TeamInviteUtil.createInvite(player.getUniqueId().toString(), invitedPlayer.getUniqueId().toString()) != null) {
                                String confirmationString = ColorUtils.translateColorCodes(messagesConfig.getString("team-invite-successful")).replace(INVITED_PLAYER, invitedPlayer.getName());
                                player.sendMessage(confirmationString);
                                String invitationString = ColorUtils.translateColorCodes(messagesConfig.getString("team-invited-player-invite-pending")).replace("%CLANOWNER%", player.getName());
                                invitedPlayer.sendMessage(invitationString);
                                return true;
                            } else {
                                String failureString = ColorUtils.translateColorCodes(messagesConfig.getString("team-invite-failed")).replace(INVITED_PLAYER, invitedPlayer.getName());
                                player.sendMessage(failureString);
                                return true;
                            }
                        }
                    }
                }
            } else {
                sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-invite-no-valid-player")));
            }
            return true;

        }
        return false;
    }
}
