package dev.xf3d3.celestyteams.commands.teamSubCommands;

import co.aikar.commands.annotation.Subcommand;
import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.api.ClanAllyAddEvent;
import dev.xf3d3.celestyteams.api.ClanAllyRemoveEvent;
import dev.xf3d3.celestyteams.models.Team;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import dev.xf3d3.celestyteams.utils.TeamStorageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class TeamAllySubCommand {

    private final FileConfiguration teamsConfig;
    private final FileConfiguration messagesConfig;
    private final Logger logger;
    private static final String ALLY_CLAN = "%ALLYTEAM%";
    private static final String ALLY_OWNER = "%ALLYTEAM%";
    private static final String CLAN_OWNER = "%TEAMOWNER%";

    private final CelestyTeams plugin;

    public TeamAllySubCommand(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
        this.teamsConfig = plugin.getConfig();
        this.messagesConfig = plugin.messagesFileManager.getMessagesConfig();
        this.logger = plugin.getLogger();
    }

    public void teamAllyAddSubCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("player-only-command")));
        }

        assert sender instanceof Player;
        final Player player = (Player) sender;

        if (!plugin.getTeamStorageUtil().isClanOwner(player)) {
            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-must-be-owner")));
            return;
        }

        if (plugin.getTeamStorageUtil().findTeamByName(args[0]) != null) {
            Team team = plugin.getTeamStorageUtil().findTeamByName(args[0]);
            Player allyClanOwner = Bukkit.getPlayer(team.getTeamOwner());

            if (allyClanOwner == null) {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("ally-team-add-owner-offline").replaceAll("%ALLYOWNER%", player.getName())));
                return;
            }

            if (plugin.getTeamStorageUtil().findTeamByOwner(player) != team){
                String allyOwnerUUIDString = team.getTeamOwner();

                if (plugin.getTeamStorageUtil().findTeamByOwner(player).getClanAllies().size() >= teamsConfig.getInt("max-team-allies")) {
                    int maxSize = teamsConfig.getInt("max-team-allies");
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-ally-max-amount-reached")).replaceAll("%LIMIT%", String.valueOf(maxSize)));
                }

                if (team.getClanEnemies().contains(allyOwnerUUIDString)){
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-cannot-ally-enemy-team")));
                }

                if (team.getClanAllies().contains(allyOwnerUUIDString)){
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-team-already-your-ally")));
                } else {
                    plugin.getTeamStorageUtil().addClanAlly(player, allyClanOwner);
                    fireClanAllyAddEvent(player, team, allyClanOwner, team);

                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("added-team-to-your-allies").replaceAll(ALLY_CLAN, team.getTeamFinalName())));
                }

                if (allyClanOwner.isOnline()) {
                    allyClanOwner.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-added-to-other-allies").replaceAll(CLAN_OWNER, player.getName())));
                } else {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-to-add-team-to-allies").replaceAll(ALLY_OWNER, args[0])));
                }
            } else {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-cannot-ally-your-own-team")));
            }
        }
    }

    public void teamAllyRemoveSubCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("player-only-command")));
        }

        assert sender instanceof Player;
        final Player player = (Player) sender;

        if (!plugin.getTeamStorageUtil().isClanOwner(player)){
            sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-must-be-owner")));
            return;
        }

        if (plugin.getTeamStorageUtil().findTeamByName(args[0]) != null) {
            Team team = plugin.getTeamStorageUtil().findTeamByName(args[0]);
            Player allyClanOwner = Bukkit.getPlayer(team.getTeamOwner());

            if (allyClanOwner == null) {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("ally-team-add-owner-offline").replaceAll("%ALLYOWNER%", player.getName())));
                return;
            }

            List<String> alliedClans = plugin.getTeamStorageUtil().findTeamByOwner(player).getClanAllies();
            UUID allyClanOwnerUUID = allyClanOwner.getUniqueId();
            String allyClanOwnerString = allyClanOwnerUUID.toString();

            if (alliedClans.contains(allyClanOwnerString)) {
                fireClanAllyRemoveEvent(plugin.getTeamStorageUtil(), player, allyClanOwner, team);

                plugin.getTeamStorageUtil().removeClanAlly(player, allyClanOwner);
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("removed-team-from-your-allies").replaceAll(ALLY_CLAN, team.getTeamFinalName())));
                if (allyClanOwner.isOnline()) {
                    allyClanOwner.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-removed-from-other-allies").replaceAll(CLAN_OWNER, player.getName())));
                }
            } else {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-to-remove-team-from-allies").replaceAll(ALLY_OWNER, args[0])));
            }

        }
    }

    private void fireClanAllyRemoveEvent(TeamStorageUtil storageUtil, Player player, Player allyClanOwner, Team allyTeam) {
        ClanAllyRemoveEvent teamAllyRemoveEvent = new ClanAllyRemoveEvent(player, storageUtil.findTeamByOwner(player), allyTeam, allyClanOwner);
        Bukkit.getPluginManager().callEvent(teamAllyRemoveEvent);
    }

    private void fireClanAllyAddEvent(Player player, Team team, Player allyClanOwner, Team allyTeam) {
        ClanAllyAddEvent teamAllyAddEvent = new ClanAllyAddEvent(player, team, allyTeam, allyClanOwner);
        Bukkit.getPluginManager().callEvent(teamAllyAddEvent);
    }
}
