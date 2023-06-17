package dev.xf3d3.ultimateteams.commands.teamSubCommands;

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

public class TeamJoinSubCommand {
    private final FileConfiguration teamsConfig;
    private final FileConfiguration messagesConfig;
    private static final String PLAYER_PLACEHOLDER = "%PLAYER%";
    private static final String TEAM_PLACEHOLDER = "%TEAM%";
    private final UltimateTeams plugin;

    public TeamJoinSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
        this.teamsConfig = plugin.getConfig();
    }

    public void teamJoinSubCommand(CommandSender sender) {
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
                if (teamsConfig.getBoolean("team-join.announce")) {
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
