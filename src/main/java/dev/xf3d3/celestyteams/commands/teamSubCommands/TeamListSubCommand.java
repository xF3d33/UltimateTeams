package dev.xf3d3.celestyteams.commands.teamSubCommands;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.models.Team;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TeamListSubCommand {

    FileConfiguration messagesConfig = CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig();

    private final CelestyTeams plugin;

    public TeamListSubCommand(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
    }

    public boolean teamListSubCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Set<Map.Entry<UUID, Team>> teams = plugin.getTeamStorageUtil().getClans();
            StringBuilder teamsString = new StringBuilder();
            if (teams.size() == 0) {
                sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("no-teams-to-list")));
            } else {
                teamsString.append(ColorUtils.translateColorCodes(messagesConfig.getString("teams-list-header") + "\n"));
                teams.forEach((team) ->
                        teamsString.append(ColorUtils.translateColorCodes(team.getValue().getTeamFinalName() + "\n")));
                teamsString.append(" ");
                teamsString.append(ColorUtils.translateColorCodes(messagesConfig.getString("teams-list-footer")));
                sender.sendMessage(teamsString.toString());
            }
            return true;

        }
        return false;
    }
}
