package dev.xf3d3.ultimateteams.commands.subCommands.members;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamManagersSubCommand {
    private static final String TEAM_PLACEHOLDER = "%TEAM%";
    private static final String PLAYER_TO_KICK = "%KICKEDPLAYER%";

    private final UltimateTeams plugin;

    public TeamManagersSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void teamPromoteSubCommand(CommandSender sender, OfflinePlayer offlinePlayer) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        if (!offlinePlayer.hasPlayedBefore() || offlinePlayer.getName() == null) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getCouldNotFindSpecifiedPlayer().replace(PLAYER_TO_KICK, offlinePlayer.toString())));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.PROMOTE)))) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getNoPermission()));
                        return;
                    }


                    if (player.getName().equalsIgnoreCase(offlinePlayer.getName())) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeamPromoteSelfError()));

                        return;
                    }

                    if (!team.getMembers().containsKey(offlinePlayer.getUniqueId())) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getTargetedPlayerIsNotInYourTeam().replace(PLAYER_TO_KICK, offlinePlayer.getName())));
                    }

                    team.getMembers().put(offlinePlayer.getUniqueId(), 2);
                    plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                    player.sendMessage(MineDown.parse(plugin.getMessages().getTeamPromoteSuccessful().replace("%PLAYER%", offlinePlayer.getName())));
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()))
        );
    }

    public void teamDemoteSubCommand(CommandSender sender, OfflinePlayer offlinePlayer) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        if (!offlinePlayer.hasPlayedBefore() || offlinePlayer.getName() == null) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getCouldNotFindSpecifiedPlayer().replace(PLAYER_TO_KICK, offlinePlayer.toString())));
            return;
        }

        if (!plugin.getTeamStorageUtil().isTeamOwner(player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getTeamMustBeOwner()));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByOwner(player.getUniqueId()).ifPresentOrElse(
                team -> {

                    if (player.getName().equalsIgnoreCase(offlinePlayer.getName())) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeamDemoteSelfError()));

                        return;
                    }

                    if (!team.getMembers().containsKey(offlinePlayer.getUniqueId())) {

                        player.sendMessage(MineDown.parse(plugin.getMessages().getTargetedPlayerIsNotInYourTeam().replace(PLAYER_TO_KICK, offlinePlayer.getName())));
                    }

                    team.getMembers().put(offlinePlayer.getUniqueId(), 1);
                    plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                    player.sendMessage(MineDown.parse(plugin.getMessages().getTeamDemoteSuccessful().replace("%PLAYER%", offlinePlayer.getName())));
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()))
        );
    }
}
