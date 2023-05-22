package dev.xf3d3.celestyteams.commands.teamSubCommands;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.models.Team;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TeamPrefixSubCommand {

    FileConfiguration teamsConfig = CelestyTeams.getPlugin().getConfig();
    FileConfiguration messagesConfig = CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig();

    int MIN_CHAR_LIMIT = teamsConfig.getInt("team-tags.min-character-limit");
    int MAX_CHAR_LIMIT = teamsConfig.getInt("team-tags.max-character-limit");

    Set<Map.Entry<UUID, Team>> teams;
    ArrayList<String> teamsPrefixList = new ArrayList<>();

    private final CelestyTeams plugin;

    public TeamPrefixSubCommand(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
        this.teams = plugin.getTeamStorageUtil().getClans();
    }

    public boolean teamPrefixSubCommand(CommandSender sender, String[] args, List<String> bannedTags) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            teams.forEach((teams) ->
                    teamsPrefixList.add(teams.getValue().getTeamPrefix()));
            if (args.length == 2) {
                if (bannedTags.contains(args[1])){
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-prefix-is-banned").replace("%CLANPREFIX%", args[1])));
                    return true;
                }
                if (teamsPrefixList.contains(args[1])){
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-prefix-already-taken").replace("%CLANPREFIX%", args[1])));
                    return true;
                }
                if (plugin.getTeamStorageUtil().isClanOwner(player)){
                    if (args[1].length() >= MIN_CHAR_LIMIT && args[1].length() <= MAX_CHAR_LIMIT) {
                        Team playerTeam = plugin.getTeamStorageUtil().findTeamByOwner(player);
                        plugin.getTeamStorageUtil().updatePrefix(player, args[1]);
                        String prefixConfirmation = ColorUtils.translateColorCodes(messagesConfig.getString("team-prefix-change-successful")).replace("%CLANPREFIX%", playerTeam.getTeamPrefix());
                        sender.sendMessage(prefixConfirmation);
                        teamsPrefixList.clear();
                        return true;
                    }else if (args[1].length() > MAX_CHAR_LIMIT) {
                        int maxCharLimit = teamsConfig.getInt("team-tags.max-character-limit");
                        sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-prefix-too-long").replace("%CHARMAX%", String.valueOf(maxCharLimit))));
                        teamsPrefixList.clear();
                        return true;
                    }else {
                        int minCharLimit = teamsConfig.getInt("team-tags.min-character-limit");
                        sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-prefix-too-short").replace("%CHARMIN%", String.valueOf(minCharLimit))));
                        teamsPrefixList.clear();
                        return true;
                    }
                }else {
                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("must-be-owner-to-change-prefix")));
                    teamsPrefixList.clear();
                    return true;
                }
            }else {
                sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-invalid-prefix")));
                teamsPrefixList.clear();
            }
            return true;

        }
        return false;
    }
}
