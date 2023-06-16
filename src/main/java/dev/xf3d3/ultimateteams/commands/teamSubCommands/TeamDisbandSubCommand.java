package dev.xf3d3.ultimateteams.commands.teamSubCommands;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamDisbandSubCommand {

    FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();

    //TODO: ask for confirmation

    private final UltimateTeams plugin;

    public TeamDisbandSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public boolean disbandClanSubCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            if (plugin.getTeamStorageUtil().deleteTeam(player)) {
                sender.sendMessage(Utils.Color(messagesConfig.getString("team-successfully-disbanded")));
            } else {
                sender.sendMessage(Utils.Color(messagesConfig.getString("team-disband-failure")));
            }
            return true;
        }
        return false;
    }
}
