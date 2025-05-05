package dev.xf3d3.ultimateteams.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
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

    public TeamAdmin(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
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
                            () -> plugin.log(Level.WARNING, "No players online to send message to other servers")
                    );
                },
                () -> sender.sendMessage(Utils.Color(messagesConfig.getString("team-admin-disband-failure")))
        );
    }

    @Subcommand("team join")
    @CommandCompletion("@players @teams @nothing")
    @CommandPermission("ultimateteams.admin.team.join")
    @Syntax("<Player> <teamName>")
    public void teamJoinSubCommand(CommandSender sender, @Values("@players") Player player, @Values("@teams") String teamName) {

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

}
