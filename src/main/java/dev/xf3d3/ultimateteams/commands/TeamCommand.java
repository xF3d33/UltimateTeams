package dev.xf3d3.ultimateteams.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.commands.subCommands.*;
import dev.xf3d3.ultimateteams.commands.subCommands.disband.TeamDisbandConfirmSubCommand;
import dev.xf3d3.ultimateteams.commands.subCommands.disband.TeamDisbandSubCommand;
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
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
@CommandAlias("team")
@CommandPermission("ultimateteams.player")
public class TeamCommand extends BaseCommand {
    private final FileConfiguration messagesConfig;
    private static List<String> bannedTags;
    private final UltimateTeams plugin;

    public TeamCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
    }

    public static void updateBannedTagsList() {
        bannedTags = UltimateTeams.getPlugin().getSettings().getTeamTagsDisallowedList();
    }

    @Default
    @CommandCompletion("@nothing")
    public void onTeamCommand(@NotNull CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));

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

            for (int i = 1; i <= 19; i++) {
                String message = messagesConfig.getString(String.format("team-command-incorrect-usage.line-%s", i));

                sender.sendMessage(Utils.Color(message));
            }
        }
    }

    // TEAM GUI
    @Subcommand("gui")
    @CommandCompletion("@nothing")
    @CommandPermission("ultimateteams.team.gui")
    public void onTeamGUICommand(@NotNull CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));

            return;
        }

        if (plugin.getTeamStorageUtil().isInTeam(player)) {
            new TeamManager(plugin, player);
        } else {
            player.sendMessage(Utils.Color(messagesConfig.getString("not-in-team")));
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
    public void onTeamWarpCommand(@NotNull CommandSender sender, @Values("@warps") String name) {
        new TeamWarpSubCommand(plugin).WarpCommand(sender, name);
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
        for (int i = 1; i <= 19; i++) {
            String message = messagesConfig.getString(String.format("team-command-incorrect-usage.line-%s", i));

            sender.sendMessage(Utils.Color(message));
        }
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
        for (int i = 1; i <= 19; i++) {
            String message = messagesConfig.getString(String.format("team-command-incorrect-usage.line-%s", i));

            sender.sendMessage(Utils.Color(message));
        }
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

    // TEAM CO-OWNER
    @Subcommand("coowner")
    @CommandCompletion("@teamPlayers")
    @CommandPermission("ultimateteams.team.coowner.promote")
    public void onTeamCoOwnerPromoteCommand(@NotNull Player sender, @Values("@teamPlayers") OfflinePlayer offlinePlayer) {
        new dev.xf3d3.ultimateteams.commands.subCommands.members.TeamCoOwnerSubCommand(plugin).promoteToCoOwner(sender, offlinePlayer);
    }

    @Subcommand("coowner demote")
    @CommandCompletion("@teamPlayers")
    @CommandPermission("ultimateteams.team.coowner.demote")
    public void onTeamCoOwnerDemoteCommand(@NotNull Player sender, @Values("@teamPlayers") OfflinePlayer offlinePlayer) {
        new dev.xf3d3.ultimateteams.commands.subCommands.members.TeamCoOwnerSubCommand(plugin).demoteFromCoOwner(sender, offlinePlayer);
    }


    // TEAM UPGRADES
    @Subcommand("upgrade")
    @CommandPermission("ultimateteams.team.upgrade.info")
    public void onTeamUpgradeInfoCommand(@NotNull CommandSender sender) {
        new dev.xf3d3.ultimateteams.commands.subCommands.members.TeamUpgradeSubCommand(plugin).showUpgradeInfo(sender);
    }

    @Subcommand("upgrade members")
    @CommandPermission("ultimateteams.team.upgrade.members")
    public void onTeamUpgradeMembersCommand(@NotNull CommandSender sender) {
        new dev.xf3d3.ultimateteams.commands.subCommands.members.TeamUpgradeSubCommand(plugin).upgradeMembers(sender);
    }

    @Subcommand("upgrade warps")
    @CommandPermission("ultimateteams.team.upgrade.warps")
    public void onTeamUpgradeWarpsCommand(@NotNull CommandSender sender) {
        new dev.xf3d3.ultimateteams.commands.subCommands.members.TeamUpgradeSubCommand(plugin).upgradeWarps(sender);
    }


    // TEAM PERMISSIONS
    @Subcommand("permissions")
    public void onTeamPermissionsCommand(@NotNull CommandSender sender) {
        for (int i = 1; i <= 19; i++) {
            String message = messagesConfig.getString(String.format("team-command-incorrect-usage.line-%s", i));

            sender.sendMessage(Utils.Color(message));
        }
    }

    @Subcommand("permissions add")
    @CommandCompletion("@permissions @nothing")
    @Syntax("<permission>")
    @CommandPermission("ultimateteams.team.permissions.add")
    public void onTeamPermissionsAddCommand(@NotNull CommandSender sender, @Values("@permissions") String permission) {
        new TeamPermissionsSubCommand(plugin).teamPermissionsAddSubCommand(sender, permission);
    }

    // TEAM ENDER CHEST
    @Subcommand("echest")
    @CommandCompletion("@nothing")
    @CommandPermission("ultimateteams.team.echest")
    public void onTeamEnderChestCommand(@NotNull CommandSender sender) {
        new dev.xf3d3.ultimateteams.commands.subCommands.echest.TeamEnderChestSubCommand(plugin).openEnderChest(sender, 1);
    }

    @Subcommand("echest")
    @CommandCompletion("<number> @nothing")
    @CommandPermission("ultimateteams.team.echest")
    public void onTeamEnderChestNumberCommand(@NotNull CommandSender sender, int chestNumber) {
        new dev.xf3d3.ultimateteams.commands.subCommands.echest.TeamEnderChestSubCommand(plugin).openEnderChest(sender, chestNumber);
    }

    @Subcommand("permissions remove")
    @CommandCompletion("@teamPermissions @nothing")
    @Syntax("<permission>")
    @CommandPermission("ultimateteams.team.permissions.remove")
    public void onTeamPermissionsRemoveCommand(@NotNull CommandSender sender, @Values("@teamPermissions") String permission) {
        new TeamPermissionsSubCommand(plugin).teamPermissionsRemoveSubCommand(sender, permission);
    }
}
