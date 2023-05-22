package dev.xf3d3.celestyteams.commands.teamSubCommands;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.api.ClanAllyAddEvent;
import dev.xf3d3.celestyteams.api.ClanAllyRemoveEvent;
import dev.xf3d3.celestyteams.models.Team;
import dev.xf3d3.celestyteams.utils.TeamStorageUtil;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class TeamAllySubCommand {

    FileConfiguration teamsConfig = CelestyTeams.getPlugin().getConfig();
    FileConfiguration messagesConfig = CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig();
    Logger logger = CelestyTeams.getPlugin().getLogger();
    private static final String ALLY_CLAN = "%ALLYCLAN%";
    private static final String ALLY_OWNER = "%ALLYOWNER%";
    private static final String CLAN_OWNER = "%CLANOWNER%";

    private final CelestyTeams plugin;

    public TeamAllySubCommand(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
    }

    //todo: args should be team name instead of team owner name
    public void teamAllyAddSubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();

                if (plugin.getTeamStorageUtil().isClanOwner(player)){
                    if (plugin.getTeamStorageUtil().findTeamByName(args[0]) != null) {
                        Team team = plugin.getTeamStorageUtil().findTeamByName(args[0]);
                        Player allyClanOwner = Bukkit.getPlayer(team.getTeamOwner());

                        if (allyClanOwner == null) {
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("ally-team-add-owner-offline")));
                            return;
                        }

                        if (plugin.getTeamStorageUtil().findTeamByOwner(player) != team){
                            String allyOwnerUUIDString = team.getTeamOwner();

                            if (plugin.getTeamStorageUtil().findTeamByOwner(player).getClanAllies().size() >= teamsConfig.getInt("max-team-allies")){
                                int maxSize = teamsConfig.getInt("max-team-allies");
                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-ally-max-amount-reached")).replace("%LIMIT%", String.valueOf(maxSize)));
                            }

                            if (team.getClanEnemies().contains(allyOwnerUUIDString)){
                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-cannot-ally-enemy-team")));
                            }

                            if (team.getClanAllies().contains(allyOwnerUUIDString)){
                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-team-already-your-ally")));
                            } else {
                                plugin.getTeamStorageUtil().addClanAlly(player, allyClanOwner);
                                fireClanAllyAddEvent(player, team, allyClanOwner, team);

                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("added-team-to-your-allies").replace(ALLY_CLAN, team.getTeamFinalName())));
                            }
                            if (allyClanOwner.isOnline()){
                                allyClanOwner.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-added-to-other-allies").replace(CLAN_OWNER, player.getName())));
                            }else {
                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-to-add-team-to-allies").replace(ALLY_OWNER, args[0])));
                            }
                        }else {
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-cannot-ally-your-own-team")));
                        }
                    }
                } else {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-must-be-owner")));
                }
            }
        }

    public void teamAllyRemoveSubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();

            if (plugin.getTeamStorageUtil().isClanOwner(player)){
                if (plugin.getTeamStorageUtil().findTeamByOwner(player) != null){
                    Player allyClanOwner = Bukkit.getPlayer(args[0]);
                    if (allyClanOwner != null){
                        if (plugin.getTeamStorageUtil().findTeamByOwner(allyClanOwner) != null){
                            Team allyTeam = plugin.getTeamStorageUtil().findTeamByOwner(allyClanOwner);
                            List<String> alliedClans = plugin.getTeamStorageUtil().findTeamByOwner(player).getClanAllies();
                            UUID allyClanOwnerUUID = allyClanOwner.getUniqueId();
                            String allyClanOwnerString = allyClanOwnerUUID.toString();
                            if (alliedClans.contains(allyClanOwnerString)){
                                fireClanAllyRemoveEvent(plugin.getTeamStorageUtil(), player, allyClanOwner, allyTeam);

                                plugin.getTeamStorageUtil().removeClanAlly(player, allyClanOwner);
                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("removed-team-from-your-allies").replace(ALLY_CLAN, allyTeam.getTeamFinalName())));
                                if (allyClanOwner.isOnline()){
                                    allyClanOwner.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-removed-from-other-allies").replace(CLAN_OWNER, player.getName())));
                                }
                            } else {
                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-to-remove-team-from-allies").replace(ALLY_OWNER, args[0])));
                            }
                        } else {
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-player-not-team-owner").replace(ALLY_OWNER, args[0])));
                        }
                    } else {
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("ally-team-remove-owner-offline").replace(ALLY_OWNER, args[0])));
                    }
                }
            } else {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-must-be-owner")));
            }
        }
    }

    private void fireClanAllyRemoveEvent(TeamStorageUtil storageUtil, Player player, Player allyClanOwner, Team allyTeam) {
        ClanAllyRemoveEvent teamAllyRemoveEvent = new ClanAllyRemoveEvent(player, storageUtil.findTeamByOwner(player), allyTeam, allyClanOwner);
        Bukkit.getPluginManager().callEvent(teamAllyRemoveEvent);
    }

    private void fireClanAllyAddEvent(Player player, Team team, Player allyClanOwner, Team allyTeam) {
        ClanAllyAddEvent teamAllyAddEvent = new ClanAllyAddEvent(player, team, allyTeam, allyClanOwner);
        Bukkit.getPluginManager().callEvent(teamAllyAddEvent);
    }
}
