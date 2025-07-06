package dev.xf3d3.ultimateteams.commands.subCommands;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TeamRenameSubCommand {

    private final int MIN_CHAR_LIMIT;
    private final int MAX_CHAR_LIMIT;
    private static final String TEAM_PLACEHOLDER = "%TEAM%";
    private final FileConfiguration messagesConfig;

    private final UltimateTeams plugin;

    public TeamRenameSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
        this.MIN_CHAR_LIMIT = plugin.getSettings().getTeamNameMinLength();
        this.MAX_CHAR_LIMIT = plugin.getSettings().getTeamNameMaxLength();
    }

    public void renameTeamSubCommand(CommandSender sender, String newname, List<String> bannedTags) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (newname.contains(" ")) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-name-contains-space").replace(TEAM_PLACEHOLDER, newname)));
            return;
        }

        if (bannedTags.stream().map(String::toLowerCase).toList().contains(newname.toLowerCase())) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-name-is-banned").replace(TEAM_PLACEHOLDER, newname)));
            return;
        }

        if (plugin.getTeamStorageUtil().getTeamsName().stream().map(String::toLowerCase).toList().contains(newname.toLowerCase())) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-name-already-taken").replace(TEAM_PLACEHOLDER, newname)));
            return;
        }

        if (!plugin.getSettings().isTeamCreateAllowColorCodes() && (newname.contains("&") || newname.contains("#"))) {

            player.sendMessage(Utils.Color(messagesConfig.getString("team-name-cannot-contain-colours")));
            return;
        }

        if (plugin.getSettings().isTeamCreateRequirePermColorCodes() && !player.hasPermission("ultimateteams.team.create.usecolors") && (newname.contains("&") || newname.contains("#"))) {

            player.sendMessage(Utils.Color(messagesConfig.getString("use-colours-missing-permission")));
            return;
        }

        final int nameLength = Utils.removeColors(newname).length();
        if (nameLength < MIN_CHAR_LIMIT) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-name-too-short").replace("%CHARMIN%", Integer.toString(MIN_CHAR_LIMIT))));

            return;
        } else if (nameLength > MAX_CHAR_LIMIT) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-name-too-long").replace("%CHARMAX%", Integer.toString(MAX_CHAR_LIMIT))));

            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.RENAME)))) {
                        sender.sendMessage(Utils.Color(messagesConfig.getString("no-permission")));
                        return;
                    }

                    team.setName(newname);
                    plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                    sender.sendMessage(Utils.Color(messagesConfig.getString("team-name-change-successful")).replace("%TEAM%", newname));
                },
                () -> sender.sendMessage(Utils.Color(messagesConfig.getString("not-in-team")))
        );
    }
}
