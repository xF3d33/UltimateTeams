package dev.xf3d3.ultimateteams.commands.teamSubCommands;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamKickSubCommand {

    FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();
    private static final String TEAM_PLACEHOLDER = "%TEAM%";
    private static final String PLAYER_TO_KICK = "%KICKEDPLAYER%";

    private final UltimateTeams plugin;

    public TeamKickSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void teamKickSubCommand(CommandSender sender, OfflinePlayer offlinePlayer) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (!plugin.getTeamStorageUtil().isTeamOwner(player)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            return;
        }

        if (!offlinePlayer.hasPlayedBefore()) {
            player.sendMessage(Utils.Color(messagesConfig.getString("could-not-find-specified-player").replace(PLAYER_TO_KICK, offlinePlayer.toString())));
            return;
        }

        final Team targetTeam = plugin.getTeamStorageUtil().findTeamByOwner(player);

        if (plugin.getTeamStorageUtil().findTeamByOwner(player) != null) {

            if (!player.getName().equalsIgnoreCase(offlinePlayer.getName())) {
                Team playerTeam = plugin.getTeamStorageUtil().findTeamByOwner(player);

                if (targetTeam.equals(playerTeam)) {

                    plugin.getTeamStorageUtil().kickPlayer(targetTeam, offlinePlayer);

                    String playerKickedMessage = Utils.Color(messagesConfig.getString("team-member-kick-successful")).replace(PLAYER_TO_KICK, offlinePlayer.getName());
                    player.sendMessage(playerKickedMessage);
                    if (offlinePlayer.isOnline()) {
                        String kickMessage = Utils.Color(messagesConfig.getString("team-kicked-player-message")).replace(TEAM_PLACEHOLDER, targetTeam.getTeamFinalName());
                        offlinePlayer.getPlayer().sendMessage(kickMessage);
                    }
                } else {
                    String differentTeamMessage = Utils.Color(messagesConfig.getString("targeted-player-is-not-in-your-team")).replace(PLAYER_TO_KICK, offlinePlayer.getName());
                    player.sendMessage(differentTeamMessage);
                }
            } else {
                player.sendMessage(Utils.Color(messagesConfig.getString("failed-cannot-kick-yourself")));
            }
        }else {
            player.sendMessage(Utils.Color(messagesConfig.getString("must-be-owner-to-kick")));
        }
    }
}
