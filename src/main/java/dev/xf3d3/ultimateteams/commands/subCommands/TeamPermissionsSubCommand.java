package dev.xf3d3.ultimateteams.commands.subCommands;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TeamPermissionsSubCommand {
    private final UltimateTeams plugin;

    public TeamPermissionsSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void teamPermissionsAddSubCommand(CommandSender sender, String permission) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        if (!plugin.getTeamStorageUtil().isTeamOwner(player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getTeamMustBeOwner()));
            return;
        }


        plugin.getTeamStorageUtil().findTeamByOwner(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    final Optional<Team.Permission> perm = Team.Permission.parse(permission);
                    if (perm.isEmpty()) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getPermissionNotFound().replace("%PERM%", permission)));
                        return;
                    }

                    team.addPermission(perm.get());
                    plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                    sender.sendMessage(MineDown.parse(plugin.getMessages().getTeamPermissionAddedSuccessful().replace("%PERM%", String.valueOf(perm.get()))));
                },
                () -> sender.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()))
        );
    }

    public void teamPermissionsRemoveSubCommand(CommandSender sender, String permission) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        if (!plugin.getTeamStorageUtil().isTeamOwner(player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getTeamMustBeOwner()));
            return;
        }


        plugin.getTeamStorageUtil().findTeamByOwner(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    final Optional<Team.Permission> perm = Team.Permission.parse(permission);
                    if (perm.isEmpty()) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getPermissionNotFound().replace("%PERM%", permission)));
                        return;
                    }

                    team.removePermission(perm.get());
                    plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                    sender.sendMessage(MineDown.parse(plugin.getMessages().getTeamPermissionRemovedSuccessful().replace("%PERM%", String.valueOf(perm.get()))));
                },
                () -> sender.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()))
        );
    }
}
