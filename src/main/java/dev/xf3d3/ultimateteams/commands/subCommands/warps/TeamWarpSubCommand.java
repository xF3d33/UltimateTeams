package dev.xf3d3.ultimateteams.commands.subCommands.warps;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeamWarpSubCommand {
    private final FileConfiguration messagesConfig;
    private final UltimateTeams plugin;
    private static final ConcurrentHashMap<UUID, Long> warpCoolDownTimer = new ConcurrentHashMap<>();
    private static final String TIME_LEFT = "%TIMELEFT%";

    public TeamWarpSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
    }

    public void WarpCommand(CommandSender sender, String name) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Utils.Color(messagesConfig.getString("player-only-command")));
            return;
        }

        if (!plugin.getSettings().teamWarpEnabled()) {
            player.sendMessage(Utils.Color(messagesConfig.getString("function-disabled")));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    if (plugin.getSettings().teamWarpCooldownEnabled()) {
                        if (!player.hasPermission("ultimateteams.bypass.warpcooldown") && warpCoolDownTimer.containsKey(player.getUniqueId())) {
                            if (warpCoolDownTimer.get(player.getUniqueId()) > System.currentTimeMillis()) {
                                long timeLeft = (warpCoolDownTimer.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;

                                player.sendMessage(Utils.Color(messagesConfig.getString("home-cool-down-timer-wait")
                                        .replaceAll(TIME_LEFT, Long.toString(timeLeft))));
                            } else {
                                warpCoolDownTimer.put(player.getUniqueId(), System.currentTimeMillis() + (plugin.getSettings().getTeamHomeCooldownValue() * 1000L));
                                tpWarp(player, team, name);
                            }
                        } else {
                            tpWarp(player, team, name);
                            warpCoolDownTimer.put(player.getUniqueId(), System.currentTimeMillis() + (plugin.getSettings().getTeamHomeCooldownValue() * 1000L));
                        }

                        return;
                    }

                    tpWarp(player, team, name);
                    player.sendMessage(Utils.Color(messagesConfig.getString("successfully-teleported-to-home")));
                },
                () -> player.sendMessage(Utils.Color(messagesConfig.getString("failed-not-in-team")))
        );
    }

    private void tpWarp(Player player, Team team, String name) {

        team.getTeamWarp(name).ifPresentOrElse(
                warp -> {
                    plugin.getUtils().teleportPlayer(player, warp.getLocation(), warp.getServer(), Utils.TeleportType.WARP, name);
                    player.sendMessage(Utils.Color(messagesConfig.getString("team-warp-teleported-successful").replaceAll("%WARP_NAME%", warp.getName())));
                },
                () -> player.sendMessage(Utils.Color(messagesConfig.getString("team-warp-not-found")))
        );
    }
}
