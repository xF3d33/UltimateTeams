package dev.xf3d3.ultimateteams.commands.subCommands.members;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamPvpSubCommand {
    private final UltimateTeams plugin;

    public TeamPvpSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void teamPvpSubCommand(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        if (!plugin.getSettings().isPvpCommandEnabled()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getFunctionDisabled()));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.PVP)))) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getNoPermission()));
                        return;
                    }

                    if (team.isFriendlyFireAllowed()){

                        team.setFriendlyFire(false);
                        player.sendMessage(MineDown.parse(plugin.getMessages().getDisabledFriendlyFire()));
                    } else {
                        team.setFriendlyFire(true);
                        player.sendMessage(MineDown.parse(plugin.getMessages().getEnabledFriendlyFire()));
                    }

                    plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getFailedNotInTeam()))
        );
    }
}
