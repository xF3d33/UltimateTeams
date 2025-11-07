package dev.xf3d3.ultimateteams.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.commands.subCommands.echest.TeamAdminEnderChestSubCommand;
import dev.xf3d3.ultimateteams.commands.subCommands.echest.TeamEnderChestRollbackSubCommand;
import dev.xf3d3.ultimateteams.commands.subCommands.echest.TeamEnderChestSubCommand;
import dev.xf3d3.ultimateteams.migrator.Migrator;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

@SuppressWarnings("unused")
@CommandAlias("teamadmin|ta")
public class TeamAdmin extends BaseCommand {

    private final FileConfiguration messagesConfig;

    private static final String PLAYER_TO_KICK = "%KICKEDPLAYER%";

    private final UltimateTeams plugin;
    private Migrator migrator;
    
    // Ender chest subcommand instances (shared for real-time sync between admin and players)
    private final TeamEnderChestSubCommand teamEnderChestSubCommand;
    private final TeamAdminEnderChestSubCommand teamAdminEnderChestSubCommand;

    public TeamAdmin(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
        
        // Initialize ender chest subcommands
        this.teamEnderChestSubCommand = new TeamEnderChestSubCommand(plugin);
        this.teamAdminEnderChestSubCommand = new TeamAdminEnderChestSubCommand(plugin, teamEnderChestSubCommand);
    }


    @Default
    @Subcommand("about")
    @CommandCompletion("@nothing")
    @CommandPermission("ultimateteams.admin.about")
    public void aboutSubcommand(CommandSender sender) {
        sender.sendMessage(Utils.Color("&3~~~~~~~~~~ &6&nUltimateTeams&r &3~~~~~~~~~~"));
        sender.sendMessage(Utils.Color("&3Version: &6" + plugin.getDescription().getVersion()));
        sender.sendMessage(Utils.Color("&3Database Type: &6" + plugin.getSettings().getDatabaseType().getDisplayName()));
        plugin.getMessageBroker().ifPresent(broker -> sender.sendMessage(Utils.Color("&3Broker Type: &6" + plugin.getSettings().getBrokerType().getDisplayName())));
        sender.sendMessage(Utils.Color("&3Author: &6" + plugin.getDescription().getAuthors()));
        sender.sendMessage(Utils.Color("&3Contributors: &6" + plugin.getDescription().getContributors()));
        sender.sendMessage(Utils.Color("&3Description: &6" + plugin.getDescription().getDescription()));
        sender.sendMessage(Utils.Color("&3Website: "));
        sender.sendMessage(Utils.Color("&6" + plugin.getDescription().getWebsite()));
        sender.sendMessage(Utils.Color("&3~~~~~~~~~~ &6&nUltimateTeams&r &3~~~~~~~~~~"));
    }

    @Subcommand("reload")
    @CommandCompletion("@nothing")
    @CommandPermission("ultimateteams.admin.reload")
    public void reloadSubcommand(CommandSender sender) {
        sender.sendMessage(Utils.Color(messagesConfig.getString("plugin-reload-begin")));

        plugin.runSync(task -> {
            plugin.loadConfigs();
            plugin.msgFileManager.reloadMessagesConfig();

            TeamCommand.updateBannedTagsList();

            sender.sendMessage(Utils.Color(messagesConfig.getString("plugin-reload-successful")));
        });
    }

    @Subcommand("migrate")
    @CommandCompletion("@nothing")
    @CommandPermission("ultimateteams.admin.migrate")
    public void migrateSubcommand(CommandSender sender) {
        sender.sendMessage("Visit: https://github.com/xF3d33/UltimateTeams/blob/main/HowToMigrate.md for a guida on how to migrate");
    }

    @Subcommand("migrate set")
    @CommandCompletion("<parameter> <value>")
    @CommandPermission("ultimateteams.admin.migrate")
    public void migrateSetSubcommand(CommandSender sender, String parameter, String value) {

        if (migrator == null)
            migrator = new Migrator(plugin);

        migrator.setParameter(parameter, value);

        sender.sendMessage(Utils.Color(String.format("Parameter %s set to %s", parameter, value)));
    }

    @Subcommand("migrate start")
    @CommandCompletion("<parameter> <value>")
    @CommandPermission("ultimateteams.admin.migrate")
    public void migrateStartSubcommand(CommandSender sender) {
        if (migrator == null) {
            sender.sendMessage(Utils.Color("&3Set migrator's parameters first!"));
            return;
        }

        migrator.startMigration();
    }

    @Subcommand("team disband")
    @CommandCompletion("@teams")
    @CommandPermission("ultimateteams.admin.team.disband")
    @Syntax("<teamName>")
    public void disbandSubcommand(CommandSender sender, @Values("@teams") String teamName) {

        plugin.getTeamStorageUtil().findTeamByName(teamName).ifPresentOrElse(
                team -> {
                    if (!plugin.getSettings().isEnableCrossServer()) {
                        plugin.runAsync(task -> plugin.getTeamStorageUtil().deleteTeamData(null, team));
                        sender.sendMessage(Utils.Color(messagesConfig.getString("team-successfully-disbanded")));

                        return;
                    }


                    Bukkit.getOnlinePlayers().stream().findAny().ifPresentOrElse(
                            randomPlayer -> {
                                plugin.runAsync(task -> plugin.getTeamStorageUtil().deleteTeamData(randomPlayer, team));
                                sender.sendMessage(Utils.Color(messagesConfig.getString("team-successfully-disbanded")));
                            },
                            () -> plugin.runAsync(task -> plugin.getTeamStorageUtil().deleteTeamData(null, team))
                    );
                },
                () -> sender.sendMessage(Utils.Color(messagesConfig.getString("team-admin-disband-failure")))
        );
    }

    @Subcommand("team join")
    @CommandCompletion("@players @teams @nothing")
    @CommandPermission("ultimateteams.admin.team.join")
    @Syntax("<Player> <teamName>")
    public void teamJoinSubCommand(CommandSender sender, OnlinePlayer user, @Values("@teams") String teamName) {
        Player player = user.getPlayer();

        if (plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).isPresent()) {
            String joinMessage = Utils.Color(messagesConfig.getString("team-invite-invited-already-in-team"));
            sender.sendMessage(joinMessage);

            return;
        }

        plugin.getTeamStorageUtil().findTeamByName(teamName).ifPresentOrElse(
                team -> {
                    plugin.getTeamStorageUtil().addTeamMember(team, player);

                    String joinMessage = Utils.Color(messagesConfig.getString("team-join-successful")).replace("%TEAM%", team.getName());
                    sender.sendMessage(joinMessage);


                    // Send message to team players
                    team.sendTeamMessage(Utils.Color(messagesConfig.getString("team-join-broadcast-chat")
                            .replace("%PLAYER%", player.getName())
                            .replace("%TEAM%", Utils.Color(team.getName()))));
                },
                () -> sender.sendMessage(Utils.Color(messagesConfig.getString("team-join-failed")).replace("%TEAM%", teamName))
        );
    }

    @Subcommand("team transfer")
    @CommandCompletion("@teams @onlineUsers @nothing")
    @CommandPermission("ultimateteams.admin.team.transfer")
    @Syntax("<team> <player>")
    public void teamTransferSubCommand(CommandSender sender, @Values("@teams") String teamName, @Values("@onlineUsers") OfflinePlayer user) {

        if (!user.hasPlayedBefore() || user.getName() == null) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-not-found")));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByName(teamName).ifPresentOrElse(
                team -> {
                    if (!team.getMembers().containsKey(user.getUniqueId())) {
                        sender.sendMessage(Utils.Color(messagesConfig.getString("team-ownership-transfer-failure-not-same-team")));
                        return;
                    }

                    plugin.getTeamStorageUtil().transferTeamOwner(team, user.getUniqueId());


                    sender.sendMessage(Utils.Color(messagesConfig.getString("team-ownership-transfer-successful")
                            .replace("%PLAYER%", user.getName())));

                    if (user.isOnline() && user.getPlayer() != null) {
                        user.getPlayer().sendMessage(Utils.Color(messagesConfig.getString("team-ownership-transfer-new-owner")
                                .replace("%TEAM%", team.getName())));
                    }

                },
                () -> sender.sendMessage(Utils.Color(messagesConfig.getString("team-not-found")))
        );

    }

    // TEAM ENDER CHEST ADMIN COMMANDS
    @Subcommand("echest add")
    @CommandCompletion("@teams 1-6|chest|doublechest @nothing")
    @CommandPermission("ultimateteams.admin.echest.add")
    @Syntax("<team-name> <rows|chest|doublechest>")
    public void addEnderChestSubCommand(CommandSender sender, @Values("@teams") String teamName, String rowsOrType) {
        teamAdminEnderChestSubCommand.addEnderChest(sender, teamName, rowsOrType);
    }

    @Subcommand("echest remove")
    @CommandCompletion("@teams <chest-number> @nothing")
    @CommandPermission("ultimateteams.admin.echest.remove")
    @Syntax("<team-name> <chest-number>")
    public void removeEnderChestSubCommand(CommandSender sender, @Values("@teams") String teamName, int chestNumber) {
        teamAdminEnderChestSubCommand.removeEnderChest(sender, teamName, chestNumber);
    }

    @Subcommand("echest list")
    @CommandCompletion("@teams @nothing")
    @CommandPermission("ultimateteams.admin.echest.list")
    @Syntax("<team-name>")
    public void listEnderChestsSubCommand(CommandSender sender, @Values("@teams") String teamName) {
        teamAdminEnderChestSubCommand.listEnderChests(sender, teamName);
    }

    @Subcommand("echest see")
    @CommandCompletion("@teams <chest-number> @nothing")
    @CommandPermission("ultimateteams.admin.echest.see")
    @Syntax("<team-name> <chest-number>")
    public void seeEnderChestSubCommand(CommandSender sender, @Values("@teams") String teamName, int chestNumber) {
        teamAdminEnderChestSubCommand.seeEnderChest(sender, teamName, chestNumber);
    }

    @Subcommand("echest backups")
    @CommandCompletion("@teams <chest-number> @nothing")
    @CommandPermission("ultimateteams.admin.echest.rollback")
    @Syntax("<team-name> <chest-number>")
    public void echestBackupsSubCommand(CommandSender sender, @Values("@teams") String teamName, int chestNumber) {
        new TeamEnderChestRollbackSubCommand(plugin).listBackupsAdmin(sender, teamName, chestNumber);
    }

    @Subcommand("echest rollback")
    @CommandCompletion("@teams <chest-number> <backup#> @nothing")
    @CommandPermission("ultimateteams.admin.echest.rollback")
    @Syntax("<team-name> <chest-number> <backup-number>")
    public void echestRollbackSubCommand(CommandSender sender, @Values("@teams") String teamName, int chestNumber, int backupNumber) {
        new TeamEnderChestRollbackSubCommand(plugin).rollbackChestAdmin(sender, teamName, chestNumber, backupNumber, false);
    }

    @Subcommand("echest forcerollback")
    @CommandCompletion("@teams <chest-number> <backup#> @nothing")
    @CommandPermission("ultimateteams.admin.echest.rollback")
    @Syntax("<team-name> <chest-number> <backup-number>")
    public void echestForceRollbackSubCommand(CommandSender sender, @Values("@teams") String teamName, int chestNumber, int backupNumber) {
        new TeamEnderChestRollbackSubCommand(plugin).rollbackChestAdmin(sender, teamName, chestNumber, backupNumber, true);
    }

    @Subcommand("echest backupall")
    @CommandCompletion("@teams @nothing")
    @CommandPermission("ultimateteams.admin.echest.backup")
    @Syntax("<team-name>")
    public void echestAllBackupSubCommand(CommandSender sender, @Values("@teams") String teamName) {
        teamAdminEnderChestSubCommand.backupAllChests(sender, teamName);
    }

    @Subcommand("echest removerow")
    @CommandCompletion("@teams <chest-number> <rows> @nothing")
    @CommandPermission("ultimateteams.admin.echest.remove")
    @Syntax("<team-name> <chest-number> <rows-to-remove>")
    public void removeRowSubCommand(CommandSender sender, @Values("@teams") String teamName, int chestNumber, int rowsToRemove) {
        teamAdminEnderChestSubCommand.removeRows(sender, teamName, chestNumber, rowsToRemove);
    }

    @Subcommand("echest removechest")
    @CommandCompletion("@teams <chest-number> @nothing")
    @CommandPermission("ultimateteams.admin.echest.remove")
    @Syntax("<team-name> <chest-number>")
    public void removeChestSubCommand(CommandSender sender, @Values("@teams") String teamName, int chestNumber) {
        teamAdminEnderChestSubCommand.removeEnderChest(sender, teamName, chestNumber);
    }

}
