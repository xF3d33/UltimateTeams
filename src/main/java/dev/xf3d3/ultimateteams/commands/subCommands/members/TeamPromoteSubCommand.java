package dev.xf3d3.ultimateteams.commands.subCommands.members;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamPromoteSubCommand {

    FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();
    private static final String TEAM_PLACEHOLDER = "%TEAM%";
    private static final String PLAYER_TO_KICK = "%KICKEDPLAYER%";

    private final UltimateTeams plugin;

    public TeamPromoteSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void teamPromoteSubCommand(CommandSender sender, OfflinePlayer offlinePlayer) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (!offlinePlayer.hasPlayedBefore() || offlinePlayer.getName() == null) {
            player.sendMessage(Utils.Color(messagesConfig.getString("could-not-find-specified-player").replace(PLAYER_TO_KICK, offlinePlayer.toString())));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.PROMOTE)))) {
                        sender.sendMessage(Utils.Color(messagesConfig.getString("no-permission")));
                        return;
                    }


                    if (player.getName().equalsIgnoreCase(offlinePlayer.getName())) {
                        player.sendMessage(Utils.Color(messagesConfig.getString("team-promote-self-error")));

                        return;
                    }

                    if (!team.getMembers().containsKey(offlinePlayer.getUniqueId())) {
                        String differentTeamMessage = Utils.Color(messagesConfig.getString("targeted-player-is-not-in-your-team")).replace(PLAYER_TO_KICK, offlinePlayer.getName());

                        player.sendMessage(differentTeamMessage);
                    }

                    team.getMembers().put(offlinePlayer.getUniqueId(), 2);
                    plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                    String playerKickedMessage = Utils.Color(messagesConfig.getString("team-promote-successful")).replace("%PLAYER%", offlinePlayer.getName());
                    player.sendMessage(playerKickedMessage);
                },
                () -> player.sendMessage(Utils.Color(messagesConfig.getString("not-in-team")))
        );
    }
}
