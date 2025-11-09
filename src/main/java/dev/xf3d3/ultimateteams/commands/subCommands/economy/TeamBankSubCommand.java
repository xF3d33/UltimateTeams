package dev.xf3d3.ultimateteams.commands.subCommands.economy;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamBankSubCommand {

    private final UltimateTeams plugin;

    public TeamBankSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void teamBankDepositSubCommand(CommandSender sender, double amount) {
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
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.DEPOSIT)))) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getNoPermission()));
                        return;
                    }

                    if (!plugin.getEconomyHook().hasMoney(player, amount)) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getNotEnoughMoney()
                                .replace("%MONEY%", String.valueOf(amount))
                        ));

                        return;
                    }

                    if (amount <= 0) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getEconomyInvalidAmount()
                                .replace("%MONEY%", String.valueOf(amount))
                        ));
                        return;
                    }

                    if (plugin.getEconomyHook().takeMoney(player, amount)) {
                        String currencyName = amount > 1 ? plugin.getEconomyHook().getCurrencyNameSingular() : plugin.getEconomyHook().getCurrencyNamePlural();

                        team.addBalance(amount);

                        plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));
                        player.sendMessage(MineDown.parse(plugin.getMessages().getMoneyDeposited()
                                .replace("%MONEY%", String.valueOf(amount))
                                .replace("%CURRENCY%", currencyName)
                        ));
                    }
                },
                () -> sender.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()))
        );
    }

    public void teamBankWithdrawSubCommand(CommandSender sender, double amount) {
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
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.WITHDRAW)))) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getNoPermission()));
                        return;
                    }

                    if (amount <= 0) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getEconomyInvalidAmount()
                                .replace("%MONEY%", String.valueOf(amount))
                        ));
                        return;
                    }

                    if (amount > team.getBalance()) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getMoneyWithdrawNotEnoughFunds()
                                .replace("%MONEY%", String.valueOf(amount))
                        ));
                        return;
                    };


                    if (team.subBalance(amount)) {
                        plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                        String currencyName = amount > 1 ? plugin.getEconomyHook().getCurrencyNameSingular() : plugin.getEconomyHook().getCurrencyNamePlural();
                        plugin.getEconomyHook().giveMoney(player, amount);

                        player.sendMessage(MineDown.parse(plugin.getMessages().getMoneyWithdrawn()
                                .replace("%MONEY%", String.valueOf(amount))
                                .replace("%CURRENCY%", currencyName)
                        ));
                    }
                },
                () -> sender.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()))
        );
    }
}
