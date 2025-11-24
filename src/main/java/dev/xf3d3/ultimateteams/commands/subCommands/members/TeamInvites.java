package dev.xf3d3.ultimateteams.commands.subCommands.members;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

@SuppressWarnings("unused")
@CommandAlias("invites|teaminvites")
public class TeamInvites extends BaseCommand {
    private final Logger logger;
    private final UltimateTeams plugin;

    public TeamInvites(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    @Default
    @CommandCompletion("@nothing")
    @Subcommand("enable")
    @CommandPermission("ultimateteams.invites.enable")
    public void onInvitesEnable(CommandSender sender) {
        if (!(sender instanceof final Player player)){
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));

            return;
        }

        plugin.getUsersStorageUtil().getPlayer(player.getUniqueId()).thenAccept(teamPlayer -> {
           teamPlayer.getPreferences().setAcceptInvitations(true);

           plugin.runAsync(task -> plugin.getDatabase().updatePlayer(teamPlayer));
           player.sendMessage(MineDown.parse(plugin.getMessages().getInvitesEnabled()));
        });


    }

    @CommandCompletion("@nothing")
    @Subcommand("disable")
    @CommandPermission("ultimateteams.invites.disable")
    public void onInvitesDisable(CommandSender sender) {
        if (!(sender instanceof final Player player)){
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));

            return;
        }

        plugin.getUsersStorageUtil().getPlayer(player.getUniqueId()).thenAccept(teamPlayer -> {
            teamPlayer.getPreferences().setAcceptInvitations(false);

            plugin.runAsync(task -> plugin.getDatabase().updatePlayer(teamPlayer));
            player.sendMessage(MineDown.parse(plugin.getMessages().getInvitesDisabled()));
        });
    }
}
