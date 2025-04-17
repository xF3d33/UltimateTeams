package dev.xf3d3.ultimateteams.commands.subCommands.allies;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamAllyAddEvent;
import dev.xf3d3.ultimateteams.api.events.TeamAllyRemoveEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.TeamsStorage;
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

    private final FileConfiguration messagesConfig;
    private final Logger logger;
    private static final String ALLY_TEAM = "%ALLYTEAM%";
    private static final String ALLY_OWNER = "%ALLYTEAM%";
    private static final String TEAM_OWNER = "%TEAMOWNER%";

    private final UltimateTeams plugin;

    public TeamAllySubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
        this.logger = plugin.getLogger();
    }

    public void teamAllyAddSubCommand(CommandSender sender, String teamName) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (!plugin.getTeamStorageUtil().isTeamOwner(player)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            return;
        }

        if (plugin.getTeamStorageUtil().findTeamByName(teamName) != null) {
            Team team = plugin.getTeamStorageUtil().findTeamByName(teamName);
            Player allyTeamOwner = Bukkit.getPlayer(UUID.fromString(team.getTeamOwner()));

            if (allyTeamOwner == null) {
                player.sendMessage(Utils.Color(messagesConfig.getString("ally-team-add-owner-offline").replaceAll("%ALLYTEAM%", team.getTeamFinalName())));
                return;
            }

            if (plugin.getTeamStorageUtil().findTeamByOwner(player) != team) {
                String allyOwnerUUIDString = team.getTeamOwner();

                if (plugin.getTeamStorageUtil().findTeamByOwner(player).getTeamAllies().size() >= plugin.getSettings().getMaxTeamAllies()) {
                    player.sendMessage(Utils.Color(messagesConfig.getString("team-ally-max-amount-reached")).replaceAll("%LIMIT%", String.valueOf(plugin.getSettings().getMaxTeamAllies())));
                    return;
                }

                final String playerUUID = player.getUniqueId().toString();

                if (team.getTeamEnemies().contains(playerUUID)) {
                    player.sendMessage(Utils.Color(messagesConfig.getString("failed-cannot-ally-enemy-team")));
                    return;
                }

                if (team.getTeamAllies().contains(playerUUID)) {
                    player.sendMessage(Utils.Color(messagesConfig.getString("failed-team-already-your-ally")));
                    return;

                } else {
                    plugin.getTeamStorageUtil().addTeamAlly(player, allyTeamOwner);
                    fireTeamAllyAddEvent(player, team, allyTeamOwner, team);

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
                for (String teamPlayerString : plugin.getTeamStorageUtil().findTeamByOwner(allyTeamOwner).getTeamMembers()) {
                    Player teamPlayer = Bukkit.getPlayer(UUID.fromString(teamPlayerString));

                    if (teamPlayer != null) {
                        teamPlayer.sendMessage(Utils.Color(messagesConfig.getString("added-team-to-your-allies").replaceAll(ALLY_TEAM, team.getTeamFinalName())));
                    }
                }

                if (allyTeamOwner.isOnline()) {
                    // send message to ally team owner
                    allyTeamOwner.sendMessage(Utils.Color(messagesConfig.getString("team-added-to-other-allies").replaceAll(TEAM_OWNER, player.getName())));
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

        if (!plugin.getTeamStorageUtil().isTeamOwner(player)){
            sender.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            return;
        }

        if (plugin.getTeamStorageUtil().findTeamByName(teamName) != null) {
            Team team = plugin.getTeamStorageUtil().findTeamByName(teamName);
            Player allyTeamOwner = Bukkit.getPlayer(UUID.fromString(team.getTeamOwner()));

            if (allyTeamOwner == null) {
                player.sendMessage(Utils.Color(messagesConfig.getString("ally-team-remove-owner-offline").replaceAll("%ALLYTEAM%", team.getTeamFinalName())));
                return;
            }

            List<String> alliedTeams = plugin.getTeamStorageUtil().findTeamByOwner(player).getTeamAllies();
            UUID allyTeamOwnerUUID = allyTeamOwner.getUniqueId();
            String allyTeamOwnerString = allyTeamOwnerUUID.toString();

            if (alliedTeams.contains(allyTeamOwnerString)) {
                fireTeamAllyRemoveEvent(plugin.getTeamStorageUtil(), player, allyTeamOwner, team);

                plugin.getTeamStorageUtil().removeTeamAlly(player, allyTeamOwner);

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
                for (String teamPlayerString : plugin.getTeamStorageUtil().findTeamByOwner(allyTeamOwner).getTeamMembers()) {
                    Player teamPlayer = Bukkit.getPlayer(UUID.fromString(teamPlayerString));

                    if (teamPlayer != null) {
                        teamPlayer.sendMessage(Utils.Color(messagesConfig.getString("team-removed-from-other-allies").replaceAll("%TEAM%", team.getTeamFinalName())));
                    }
                }

                if (allyTeamOwner.isOnline()) {
                    // send message to ally team owner
                    allyTeamOwner.sendMessage(Utils.Color(messagesConfig.getString("team-removed-from-other-allies").replaceAll("%TEAM%", team.getTeamFinalName())));
                }
            } else {
                player.sendMessage(Utils.Color(messagesConfig.getString("failed-to-remove-team-from-allies").replaceAll(ALLY_OWNER, teamName)));
            }

        }
    }

    private void fireTeamAllyRemoveEvent(TeamsStorage storageUtil, Player player, Player allyTeamOwner, Team allyTeam) {
        TeamAllyRemoveEvent teamAllyRemoveEvent = new TeamAllyRemoveEvent(player, storageUtil.findTeamByOwner(player), allyTeam, allyTeamOwner);
        Bukkit.getPluginManager().callEvent(teamAllyRemoveEvent);
    }

    private void fireTeamAllyAddEvent(Player player, Team team, Player allyTeamOwner, Team allyTeam) {
        TeamAllyAddEvent teamAllyAddEvent = new TeamAllyAddEvent(player, team, allyTeam, allyTeamOwner);
        Bukkit.getPluginManager().callEvent(teamAllyAddEvent);
    }
}
