package dev.xf3d3.ultimateteams.commands.subCommands;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class TeamMotdSubCommand {

    private final int MIN_CHAR_LIMIT;
    private final int MAX_CHAR_LIMIT;

    private final UltimateTeams plugin;

    public static Pattern motdRegex;

    public TeamMotdSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.MAX_CHAR_LIMIT = plugin.getSettings().getTeam().getMotd().getLength().getMax();
        this.MIN_CHAR_LIMIT = plugin.getSettings().getTeam().getMotd().getLength().getMin();

        if (motdRegex == null) motdRegex = Pattern.compile(plugin.getSettings().getTeam().getMotd().getRegex().getValue());
    }

    public void teamSetMotdSubCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getPlayerOnlyCommand()));
            return;
        }

        if (!plugin.getSettings().getTeam().getMotd().isEnable()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getFunctionDisabled()));

            return;
        }

        final String motd = String.join(" ", args);

        if (plugin.getSettings().getTeam().getMotd().getRegex().isEnable() && !motdRegex.matcher(motd).matches()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getMotd().getNotValid()));

            return;
        }


        if (!plugin.getSettings().getTeam().getMotd().isAllowColors() && (motd.contains("&") || motd.contains("#"))) {

            player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getMotd().getCannotContainColours()));
            return;
        }

        if (plugin.getSettings().getTeam().getMotd().isAllowColors() && !player.hasPermission("ultimateteams.team.motd.usecolors") && (motd.contains("&") || motd.contains("#"))) {

            player.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getNoColourPermission()));
            return;
        }

        final int motdLength = Utils.removeColors(motd).length();
        if (motdLength >= MIN_CHAR_LIMIT && motdLength <= MAX_CHAR_LIMIT) {

            plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                    team -> {
                        // Check permission
                        if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.MOTD)))) {
                            sender.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getNoPermission()));
                            return;
                        }

                        team.setMotd(motd);
                        plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                        sender.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getMotd().getChangeSuccessful().replace("%MOTD%", Utils.Color(motd))));
                    },
                    () -> sender.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getInfo().getNotInTeam()))
            );

        } else if (motdLength > MAX_CHAR_LIMIT) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getMotd().getTooLong().replace("%CHARMAX%", String.valueOf(MAX_CHAR_LIMIT))));
        } else {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getMotd().getTooShort().replace("%CHARMIN%", String.valueOf(MIN_CHAR_LIMIT))));
        }
    }

    public void teamRemoveMotdSubCommand(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getPlayerOnlyCommand()));
            return;
        }

        if (!plugin.getSettings().getTeam().getMotd().isEnable()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getFunctionDisabled()));

            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    // Check permission
                    if (!(plugin.getTeamStorageUtil().isTeamOwner(player) || (plugin.getTeamStorageUtil().isTeamManager(player) && team.hasPermission(Team.Permission.MOTD)))) {
                        sender.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getNoPermission()));
                        return;
                    }

                    team.setMotd(null);
                    plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(player, team));

                    sender.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getMotd().getDisabled()));
                },
                () -> sender.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getInfo().getNotInTeam()))
        );
    }
}
