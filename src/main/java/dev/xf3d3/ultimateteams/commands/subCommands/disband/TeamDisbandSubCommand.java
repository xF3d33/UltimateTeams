package dev.xf3d3.ultimateteams.commands.subCommands.disband;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamDisbandSubCommand {

    private final UltimateTeams plugin;

    public TeamDisbandSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void disbandTeamSubCommand(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        if (!plugin.getTeamStorageUtil().isTeamOwner(player)) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamMustBeOwner()));
            return;
        }

        sender.sendMessage(MineDown.parse(plugin.getMessages().getTeamDisbandWarning()));
    }
}
