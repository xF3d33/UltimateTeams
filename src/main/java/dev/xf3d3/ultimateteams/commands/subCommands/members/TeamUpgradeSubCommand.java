package dev.xf3d3.ultimateteams.commands.subCommands.members;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.hooks.VaultHook;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamUpgradeSubCommand {

    FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();
    private final UltimateTeams plugin;

    public TeamUpgradeSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    /**
     * Upgrade max members for the team
     */
    public void upgradeMembers(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        // Check if Vault is enabled
        if (!plugin.getSettings().isEconomyEnabled()) {
            player.sendMessage(Utils.Color("&cEconomy is not enabled on this server!"));
            return;
        }

        // Only owners can upgrade
        if (!plugin.getTeamStorageUtil().isTeamOwner(player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByOwner(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    VaultHook vaultHook = plugin.getEconomyHook();
                    double cost = plugin.getSettings().getUpgradeCostMembers();
                    int upgradeAmount = plugin.getSettings().getUpgradeAmount();

                    // Check if player has enough money
                    if (!vaultHook.hasMoney(player, cost)) {
                        player.sendMessage(Utils.Color("&cYou need &e$" + cost + " &cto upgrade max members!"));
                        return;
                    }

                    // Process upgrade
                    vaultHook.takeMoney(player, cost);
                    team.upgradeMaxMembers(upgradeAmount);
                    plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                    String message = Utils.Color("&aSuccessfully upgraded max members by &e" + upgradeAmount + "&a!");
                    player.sendMessage(message);
                    player.sendMessage(Utils.Color("&aNew max members: &e" + team.getMaxMembers()));
                    player.sendMessage(Utils.Color("&aRemaining slots: &e" + team.getRemainingMemberSlots()));
                    
                    team.sendTeamMessage(Utils.Color("&e" + player.getName() + " &ahas upgraded the team's max members to &e" + team.getMaxMembers() + "&a!"));
                },
                () -> player.sendMessage(Utils.Color(messagesConfig.getString("not-in-team")))
        );
    }

    /**
     * Upgrade max warps for the team
     */
    public void upgradeWarps(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        // Check if Vault is enabled
        if (!plugin.getSettings().isEconomyEnabled()) {
            player.sendMessage(Utils.Color("&cEconomy is not enabled on this server!"));
            return;
        }

        // Only owners can upgrade
        if (!plugin.getTeamStorageUtil().isTeamOwner(player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByOwner(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    VaultHook vaultHook = plugin.getEconomyHook();
                    double cost = plugin.getSettings().getUpgradeCostWarps();
                    int upgradeAmount = plugin.getSettings().getUpgradeAmount();

                    // Check if player has enough money
                    if (!vaultHook.hasMoney(player, cost)) {
                        player.sendMessage(Utils.Color("&cYou need &e$" + cost + " &cto upgrade max warps!"));
                        return;
                    }

                    // Process upgrade
                    vaultHook.takeMoney(player, cost);
                    team.upgradeMaxWarps(upgradeAmount);
                    plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                    String message = Utils.Color("&aSuccessfully upgraded max warps by &e" + upgradeAmount + "&a!");
                    player.sendMessage(message);
                    player.sendMessage(Utils.Color("&aNew max warps: &e" + team.getMaxWarps()));
                    player.sendMessage(Utils.Color("&aRemaining warp slots: &e" + team.getRemainingWarpSlots()));
                    
                    team.sendTeamMessage(Utils.Color("&e" + player.getName() + " &ahas upgraded the team's max warps to &e" + team.getMaxWarps() + "&a!"));
                },
                () -> player.sendMessage(Utils.Color(messagesConfig.getString("not-in-team")))
        );
    }

    /**
     * Show upgrade information
     */
    public void showUpgradeInfo(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    player.sendMessage(Utils.Color("&e&l--- Team Upgrades ---"));
                    player.sendMessage(Utils.Color("&aMax Members: &e" + team.getMaxMembers() + " &7(Available slots: " + team.getRemainingMemberSlots() + ")"));
                    player.sendMessage(Utils.Color("&aMax Warps: &e" + team.getMaxWarps() + " &7(Available slots: " + team.getRemainingWarpSlots() + ")"));
                    
                    if (plugin.getSettings().isEconomyEnabled()) {
                        player.sendMessage(Utils.Color(""));
                        player.sendMessage(Utils.Color("&e&lUpgrade Costs:"));
                        player.sendMessage(Utils.Color("&aMembers: &e$" + plugin.getSettings().getUpgradeCostMembers() + " &7(+" + plugin.getSettings().getUpgradeAmount() + " slots)"));
                        player.sendMessage(Utils.Color("&aWarps: &e$" + plugin.getSettings().getUpgradeCostWarps() + " &7(+" + plugin.getSettings().getUpgradeAmount() + " slots)"));
                        player.sendMessage(Utils.Color(""));
                        player.sendMessage(Utils.Color("&7Use &e/team upgrade members &7or &e/team upgrade warps &7to upgrade!"));
                    }
                },
                () -> player.sendMessage(Utils.Color(messagesConfig.getString("not-in-team")))
        );
    }
}
