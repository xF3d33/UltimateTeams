package dev.xf3d3.ultimateteams.commands.teamSubCommands;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.database.daos.TeamDao;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TeamLeaveSubCommand {

    FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();
    private static final String Team_PLACEHOLDER = "%TEAM%";

    private final UltimateTeams plugin;

    public TeamLeaveSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void teamLeaveSubCommand(CommandSender sender) {
        if (sender instanceof final Player player) {

            if (plugin.getTeamStorageUtil().findTeamByOwner(player) != null) {
                player.sendMessage(Utils.Color(messagesConfig.getString("failed-team-owner")));
                return;
            }

            Team targetTeam = plugin.getTeamStorageUtil().findTeamByPlayer(player);
            if (targetTeam != null) {
                if (targetTeam.removeTeamMember(player.getUniqueId().toString())) {
                    plugin.runAsync(() -> TeamDao.updateTeam(targetTeam));

                    String leaveMessage = Utils.Color(messagesConfig.getString("team-leave-successful")).replace(Team_PLACEHOLDER, targetTeam.getTeamFinalName());
                    player.sendMessage(leaveMessage);

                    // Send message to team players
                    if (plugin.getSettings().teamLeftAnnounce()) {
                        for (String playerUUID : targetTeam.getTeamMembers()) {
                            final Player teamPlayer = Bukkit.getPlayer(UUID.fromString(playerUUID));

                            if (teamPlayer != null) {
                                teamPlayer.sendMessage(Utils.Color(messagesConfig.getString("team-left-broadcast-chat")
                                        .replace("%PLAYER%", player.getName())
                                        .replace("%TEAM%", Utils.Color(targetTeam.getTeamFinalName()))));
                            }
                        }
                    }
                } else {
                    player.sendMessage(Utils.Color(messagesConfig.getString("team-leave-failed")));
                }
            }

        }
    }
}
