package dev.xf3d3.celestyteams.commands.teamSubCommands;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.api.ClanHomePreTeleportEvent;
import dev.xf3d3.celestyteams.api.ClanHomeTeleportEvent;
import dev.xf3d3.celestyteams.models.Team;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public class TeamHomeSubCommand {

    FileConfiguration teamsConfig = CelestyTeams.getPlugin().getConfig();
    FileConfiguration messagesConfig = CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig();
    Logger logger = CelestyTeams.getPlugin().getLogger();
    private static final String TIME_LEFT = "%TIMELEFT%";

    private static ClanHomePreTeleportEvent homePreTeleportEvent = null;

    HashMap<UUID, Long> homeCoolDownTimer = new HashMap<>();

    private final CelestyTeams plugin;

    public TeamHomeSubCommand(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
    }

    public boolean tpClanHomeSubCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            if (teamsConfig.getBoolean("team-home.enabled")){
                UUID uuid = player.getUniqueId();
                if (plugin.getTeamStorageUtil().findTeamByOwner(player) != null){
                    Team teamByOwner = plugin.getTeamStorageUtil().findTeamByOwner(player);
                    if (teamByOwner.getClanHomeWorld() != null){
                        fireClanHomePreTPEvent(player, teamByOwner);
                        if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                            logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanHomePreTPEvent"));
                        }
                        if (homePreTeleportEvent.isCancelled()){
                            if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                                logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aClanHomePreTPEvent cancelled by external source"));
                            }
                            return true;
                        }
                        World world = Bukkit.getWorld(teamByOwner.getClanHomeWorld());
                        double x = teamByOwner.getClanHomeX();
                        double y = teamByOwner.getClanHomeY() + 0.2;
                        double z = teamByOwner.getClanHomeZ();
                        float yaw = teamByOwner.getClanHomeYaw();
                        float pitch = teamByOwner.getClanHomePitch();
                        if (teamsConfig.getBoolean("team-home.cool-down.enabled")){
                            if (homeCoolDownTimer.containsKey(uuid)){
                                if (!(player.hasPermission("celestyteams.bypass.homecooldown")||player.hasPermission("celestyteams.bypass.*")
                                        ||player.hasPermission("celestyteams.bypass")||player.hasPermission("celestyteams.*")||player.isOp())){
                                    if (homeCoolDownTimer.get(uuid) > System.currentTimeMillis()){
                                        long timeLeft = (homeCoolDownTimer.get(uuid) - System.currentTimeMillis()) / 1000;
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-cool-down-timer-wait")
                                                .replace(TIME_LEFT, Long.toString(timeLeft))));
                                    }else {
                                        homeCoolDownTimer.put(uuid, System.currentTimeMillis() + (teamsConfig.getLong("team-home.cool-down.time") * 1000));
                                        Location location = new Location(world, x, y, z, yaw, pitch);
                                        fireClanHomeTeleportEvent(player, teamByOwner, location, player.getLocation());
                                        if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                                            logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanHomeTeleportEvent"));
                                        }
                                        PaperLib.teleportAsync(player, location);
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("successfully-teleported-to-home")));
                                    }
                                }else {
                                    Location location = new Location(world, x, y, z, yaw, pitch);
                                    fireClanHomeTeleportEvent(player, teamByOwner, location, player.getLocation());
                                    if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                                        logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanHomeTeleportEvent"));
                                    }
                                    PaperLib.teleportAsync(player, location);
                                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("successfully-teleported-to-home")));
                                }
                            }else {
                                homeCoolDownTimer.put(uuid, System.currentTimeMillis() + (teamsConfig.getLong("team-home.cool-down.time") * 1000));
                                Location location = new Location(world, x, y, z, yaw, pitch);
                                fireClanHomeTeleportEvent(player, teamByOwner, location, player.getLocation());
                                if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                                    logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanHomeTeleportEvent"));
                                }
                                PaperLib.teleportAsync(player, location);
                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("successfully-teleported-to-home")));
                            }
                        }else {
                            Location location = new Location(world, x, y, z, yaw, pitch);
                            fireClanHomeTeleportEvent(player, teamByOwner, location, player.getLocation());
                            if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                                logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanHomeTeleportEvent"));
                            }
                            PaperLib.teleportAsync(player, location);
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("successfully-teleported-to-home")));
                        }
                    }else {
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-no-home-set")));
                    }
                }else if (plugin.getTeamStorageUtil().findClanByPlayer(player) != null){
                    Team teamByPlayer = plugin.getTeamStorageUtil().findClanByPlayer(player);
                    fireClanHomePreTPEvent(player, teamByPlayer);
                    if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                        logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanHomePreTPEvent"));
                    }
                    if (homePreTeleportEvent.isCancelled()){
                        if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                            logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aClanHomePreTPEvent cancelled by external source"));
                        }
                        return true;
                    }
                    if (teamByPlayer.getClanHomeWorld() != null){
                        World world = Bukkit.getWorld(teamByPlayer.getClanHomeWorld());
                        double x = teamByPlayer.getClanHomeX();
                        double y = teamByPlayer.getClanHomeY() + 0.2;
                        double z = teamByPlayer.getClanHomeZ();
                        float yaw = teamByPlayer.getClanHomeYaw();
                        float pitch = teamByPlayer.getClanHomePitch();
                        if (teamsConfig.getBoolean("team-home.cool-down.enabled")){
                            if (homeCoolDownTimer.containsKey(uuid)){
                                if (!(player.hasPermission("celestyteams.bypass.homecooldown")||player.hasPermission("celestyteams.bypass.*")
                                        ||player.hasPermission("celestyteams.bypass")||player.hasPermission("celestyteams.*")||player.isOp())){
                                    if (homeCoolDownTimer.get(uuid) > System.currentTimeMillis()){
                                        long timeLeft = (homeCoolDownTimer.get(uuid) - System.currentTimeMillis()) / 1000;
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-cool-down-timer-wait")
                                                .replace(TIME_LEFT, Long.toString(timeLeft))));
                                    }else {
                                        homeCoolDownTimer.put(uuid, System.currentTimeMillis() + (teamsConfig.getLong("team-home.cool-down.time") * 1000));
                                        Location location = new Location(world, x, y, z, yaw, pitch);
                                        fireClanHomeTeleportEvent(player, teamByPlayer, location, player.getLocation());
                                        if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                                            logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanHomeTeleportEvent"));
                                        }
                                        PaperLib.teleportAsync(player, location);
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("successfully-teleported-to-home")));
                                    }
                                }else {
                                    Location location = new Location(world, x, y, z, yaw, pitch);
                                    fireClanHomeTeleportEvent(player, teamByPlayer, location, player.getLocation());
                                    if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                                        logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanHomeTeleportEvent"));
                                    }
                                    PaperLib.teleportAsync(player, location);
                                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("successfully-teleported-to-home")));
                                }
                            }else {
                                homeCoolDownTimer.put(uuid, System.currentTimeMillis() + (teamsConfig.getLong("team-home.cool-down.time") * 1000));
                                Location location = new Location(world, x, y, z, yaw, pitch);
                                fireClanHomeTeleportEvent(player, teamByPlayer, location, player.getLocation());
                                if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                                    logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanHomeTeleportEvent"));
                                }
                                PaperLib.teleportAsync(player, location);
                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("successfully-teleported-to-home")));
                            }
                        }else {
                            Location location = new Location(world, x, y, z, yaw, pitch);
                            fireClanHomeTeleportEvent(player, teamByPlayer, location, player.getLocation());
                            if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                                logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanHomeTeleportEvent"));
                            }
                            PaperLib.teleportAsync(player, location);
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("successfully-teleported-to-home")));
                        }
                    }else {
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-no-home-set")));
                    }
                }else {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-tp-not-in-team")));
                }
            }else {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("function-disabled")));
            }
            return true;

        }
        return false;
    }

    private static void fireClanHomePreTPEvent(Player player, Team team) {
        ClanHomePreTeleportEvent teamHomePreTeleportEvent = new ClanHomePreTeleportEvent(player, team);
        Bukkit.getPluginManager().callEvent(teamHomePreTeleportEvent);
        homePreTeleportEvent = teamHomePreTeleportEvent;
    }

    private static void fireClanHomeTeleportEvent(Player player, Team team, Location homeLocation, Location tpFromLocation) {
        ClanHomeTeleportEvent teamHomeTeleportEvent = new ClanHomeTeleportEvent(player, team, homeLocation, tpFromLocation);
        Bukkit.getPluginManager().callEvent(teamHomeTeleportEvent);
    }
}
