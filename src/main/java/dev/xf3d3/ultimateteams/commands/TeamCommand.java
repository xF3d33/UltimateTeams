package dev.xf3d3.ultimateteams.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.commands.teamSubCommands.*;
import dev.xf3d3.ultimateteams.menuSystem.TeamListGUI;
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
                new TeamListGUI(plugin).open(player);
                return;
            }

            for (int i = 1; i <= 15; i++) {
                String message = messagesConfig.getString(String.format("team-command-incorrect-usage.line-%s", i));

                sender.sendMessage(Utils.Color(message));
            }
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
    @CommandCompletion("@players @nothing")
    @Syntax("<playername>")
    @CommandPermission("ultimateteams.team.invite.send")
    public void onTeamInviteSendCommand(@NotNull CommandSender sender, @Values("@players") OnlinePlayer onlinePlayer) {
        new TeamInviteSubCommand(plugin).teamInviteSendSubCommand(sender, onlinePlayer);
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
        for (int i = 1; i <= 15; i++) {
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
    @CommandCompletion("@teams @nothing")
    @Syntax("<teamName>")
    @CommandPermission("ultimateteams.team.enemy.remove")
    public void onTeamEnemyRemoveCommand(@NotNull CommandSender sender, @Values("@teams") String teamName) {
        new TeamEnemySubCommand(plugin).teamEnemySubRemoveCommand(sender, teamName);
    }


    // TEAM ALLIES
    @Subcommand("ally")
    public void onTeamAllyCommand(@NotNull CommandSender sender) {
        for (int i = 1; i <= 15; i++) {
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
    @CommandCompletion("@teams @nothing")
    @Syntax("<teamName>")
    @CommandPermission("ultimateteams.team.ally.remove")
    public void onTeamAllyRemoveCommand(@NotNull CommandSender sender, @Values("@teams") String teamName) {
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
    @CommandCompletion("@players @nothing")
    @Syntax("<player>")
    @CommandPermission("ultimateteams.team.transfer")
    public void onTeamTransferCommand(@NotNull CommandSender sender, @Values("@players") OnlinePlayer onlinePlayer) {
        new TeamTransferOwnerSubCommand(plugin).transferTeamOwnerSubCommand(sender, onlinePlayer);
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
}
