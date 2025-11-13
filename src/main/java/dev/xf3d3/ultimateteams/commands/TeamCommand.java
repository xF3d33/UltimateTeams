package dev.xf3d3.ultimateteams.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.commands.subCommands.*;
import dev.xf3d3.ultimateteams.commands.subCommands.disband.TeamDisbandConfirmSubCommand;
import dev.xf3d3.ultimateteams.commands.subCommands.disband.TeamDisbandSubCommand;
import dev.xf3d3.ultimateteams.commands.subCommands.echest.TeamEnderChestSubCommand;
import dev.xf3d3.ultimateteams.commands.subCommands.economy.TeamBankSubCommand;
import dev.xf3d3.ultimateteams.commands.subCommands.economy.TeamFeeSubCommand;
import dev.xf3d3.ultimateteams.commands.subCommands.home.TeamDelHomeSubCommand;
import dev.xf3d3.ultimateteams.commands.subCommands.home.TeamHomeSubCommand;
import dev.xf3d3.ultimateteams.commands.subCommands.home.TeamSetHomeSubCommand;
import dev.xf3d3.ultimateteams.commands.subCommands.members.*;
import dev.xf3d3.ultimateteams.commands.subCommands.relations.TeamAllySubCommand;
import dev.xf3d3.ultimateteams.commands.subCommands.relations.TeamEnemySubCommand;
import dev.xf3d3.ultimateteams.commands.subCommands.warps.TeamDelWarpSubCommand;
import dev.xf3d3.ultimateteams.commands.subCommands.warps.TeamSetWarpSubCommand;
import dev.xf3d3.ultimateteams.commands.subCommands.warps.TeamWarpSubCommand;
import dev.xf3d3.ultimateteams.gui.TeamList;
import dev.xf3d3.ultimateteams.gui.TeamManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
@CommandAlias("team")
@CommandPermission("ultimateteams.player")
public class TeamCommand extends BaseCommand {
    private static List<String> bannedTags;
    private final UltimateTeams plugin;

    public TeamCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public static void updateBannedTagsList() {
        bannedTags = UltimateTeams.getPlugin().getSettings().getTeamTagsDisallowedList();
    }

    @Default
    @CommandCompletion("@nothing")
    public void onTeamCommand(@NotNull CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));

            return;
        }

        if (sender instanceof final Player player) {
            if (plugin.getSettings().useGlobalGui()) {
                // Check if player is in a team
                if (plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).isPresent()) {
                    // Player is in a team, open team manager
                    new TeamManager(plugin, player);
                } else {
                    // Player is not in a team, open team list
                    new TeamList(plugin, player);
                }
                return;
            }

            plugin.getMessages().getTeamCommandIncorrectUsage().forEach(line -> sender.sendMessage(MineDown.parse(line)));

            /*for (int i = 1; i <= 19; i++) {
                String message = messagesConfig.getString(String.format("team-command-incorrect-usage.line-%s", i));

                sender.sendMessage(Utils.Color(plugin.getMessages().getTeamCommandIncorrectUsage()));
            }*/
        }
    }

    @HelpCommand
    @Subcommand("help")
    public void doHelp(CommandSender sender, CommandHelp help) {
        plugin.getMessages().getTeamCommandIncorrectUsage().forEach(line -> sender.sendMessage(MineDown.parse(line)));
    }

    // TEAM GUI
    @Subcommand("gui")
    @CommandCompletion("@nothing")
    @CommandPermission("ultimateteams.team.gui")
    public void onTeamGUICommand(@NotNull CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));

            return;
        }

        if (plugin.getTeamStorageUtil().isInTeam(player)) {
            new TeamManager(plugin, player);
        } else {
            player.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()));
        }
    }

    // TEAM CREATE
    @Subcommand("create")
    @CommandCompletion("<name> @nothing")
    @Syntax("<name>")
    @CommandPermission("ultimateteams.team.create")
    public void onTeamCreateCommand(@NotNull CommandSender sender, String name) {
        new TeamCreateSubCommand(plugin).createTeamSubCommand(sender, name, bannedTags);
    }


    // TEAM RENAME
    @Subcommand("rename")
    @CommandCompletion("<newname> @nothing")
    @Syntax("<newname>")
    @CommandPermission("ultimateteams.team.rename")
    public void onTeamRenameCommand(@NotNull CommandSender sender, String name) {
        new TeamRenameSubCommand(plugin).renameTeamSubCommand(sender, name, bannedTags);
    }


    // WARPS
    @Subcommand("warp")
    @CommandCompletion("@warps @nothing")
    @Syntax("<name>")
    @CommandPermission("ultimateteams.team.warp")
    public void onTeamWarpCommand(@NotNull CommandSender sender, @Optional @Values("@warps") String name) {
        if (name != null) {
            new TeamWarpSubCommand(plugin).WarpCommand(sender, name);
            return;
        }

        new TeamWarpSubCommand(plugin).showWarpsMenu(sender);
    }

    @Subcommand("setwarp")
    @CommandCompletion("<name> @nothing")
    @Syntax("<name>")
    @CommandPermission("ultimateteams.team.setwarp")
    public void onTeamSetWarpCommand(@NotNull CommandSender sender, String name) {
        new TeamSetWarpSubCommand(plugin).setWarpCommand(sender, name);
    }

    @Subcommand("delwarp")
    @CommandCompletion("@warps @nothing")
    @Syntax("<name>")
    @CommandPermission("ultimateteams.team.delwarp")
    public void onTeamDelWarpCommand(@NotNull CommandSender sender, @Values("@warps") String name) {
        new TeamDelWarpSubCommand(plugin).delWarpCommand(sender, name);
    }


    // TEAM DISBAND
    @Subcommand("disband")
    @CommandCompletion("@nothing")
    @CommandPermission("ultimateteams.team.disband")
    public void onTeamDisbandCommand(@NotNull CommandSender sender) {
        new TeamDisbandSubCommand(plugin).disbandTeamSubCommand(sender);
    }

    @Subcommand("disband confirm")
    @CommandCompletion("@nothing")
    @CommandPermission("ultimateteams.team.disband")
    public void onTeamDisbandConfirmCommand(@NotNull CommandSender sender) {
        new TeamDisbandConfirmSubCommand(plugin).disbandTeamSubCommand(sender);
    }


    // TEAM INVITES
    @Subcommand("invite send")
    @CommandCompletion("@onlineUsers @nothing")
    @Syntax("<playername>")
    @CommandPermission("ultimateteams.team.invite.send")
    public void onTeamInviteSendCommand(@NotNull CommandSender sender, @Values("@onlineUsers") String invitee) {
        new TeamInviteSubCommand(plugin).teamInviteSendSubCommand(sender, invitee);
    }

    @Subcommand("invite")
    @CommandCompletion("@onlineUsers @nothing")
    @Syntax("<playername>")
    @CommandPermission("ultimateteams.team.invite.send")
    public void onTeamInviteCommand(@NotNull CommandSender sender, @Values("@onlineUsers") String invitee) {
        // Simplified invite command - same as /team invite send
        new TeamInviteSubCommand(plugin).teamInviteSendSubCommand(sender, invitee);
    }

    @Subcommand("invite accept")
    @CommandCompletion("@nothing")
    @CommandPermission("ultimateteams.team.invite.accept")
    public void onTeamInviteAcceptCommand(@NotNull CommandSender sender) {
        new TeamInviteSubCommand(plugin).teamInviteAcceptSubCommand(sender);
    }

    @Subcommand("invite deny")
    @CommandCompletion("@nothing")
    @CommandPermission("ultimateteams.team.invite.deny")
    public void onTeamInviteDenyCommand(@NotNull CommandSender sender) {
        new TeamInviteSubCommand(plugin).teamInviteDenySubCommand(sender);
    }


    // TEAM HOME
    @Subcommand("sethome")
    @CommandCompletion("@nothing")
    @CommandPermission("ultimateteams.team.sethome")
    public void onTeamSetHomeCommand(@NotNull CommandSender sender) {
        new TeamSetHomeSubCommand(plugin).setTeamHomeSubCommand(sender);
    }

    @Subcommand("delhome")
    @CommandCompletion("@nothing")
    @CommandPermission("ultimateteams.team.delhome")
    public void onTeamDelHomeCommand(@NotNull CommandSender sender) {
        new TeamDelHomeSubCommand(plugin).deleteTeamHomeSubCommand(sender);
    }

    @Subcommand("home")
    @CommandCompletion("@nothing")
    @CommandPermission("ultimateteams.team.home")
    public void onTeamHomeCommand(@NotNull CommandSender sender) {
        new TeamHomeSubCommand(plugin).tpTeamHomeSubCommand(sender);
    }


    // TEAM PVP
    @Subcommand("pvp")
    @CommandCompletion("@nothing")
    @Syntax("<true/false>")
    @CommandPermission("ultimateteams.team.pvp")
    public void onTeamPvPCommand(@NotNull CommandSender sender) {
        new TeamPvpSubCommand(plugin).teamPvpSubCommand(sender);
    }



    // TEAM ENEMIES
    @Subcommand("enemy")
    public void onTeamEnemyCommand(@NotNull CommandSender sender) {
        plugin.getMessages().getTeamCommandIncorrectUsage().forEach(line -> sender.sendMessage(MineDown.parse(line)));
    }

    @Subcommand("enemy add")
    @CommandCompletion("@teams @nothing")
    @Syntax("<teamName>")
    @CommandPermission("ultimateteams.team.enemy.add")
    public void onTeamEnemyAddCommand(@NotNull CommandSender sender, @Values("@teams") String teamName) {
        new TeamEnemySubCommand(plugin).teamEnemySubAddCommand(sender, teamName);
    }

    @Subcommand("enemy remove")
    @CommandCompletion("@enemies @nothing")
    @Syntax("<teamName>")
    @CommandPermission("ultimateteams.team.enemy.remove")
    public void onTeamEnemyRemoveCommand(@NotNull CommandSender sender, @Values("@enemies") String teamName) {
        new TeamEnemySubCommand(plugin).teamEnemySubRemoveCommand(sender, teamName);
    }


    // TEAM ALLIES
    @Subcommand("ally")
    public void onTeamAllyCommand(@NotNull CommandSender sender) {
        plugin.getMessages().getTeamCommandIncorrectUsage().forEach(line -> sender.sendMessage(MineDown.parse(line)));
    }

    @Subcommand("ally add")
    @CommandCompletion("@teams @nothing")
    @Syntax("<teamName>")
    @CommandPermission("ultimateteams.team.ally.add")
    public void onTeamAllyAddCommand(@NotNull CommandSender sender, @Values("@teams") String teamName) {
        new TeamAllySubCommand(plugin).teamAllyAddSubCommand(sender, teamName);
    }

    @Subcommand("ally remove")
    @CommandCompletion("@allies  @nothing")
    @Syntax("<teamName>")
    @CommandPermission("ultimateteams.team.ally.remove")
    public void onTeamAllyRemoveCommand(@NotNull CommandSender sender, @Values("@allies") String teamName) {
        new TeamAllySubCommand(plugin).teamAllyRemoveSubCommand(sender, teamName);
    }


    // TEAM LEAVE
    @Subcommand("leave")
    @CommandCompletion("@nothing")
    @CommandPermission("ultimateteams.team.leave")
    public void onTeamLeaveCommand(@NotNull CommandSender sender) {
        new TeamLeaveSubCommand(plugin).teamLeaveSubCommand(sender);
    }


    // TEAM KICK
    @Subcommand("kick")
    @CommandCompletion("@teamPlayers @nothing")
    @Syntax("<player>")
    @CommandPermission("ultimateteams.team.kick")
    public void onTeamKickCommand(@NotNull CommandSender sender, @Values("@teamPlayers") OfflinePlayer offlinePlayer) {
        new TeamKickSubCommand(plugin).teamKickSubCommand(sender, offlinePlayer);
    }


    // TEAM LIST
    @Subcommand("list")
    @CommandCompletion("@nothing")
    @CommandPermission("ultimateteams.team.list")
    public void onTeamListCommand(@NotNull CommandSender sender) {
        if (plugin.getSettings().isTeamListUseGui() && (sender instanceof final Player player)) {
            new TeamList(plugin, player);

            return;
        }

        new TeamListSubCommand(plugin).teamListSubCommand(sender);
    }


    // TEAM TRANSFER
    @Subcommand("transfer")
    @CommandCompletion("@teamPlayers @nothing")
    @Syntax("<player>")
    @CommandPermission("ultimateteams.team.transfer")
    public void onTeamTransferCommand(@NotNull CommandSender sender, @Values("@teamPlayers") String user) {
        new TeamTransferOwnerSubCommand(plugin).transferTeamOwnerSubCommand(sender, user);
    }


    // TEAM PREFIX
    @Subcommand("prefix")
    @CommandCompletion("<prefix> @nothing")
    @Syntax("<prefix>")
    @CommandPermission("ultimateteams.team.prefix")
    public void onTeamPrefixCommand(@NotNull CommandSender sender, String prefix) {
        new TeamPrefixSubCommand(plugin).teamPrefixSubCommand(sender, prefix, bannedTags);
    }


    // TEAM INFO
    @Subcommand("info")
    @CommandCompletion("@teams")
    @CommandPermission("ultimateteams.team.info")
    public void onTeamInfoCommand(@NotNull CommandSender sender, @Optional @Values("@teams") String teamName) {
        new TeamInfoSubCommand(plugin).teamInfoSubCommand(sender, teamName);
    }

    // TEAM MANAGERS
    @Subcommand("promote")
    @CommandCompletion("@teamPlayers")
    @CommandPermission("ultimateteams.team.promote")
    public void onTeamPromoteCommand(@NotNull Player sender, @Values("@teamPlayers") OfflinePlayer offlinePlayer) {
        new TeamManagersSubCommand(plugin).teamPromoteSubCommand(sender, offlinePlayer);
    }

    @Subcommand("demote")
    @CommandCompletion("@teamPlayers")
    @CommandPermission("ultimateteams.team.demote")
    public void onTeamDemoteCommand(@NotNull Player sender, @Values("@teamPlayers") OfflinePlayer offlinePlayer) {
        new TeamManagersSubCommand(plugin).teamDemoteSubCommand(sender, offlinePlayer);
    }


    // TEAM PERMISSIONS
    @Subcommand("permissions")
    public void onTeamPermissionsCommand(@NotNull CommandSender sender) {
        plugin.getMessages().getTeamCommandIncorrectUsage().forEach(line -> sender.sendMessage(MineDown.parse(line)));
    }

    @Subcommand("permissions add")
    @CommandCompletion("@permissions @nothing")
    @Syntax("<permission>")
    @CommandPermission("ultimateteams.team.permissions.add")
    public void onTeamPermissionsAddCommand(@NotNull CommandSender sender, @Values("@permissions") String permission) {
        new TeamPermissionsSubCommand(plugin).teamPermissionsAddSubCommand(sender, permission);
    }

    @Subcommand("permissions remove")
    @CommandCompletion("@teamPermissions @nothing")
    @Syntax("<permission>")
    @CommandPermission("ultimateteams.team.permissions.remove")
    public void onTeamPermissionsRemoveCommand(@NotNull CommandSender sender, @Values("@teamPermissions") String permission) {
        new TeamPermissionsSubCommand(plugin).teamPermissionsRemoveSubCommand(sender, permission);
    }

    // TEAM ENDER CHEST
    @Subcommand("echest")
    @CommandCompletion("@nothing")
    @CommandPermission("ultimateteams.team.echest")
    public void onTeamEnderChestCommand(@NotNull CommandSender sender) {
        new TeamEnderChestSubCommand(plugin).openEnderChest(sender, 1);
    }

    @Subcommand("echest")
    @CommandCompletion("@teamChests @nothing")
    @CommandPermission("ultimateteams.team.echest")
    public void onTeamEnderChestNumberCommand(@NotNull CommandSender sender, int chestNumber) {
        new TeamEnderChestSubCommand(plugin).openEnderChest(sender, chestNumber);
    }

    // TEAM BANK
    @Subcommand("deposit")
    @CommandCompletion("1000|10000")
    @CommandPermission("ultimateteams.team.deposit")
    public void onTeamDepositCommand(@NotNull CommandSender sender, double amount) {
        new TeamBankSubCommand(plugin).teamBankDepositSubCommand(sender, amount);
    }

    @Subcommand("withdraw")
    @CommandCompletion("1000|10000")
    @CommandPermission("ultimateteams.team.withdraw")
    public void onTeamWithdrawCommand(@NotNull CommandSender sender, double amount) {
        new TeamBankSubCommand(plugin).teamBankWithdrawSubCommand(sender, amount);
    }


    // TEAM FEE
    @Subcommand("fee")
    @CommandCompletion("@nothing")
    @CommandPermission("ultimateteams.team.fee.see")
    public void onTeamFeeCommand(@NotNull CommandSender sender) {
        new TeamFeeSubCommand(plugin).teamFeeSeeSubCommand(sender);
    }

    @Subcommand("fee set")
    @CommandCompletion("<amount> @nothing")
    @CommandPermission("ultimateteams.team.fee.set")
    public void onTeamFeeSetCommand(@NotNull CommandSender sender, double amount) {
        new TeamFeeSubCommand(plugin).teamSetFeeSubCommand(sender, amount);
    }

    @Subcommand("fee disable")
    @CommandCompletion("@nothing")
    @CommandPermission("ultimateteams.team.fee.disable")
    public void onTeamFeeDisableCommand(@NotNull CommandSender sender) {
        new TeamFeeSubCommand(plugin).teamDisableFeeSubCommand(sender);
    }

    @Subcommand("motd set")
    @CommandCompletion("@nothing")
    @CommandPermission("ultimateteams.team.motd.set")
    public void onTeamSetMotdCommand(@NotNull CommandSender sender, String[] args) {
        new TeamMotdSubCommand(plugin).teamSetMotdSubCommand(sender, args);
    }

    @Subcommand("motd disable")
    @CommandCompletion("@nothing")
    @CommandPermission("ultimateteams.team.motd.disable")
    public void onTeamDisableMotdCommand(@NotNull CommandSender sender) {
        new TeamMotdSubCommand(plugin).teamRemoveMotdSubCommand(sender);
    }
}
