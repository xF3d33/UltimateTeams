package dev.xf3d3.ultimateteams.commands.subCommands;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class TeamPermissionsSubCommand {
    private final FileConfiguration messagesConfig;

    private final UltimateTeams plugin;

    public TeamPermissionsSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
    }

    public void teamPermissionsAddSubCommand(CommandSender sender, String permission) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (!plugin.getTeamStorageUtil().isTeamOwner(player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            return;
        }


        plugin.getTeamStorageUtil().findTeamByOwner(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    final Optional<Team.Permission> perm = Team.Permission.parse(permission);
                    if (perm.isEmpty()) {
                        sender.sendMessage(Utils.Color(messagesConfig.getString("permission-not-found").replace("%PERM%", permission)));
                        return;
                    }

                    team.addPermission(perm.get());
                    plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                    sender.sendMessage(Utils.Color(messagesConfig.getString("team-permission-added-successful")).replace("%PERM%", String.valueOf(perm.get())));
                },
                () -> sender.sendMessage(Utils.Color(messagesConfig.getString("not-in-team")))
        );
    }

    public void teamPermissionsRemoveSubCommand(CommandSender sender, String permission) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (!plugin.getTeamStorageUtil().isTeamOwner(player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            return;
        }


        plugin.getTeamStorageUtil().findTeamByOwner(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    final Optional<Team.Permission> perm = Team.Permission.parse(permission);
                    if (perm.isEmpty()) {
                        sender.sendMessage(Utils.Color(messagesConfig.getString("permission-not-found").replace("%PERM%", permission)));
                        return;
                    }

                    team.removePermission(perm.get());
                    plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                    sender.sendMessage(Utils.Color(messagesConfig.getString("team-permission-removed-successful")).replace("%PERM%", String.valueOf(perm.get())));
                },
                () -> sender.sendMessage(Utils.Color(messagesConfig.getString("not-in-team")))
        );
    }
}
