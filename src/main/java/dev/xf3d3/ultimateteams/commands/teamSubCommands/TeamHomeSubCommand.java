package dev.xf3d3.ultimateteams.commands.teamSubCommands;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.TeamHomePreTeleportEvent;
import dev.xf3d3.ultimateteams.api.TeamHomeTeleportEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TeamHomeSubCommand {

    private final FileConfiguration teamsConfig;
    private final FileConfiguration messagesConfig;
    private final Logger logger;
    private static final String TIME_LEFT = "%TIMELEFT%";
    private static TeamHomePreTeleportEvent homePreTeleportEvent = null;
    private static final HashMap<UUID, Long> homeCoolDownTimer = new HashMap<>();
    private final UltimateTeams plugin;

    public TeamHomeSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
        this.teamsConfig = plugin.getConfig();
    }

    public void tpTeamHomeSubCommand(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        // check if function is enabled
        if (!teamsConfig.getBoolean("team-home.enabled")) {
            player.sendMessage(Utils.Color(messagesConfig.getString("function-disabled")));
            return;
        }

        UUID uuid = player.getUniqueId();

        // check if team exists
        if (!(plugin.getTeamStorageUtil().findTeamByOwner(player) != null || plugin.getTeamStorageUtil().findTeamByPlayer(player) != null)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("failed-tp-not-in-team")));
            return;
        }

        Team team;
        if (plugin.getTeamStorageUtil().findTeamByOwner(player) != null) {
            team = plugin.getTeamStorageUtil().findTeamByOwner(player);
        } else {
            team = plugin.getTeamStorageUtil().findTeamByPlayer(player);
        }

        // check if home exists
        if (team.getTeamHomeWorld() == null) {
            player.sendMessage(Utils.Color(messagesConfig.getString("failed-no-home-set")));
            return;
        }

        fireTeamHomePreTPEvent(player, team);
        if (homePreTeleportEvent.isCancelled()){
            if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                plugin.log(Level.INFO, Utils.Color("&6UltimateTeams-Debug: &aTeamHomePreTPEvent cancelled by external source"));
            }
            return;
        }

        World world = Bukkit.getWorld(team.getTeamHomeWorld());
        double x = team.getTeamHomeX();
        double y = team.getTeamHomeY(); //+ 0.2;
        double z = team.getTeamHomeZ();
        float yaw = team.getTeamHomeYaw();
        float pitch = team.getTeamHomePitch();
        Location location = new Location(world, x, y, z, yaw, pitch);

        if (teamsConfig.getBoolean("team-home.cool-down.enabled") && homeCoolDownTimer.containsKey(uuid)) {
                if (!player.hasPermission("ultimateteams.bypass.homecooldown")) {
                    if (homeCoolDownTimer.get(uuid) > System.currentTimeMillis()) {
                        long timeLeft = (homeCoolDownTimer.get(uuid) - System.currentTimeMillis()) / 1000;
                        player.sendMessage(Utils.Color(messagesConfig.getString("home-cool-down-timer-wait")
                                .replace(TIME_LEFT, Long.toString(timeLeft))));
                    } else {
                        homeCoolDownTimer.put(uuid, System.currentTimeMillis() + (teamsConfig.getLong("team-home.cool-down.time") * 1000));
                        fireTeamHomeTeleportEvent(player, team, location, player.getLocation());

                        // Run on the appropriate thread scheduler for this platform
                        plugin.getScheduler().entitySpecificScheduler(player).run(
                                () -> PaperLib.teleportAsync(player, location, PlayerTeleportEvent.TeleportCause.PLUGIN),
                                () -> plugin.log(Level.WARNING, "User offline when teleporting: " + player.getName())
                        );

                        player.sendMessage(Utils.Color(messagesConfig.getString("successfully-teleported-to-home")));
                    }
                } else {
                    fireTeamHomeTeleportEvent(player, team, location, player.getLocation());

                    PaperLib.teleportAsync(player, location);
                    player.sendMessage(Utils.Color(messagesConfig.getString("successfully-teleported-to-home")));
                }
        } else {
            fireTeamHomeTeleportEvent(player, team, location, player.getLocation());
            PaperLib.teleportAsync(player, location);
            player.sendMessage(Utils.Color(messagesConfig.getString("successfully-teleported-to-home")));
        }
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
