package dev.xf3d3.ultimateteams.commands.subCommands.economy;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamBankDepositEvent;
import dev.xf3d3.ultimateteams.api.events.TeamBankWithdrawEvent;
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
            sender.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getPlayerOnlyCommand()));
            return;
        }

        if (plugin.getEconomyHook() == null || !plugin.getSettings().getEconomy().getTeamBank().isEnabled()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getFunctionDisabled()));

            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.DEPOSIT)))) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getNoPermission()));
                        return;
                    }

                    if (!plugin.getEconomyHook().hasMoney(player, amount)) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getEconomy().getNotEnoughMoney()
                                .replace("%MONEY%", String.valueOf(amount))
                        ));

                        return;
                    }

                    if (amount <= 0) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getEconomy().getInvalidAmount()
                                .replace("%MONEY%", String.valueOf(amount))
                        ));

                        return;
                    }

                    if (plugin.getSettings().getEconomy().getTeamBank().isBankLimit() && team.getBalance() + amount > plugin.getSettings().getEconomy().getTeamBank().getLimit()) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getEconomy().getLimitReached()
                                .replace("%LIMIT%", String.valueOf(plugin.getSettings().getEconomy().getTeamBank().getLimit()))
                                .replace("%CURRENCY%", plugin.getSettings().getEconomy().getTeamBank().getLimit() == 1 ? plugin.getEconomyHook().getCurrencyNameSingular() : plugin.getEconomyHook().getCurrencyNamePlural())
                        ));

                        return;
                    }


                    if (!(new TeamBankDepositEvent(player, team, team.getBalance(), team.getBalance() + amount).callEvent())) return;

                    if (plugin.getEconomyHook().takeMoney(player, amount)) {
                        String currencyName = amount == 1 ? plugin.getEconomyHook().getCurrencyNameSingular() : plugin.getEconomyHook().getCurrencyNamePlural();

                        team.addBalance(amount);

                        plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));
                        player.sendMessage(MineDown.parse(plugin.getMessages().getEconomy().getDeposited()
                                .replace("%MONEY%", String.valueOf(amount))
                                .replace("%CURRENCY%", currencyName)
                        ));
                    }
                },
                () -> sender.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getInfo().getNotInTeam()))
        );
    }

    public void teamBankWithdrawSubCommand(CommandSender sender, double amount) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getPlayerOnlyCommand()));
            return;
        }

        if (plugin.getEconomyHook() == null || !plugin.getSettings().getEconomy().getTeamBank().isEnabled()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getFunctionDisabled()));

            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.WITHDRAW)))) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getNoPermission()));
                        return;
                    }

                    if (amount <= 0) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getEconomy().getInvalidAmount()
                                .replace("%MONEY%", String.valueOf(amount))
                        ));
                        return;
                    }

                    if (amount > team.getBalance()) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getEconomy().getWithdraw().getNotEnoughFunds()
                                .replace("%MONEY%", String.valueOf(amount))
                        ));
                        return;
                    };

                    if (!(new TeamBankWithdrawEvent(player, team, team.getBalance(), team.getBalance() - amount).callEvent())) return;

                    if (team.subBalance(amount)) {
                        plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                        String currencyName = amount == 1 ? plugin.getEconomyHook().getCurrencyNameSingular() : plugin.getEconomyHook().getCurrencyNamePlural();
                        plugin.getEconomyHook().giveMoney(player, amount);

                        player.sendMessage(MineDown.parse(plugin.getMessages().getEconomy().getWithdraw().getSuccess()
                                .replace("%MONEY%", String.valueOf(amount))
                                .replace("%CURRENCY%", currencyName)
                        ));
                    }
                },
                () -> sender.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getInfo().getNotInTeam()))
        );
    }
}
