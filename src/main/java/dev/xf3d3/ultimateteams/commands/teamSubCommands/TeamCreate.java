package dev.xf3d3.ultimateteams.commands.teamSubCommands;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.TeamCreateEvent;
import dev.xf3d3.ultimateteams.team.Team;
import dev.xf3d3.ultimateteams.manager.TeamsManager;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TeamCreate {
    private final FileConfiguration messagesConfig;
    private static final String TEAM_PLACEHOLDER = "%TEAM%";

    int MIN_CHAR_LIMIT;
    int MAX_CHAR_LIMIT;

    private final ConcurrentLinkedQueue<Team> teams;
    ArrayList<String> teamNamesList = new ArrayList<>();

    private final UltimateTeams plugin;

    public TeamCreate(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.teams = plugin.getTeams();
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
        this.MIN_CHAR_LIMIT = plugin.getSettings().getTeamTagsMinCharLimit();
        this.MAX_CHAR_LIMIT = plugin.getSettings().getTeamTagsMaxCharLimit();
    }

    public void createTeamSubCommand(CommandSender sender, @NotNull String name, List<String> bannedTags) {

        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        final TeamsManager storageUtil = plugin.getManager().teams();

        teams.forEach(team -> teamNamesList.add(team.getName()));


        // Check name length
        if (name.isEmpty()) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-create-incorrect-usage")));
        }


        // Check if name is banned
        if (bannedTags.contains(name) || name.contains(" ")) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-name-is-banned").replace(TEAM_PLACEHOLDER, name)));
            return;
        }

        // Check if name has already been taken
        if (teamNamesList.contains(name)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-name-already-taken").replace(TEAM_PLACEHOLDER, name)));
            return;
        }

        for (String names : teamNamesList) {
            if (StringUtils.containsAnyIgnoreCase(names, name)) {
                player.sendMessage(Utils.Color(messagesConfig.getString("team-name-already-taken").replace(TEAM_PLACEHOLDER, name)));

                return;
            }
        }

        // Check if name contains colours
        if (name.contains("&") || name.contains("#")) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-name-cannot-contain-colours")));
            return;
        }

        // Check name max and min length
        if (name.length() < MIN_CHAR_LIMIT) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-name-too-short").replace("%CHARMIN%", Integer.toString(MIN_CHAR_LIMIT))));
        } else if (name.length() > MAX_CHAR_LIMIT) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-name-too-long").replace("%CHARMAX%", Integer.toString(MAX_CHAR_LIMIT))));
        }

        // Check if player is already a team owner
        if (storageUtil.isTeamOwner(player)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-creation-failed").replace(TEAM_PLACEHOLDER, Utils.Color(name))));
            return;
        }

        storageUtil.findTeamByMember(player).ifPresentOrElse(
                // Team player found, cannot create a new team
                team -> {
                    player.sendMessage(Utils.Color(messagesConfig.getString("team-creation-failed").replace(TEAM_PLACEHOLDER, Utils.Color(name))));
                },

                // Team player not found, proceed with team creation
                () -> {
                    Team team = storageUtil.createTeam(player, name);
                    String teamCreated = Utils.Color(messagesConfig.getString("team-created-successfully")).replace(TEAM_PLACEHOLDER, Utils.Color(name));

                    player.sendMessage(teamCreated);

                    fireTeamCreateEvent(player, team);

                    teamNamesList.clear();
                }
        );
    }


    private void fireTeamCreateEvent(Player player, Team team) {
        TeamCreateEvent teamCreateEvent = new TeamCreateEvent(player, team);
        Bukkit.getPluginManager().callEvent(teamCreateEvent);
    }
}
