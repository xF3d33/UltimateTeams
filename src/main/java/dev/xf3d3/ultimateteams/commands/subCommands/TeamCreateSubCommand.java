package dev.xf3d3.ultimateteams.commands.subCommands;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamCreateEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.TeamsStorage;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TeamCreateSubCommand {
    private final UltimateTeams plugin;

    private final FileConfiguration messagesConfig;
    private final TeamsStorage storageUtil;
    private static final String TEAM_PLACEHOLDER = "%TEAM%";

    int MIN_CHAR_LIMIT;
    int MAX_CHAR_LIMIT;

    public TeamCreateSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;

        this.storageUtil = plugin.getTeamStorageUtil();
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();

        this.MIN_CHAR_LIMIT = plugin.getSettings().getTeamTagsMinCharLimit();
        this.MAX_CHAR_LIMIT = plugin.getSettings().getTeamTagsMaxCharLimit();
    }

    public void createTeamSubCommand(CommandSender sender, String name, List<String> bannedTags) {

        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }


        if (name.contains(" ")) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-name-contains-space").replace(TEAM_PLACEHOLDER, name)));
            return;
        }

        if (bannedTags.contains(name)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-name-is-banned").replace(TEAM_PLACEHOLDER, name)));
            return;
        }

        if (plugin.getTeamStorageUtil().getTeamsName().contains(name)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-name-already-taken").replace(TEAM_PLACEHOLDER, name)));
            return;
        }

        for (String names : plugin.getTeamStorageUtil().getTeamsName()) {
            if (names.toLowerCase().contains(name.toLowerCase())) {
                player.sendMessage(Utils.Color(messagesConfig.getString("team-name-already-taken").replace(TEAM_PLACEHOLDER, name)));
                return;
            }
        }

        if (name.contains("&") || name.contains("#")) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-name-cannot-contain-colours")));
            return;
        }

        if (storageUtil.isInTeam(player)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-creation-failed").replace(TEAM_PLACEHOLDER, Utils.Color(name))));
            return;
        }


        if (name.length() < MIN_CHAR_LIMIT) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-name-too-short").replace("%CHARMIN%", Integer.toString(MIN_CHAR_LIMIT))));

            return;
        } else if (name.length() > MAX_CHAR_LIMIT) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-name-too-long").replace("%CHARMAX%", Integer.toString(MAX_CHAR_LIMIT))));

            return;
        }


        storageUtil.createTeam(player, name);
        String teamCreated = Utils.Color(messagesConfig.getString("team-created-successfully")).replace(TEAM_PLACEHOLDER, Utils.Color(name));

        player.sendMessage(teamCreated);

        //fireTeamCreateEvent(player, team);
    }

    private void fireTeamCreateEvent(Player player, Team team) {
        TeamCreateEvent teamCreateEvent = new TeamCreateEvent(player, team);
        Bukkit.getPluginManager().callEvent(teamCreateEvent);
    }
}
