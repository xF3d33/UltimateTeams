package dev.xf3d3.ultimateteams.commands.subCommands;

import de.themoep.minedown.adventure.MineDown;
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

    private final UltimateTeams plugin;

    public TeamRenameSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.MIN_CHAR_LIMIT = plugin.getSettings().getTeamNameMinLength();
        this.MAX_CHAR_LIMIT = plugin.getSettings().getTeamNameMaxLength();
    }

    public void renameTeamSubCommand(CommandSender sender, String newname, List<String> bannedTags) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        if (newname.contains(" ")) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamNameContainsSpace().replace(TEAM_PLACEHOLDER, newname)));
            return;
        }

        if (bannedTags.stream().map(String::toLowerCase).toList().contains(newname.toLowerCase())) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamNameIsBanned().replace(TEAM_PLACEHOLDER, newname)));
            return;
        }

        if (plugin.getTeamStorageUtil().getTeamsName().stream().map(String::toLowerCase).toList().contains(newname.toLowerCase())) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamNameAlreadyTaken().replace(TEAM_PLACEHOLDER, newname)));
            return;
        }

        if (!plugin.getSettings().isTeamCreateAllowColorCodes() && (newname.contains("&") || newname.contains("#"))) {

            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamNameCannotContainColours()));
            return;
        }

        if (plugin.getSettings().isTeamCreateRequirePermColorCodes() && !player.hasPermission("ultimateteams.team.create.usecolors") && (newname.contains("&") || newname.contains("#"))) {

            player.sendMessage(MineDown.parse(plugin.getMessages().getUseColoursMissingPermission()));
            return;
        }

        final int nameLength = Utils.removeColors(newname).length();
        if (nameLength < MIN_CHAR_LIMIT) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamNameTooShort().replace("%CHARMIN%", Integer.toString(MIN_CHAR_LIMIT))));

            return;
        } else if (nameLength > MAX_CHAR_LIMIT) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamNameTooLong().replace("%CHARMAX%", Integer.toString(MAX_CHAR_LIMIT))));

            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.RENAME)))) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getNoPermission()));
                        return;
                    }

                    team.setName(newname);
                    plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                    sender.sendMessage(MineDown.parse(plugin.getMessages().getTeamNameChangeSuccessful().replace("%TEAM%", newname)));
                },
                () -> sender.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()))
        );
    }
}
