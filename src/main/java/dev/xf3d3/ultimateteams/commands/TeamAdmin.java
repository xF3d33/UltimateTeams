package dev.xf3d3.ultimateteams.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@CommandAlias("teamadmin|ta")
public class TeamAdmin extends BaseCommand {

    private final FileConfiguration messagesConfig;

    private static final String PLAYER_TO_KICK = "%KICKEDPLAYER%";

    private final UltimateTeams plugin;

    public TeamAdmin(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
    }


    @Default
    @Subcommand("about")
    @CommandCompletion("@nothing")
    @CommandPermission("ultimateteams.admin.about")
    public void aboutSubcommand(CommandSender sender) {
        sender.sendMessage(Utils.Color("&3~~~~~~~~~~ &6&nUltimateTeams&r &3~~~~~~~~~~"));
        sender.sendMessage(Utils.Color("&3Version: &6" + plugin.getDescription().getVersion()));
        sender.sendMessage(Utils.Color("&3Database Type: &6" + plugin.getSettings().getDatabaseType().getDisplayName()));
        sender.sendMessage(Utils.Color("&3Author: &6" + plugin.getDescription().getAuthors()));
        sender.sendMessage(Utils.Color("&3Description: &6" + plugin.getDescription().getDescription()));
        sender.sendMessage(Utils.Color("&3Website: "));
        sender.sendMessage(Utils.Color("&6" + plugin.getDescription().getWebsite()));
        sender.sendMessage(Utils.Color("&3~~~~~~~~~~ &6&nUltimateTeams&r &3~~~~~~~~~~"));
    }

    @Subcommand("reload")
    @CommandCompletion("@nothing")
    @CommandPermission("ultimateteams.admin.reload")
    public void reloadSubcommand(CommandSender sender) {
        sender.sendMessage(Utils.Color(messagesConfig.getString("plugin-reload-begin")));

        plugin.runSync(() -> {
            plugin.loadConfigs();
            plugin.msgFileManager.reloadMessagesConfig();

            TeamCommand.updateBannedTagsList();

            sender.sendMessage(Utils.Color(messagesConfig.getString("plugin-reload-successful")));
        });
    }

    @Subcommand("team disband")
    @CommandCompletion("@teams")
    @CommandPermission("ultimateteams.admin.team.disband")
    @Syntax("/teamadmin disband <teamName>")
    public void disbandSubcommand(CommandSender sender, String[] args) {
        if (args[0].length() > 1) {
            final Team team = plugin.getTeamStorageUtil().findTeamByName(args[0]);

            if (team != null) {
                if (plugin.getTeamStorageUtil().deleteTeam(args[0])) {
                    sender.sendMessage(Utils.Color(messagesConfig.getString("team-successfully-disbanded")));
                } else {
                    sender.sendMessage(Utils.Color(messagesConfig.getString("team-admin-disband-failure")));
                }
            } else {
                sender.sendMessage(Utils.Color(messagesConfig.getString("could-not-find-specified-player").replace(PLAYER_TO_KICK, args[1])));
            }
        } else {
            sender.sendMessage(Utils.Color(messagesConfig.getString("incorrect-disband-command-usage")));
        }
    }

    @Subcommand("team join")
    @CommandCompletion("@players @teams @nothing")
    @CommandPermission("ultimateteams.admin.team.join")
    @Syntax("/teamadmin team join <Player> <teamName>")
    public void teamInviteAcceptSubCommand(CommandSender sender, @Values("@players") Player player, @Values("@teams") String teamName) {
        //final Player player = OnlinePlayer.getPlayer();

        Team playerTeam;
        if (plugin.getTeamStorageUtil().findTeamByOwner(player) != null) {
            playerTeam = plugin.getTeamStorageUtil().findTeamByOwner(player);
        } else {
            playerTeam = plugin.getTeamStorageUtil().findTeamByPlayer(player);
        }

        if (playerTeam != null) {
            String joinMessage = Utils.Color(messagesConfig.getString("team-invite-invited-already-in-team"));
            sender.sendMessage(joinMessage);

            return;
        }

        Team team = plugin.getTeamStorageUtil().findTeamByName(teamName);
        if (team != null) {
            if (plugin.getTeamStorageUtil().addTeamMember(team, player)) {

                String joinMessage = Utils.Color(messagesConfig.getString("team-join-successful")).replace("%TEAM%", team.getTeamFinalName());
                sender.sendMessage(joinMessage);

                // Send message to team owner
                Player owner = Bukkit.getPlayer(UUID.fromString(team.getTeamOwner()));

                if (owner != null) {
                    player.sendMessage(Utils.Color(messagesConfig.getString("team-join-broadcast-chat")
                            .replace("%PLAYER%", player.getName())
                            .replace("%TEAM%", Utils.Color(team.getTeamFinalName()))));
                }

                // Send message to team players
                if (plugin.getSettings().teamJoinAnnounce()) {
                    for (String playerUUID : team.getTeamMembers()) {
                        final Player teamPlayer = Bukkit.getPlayer(UUID.fromString(playerUUID));

                        if (teamPlayer != null) {
                            teamPlayer.sendMessage(Utils.Color(messagesConfig.getString("team-join-broadcast-chat")
                                    .replace("%PLAYER%", player.getName())
                                    .replace("%TEAM%", Utils.Color(team.getTeamFinalName()))));
                        }
                    }
                }
            } else {
                String failureMessage = Utils.Color(messagesConfig.getString("team-join-failed")).replace("%TEAM%", team.getTeamFinalName());
                sender.sendMessage(failureMessage);
            }
        } else {
            sender.sendMessage(Utils.Color(messagesConfig.getString("team-join-failed-no-valid-team")));
        }
    }

}
