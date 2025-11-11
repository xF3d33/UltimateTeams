package dev.xf3d3.ultimateteams.commands.subCommands.members;

import co.aikar.commands.annotation.Values;
import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamMemberLeaveEvent;
import dev.xf3d3.ultimateteams.api.events.TeamTransferOwnershipEvent;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamTransferOwnerSubCommand {

    private final UltimateTeams plugin;

    public TeamTransferOwnerSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void transferTeamOwnerSubCommand(CommandSender sender, @Values("teamPlayers") String memberName) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        final String PLAYER_PLACEHOLDER = "%PLAYER%";
        final OfflinePlayer newTeamOwner = Bukkit.getOfflinePlayer(memberName);

        if (!plugin.getTeamStorageUtil().isTeamOwner(player)) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamMustBeOwner()));
            return;
        }

        if (newTeamOwner.getName() == null) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getPlayerNotFound()));
            return;
        }

        if (player.getUniqueId().equals(newTeamOwner.getUniqueId())) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamOwnershipTransferFailedCannotTransferToSelf()
                    .replace(PLAYER_PLACEHOLDER, player.getName())));

            return;
        }

        plugin.getTeamStorageUtil().findTeamByOwner(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    if (!team.getMembers().containsKey(newTeamOwner.getUniqueId())) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeamOwnershipTransferFailureNotSameTeam()));
                        return;
                    }

                    if (new TeamTransferOwnershipEvent(team.getOwner(), player.getUniqueId(), team).callEvent()) return;

                    plugin.getTeamStorageUtil().transferTeamOwner(team, newTeamOwner.getUniqueId());

                    String OLD_OWNER_PLACEHOLDER = "%OLDOWNER%";
                    String NEW_Team_NAME = "%Team%";

                    player.sendMessage(MineDown.parse(plugin.getMessages().getTeamOwnershipTransferSuccessful()
                            .replace(PLAYER_PLACEHOLDER, newTeamOwner.getName())));

                    if (newTeamOwner.isOnline() && newTeamOwner.getPlayer() != null) {
                        newTeamOwner.getPlayer().sendMessage(MineDown.parse(plugin.getMessages().getTeamOwnershipTransferNewOwner()
                                .replace(OLD_OWNER_PLACEHOLDER, player.getName()).replace(NEW_Team_NAME, team.getName())));
                    }
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getFailedNotInTeam()))
        );

    }
}
