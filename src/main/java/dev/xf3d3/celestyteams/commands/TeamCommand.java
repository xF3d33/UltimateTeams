package dev.xf3d3.celestyteams.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.commands.teamSubCommands.*;
import dev.xf3d3.celestyteams.menuSystem.paginatedMenu.ClanListGUI;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Logger;

@CommandAlias("team")
public class TeamCommand extends BaseCommand {

    private final Logger logger;
    private final FileConfiguration teamsConfig;
    private final FileConfiguration messagesConfig;
    private static List<String> bannedTags;
    private final CelestyTeams plugin;

    public TeamCommand(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.messagesFileManager.getMessagesConfig();
        this.teamsConfig = plugin.getConfig();
        this.logger = plugin.getLogger();
    }

    public static void updateBannedTagsList() {
        bannedTags = CelestyTeams.getPlugin().getConfig().getStringList("team-tags.disallowed-tags");
    }

    @Default
    @CommandCompletion("create|disband|invite|join|leave|kick|info|list|prefix|transfer|ally add|ally remove|enemy|pvp|sethome|delhome|home")
    public void onClanCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            if (args.length < 1) {
                if (teamsConfig.getBoolean("use-global-GUI-system")) {
                    new ClanListGUI(plugin, CelestyTeams.getPlayerMenuUtility(player)).open();
                    return;
                }
                if (teamsConfig.getBoolean("team-home.enabled") && teamsConfig.getBoolean("protections.pvp.pvp-command-enabled")) {
                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-command-incorrect-usage.line-1")));
                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-command-incorrect-usage.line-2")));
                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-command-incorrect-usage.line-3")));
                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-command-incorrect-usage.line-4")));
                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-command-incorrect-usage.line-5")));
                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-command-incorrect-usage.line-6")));
                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-command-incorrect-usage.line-7")));
                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-command-incorrect-usage.line-8")));
                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-command-incorrect-usage.line-9")));
                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-command-incorrect-usage.line-10")));
                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-command-incorrect-usage.line-11")));
                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-command-incorrect-usage.line-12")));
                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-command-incorrect-usage.line-13")));
                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-command-incorrect-usage.line-14")));
                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-command-incorrect-usage.line-15")));
                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-command-incorrect-usage.line-16")));

                }
            }

            if (sender instanceof ConsoleCommandSender) {
                logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("player-only-command")));
            }
        }
    }

    @Subcommand("create")
    @CommandCompletion("<name> @nothing")
    public void onClanCreateCommand(CommandSender sender, String[] args) {
        new TeamCreateSubCommand(plugin).createClanSubCommand(sender, args, bannedTags);
    }

    @Subcommand("disband")
    @CommandCompletion("@nothing")
    public void onClanDisbandCommand(CommandSender sender) {
        new TeamDisbandSubCommand(plugin).disbandClanSubCommand(sender);
    }

    @Subcommand("invite")
    @CommandCompletion("@players @nothing")
    public void onClanInviteCommand(CommandSender sender, String[] args) {
        new TeamInviteSubCommand(plugin).teamInviteSubCommand(sender, args);
    }

    @Subcommand("sethome")
    @CommandCompletion("@nothing")
    public void onClanSetHomeCommand(CommandSender sender) {
        new TeamSetHomeSubCommand(plugin).setClanHomeSubCommand(sender);
    }

    @Subcommand("delhome")
    @CommandCompletion("@nothing")
    public void onClanDelHomeCommand(CommandSender sender) {
        new TeamDelHomeSubCommand(plugin).deleteClanHomeSubCommand(sender);
    }

    @Subcommand("home")
    @CommandCompletion("@nothing")
    public void onClanHomeCommand(CommandSender sender) {
        new TeamHomeSubCommand(plugin).tpClanHomeSubCommand(sender);
    }


    @Subcommand("pvp")
    @CommandCompletion("@nothing")
    public void onClanPvPCommand(CommandSender sender) {
        new TeamPvpSubCommand(plugin).teamPvpSubCommand(sender);
    }


    @Subcommand("enemy")
    @CommandCompletion("[add/remove] @teams @nothing")
    public void onClanEnemyCommand(CommandSender sender, String[] args) {
        new TeamEnemySubCommand(plugin).teamEnemySubCommand(sender, args);
    }

    @Subcommand("ally add")
    @CommandCompletion("@teams @nothing")
    public void onClanAllyAddCommand(CommandSender sender, String[] args) {
        new TeamAllySubCommand(plugin).teamAllyAddSubCommand(sender, args);
    }

    @Subcommand("ally remove")
    @CommandCompletion("@teams @nothing")
    public void onClanAllyRemoveCommand(CommandSender sender, String[] args) {
        new TeamAllySubCommand(plugin).teamAllyRemoveSubCommand(sender, args);
    }
    @Subcommand("leave")
    @CommandCompletion("@nothing")
    public void onClanLeaveCommand(CommandSender sender) {
        new TeamLeaveSubCommand(plugin).teamLeaveSubCommand(sender);
    }

    @Subcommand("kick")
    @CommandCompletion("@players @nothing")
    public void onClanKickCommand(CommandSender sender, String[] args) {
        new TeamKickSubCommand(plugin).teamKickSubCommand(sender, args);
    }

    @Subcommand("join")
    @CommandCompletion("@nothing")
    public void onClanJoinCommand(CommandSender sender) {
        new TeamJoinSubCommand(plugin).teamJoinSubCommand(sender);
    }

    @Subcommand("list")
    @CommandCompletion("@nothing")
    public void onClanListCommand(CommandSender sender) {
        new TeamListSubCommand(plugin).teamListSubCommand(sender);
    }

    @Subcommand("transfer")
    @CommandCompletion("@players")
    public void onClanTransferCommand(CommandSender sender, String[] args) {
        new TeamTransferOwnerSubCommand(plugin).transferClanOwnerSubCommand(sender, args);
    }

    @Subcommand("prefix")
    @CommandCompletion("<prefix> @nothing")
    public void onClanPrefixCommand(CommandSender sender, String[] args) {
        new TeamPrefixSubCommand(plugin).teamPrefixSubCommand(sender, args, bannedTags);
    }

    @Subcommand("info")
    @CommandCompletion("@nothing")
    public void onClanInfoCommand(CommandSender sender) {
        new TeamInfoSubCommand(plugin).teamInfoSubCommand(sender);
    }
}
