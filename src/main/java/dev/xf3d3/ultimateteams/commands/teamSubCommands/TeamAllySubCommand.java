package dev.xf3d3.ultimateteams.commands.teamSubCommands;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.TeamAllyAddEvent;
import dev.xf3d3.ultimateteams.api.TeamAllyRemoveEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.TeamStorageUtil;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class TeamAllySubCommand {

    private final FileConfiguration teamsConfig;
    private final FileConfiguration messagesConfig;
    private final Logger logger;
    private static final String ALLY_TEAM = "%ALLYTEAM%";
    private static final String ALLY_OWNER = "%ALLYTEAM%";
    private static final String TEAM_OWNER = "%TEAMOWNER%";

    private final UltimateTeams plugin;

    public TeamAllySubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.teamsConfig = plugin.getConfig();
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
        this.logger = plugin.getLogger();
    }

    public void teamAllyAddSubCommand(CommandSender sender, String teamName) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (!plugin.getTeamStorageUtil().isClanOwner(player)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            return;
        }

        if (plugin.getTeamStorageUtil().findTeamByName(teamName) != null) {
            Team team = plugin.getTeamStorageUtil().findTeamByName(teamName);
            Player allyClanOwner = Bukkit.getPlayer(UUID.fromString(team.getTeamOwner()));

            if (allyClanOwner == null) {
                player.sendMessage(Utils.Color(messagesConfig.getString("ally-team-add-owner-offline").replaceAll("%ALLYTEAM%", team.getTeamFinalName())));
                return;
            }

            if (plugin.getTeamStorageUtil().findTeamByOwner(player) != team){
                String allyOwnerUUIDString = team.getTeamOwner();

                if (plugin.getTeamStorageUtil().findTeamByOwner(player).getTeamAllies().size() >= teamsConfig.getInt("max-team-allies")) {
                    int maxSize = teamsConfig.getInt("max-team-allies");
                    player.sendMessage(Utils.Color(messagesConfig.getString("team-ally-max-amount-reached")).replaceAll("%LIMIT%", String.valueOf(maxSize)));
                    return;
                }

                if (team.getTeamEnemies().contains(allyOwnerUUIDString)){
                    player.sendMessage(Utils.Color(messagesConfig.getString("failed-cannot-ally-enemy-team")));
                    return;
                }

                if (team.getTeamAllies().contains(allyOwnerUUIDString)){
                    player.sendMessage(Utils.Color(messagesConfig.getString("failed-team-already-your-ally")));
                    return;
                } else {
                    plugin.getTeamStorageUtil().addClanAlly(player, allyClanOwner);
                    fireClanAllyAddEvent(player, team, allyClanOwner, team);

                    // send message to player
                    player.sendMessage(Utils.Color(messagesConfig.getString("added-team-to-your-allies").replaceAll(ALLY_TEAM, team.getTeamFinalName())));
                }

                // send message to player team
                for (String teamPlayerString : plugin.getTeamStorageUtil().findTeamByOwner(player).getTeamMembers()) {
                    Player teamPlayer = Bukkit.getPlayer(UUID.fromString(teamPlayerString));

                    if (teamPlayer != null) {
                        teamPlayer.sendMessage(Utils.Color(messagesConfig.getString("added-team-to-your-allies").replaceAll(ALLY_TEAM, team.getTeamFinalName())));
                    }
                }

                // send message to ally team
                for (String teamPlayerString : plugin.getTeamStorageUtil().findTeamByOwner(allyClanOwner).getTeamMembers()) {
                    Player teamPlayer = Bukkit.getPlayer(UUID.fromString(teamPlayerString));

                    if (teamPlayer != null) {
                        teamPlayer.sendMessage(Utils.Color(messagesConfig.getString("added-team-to-your-allies").replaceAll(ALLY_TEAM, team.getTeamFinalName())));
                    }
                }

                if (allyClanOwner.isOnline()) {
                    // send message to ally team owner
                    allyClanOwner.sendMessage(Utils.Color(messagesConfig.getString("team-added-to-other-allies").replaceAll(TEAM_OWNER, player.getName())));
                }
            } else {
                player.sendMessage(Utils.Color(messagesConfig.getString("failed-cannot-ally-your-own-team")));
            }
        }
    }

    public void teamAllyRemoveSubCommand(CommandSender sender, String teamName) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (!plugin.getTeamStorageUtil().isClanOwner(player)){
            sender.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            return;
        }

        if (plugin.getTeamStorageUtil().findTeamByName(teamName) != null) {
            Team team = plugin.getTeamStorageUtil().findTeamByName(teamName);
            Player allyClanOwner = Bukkit.getPlayer(UUID.fromString(team.getTeamOwner()));

            if (allyClanOwner == null) {
                player.sendMessage(Utils.Color(messagesConfig.getString("ally-team-remove-owner-offline").replaceAll("%ALLYTEAM%", team.getTeamFinalName())));
                return;
            }

            List<String> alliedClans = plugin.getTeamStorageUtil().findTeamByOwner(player).getTeamAllies();
            UUID allyClanOwnerUUID = allyClanOwner.getUniqueId();
            String allyClanOwnerString = allyClanOwnerUUID.toString();

            if (alliedClans.contains(allyClanOwnerString)) {
                fireClanAllyRemoveEvent(plugin.getTeamStorageUtil(), player, allyClanOwner, team);

                plugin.getTeamStorageUtil().removeClanAlly(player, allyClanOwner);

                // send message to player
                player.sendMessage(Utils.Color(messagesConfig.getString("removed-team-from-your-allies").replaceAll(ALLY_TEAM, team.getTeamFinalName())));

                // send message to player team
                for (String teamPlayerString : plugin.getTeamStorageUtil().findTeamByOwner(player).getTeamMembers()) {
                    Player teamPlayer = Bukkit.getPlayer(UUID.fromString(teamPlayerString));

                    if (teamPlayer != null) {
                        teamPlayer.sendMessage(Utils.Color(messagesConfig.getString("team-owner-removed-team-from-allies").replaceAll("%TEAM%", team.getTeamFinalName())));
                    }
                }

                // send message to ally team
                for (String teamPlayerString : plugin.getTeamStorageUtil().findTeamByOwner(allyClanOwner).getTeamMembers()) {
                    Player teamPlayer = Bukkit.getPlayer(UUID.fromString(teamPlayerString));

                    if (teamPlayer != null) {
                        teamPlayer.sendMessage(Utils.Color(messagesConfig.getString("team-removed-from-other-allies").replaceAll("%TEAM%", team.getTeamFinalName())));
                    }
                }

                if (allyClanOwner.isOnline()) {
                    // send message to ally team owner
                    allyClanOwner.sendMessage(Utils.Color(messagesConfig.getString("team-removed-from-other-allies").replaceAll("%TEAM%", team.getTeamFinalName())));
                }
            } else {
                player.sendMessage(Utils.Color(messagesConfig.getString("failed-to-remove-team-from-allies").replaceAll(ALLY_OWNER, teamName)));
            }

        }
    }

    private void fireClanAllyRemoveEvent(TeamStorageUtil storageUtil, Player player, Player allyClanOwner, Team allyTeam) {
        TeamAllyRemoveEvent teamAllyRemoveEvent = new TeamAllyRemoveEvent(player, storageUtil.findTeamByOwner(player), allyTeam, allyClanOwner);
        Bukkit.getPluginManager().callEvent(teamAllyRemoveEvent);
    }

    private void fireClanAllyAddEvent(Player player, Team team, Player allyClanOwner, Team allyTeam) {
        TeamAllyAddEvent teamAllyAddEvent = new TeamAllyAddEvent(player, team, allyTeam, allyClanOwner);
        Bukkit.getPluginManager().callEvent(teamAllyAddEvent);
    }
}
