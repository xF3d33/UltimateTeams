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

        if (!offlinePlayer.hasPlayedBefore() || offlinePlayer.getPlayer() != null) {
            player.sendMessage(Utils.Color(messagesConfig.getString("could-not-find-specified-player").replace(PLAYER_TO_KICK, offlinePlayer.toString())));
            return;
        }

        final Player playerToKick = offlinePlayer.getPlayer();
        final Team targetTeam = plugin.getTeamStorageUtil().findTeamByOwner(player);

        if (plugin.getTeamStorageUtil().findTeamByOwner(player) != null) {
            OfflinePlayer offlinePlayerToKick = plugin.getUsersStorageUtil().getBukkitOfflinePlayerByName(offlinePlayer.getName());

            if (playerToKick != null) {
                if (!player.getName().equalsIgnoreCase(playerToKick.getName())){
                    Team playerTeam = plugin.getTeamStorageUtil().findTeamByPlayer(playerToKick);

                    if (targetTeam.equals(playerTeam)) {

                        targetTeam.removeTeamMember(playerToKick.getUniqueId().toString());
                        plugin.runAsync(() -> plugin.getDatabase().updateTeam(targetTeam));

                        String playerKickedMessage = Utils.Color(messagesConfig.getString("team-member-kick-successful")).replace(PLAYER_TO_KICK, offlinePlayer.getName());
                        player.sendMessage(playerKickedMessage);
                        if (playerToKick.isOnline()) {
                            String kickMessage = Utils.Color(messagesConfig.getString("team-kicked-player-message")).replace(TEAM_PLACEHOLDER, targetTeam.getTeamFinalName());
                            playerToKick.sendMessage(kickMessage);
                        }
                    } else {
                        String differentClanMessage = Utils.Color(messagesConfig.getString("targeted-player-is-not-in-your-team")).replace(PLAYER_TO_KICK, offlinePlayer.getName());
                        player.sendMessage(differentClanMessage);
                    }
                } else {
                    player.sendMessage(Utils.Color(messagesConfig.getString("failed-cannot-kick-yourself")));
                }
            } else if (offlinePlayerToKick != null){
                if (!player.getName().equalsIgnoreCase(offlinePlayer.getName())){
                    Team offlinePlayerTeam = plugin.getTeamStorageUtil().findClanByOfflinePlayer(offlinePlayerToKick);
                    if (targetTeam.equals(offlinePlayerTeam)){

                        targetTeam.removeTeamMember(offlinePlayerToKick.getUniqueId().toString());
                        plugin.runAsync(() -> plugin.getDatabase().updateTeam(targetTeam));

                        String playerKickedMessage = Utils.Color(messagesConfig.getString("team-member-kick-successful")).replace(PLAYER_TO_KICK, offlinePlayer.getName());
                        player.sendMessage(playerKickedMessage);
                    }else {
                        String differentClanMessage = Utils.Color(messagesConfig.getString("targeted-player-is-not-in-your-team")).replace(PLAYER_TO_KICK, offlinePlayer.getName());
                        player.sendMessage(differentClanMessage);
                    }
                }else {
                    player.sendMessage(Utils.Color(messagesConfig.getString("failed-cannot-kick-yourself")));
                }
            }else {
                player.sendMessage(Utils.Color(messagesConfig.getString("could-not-find-specified-player").replace(PLAYER_TO_KICK, offlinePlayer.getName())));
            }
        }else {
            player.sendMessage(Utils.Color(messagesConfig.getString("must-be-owner-to-kick")));
        }
    }
}
