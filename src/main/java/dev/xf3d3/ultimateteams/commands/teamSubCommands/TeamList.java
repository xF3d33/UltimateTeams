package dev.xf3d3.ultimateteams.commands.teamSubCommands;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.team.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TeamList {

    FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();

    private final UltimateTeams plugin;

    public TeamList(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void teamListSubCommand(CommandSender sender) {
            Set<Map.Entry<UUID, Team>> teams = plugin.getTeamStorageUtil().getTeams();
            StringBuilder teamsString = new StringBuilder();

            if (teams.size() == 0) {
                sender.sendMessage(Utils.Color(messagesConfig.getString("no-teams-to-list")));
            } else {
                teamsString.append(Utils.Color(messagesConfig.getString("teams-list-header") + "\n"));

                teams.forEach((team) -> teamsString.append(Utils.Color(team.getValue().getName() + "\n")));

                teamsString.append(" ");
                teamsString.append(Utils.Color(messagesConfig.getString("teams-list-footer")));

                sender.sendMessage(teamsString.toString());
            }
    }
}
