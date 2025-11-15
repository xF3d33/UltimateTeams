package dev.xf3d3.ultimateteams.commands.subCommands.members;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamMemberLeaveEvent;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamLeaveSubCommand {

    private static final String Team_PLACEHOLDER = "%TEAM%";

    private final UltimateTeams plugin;

    public TeamLeaveSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void teamLeaveSubCommand(CommandSender sender) {
        if (sender instanceof final Player player) {

            if (plugin.getTeamStorageUtil().isTeamOwner(player)) {
                player.sendMessage(MineDown.parse(plugin.getMessages().getFailedTeamOwner()));
                return;
            }

            plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                    team -> {
                        if (!(new TeamMemberLeaveEvent(player.getUniqueId(), team, TeamMemberLeaveEvent.LeaveReason.MEMBER_LEFT).callEvent())) return;

                        team.removeMember(player.getUniqueId());
                        plugin.getUsersStorageUtil().getPlayer(player.getUniqueId()).thenAcceptAsync(teamPlayer -> {
                           if (teamPlayer.getPreferences().isTeamChatTalking() || teamPlayer.getPreferences().isAllyChatTalking()) {
                               teamPlayer.getPreferences().setTeamChatTalking(false);
                               teamPlayer.getPreferences().setAllyChatTalking(false);

                               plugin.getDatabase().updatePlayer(teamPlayer);
                           }
                        });

                        plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeamLeaveSuccessful().replace(Team_PLACEHOLDER, Utils.Color(team.getName()))));

                        // Send message to team players
                        if (plugin.getSettings().teamLeftAnnounce()) {
                            team.sendTeamMessage(MineDown.parse(plugin.getMessages().getTeamLeftBroadcastChat()
                                    .replace("%PLAYER%", player.getName())
                                    .replace("%TEAM%", Utils.Color(team.getName()))));
                        }
                    },
                    () -> player.sendMessage(MineDown.parse(plugin.getMessages().getTeamLeaveFailed()))
            );
        }
    }
}
