package dev.xf3d3.celestyteams.commands.teamSubCommands;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.api.ClanCreateEvent;
import dev.xf3d3.celestyteams.models.Team;
import dev.xf3d3.celestyteams.utils.TeamStorageUtil;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Logger;

public class TeamCreateSubCommand {

    FileConfiguration teamsConfig = CelestyTeams.getPlugin().getConfig();
    FileConfiguration messagesConfig = CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig();
    Logger logger = CelestyTeams.getPlugin().getLogger();
    private static final String CLAN_PLACEHOLDER = "%CLAN%";
    private static final String CLAN_OWNER = "%CLANOWNER%";

    int MIN_CHAR_LIMIT = teamsConfig.getInt("team-tags.min-character-limit");
    int MAX_CHAR_LIMIT = teamsConfig.getInt("team-tags.max-character-limit");

    private final Set<Map.Entry<UUID, Team>> teams;
    ArrayList<String> teamNamesList = new ArrayList<>();

    private final CelestyTeams plugin;

    public TeamCreateSubCommand(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
        this.teams = plugin.getTeamStorageUtil().getClans();
    }

    public void createClanSubCommand(CommandSender sender, String[] args, List<String> bannedTags) {

        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            final TeamStorageUtil storageUtil = plugin.getTeamStorageUtil();

            teams.forEach((teams) ->
                    teamNamesList.add(teams.getValue().getTeamFinalName()));
            if (args.length >= 1) {
                if (bannedTags.contains(args[0])) {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-name-is-banned").replace(CLAN_PLACEHOLDER, args[0])));
                }
                if (teamNamesList.contains(args[0])) {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-name-already-taken").replace(CLAN_PLACEHOLDER, args[0])));
                }
                for (String names : teamNamesList){
                    if (StringUtils.containsAnyIgnoreCase(names, args[0])){
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-name-already-taken").replace(CLAN_PLACEHOLDER, args[0])));

                    }
                }
                if (args[0].contains("&")||args[0].contains("#")){
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-name-cannot-contain-colours")));

                }
                if (storageUtil.isClanOwner(player)){
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-creation-failed").replace(CLAN_PLACEHOLDER, ColorUtils.translateColorCodes(args[0]))));

                }
                if (storageUtil.findClanByPlayer(player) != null){
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-creation-failed").replace(CLAN_PLACEHOLDER, ColorUtils.translateColorCodes(args[0]))));

                }
                if (args[0].length() < MIN_CHAR_LIMIT) {
                    int minCharLimit = teamsConfig.getInt("team-tags.min-character-limit");
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-name-too-short").replace("%CHARMIN%", Integer.toString(minCharLimit))));

                } else if (args[0].length() > MAX_CHAR_LIMIT) {
                    int maxCharLimit = teamsConfig.getInt("team-tags.max-character-limit");
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-name-too-long").replace("%CHARMAX%", Integer.toString(maxCharLimit))));

                } else {
                    if (!storageUtil.isTeamExisting(player)) {
                        Team team = storageUtil.createTeam(player, args[0]);
                        String teamCreated = ColorUtils.translateColorCodes(messagesConfig.getString("team-created-successfully")).replace(CLAN_PLACEHOLDER, ColorUtils.translateColorCodes(args[0]));

                        player.sendMessage(teamCreated);

                        fireClanCreateEvent(player, team);

                        if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                            logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanCreateEvent"));
                        }

                    } else {
                        String teamNotCreated = ColorUtils.translateColorCodes(messagesConfig.getString("team-creation-failed")).replace(CLAN_PLACEHOLDER, ColorUtils.translateColorCodes(args[0]));
                        player.sendMessage(teamNotCreated);
                    }
                    teamNamesList.clear();

                }
            } else {
                //TODO: message
                System.out.println("aaah");
            }
        }

    }

    private static void fireClanCreateEvent(Player player, Team team) {
        ClanCreateEvent teamCreateEvent = new ClanCreateEvent(player, team);
        Bukkit.getPluginManager().callEvent(teamCreateEvent);
    }
}
