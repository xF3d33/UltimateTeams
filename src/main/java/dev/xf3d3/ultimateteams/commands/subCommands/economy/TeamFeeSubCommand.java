package dev.xf3d3.ultimateteams.commands.subCommands.economy;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamFeeSubCommand {

    private final UltimateTeams plugin;

    public TeamFeeSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void teamFeeSeeSubCommand(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        if (plugin.getEconomyHook() == null) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getFunctionDisabled()));

            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.FEE)))) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getNoPermission()));
                        return;
                    }

                    player.sendMessage(MineDown.parse(plugin.getMessages().getTeamFeeCurrent()
                            .replace("%AMOUNT%", String.valueOf(team.getJoin_fee()))
                    ));
                },
                () -> sender.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()))
        );
    }

    public void teamSetFeeSubCommand(CommandSender sender, double amount) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        if (plugin.getEconomyHook() == null || !plugin.getSettings().isTeamJoinFeeEnabled()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getFunctionDisabled()));

            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.FEE)))) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getNoPermission()));
                        return;
                    }

                    if (amount <= 0) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getEconomyInvalidAmount()
                                .replace("%AMOUNT%", String.valueOf(team.getJoin_fee()))
                        ));

                        return;
                    }

                    if (amount > plugin.getSettings().getTeamJoinFeeMax()) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getTeamFeeTooBig()
                                .replace("%AMOUNT%", String.valueOf(plugin.getSettings().getTeamJoinFeeMax()))
                        ));

                        return;
                    }

                    team.setJoin_fee(amount);
                    plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                    player.sendMessage(MineDown.parse(plugin.getMessages().getTeamFeeSet()
                            .replace("%AMOUNT%", String.valueOf(team.getJoin_fee()))
                    ));
                },
                () -> sender.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()))
        );
    }

    public void teamDisableFeeSubCommand(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        if (plugin.getEconomyHook() == null) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getFunctionDisabled()));

            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.FEE)))) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getNoPermission()));
                        return;
                    }


                    team.setJoin_fee(0);
                    plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                    player.sendMessage(MineDown.parse(plugin.getMessages().getTeamFeeDisable()));
                },
                () -> sender.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()))
        );
    }
}
