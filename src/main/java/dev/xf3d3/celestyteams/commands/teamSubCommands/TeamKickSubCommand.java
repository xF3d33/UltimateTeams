package dev.xf3d3.celestyteams.commands.teamSubCommands;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.models.Team;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamKickSubCommand {

    FileConfiguration messagesConfig = CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig();
    private static final String CLAN_PLACEHOLDER = "%CLAN%";
    private static final String PLAYER_TO_KICK = "%KICKEDPLAYER%";

    private final CelestyTeams plugin;

    public TeamKickSubCommand(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
    }

    public boolean teamKickSubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            if (args.length == 2) {
                if (args[1].length() > 1) {
                    Team targetTeam = plugin.getTeamStorageUtil().findTeamByOwner(player);
                    if (plugin.getTeamStorageUtil().findTeamByOwner(player) != null) {
                        Player playerToKick = Bukkit.getPlayer(args[1]);
                        OfflinePlayer offlinePlayerToKick = plugin.getUsersStorageUtil().getBukkitOfflinePlayerByName(args[1]);
                        if (playerToKick != null) {
                            if (!player.getName().equalsIgnoreCase(args[1])){
                                Team playerTeam = plugin.getTeamStorageUtil().findClanByPlayer(playerToKick);
                                if (targetTeam.equals(playerTeam)) {

                                    targetTeam.removeClanMember(playerToKick.getUniqueId().toString());
                                    plugin.runAsync(() -> plugin.getDatabase().updateTeam(targetTeam));

                                    String playerKickedMessage = ColorUtils.translateColorCodes(messagesConfig.getString("team-member-kick-successful")).replace(PLAYER_TO_KICK, args[1]);
                                    player.sendMessage(playerKickedMessage);
                                    if (playerToKick.isOnline()) {
                                        String kickMessage = ColorUtils.translateColorCodes(messagesConfig.getString("team-kicked-player-message")).replace(CLAN_PLACEHOLDER, targetTeam.getTeamFinalName());
                                        playerToKick.sendMessage(kickMessage);
                                        return true;
                                    }
                                }else {
                                    String differentClanMessage = ColorUtils.translateColorCodes(messagesConfig.getString("targeted-player-is-not-in-your-team")).replace(PLAYER_TO_KICK, args[1]);
                                    player.sendMessage(differentClanMessage);
                                }
                            }else {
                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-cannot-kick-yourself")));
                            }
                        } else if (offlinePlayerToKick != null){
                            if (!player.getName().equalsIgnoreCase(args[1])){
                                Team offlinePlayerTeam = plugin.getTeamStorageUtil().findClanByOfflinePlayer(offlinePlayerToKick);
                                if (targetTeam.equals(offlinePlayerTeam)){

                                    targetTeam.removeClanMember(offlinePlayerToKick.getUniqueId().toString());
                                    plugin.runAsync(() -> plugin.getDatabase().updateTeam(targetTeam));

                                    String playerKickedMessage = ColorUtils.translateColorCodes(messagesConfig.getString("team-member-kick-successful")).replace(PLAYER_TO_KICK, args[1]);
                                    player.sendMessage(playerKickedMessage);
                                    return true;
                                }else {
                                    String differentClanMessage = ColorUtils.translateColorCodes(messagesConfig.getString("targeted-player-is-not-in-your-team")).replace(PLAYER_TO_KICK, args[1]);
                                    player.sendMessage(differentClanMessage);
                                }
                            }else {
                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-cannot-kick-yourself")));
                            }
                        }else {
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("could-not-find-specified-player").replace(PLAYER_TO_KICK, args[1])));
                        }
                    }else {
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("must-be-owner-to-kick")));
                    }
                }else {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-kick-command-usage")));
                }
            }
            return true;

        }
        return false;
    }
}
