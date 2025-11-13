package dev.xf3d3.ultimateteams.commands.subCommands;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TeamPrefixSubCommand {

    private final int MIN_CHAR_LIMIT;
    private final int MAX_CHAR_LIMIT;

    private final UltimateTeams plugin;

    public TeamPrefixSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.MAX_CHAR_LIMIT = plugin.getSettings().getTeamTagsMaxCharLimit();
        this.MIN_CHAR_LIMIT = plugin.getSettings().getTeamTagsMinCharLimit();
    }

    public void teamPrefixSubCommand(CommandSender sender, String prefix, List<String> bannedTags) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        if (bannedTags.stream().map(String::toLowerCase).toList().contains(prefix.toLowerCase())) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamPrefixIsBanned().replace("%TEAMPREFIX%", prefix)));
            return;
        }

        if (plugin.getTeamStorageUtil().getTeams().stream().map(Team::getPrefix).toList().contains(prefix)) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamPrefixAlreadyTaken().replace("%TEAMPREFIX%", prefix)));
            return;
        }

        if (!plugin.getSettings().isTeamTagAllowColorCodes() && (prefix.contains("&") || prefix.contains("#"))) {

            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamTagCannotContainColours()));
            return;
        }

        if (plugin.getSettings().isTeamTagRequirePermColorCodes() && !player.hasPermission("ultimateteams.team.tag.usecolors") && (prefix.contains("&") || prefix.contains("#"))) {

            player.sendMessage(MineDown.parse(plugin.getMessages().getUseColoursMissingPermission()));
            return;
        }

        final int prefixLength = Utils.removeColors(prefix).length();
        if (prefixLength >= MIN_CHAR_LIMIT && prefixLength <= MAX_CHAR_LIMIT) {

            plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                    team -> {
                        // Check permission
                        if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.PREFIX)))) {
                            sender.sendMessage(MineDown.parse(plugin.getMessages().getNoPermission()));
                            return;
                        }

                        team.setPrefix(prefix);
                        plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                        sender.sendMessage(MineDown.parse(plugin.getMessages().getTeamPrefixChangeSuccessful().replace("%TEAMPREFIX%", Utils.Color(prefix))));
                    },
                    () -> sender.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()))
            );

        } else if (prefixLength > MAX_CHAR_LIMIT) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getTeamPrefixTooLong().replace("%CHARMAX%", String.valueOf(MAX_CHAR_LIMIT))));
        } else {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getTeamPrefixTooShort().replace("%CHARMIN%", String.valueOf(MIN_CHAR_LIMIT))));
        }
    }}
