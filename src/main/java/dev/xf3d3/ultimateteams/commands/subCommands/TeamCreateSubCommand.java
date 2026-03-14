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
import java.util.regex.Pattern;

public class TeamCreateSubCommand {
    private final UltimateTeams plugin;

    private final TeamsStorage storageUtil;
    private static final String TEAM_PLACEHOLDER = "%TEAM%";

    public static Pattern teamNameRegex;

    int MIN_CHAR_LIMIT;
    int MAX_CHAR_LIMIT;

    public TeamCreateSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;

        this.storageUtil = plugin.getTeamStorageUtil();

        this.MIN_CHAR_LIMIT = plugin.getSettings().getTeam().getName().getMinLength();
        this.MAX_CHAR_LIMIT = plugin.getSettings().getTeam().getName().getMaxLength();

        if (teamNameRegex == null) teamNameRegex = Pattern.compile(plugin.getSettings().getTeam().getName().getRegex().getValue());
    }

    public void createTeamSubCommand(CommandSender sender, String name, List<String> bannedTags) {

        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getPlayerOnlyCommand()));
            return;
        }

        if (plugin.getSettings().getTeam().getName().getRegex().isEnable() && !teamNameRegex.matcher(name).matches()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getCreate().getNameBanned()));

            return;
        }


        if (name.contains(" ")) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getCreate().getNameContainsSpace().replace(TEAM_PLACEHOLDER, name)));
            return;
        }

        if (bannedTags.stream().map(String::toLowerCase).toList().contains(name.toLowerCase())) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getCreate().getNameBanned().replace(TEAM_PLACEHOLDER, name)));
            return;
        }

        if (plugin.getTeamStorageUtil().getTeamsName().stream().map(String::toLowerCase).toList().contains(name.toLowerCase())) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getCreate().getNameTaken().replace(TEAM_PLACEHOLDER, name)));
            return;
        }

        if (!plugin.getSettings().getTeam().getName().isAllowColorCodes() && (name.contains("&") || name.contains("#"))) {

            player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getCreate().getNameCannotContainColours()));
            return;
        }

        if (plugin.getSettings().getTeam().getName().isRequirePermForColorCodes() && !player.hasPermission("ultimateteams.team.create.usecolors") && (name.contains("&") || name.contains("#"))) {

            player.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getNoColourPermission()));
            return;
        }

        if (storageUtil.isInTeam(player)) {

            player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getCreate().getFailed().replace(TEAM_PLACEHOLDER, Utils.Color(name))));
            return;
        }

        final int nameLength = Utils.removeColors(name).length();
        if (nameLength < MIN_CHAR_LIMIT) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getCreate().getNameTooShort().replace("%CHARMIN%", Integer.toString(MIN_CHAR_LIMIT))));

            return;
        } else if (nameLength > MAX_CHAR_LIMIT) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getCreate().getNameTooLong().replace("%CHARMAX%", Integer.toString(MAX_CHAR_LIMIT))));

            return;
        }

        if (plugin.getEconomyHook() != null && !plugin.getEconomyHook().takeMoney(player, plugin.getSettings().getEconomy().getTeamCreate().getCost())) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getEconomy().getNotEnoughMoney().replace("%MONEY%", String.valueOf(plugin.getSettings().getEconomy().getTeamCreate().getCost()))));
            return;
        }

        TeamPreCreateEvent event = new TeamPreCreateEvent(player, name);

        if (!event.callEvent()) return;

        storageUtil.createTeam(player, event.getName());

        player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getCreate().getSuccessful().replace(TEAM_PLACEHOLDER, Utils.Color(name))));

    }
}
