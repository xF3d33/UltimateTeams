package dev.xf3d3.ultimateteams.commands.subCommands.home;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamHomePreTeleportEvent;
import dev.xf3d3.ultimateteams.api.events.TeamHomeTeleportEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamHome;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class TeamHomeSubCommand {
    private final FileConfiguration messagesConfig;
    private static final String TIME_LEFT = "%TIMELEFT%";
    private static TeamHomePreTeleportEvent homePreTeleportEvent = null;
    private static final ConcurrentHashMap<UUID, Long> homeCoolDownTimer = new ConcurrentHashMap<>();
    private final UltimateTeams plugin;

    public TeamHomeSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
    }

    public void tpTeamHomeSubCommand(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        // check if function is enabled
        if (!plugin.getSettings().teamHomeEnabled()) {
            player.sendMessage(Utils.Color(messagesConfig.getString("function-disabled")));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    final TeamHome home = team.getHome();

                    // check if home exists
                    if (home == null) {
                        player.sendMessage(Utils.Color(messagesConfig.getString("failed-no-home-set")));
                        return;
                    }

                    fireTeamHomePreTPEvent(player, team);
                    if (homePreTeleportEvent.isCancelled()){
                        if (plugin.getSettings().debugModeEnabled()){
                            plugin.log(Level.INFO, Utils.Color("&6UltimateTeams-Debug: &aTeamHomePreTPEvent cancelled by external source"));
                        }
                        return;
                    }

                    if (plugin.getSettings().teamHomeCooldownEnabled()) {
                        if (!player.hasPermission("ultimateteams.bypass.homecooldown") && homeCoolDownTimer.containsKey(player.getUniqueId())) {
                            if (homeCoolDownTimer.get(player.getUniqueId()) > System.currentTimeMillis()) {
                                long timeLeft = (homeCoolDownTimer.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;

                                player.sendMessage(Utils.Color(messagesConfig.getString("home-cool-down-timer-wait")
                                        .replaceAll(TIME_LEFT, Long.toString(timeLeft))));
                            } else {
                                homeCoolDownTimer.put(player.getUniqueId(), System.currentTimeMillis() + (plugin.getSettings().getTeamHomeCooldownValue() * 1000L));
                                fireTeamHomeTeleportEvent(player, team, home.getLocation(), player.getLocation());
                                tpHome(player, home);
                            }
                        } else {
                            fireTeamHomeTeleportEvent(player, team, home.getLocation(), player.getLocation());
                            tpHome(player, home);
                            homeCoolDownTimer.put(player.getUniqueId(), System.currentTimeMillis() + (plugin.getSettings().getTeamHomeCooldownValue() * 1000L));
                        }
                        return;
                    }

                    fireTeamHomeTeleportEvent(player, team, home.getLocation(), player.getLocation());
                    tpHome(player, home);
                },
                () -> player.sendMessage(Utils.Color(messagesConfig.getString("failed-tp-not-in-team")))
        );


    }

    private void tpHome(@NotNull Player player, @NotNull TeamHome home) {
        plugin.getUtils().teleportPlayer(player, home.getLocation(), home.getServer(), Utils.TeleportType.HOME, null);
        //player.sendMessage(Utils.Color(messagesConfig.getString("successfully-teleported-to-home")));
    }

    private void fireTeamHomePreTPEvent(Player player, Team team) {
        TeamHomePreTeleportEvent teamHomePreTeleportEvent = new TeamHomePreTeleportEvent(player, team);
        Bukkit.getPluginManager().callEvent(teamHomePreTeleportEvent);
        homePreTeleportEvent = teamHomePreTeleportEvent;
    }

    private void fireTeamHomeTeleportEvent(Player player, Team team, Location homeLocation, Location tpFromLocation) {
        TeamHomeTeleportEvent teamHomeTeleportEvent = new TeamHomeTeleportEvent(player, team, homeLocation, tpFromLocation);
        Bukkit.getPluginManager().callEvent(teamHomeTeleportEvent);
    }
}
