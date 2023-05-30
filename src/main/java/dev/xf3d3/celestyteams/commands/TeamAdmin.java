package dev.xf3d3.celestyteams.commands;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Logger;

public class TeamAdmin implements CommandExecutor {

    private final Logger logger;
    private final FileConfiguration messagesConfig;

    private static final String PLAYER_TO_KICK = "%KICKEDPLAYER%";

    private final CelestyTeams plugin;

    public TeamAdmin(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.messagesFileManager.getMessagesConfig();
        this.logger = plugin.getLogger();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length > 0) {

//----------------------------------------------------------------------------------------------------------------------
                if (args[0].equalsIgnoreCase("reload")) {
                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("plugin-reload-begin")));
                    plugin.runSync(() -> {
                            plugin.reloadConfig();
                            TeamCommand.updateBannedTagsList();
                            plugin.messagesFileManager.reloadMessagesConfig();
                            plugin.teamGUIFileManager.reloadClanGUIConfig();
                            sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("plugin-reload-successful")));
                    });
                }

//----------------------------------------------------------------------------------------------------------------------
                if (args[0].equalsIgnoreCase("disband")) {
                    if (args.length == 2){
                        if (args[1].length() > 1){
                            Player onlinePlayerOwner = Bukkit.getPlayer(args[1]);
                            OfflinePlayer offlinePlayerOwner = plugin.getUsersStorageUtil().getBukkitOfflinePlayerByName(args[1]);
                            if (onlinePlayerOwner != null){
                                try {
                                    if (plugin.getTeamStorageUtil().deleteTeam(onlinePlayerOwner)){
                                        sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-successfully-disbanded")));
                                    }else {
                                        sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-admin-disband-failure")));
                                    }
                                } catch (IOException e) {
                                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("teams-update-error-1")));
                                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("teams-update-error-2")));
                                    e.printStackTrace();
                                }
                            }else if (offlinePlayerOwner != null){
                                try {
                                    if (plugin.getTeamStorageUtil().deleteOfflineClan(offlinePlayerOwner)){
                                        sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-successfully-disbanded")));
                                    }else {
                                        sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-admin-disband-failure")));
                                    }
                                } catch (IOException e) {
                                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("teams-update-error-1")));
                                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("teams-update-error-2")));
                                    e.printStackTrace();
                                }
                            }else {
                                sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("could-not-find-specified-player").replace(PLAYER_TO_KICK, args[1])));
                            }
                        }else {
                            sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-disband-command-usage")));
                        }
                    }
                }

//----------------------------------------------------------------------------------------------------------------------
                if (args[0].equalsIgnoreCase("about")) {
                    sender.sendMessage(ColorUtils.translateColorCodes("&3~~~~~~~~~~ &6&nCelestyTeams&r &3~~~~~~~~~~"));
                    sender.sendMessage(ColorUtils.translateColorCodes("&3Version: &6" + CelestyTeams.getPlugin().getDescription().getVersion()));
                    sender.sendMessage(ColorUtils.translateColorCodes("&3Authors: &6" + CelestyTeams.getPlugin().getDescription().getAuthors()));
                    sender.sendMessage(ColorUtils.translateColorCodes("&3Description: &6" + CelestyTeams.getPlugin().getDescription().getDescription()));
                    sender.sendMessage(ColorUtils.translateColorCodes("&3Website: "));
                    sender.sendMessage(ColorUtils.translateColorCodes("&6" + CelestyTeams.getPlugin().getDescription().getWebsite()));
                    sender.sendMessage(ColorUtils.translateColorCodes("&3Discord:"));
                    sender.sendMessage(ColorUtils.translateColorCodes("&6https://discord.gg/crapticraft"));
                    sender.sendMessage(ColorUtils.translateColorCodes("&3~~~~~~~~~~ &6&nCelestyTeams&r &3~~~~~~~~~~"));
                }

//----------------------------------------------------------------------------------------------------------------------
            }else {
                sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("teamadmin-command-incorrect-usage.line-1")));
                sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("teamadmin-command-incorrect-usage.line-2")));
                sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("teamadmin-command-incorrect-usage.line-3")));
                sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("teamadmin-command-incorrect-usage.line-4")));
                sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("teamadmin-command-incorrect-usage.line-5")));
            }
        }

//----------------------------------------------------------------------------------------------------------------------


//----------------------------------------------------------------------------------------------------------------------
        if (sender instanceof ConsoleCommandSender) {
            if (args.length > 0){

//----------------------------------------------------------------------------------------------------------------------
                if (args[0].equalsIgnoreCase("reload")) {
                    plugin.reloadConfig();
                    plugin.messagesFileManager.reloadMessagesConfig();
                    plugin.teamGUIFileManager.reloadClanGUIConfig();
                }

//----------------------------------------------------------------------------------------------------------------------
                if (args[0].equalsIgnoreCase("disband")) {
                    if (args.length == 2){
                        if (args[1].length() > 1){
                            Player onlinePlayerOwner = Bukkit.getPlayer(args[1]);
                            OfflinePlayer offlinePlayerOwner = plugin.getUsersStorageUtil().getBukkitOfflinePlayerByName(args[1]);
                            if (onlinePlayerOwner != null){
                                try {
                                    if (plugin.getTeamStorageUtil().deleteTeam(onlinePlayerOwner)){
                                        logger.info(ColorUtils.translateColorCodes(messagesConfig.getString("team-successfully-disbanded")));
                                    }else {
                                        logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("team-admin-disband-failure")));
                                    }
                                } catch (IOException e) {
                                    logger.severe(ColorUtils.translateColorCodes(messagesConfig.getString("teams-update-error-1")));
                                    logger.severe(ColorUtils.translateColorCodes(messagesConfig.getString("teams-update-error-2")));
                                    e.printStackTrace();
                                }
                            }else if (offlinePlayerOwner != null){
                                try {
                                    if (plugin.getTeamStorageUtil().deleteOfflineClan(offlinePlayerOwner)){
                                        logger.info(ColorUtils.translateColorCodes(messagesConfig.getString("team-successfully-disbanded")));
                                    }else {
                                        logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("team-admin-disband-failure")));
                                    }
                                } catch (IOException e) {
                                    logger.severe(ColorUtils.translateColorCodes(messagesConfig.getString("teams-update-error-1")));
                                    logger.severe(ColorUtils.translateColorCodes(messagesConfig.getString("teams-update-error-2")));
                                    e.printStackTrace();
                                }
                            }else {
                                logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("could-not-find-specified-player").replace(PLAYER_TO_KICK, args[1])));
                            }
                        }else {
                            logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-disband-command-usage")));
                        }
                    }
                }

//----------------------------------------------------------------------------------------------------------------------
                if (args[0].equalsIgnoreCase("about")) {
                    logger.info(ColorUtils.translateColorCodes("&3~~~~~~~~~~ &6CelestyTeams &3~~~~~~~~~~"));
                    logger.info(ColorUtils.translateColorCodes("&3Version: &6" + plugin.getDescription().getVersion()));
                    logger.info(ColorUtils.translateColorCodes("&3Authors: &6" + plugin.getDescription().getAuthors()));
                    logger.info(ColorUtils.translateColorCodes("&3Description: &6" + plugin.getDescription().getDescription()));
                    logger.info(ColorUtils.translateColorCodes("&3Discord:"));
                    logger.info(ColorUtils.translateColorCodes("&6https://discord.gg/celestymc"));
                    logger.info(ColorUtils.translateColorCodes("&3~~~~~~~~~~ &6CelestyTeams &3~~~~~~~~~~"));
                }

//----------------------------------------------------------------------------------------------------------------------
            }else {
                logger.info(ColorUtils.translateColorCodes(messagesConfig.getString("teamadmin-command-incorrect-usage.line-1")));
                logger.info(ColorUtils.translateColorCodes(messagesConfig.getString("teamadmin-command-incorrect-usage.line-2")));
                logger.info(ColorUtils.translateColorCodes(messagesConfig.getString("teamadmin-command-incorrect-usage.line-3")));
                logger.info(ColorUtils.translateColorCodes(messagesConfig.getString("teamadmin-command-incorrect-usage.line-4")));
                logger.info(ColorUtils.translateColorCodes(messagesConfig.getString("teamadmin-command-incorrect-usage.line-5")));
            }
        }
        return true;
    }
}
