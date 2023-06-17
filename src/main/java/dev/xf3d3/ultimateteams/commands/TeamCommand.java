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
public class TeamCommand extends BaseCommand {

    private final FileConfiguration teamsConfig;
    private final FileConfiguration messagesConfig;
    private static List<String> bannedTags;
    private final UltimateTeams plugin;

    public TeamCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
        this.teamsConfig = plugin.getConfig();
    }

    public static void updateBannedTagsList() {
        bannedTags = UltimateTeams.getPlugin().getConfig().getStringList("team-tags.disallowed-tags");
    }

    @Default
    @CommandCompletion("@nothing")
    public void onTeamCommand(@NotNull CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
        }

        if (sender instanceof final Player player) {
            if (teamsConfig.getBoolean("use-global-GUI-system")) {
                new TeamListGUI(plugin).open(player);
                return;
            }

            if (teamsConfig.getBoolean("team-home.enabled") && teamsConfig.getBoolean("protections.pvp.pvp-command-enabled")) {
                for (int i = 1; i <= 16; i++) {
                    String message = messagesConfig.getString(String.format("team-command-incorrect-usage.line-%s", i));
                    assert message != null;

                    sender.sendMessage(Utils.Color(message));
                }
            }
        }
    }

    @Subcommand("create")
    @CommandCompletion("<name> @nothing")
    @Syntax("/team create <name>")
    public void onTeamCreateCommand(@NotNull CommandSender sender, String name) {
        new TeamCreateSubCommand(plugin).createTeamSubCommand(sender, name, bannedTags);
    }

    @Subcommand("disband")
    @CommandCompletion("@nothing")
    @Syntax("/team disband")
    public void onTeamDisbandCommand(@NotNull CommandSender sender) {
        new TeamDisbandSubCommand(plugin).disbandTeamSubCommand(sender);
    }

    @Subcommand("invite")
    @CommandCompletion("@players @nothing")
    @Syntax("/team invite player")
    public void onTeamInviteCommand(@NotNull CommandSender sender, @Values("@players") OnlinePlayer onlinePlayer) {
        new TeamInviteSubCommand(plugin).teamInviteSubCommand(sender, onlinePlayer);
    }

    @Subcommand("sethome")
    @CommandCompletion("@nothing")
    public void onTeamSetHomeCommand(@NotNull CommandSender sender) {
        new TeamSetHomeSubCommand(plugin).setTeamHomeSubCommand(sender);
    }

    @Subcommand("delhome")
    @CommandCompletion("@nothing")
    public void onTeamDelHomeCommand(@NotNull CommandSender sender) {
        new TeamDelHomeSubCommand(plugin).deleteTeamHomeSubCommand(sender);
    }

    @Subcommand("home")
    @CommandCompletion("@nothing")
    public void onTeamHomeCommand(@NotNull CommandSender sender) {
        new TeamHomeSubCommand(plugin).tpTeamHomeSubCommand(sender);
    }


    @Subcommand("pvp")
    @CommandCompletion("@nothing")
    @Syntax("/team pvp <true/false>")
    public void onTeamPvPCommand(@NotNull CommandSender sender) {
        new TeamPvpSubCommand(plugin).teamPvpSubCommand(sender);
    }


    @Subcommand("enemy add")
    @CommandCompletion("@teams @nothing")
    @Syntax("/team enemy add <name>")
    public void onTeamEnemyAddCommand(@NotNull CommandSender sender, @Values("@teams") String teamName) {
        new TeamEnemySubCommand(plugin).teamEnemySubAddCommand(sender, teamName);
    }

    @Subcommand("enemy remove")
    @CommandCompletion("@teams @nothing")
    @Syntax("/team enemy remove <name>")
    public void onTeamEnemyRemoveCommand(@NotNull CommandSender sender, @Values("@teams") String teamName) {
        new TeamEnemySubCommand(plugin).teamEnemySubRemoveCommand(sender, teamName);
    }

    @Subcommand("ally add")
    @CommandCompletion("@teams @nothing")
    @Syntax("/team ally add <name>")
    public void onTeamAllyAddCommand(@NotNull CommandSender sender, @Values("@teams") String teamName) {
        new TeamAllySubCommand(plugin).teamAllyAddSubCommand(sender, teamName);
    }

    @Subcommand("ally remove")
    @CommandCompletion("@teams @nothing")
    @Syntax("/team ally remove <name>")
    public void onTeamAllyRemoveCommand(@NotNull CommandSender sender, @Values("@teams") String teamName) {
        new TeamAllySubCommand(plugin).teamAllyRemoveSubCommand(sender, teamName);
    }
    @Subcommand("leave")
    @CommandCompletion("@nothing")
    public void onTeamLeaveCommand(@NotNull CommandSender sender) {
        new TeamLeaveSubCommand(plugin).teamLeaveSubCommand(sender);
    }

    @Subcommand("kick")
    @CommandCompletion("@teamPlayers @nothing")
    @Syntax("/team kick <player>")
    public void onTeamKickCommand(@NotNull CommandSender sender, @Values("@teamPlayers") OfflinePlayer offlinePlayer) {
        new TeamKickSubCommand(plugin).teamKickSubCommand(sender, offlinePlayer);
    }

    @Subcommand("join")
    @CommandCompletion("@nothing")
    public void onTeamJoinCommand(@NotNull CommandSender sender) {
        new TeamJoinSubCommand(plugin).teamJoinSubCommand(sender);
    }

    @Subcommand("list")
    @CommandCompletion("@nothing")
    public void onTeamListCommand(@NotNull CommandSender sender) {
        new TeamListSubCommand(plugin).teamListSubCommand(sender);
    }

    @Subcommand("transfer")
    @CommandCompletion("@players")
    @Syntax("/team transfer <player>")
    public void onTeamTransferCommand(@NotNull CommandSender sender, @Values("@players") OnlinePlayer onlinePlayer) {
        new TeamTransferOwnerSubCommand(plugin).transferTeamOwnerSubCommand(sender, onlinePlayer);
    }

    @Subcommand("prefix")
    @CommandCompletion("<prefix> @nothing")
    @Syntax("/team prefix <prefix>")
    public void onTeamPrefixCommand(@NotNull CommandSender sender, String prefix) {
        new TeamPrefixSubCommand(plugin).teamPrefixSubCommand(sender, prefix, bannedTags);
    }

    @Subcommand("info")
    @CommandCompletion("@teams")
    public void onTeamInfoCommand(@NotNull CommandSender sender, @Optional @Values("@teams") String teamName) {
        new TeamInfoSubCommand(plugin).teamInfoSubCommand(sender, teamName);
    }
}