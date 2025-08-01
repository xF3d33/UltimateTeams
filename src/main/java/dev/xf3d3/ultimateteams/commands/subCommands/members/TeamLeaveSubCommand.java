package dev.xf3d3.ultimateteams.commands.subCommands.members;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamLeaveSubCommand {

    FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();
    private static final String Team_PLACEHOLDER = "%TEAM%";

    private final UltimateTeams plugin;

    public TeamLeaveSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void teamLeaveSubCommand(CommandSender sender) {
        if (sender instanceof final Player player) {

            if (plugin.getTeamStorageUtil().isTeamOwner(player)) {
                player.sendMessage(Utils.Color(messagesConfig.getString("failed-team-owner")));
                return;
            }

            plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                    team -> {
                        team.removeMember(player.getUniqueId());
                        plugin.getUsersStorageUtil().getPlayer(player.getUniqueId()).thenAcceptAsync(teamPlayer -> {
                           if (teamPlayer.getPreferences().isTeamChatTalking()) {
                               teamPlayer.getPreferences().setTeamChatTalking(false);
                               plugin.getDatabase().updatePlayer(teamPlayer);
                           }
                        });

                        plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                        String leaveMessage = Utils.Color(messagesConfig.getString("team-leave-successful")).replace(Team_PLACEHOLDER, Utils.Color(team.getName()));
                        player.sendMessage(leaveMessage);

                        // Send message to team players
                        if (plugin.getSettings().teamLeftAnnounce()) {
                            team.sendTeamMessage(Utils.Color(messagesConfig.getString("team-left-broadcast-chat")
                                    .replace("%PLAYER%", player.getName())
                                    .replace("%TEAM%", Utils.Color(team.getName()))));
                        }
                    },
                    () -> player.sendMessage(Utils.Color(messagesConfig.getString("team-leave-failed")))
            );
        }
    }
}
