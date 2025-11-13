package dev.xf3d3.ultimateteams.commands.subCommands;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamPreCreateEvent;
import dev.xf3d3.ultimateteams.utils.TeamsStorage;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TeamCreateSubCommand {
    private final UltimateTeams plugin;

    private final TeamsStorage storageUtil;
    private static final String TEAM_PLACEHOLDER = "%TEAM%";

    int MIN_CHAR_LIMIT;
    int MAX_CHAR_LIMIT;

    public TeamCreateSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;

        this.storageUtil = plugin.getTeamStorageUtil();

        this.MIN_CHAR_LIMIT = plugin.getSettings().getTeamNameMinLength();
        this.MAX_CHAR_LIMIT = plugin.getSettings().getTeamNameMaxLength();
    }

    public void createTeamSubCommand(CommandSender sender, String name, List<String> bannedTags) {

        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }


        if (name.contains(" ")) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamNameContainsSpace().replace(TEAM_PLACEHOLDER, name)));
            return;
        }

        if (bannedTags.stream().map(String::toLowerCase).toList().contains(name.toLowerCase())) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamNameIsBanned().replace(TEAM_PLACEHOLDER, name)));
            return;
        }

        if (plugin.getTeamStorageUtil().getTeamsName().stream().map(String::toLowerCase).toList().contains(name.toLowerCase())) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamNameAlreadyTaken().replace(TEAM_PLACEHOLDER, name)));
            return;
        }

        if (!plugin.getSettings().isTeamCreateAllowColorCodes() && (name.contains("&") || name.contains("#"))) {

            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamNameCannotContainColours()));
            return;
        }

        if (plugin.getSettings().isTeamCreateRequirePermColorCodes() && !player.hasPermission("ultimateteams.team.create.usecolors") && (name.contains("&") || name.contains("#"))) {

            player.sendMessage(MineDown.parse(plugin.getMessages().getUseColoursMissingPermission()));
            return;
        }

        if (storageUtil.isInTeam(player)) {

            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamCreationFailed().replace(TEAM_PLACEHOLDER, Utils.Color(name))));
            return;
        }

        final int nameLength = Utils.removeColors(name).length();
        if (nameLength < MIN_CHAR_LIMIT) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamNameTooShort().replace("%CHARMIN%", Integer.toString(MIN_CHAR_LIMIT))));

            return;
        } else if (nameLength > MAX_CHAR_LIMIT) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamNameTooLong().replace("%CHARMAX%", Integer.toString(MAX_CHAR_LIMIT))));

            return;
        }

        if (plugin.getEconomyHook() != null && !plugin.getEconomyHook().takeMoney(player, plugin.getSettings().getTeamCreateCost())) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getNotEnoughMoney().replace("%MONEY%", String.valueOf(plugin.getSettings().getTeamCreateCost()))));
            return;
        }

        TeamPreCreateEvent event = new TeamPreCreateEvent(player, name);

        if (event.callEvent()) return;

        storageUtil.createTeam(player, event.getName());

        player.sendMessage(MineDown.parse(plugin.getMessages().getTeamCreatedSuccessfully().replace(TEAM_PLACEHOLDER, Utils.Color(name))));

    }
}
