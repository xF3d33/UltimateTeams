package dev.xf3d3.ultimateteams.commands.subCommands.members;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamPvpSubCommand {
    private final UltimateTeams plugin;

    public TeamPvpSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void teamPvpSubCommand(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getPlayerOnlyCommand()));
            return;
        }

        if (!plugin.getSettings().getTeam().getPvp().isEnabled()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getFunctionDisabled()));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.PVP)))) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getNoPermission()));
                        return;
                    }

                    if (team.isFriendlyFireAllowed()){

                        team.setFriendlyFire(false);
                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getPvp().getDisabled()));
                    } else {
                        team.setFriendlyFire(true);
                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getPvp().getEnabled()));
                    }

                    plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getPvp().getFailedNotInTeam()))
        );
    }
}
