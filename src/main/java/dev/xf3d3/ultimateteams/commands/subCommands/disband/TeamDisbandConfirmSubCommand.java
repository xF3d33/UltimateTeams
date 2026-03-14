package dev.xf3d3.ultimateteams.commands.subCommands.disband;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamDisbandEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamDisbandConfirmSubCommand {

    private final UltimateTeams plugin;

    public TeamDisbandConfirmSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void disbandTeamSubCommand(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getPlayerOnlyCommand()));
            return;
        }

        if (!plugin.getTeamStorageUtil().isTeamOwner(player)) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getMustBeOwner()));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByOwner(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    if (!(new TeamDisbandEvent(player, team).callEvent())) return;

                    plugin.runAsync(task -> plugin.getTeamStorageUtil().deleteTeamData(player, team));
                    player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getDisband().getSuccessful()));
                },
                () -> sender.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getDisband().getFailure()))
        );
    }
}
