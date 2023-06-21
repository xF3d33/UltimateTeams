package dev.xf3d3.ultimateteams.commands.teamSubCommands;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.TeamCreateEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.TeamStorageUtil;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TeamCreateSubCommand {
    private final FileConfiguration messagesConfig;
    private static final String TEAM_PLACEHOLDER = "%TEAM%";

    int MIN_CHAR_LIMIT;
    int MAX_CHAR_LIMIT;

    private final Set<Map.Entry<UUID, Team>> teams;
    ArrayList<String> teamNamesList = new ArrayList<>();

    private final UltimateTeams plugin;

    public TeamCreateSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.teams = plugin.getTeamStorageUtil().getTeams();
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
        this.MIN_CHAR_LIMIT = plugin.getSettings().getTeamTagsMinCharLimit();
        this.MAX_CHAR_LIMIT = plugin.getSettings().getTeamTagsMaxCharLimit();
    }

    public void createTeamSubCommand(CommandSender sender, String name, List<String> bannedTags) {

        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        final TeamStorageUtil storageUtil = plugin.getTeamStorageUtil();

        teams.forEach((teams) -> teamNamesList.add(teams.getValue().getTeamFinalName()));

        System.out.println(player.getName() + " " + name);

        if (name.length() >= 1) {
            if (bannedTags.contains(name)) {
                player.sendMessage(Utils.Color(messagesConfig.getString("team-name-is-banned").replace(TEAM_PLACEHOLDER, name)));
                return;
            }

            if (teamNamesList.contains(name)) {
                player.sendMessage(Utils.Color(messagesConfig.getString("team-name-already-taken").replace(TEAM_PLACEHOLDER, name)));
                return;
            }

            for (String names : teamNamesList){
                if (StringUtils.containsAnyIgnoreCase(names, name)){
                    player.sendMessage(Utils.Color(messagesConfig.getString("team-name-already-taken").replace(TEAM_PLACEHOLDER, name)));

                }
                return;
            }

            if (name.contains("&") || name.contains("#")) {
                player.sendMessage(Utils.Color(messagesConfig.getString("team-name-cannot-contain-colours")));
                return;
            }

            if (storageUtil.isTeamOwner(player)) {
                player.sendMessage(Utils.Color(messagesConfig.getString("team-creation-failed").replace(TEAM_PLACEHOLDER, Utils.Color(name))));
                return;
            }

            if (storageUtil.findTeamByPlayer(player) != null){
                player.sendMessage(Utils.Color(messagesConfig.getString("team-creation-failed").replace(TEAM_PLACEHOLDER, Utils.Color(name))));
                return;
            }

            if (name.length() < MIN_CHAR_LIMIT) {
                player.sendMessage(Utils.Color(messagesConfig.getString("team-name-too-short").replace("%CHARMIN%", Integer.toString(MIN_CHAR_LIMIT))));

            } else if (name.length() > MAX_CHAR_LIMIT) {
                player.sendMessage(Utils.Color(messagesConfig.getString("team-name-too-long").replace("%CHARMAX%", Integer.toString(MAX_CHAR_LIMIT))));

            } else {
                if (!storageUtil.isTeamExisting(player)) {
                    Team team = storageUtil.createTeam(player, name);
                    String teamCreated = Utils.Color(messagesConfig.getString("team-created-successfully")).replace(TEAM_PLACEHOLDER, Utils.Color(name));

                    player.sendMessage(teamCreated);

                    fireTeamCreateEvent(player, team);

                } else {
                    String teamNotCreated = Utils.Color(messagesConfig.getString("team-creation-failed")).replace(TEAM_PLACEHOLDER, Utils.Color(name));
                    player.sendMessage(teamNotCreated);
                }
                teamNamesList.clear();

            }
        } else {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-create-incorrect-usage")));
        }
    }

    private void fireTeamCreateEvent(Player player, Team team) {
        TeamCreateEvent teamCreateEvent = new TeamCreateEvent(player, team);
        Bukkit.getPluginManager().callEvent(teamCreateEvent);
    }
}
