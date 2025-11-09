package dev.xf3d3.ultimateteams.commands.subCommands;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class TeamListSubCommand {


    private final UltimateTeams plugin;

    public TeamListSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void teamListSubCommand(CommandSender sender) {
            StringBuilder teamsString = new StringBuilder();

            if (plugin.getTeamStorageUtil().getTeams().isEmpty()) {
                sender.sendMessage(MineDown.parse(plugin.getMessages().getNoTeamsToList()));
            } else {
                teamsString.append(Utils.Color(plugin.getMessages().getTeamsListHeader() + "\n"));

                plugin.getTeamStorageUtil().getTeams().forEach(team -> teamsString.append(Utils.Color(team.getName() + "&r\n")));

                teamsString.append(" ");
                teamsString.append(Utils.Color(plugin.getMessages().getTeamsListFooter()));

                sender.sendMessage(teamsString.toString());
            }
    }
}
