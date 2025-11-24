package dev.xf3d3.ultimateteams.commands.subCommands;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static dev.xf3d3.ultimateteams.commands.subCommands.TeamCreateSubCommand.teamNameRegex;

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

    public void renameTeamSubCommand(CommandSender sender, String newName, List<String> bannedTags) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        if (plugin.getSettings().isTeamNameUseRegex() && !teamNameRegex.matcher(newName).matches()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamNameIsBanned()));

            return;
        }

        if (newName.contains(" ")) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamNameContainsSpace().replace(TEAM_PLACEHOLDER, newName)));
            return;
        }

        if (bannedTags.stream().map(String::toLowerCase).toList().contains(newName.toLowerCase())) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamNameIsBanned().replace(TEAM_PLACEHOLDER, newName)));
            return;
        }

        if (plugin.getTeamStorageUtil().getTeamsName().stream().map(String::toLowerCase).toList().contains(newName.toLowerCase())) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamNameAlreadyTaken().replace(TEAM_PLACEHOLDER, newName)));
            return;
        }

        if (!plugin.getSettings().isTeamCreateAllowColorCodes() && (newName.contains("&") || newName.contains("#"))) {

            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamNameCannotContainColours()));
            return;
        }

        if (plugin.getSettings().isTeamCreateRequirePermColorCodes() && !player.hasPermission("ultimateteams.team.create.usecolors") && (newName.contains("&") || newName.contains("#"))) {

            player.sendMessage(MineDown.parse(plugin.getMessages().getUseColoursMissingPermission()));
            return;
        }

        final int nameLength = Utils.removeColors(newName).length();
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

                    team.setName(newName);
                    plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                    sender.sendMessage(MineDown.parse(plugin.getMessages().getTeamNameChangeSuccessful().replace("%TEAM%", newName)));
                },
                () -> sender.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()))
        );
    }
}
