package dev.xf3d3.ultimateteams.commands.subCommands;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TeamMotdSubCommand {

    private final int MIN_CHAR_LIMIT;
    private final int MAX_CHAR_LIMIT;

    private final UltimateTeams plugin;

    public TeamMotdSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.MAX_CHAR_LIMIT = plugin.getSettings().getMotdMaxLength();
        this.MIN_CHAR_LIMIT = plugin.getSettings().getMotdMinLength();
    }

    public void teamSetMotdSubCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        final String motd = String.join(" ", args);
        
        if (!plugin.getSettings().isMotdAllowColors() && (motd.contains("&") || motd.contains("#"))) {

            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamMotdCannotContainColours()));
            return;
        }

        if (plugin.getSettings().isMotdColorsRequirePerm() && !player.hasPermission("ultimateteams.team.motd.usecolors") && (motd.contains("&") || motd.contains("#"))) {

            player.sendMessage(MineDown.parse(plugin.getMessages().getUseColoursMissingPermission()));
            return;
        }

        final int motdLength = Utils.removeColors(motd).length();
        if (motdLength >= MIN_CHAR_LIMIT && motdLength <= MAX_CHAR_LIMIT) {

            plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                    team -> {
                        // Check permission
                        if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.MOTD)))) {
                            sender.sendMessage(MineDown.parse(plugin.getMessages().getNoPermission()));
                            return;
                        }

                        team.setMotd(motd);
                        plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                        sender.sendMessage(MineDown.parse(plugin.getMessages().getTeamMotdChangeSuccessful().replace("%MOTD%", Utils.Color(motd))));
                    },
                    () -> sender.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()))
            );

        } else if (motdLength > MAX_CHAR_LIMIT) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getTeamMotdTooLong().replace("%CHARMAX%", String.valueOf(MAX_CHAR_LIMIT))));
        } else {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getTeamMotdTooShort().replace("%CHARMIN%", String.valueOf(MIN_CHAR_LIMIT))));
        }
    }

    public void teamRemoveMotdSubCommand(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }


        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.MOTD)))) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getNoPermission()));
                        return;
                    }

                    team.setMotd(null);
                    plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                    sender.sendMessage(MineDown.parse(plugin.getMessages().getTeamMotdDisabledSuccessful()));
                },
                () -> sender.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()))
        );
    }
}
