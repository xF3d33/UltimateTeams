package dev.xf3d3.celestyteams.commands.teamSubCommands;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.models.Team;
import dev.xf3d3.celestyteams.models.TeamInvite;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import dev.xf3d3.celestyteams.utils.TeamInviteUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class TeamJoinSubCommand {

    Logger logger = CelestyTeams.getPlugin().getLogger();

    FileConfiguration teamsConfig = CelestyTeams.getPlugin().getConfig();
    FileConfiguration messagesConfig = CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig();
    private static final String PLAYER_PLACEHOLDER = "%PLAYER%";
    private static final String CLAN_PLACEHOLDER = "%CLAN%";

    private final CelestyTeams plugin;

    public TeamJoinSubCommand(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
    }

    public boolean teamJoinSubCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            AtomicReference<String> inviterUUIDString = new AtomicReference<>("");
            Set<Map.Entry<UUID, TeamInvite>> teamInvitesList = TeamInviteUtil.getInvites();
            if (plugin.getTeamInviteUtil().searchInvitee(player.getUniqueId().toString())) {
                teamInvitesList.forEach((invites) ->
                        inviterUUIDString.set(invites.getValue().getInviter()));
                logger.info(String.valueOf(inviterUUIDString.get()));
                Player inviterPlayer = Bukkit.getPlayer(UUID.fromString(inviterUUIDString.get()));
                Team team = plugin.getTeamStorageUtil().findTeamByOwner(inviterPlayer);
                if (team != null) {
                    if (plugin.getTeamStorageUtil().addClanMember(team, player)) {
                        TeamInviteUtil.removeInvite(inviterUUIDString.get());
                        String joinMessage = ColorUtils.translateColorCodes(messagesConfig.getString("team-join-successful")).replace(CLAN_PLACEHOLDER, team.getTeamFinalName());
                        player.sendMessage(joinMessage);
                        if (teamsConfig.getBoolean("team-join.announce-to-all")){
                            if (teamsConfig.getBoolean("team-join.send-as-title")){
                                for (Player onlinePlayers : plugin.getConnectedPlayers().keySet()){
                                    onlinePlayers.sendTitle(ColorUtils.translateColorCodes(messagesConfig.getString("team-join-broadcast-title-1")
                                                    .replace(PLAYER_PLACEHOLDER, player.getName())
                                                    .replace(CLAN_PLACEHOLDER, ColorUtils.translateColorCodes(team.getTeamFinalName()))),
                                            ColorUtils.translateColorCodes(messagesConfig.getString("team-join-broadcast-title-2")
                                                    .replace(PLAYER_PLACEHOLDER, player.getName())
                                                    .replace(CLAN_PLACEHOLDER, ColorUtils.translateColorCodes(team.getTeamFinalName()))),
                                            30, 30, 30);
                                }
                            }else {
                                Bukkit.broadcastMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-join-broadcast-chat")
                                        .replace(PLAYER_PLACEHOLDER, player.getName())
                                        .replace(CLAN_PLACEHOLDER, ColorUtils.translateColorCodes(team.getTeamFinalName()))));
                            }
                        }
                    }else {
                        String failureMessage = ColorUtils.translateColorCodes(messagesConfig.getString("team-join-failed")).replace(CLAN_PLACEHOLDER, team.getTeamFinalName());
                        player.sendMessage(failureMessage);
                    }
                }else {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-join-failed-no-valid-team")));
                }
            }else {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-join-failed-no-invite")));
            }
            return true;

        }
        return false;
    }
}
