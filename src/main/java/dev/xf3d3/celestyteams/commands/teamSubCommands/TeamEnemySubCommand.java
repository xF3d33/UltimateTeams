package dev.xf3d3.celestyteams.commands.teamSubCommands;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.api.ClanEnemyAddEvent;
import dev.xf3d3.celestyteams.api.ClanEnemyRemoveEvent;
import dev.xf3d3.celestyteams.models.Team;
import dev.xf3d3.celestyteams.utils.TeamStorageUtil;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class TeamEnemySubCommand {

    FileConfiguration teamsConfig = CelestyTeams.getPlugin().getConfig();
    FileConfiguration messagesConfig = CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig();
    Logger logger = CelestyTeams.getPlugin().getLogger();
    private static final String ENEMY_CLAN = "%ENEMYCLAN%";
    private static final String ENEMY_OWNER = "%ENEMYOWNER%";
    private static final String CLAN_OWNER = "%CLANOWNER%";

    private final CelestyTeams plugin;

    public TeamEnemySubCommand(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
    }
    //todo: args should be team name instead of team owner name
    public boolean teamEnemySubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            final TeamStorageUtil storageUtil = plugin.getTeamStorageUtil();

            if (args.length > 2){
                if (args[1].equalsIgnoreCase("add")){
                    if (args[2].length() > 1){
                        if (storageUtil.isClanOwner(player)){
                            if (storageUtil.findTeamByOwner(player) != null){
                                Team team = storageUtil.findTeamByOwner(player);
                                Player enemyClanOwner = Bukkit.getPlayer(args[2]);
                                if (enemyClanOwner != null){
                                    if (storageUtil.findTeamByOwner(enemyClanOwner) != null){
                                        if (storageUtil.findTeamByOwner(player) != storageUtil.findTeamByOwner(enemyClanOwner)){
                                            Team enemyTeam = storageUtil.findTeamByOwner(enemyClanOwner);
                                            String enemyOwnerUUIDString = enemyTeam.getTeamOwner();
                                            if (storageUtil.findTeamByOwner(player).getClanEnemies().size() >= teamsConfig.getInt("max-team-enemies")){
                                                int maxSize = teamsConfig.getInt("max-team-enemies");
                                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-enemy-max-amount-reached")).replace("%LIMIT%", String.valueOf(maxSize)));
                                                return true;
                                            }
                                            if (team.getClanAllies().contains(enemyOwnerUUIDString)){
                                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-cannot-enemy-allied-team")));
                                                return true;
                                            }
                                            if (team.getClanEnemies().contains(enemyOwnerUUIDString)){
                                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-team-already-your-enemy")));
                                                return true;
                                            }else {
                                                storageUtil.addClanEnemy(player, enemyClanOwner);
                                                fireClanEnemyAddEvent(player, team, enemyClanOwner, enemyTeam);
                                                if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                                                    logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanEnemyAddEvent"));
                                                }
                                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("added-team-to-your-enemies").replace(ENEMY_CLAN, enemyTeam.getTeamFinalName())));
                                                String titleMain = ColorUtils.translateColorCodes(messagesConfig.getString("added-enemy-team-to-your-enemies-title-1").replace(CLAN_OWNER, enemyClanOwner.getName()));
                                                String titleAux = ColorUtils.translateColorCodes(messagesConfig.getString("added-enemy-team-to-your-enemies-title-2").replace(CLAN_OWNER, enemyClanOwner.getName()));
                                                player.sendTitle(titleMain, titleAux, 10, 70, 20);
                                                ArrayList<String> playerClanMembers = storageUtil.findTeamByOwner(player).getClanMembers();
                                                for (String playerClanMember : playerClanMembers){
                                                    if (playerClanMember != null){
                                                        UUID memberUUID = UUID.fromString(playerClanMember);
                                                        Player playerClanPlayer = Bukkit.getPlayer(memberUUID);
                                                        if (playerClanPlayer != null){
                                                            playerClanPlayer.sendTitle(titleMain, titleAux, 10, 70, 20);
                                                        }
                                                    }
                                                }
                                            }
                                            if (enemyClanOwner.isOnline()){
                                                enemyClanOwner.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-added-to-other-enemies").replace(CLAN_OWNER, player.getName())));
                                                String titleMainEnemy = ColorUtils.translateColorCodes(messagesConfig.getString("team-added-to-other-enemies-title-1").replace(CLAN_OWNER, player.getName()));
                                                String titleAuxEnemy = ColorUtils.translateColorCodes(messagesConfig.getString("team-added-to-other-enemies-title-2").replace(CLAN_OWNER, player.getName()));
                                                enemyClanOwner.sendTitle(titleMainEnemy, titleAuxEnemy, 10, 70, 20);
                                                ArrayList<String> enemyClanMembers = enemyTeam.getClanMembers();
                                                for (String enemyClanMember : enemyClanMembers){
                                                    if (enemyClanMember != null) {
                                                        UUID memberUUID = UUID.fromString(enemyClanMember);
                                                        Player enemyClanPlayer = Bukkit.getPlayer(memberUUID);
                                                        if (enemyClanPlayer != null) {
                                                            enemyClanPlayer.sendTitle(titleMainEnemy, titleAuxEnemy, 10, 70, 20);
                                                        }
                                                    }
                                                }
                                            }else {
                                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-to-add-team-to-enemies").replace(ENEMY_OWNER, args[2])));
                                            }
                                        }else {
                                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-cannot-enemy-your-own-team")));
                                        }
                                    }else {
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-enemy-player-not-team-owner").replace(ENEMY_OWNER, args[2])));
                                    }
                                }else {
                                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("enemy-team-add-owner-offline").replace(ENEMY_OWNER, args[2])));
                                }
                            }
                        }else {
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-must-be-owner")));
                        }
                    }else {
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-team-enemy-command-usage")));
                    }
                    return true;
                }else if (args[1].equalsIgnoreCase("remove")){
                    if (args[2].length() > 1){
                        if (storageUtil.isClanOwner(player)){
                            if (storageUtil.findTeamByOwner(player) != null){
                                Player enemyClanOwner = Bukkit.getPlayer(args[2]);
                                if (enemyClanOwner != null){
                                    if (storageUtil.findTeamByOwner(enemyClanOwner) != null){
                                        Team enemyTeam = storageUtil.findTeamByOwner(enemyClanOwner);
                                        List<String> enemyClans = storageUtil.findTeamByOwner(player).getClanEnemies();
                                        UUID enemyClanOwnerUUID = enemyClanOwner.getUniqueId();
                                        String enemyClanOwnerString = enemyClanOwnerUUID.toString();
                                        if (enemyClans.contains(enemyClanOwnerString)){
                                            fireClanEnemyRemoveEvent(storageUtil, player, enemyClanOwner, enemyTeam);
                                            if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                                                logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanEnemyRemoveEvent"));
                                            }
                                            storageUtil.removeClanEnemy(player, enemyClanOwner);
                                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("removed-team-from-your-enemies").replace(ENEMY_CLAN, enemyTeam.getTeamFinalName())));
                                            String titleMain = ColorUtils.translateColorCodes(messagesConfig.getString("removed-enemy-team-from-your-enemies-title-1").replace(CLAN_OWNER, enemyClanOwner.getName()));
                                            String titleAux = ColorUtils.translateColorCodes(messagesConfig.getString("removed-enemy-team-from-your-enemies-title-1").replace(CLAN_OWNER, enemyClanOwner.getName()));
                                            player.sendTitle(titleMain, titleAux, 10, 70, 20);
                                            ArrayList<String> playerClanMembers = storageUtil.findTeamByOwner(player).getClanMembers();
                                            for (String playerClanMember : playerClanMembers){
                                                if (playerClanMember != null){
                                                    UUID memberUUID = UUID.fromString(playerClanMember);
                                                    Player playerClanPlayer = Bukkit.getPlayer(memberUUID);
                                                    if (playerClanPlayer != null){
                                                        playerClanPlayer.sendTitle(titleMain, titleAux, 10, 70, 20);
                                                    }
                                                }
                                            }
                                            if (enemyClanOwner.isOnline()){
                                                enemyClanOwner.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-removed-from-other-enemies").replace(ENEMY_OWNER, player.getName())));
                                                String titleMainEnemy = ColorUtils.translateColorCodes(messagesConfig.getString("team-removed-from-other-enemies-title-1").replace(CLAN_OWNER, player.getName()));
                                                String titleAuxEnemy = ColorUtils.translateColorCodes(messagesConfig.getString("team-removed-from-other-enemies-title-2").replace(CLAN_OWNER, player.getName()));
                                                enemyClanOwner.sendTitle(titleMainEnemy, titleAuxEnemy, 10, 70, 20);
                                                ArrayList<String> enemyClanMembers = enemyTeam.getClanMembers();
                                                for (String enemyClanMember : enemyClanMembers){
                                                    if (enemyClanMember != null) {
                                                        UUID memberUUID = UUID.fromString(enemyClanMember);
                                                        Player enemyClanPlayer = Bukkit.getPlayer(memberUUID);
                                                        if (enemyClanPlayer != null) {
                                                            enemyClanPlayer.sendTitle(titleMain, titleAux, 10, 70, 20);
                                                        }
                                                    }
                                                }
                                            }
                                        }else {
                                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-to-remove-team-from-enemies").replace(ENEMY_OWNER, args[2])));
                                        }
                                    }else {
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-enemy-player-not-team-owner").replace(ENEMY_OWNER, args[2])));
                                    }
                                }else {
                                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("enemy-team-remove-owner-offline").replace(ENEMY_OWNER, args[2])));
                                }
                            }
                        }else {
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-must-be-owner")));
                        }
                    }else {
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-team-enemy-command-usage")));
                    }
                }
                return true;
            }else {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-team-enemy-command-usage")));
            }

        }
        return false;
    }

    private void fireClanEnemyRemoveEvent(TeamStorageUtil storageUtil, Player player, Player enemyClanOwner, Team enemyTeam) {
        ClanEnemyRemoveEvent teamEnemyRemoveEvent = new ClanEnemyRemoveEvent(player, storageUtil.findClanByPlayer(player), enemyTeam, enemyClanOwner);
        Bukkit.getPluginManager().callEvent(teamEnemyRemoveEvent);
    }
    private void fireClanEnemyAddEvent(Player player, Team team, Player enemyClanOwner, Team enemyTeam) {
        ClanEnemyAddEvent teamEnemyAddEvent = new ClanEnemyAddEvent(player, team, enemyTeam, enemyClanOwner);
        Bukkit.getPluginManager().callEvent(teamEnemyAddEvent);
    }
}
