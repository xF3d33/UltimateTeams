package dev.xf3d3.ultimateteams.commands.subCommands.members;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamKickSubCommand {

    private static final String TEAM_PLACEHOLDER = "%TEAM%";
    private static final String PLAYER_TO_KICK = "%KICKEDPLAYER%";

    private final UltimateTeams plugin;

    public TeamKickSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void teamKickSubCommand(CommandSender sender, OfflinePlayer offlinePlayer) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        if (!offlinePlayer.hasPlayedBefore()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getCouldNotFindSpecifiedPlayer().replace(PLAYER_TO_KICK, offlinePlayer.toString())));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.KICK)))) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getNoPermission()));
                        return;
                    }


                    if (player.getName().equalsIgnoreCase(offlinePlayer.getName())) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getFailedCannotKickYourself()));

                        return;
                    }

                    if (!team.getMembers().containsKey(offlinePlayer.getUniqueId())) {

                        player.sendMessage(MineDown.parse(plugin.getMessages().getTargetedPlayerIsNotInYourTeam().replace(PLAYER_TO_KICK, offlinePlayer.getName())));
                    }

                    plugin.getTeamStorageUtil().kickPlayer(player, team, offlinePlayer);

                    player.sendMessage(MineDown.parse(plugin.getMessages().getTeamMemberKickSuccessful().replace(PLAYER_TO_KICK, offlinePlayer.getName())));
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()))
        );
    }
}