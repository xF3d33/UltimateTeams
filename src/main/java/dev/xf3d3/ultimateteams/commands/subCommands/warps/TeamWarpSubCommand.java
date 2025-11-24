package dev.xf3d3.ultimateteams.commands.subCommands.warps;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamTeleportEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamWarp;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeamWarpSubCommand {
    private final UltimateTeams plugin;
    private static final ConcurrentHashMap<UUID, Long> warpCoolDownTimer = new ConcurrentHashMap<>();
    private static final String TIME_LEFT = "%TIMELEFT%";

    public TeamWarpSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void WarpCommand(CommandSender sender, String name) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        if (!plugin.getSettings().teamWarpEnabled()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getFunctionDisabled()));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    if (!plugin.getSettings().teamWarpCooldownEnabled()) {
                        tpWarp(player, team, name);

                        return;
                    }

                    if (!player.hasPermission("ultimateteams.bypass.warpcooldown") && warpCoolDownTimer.containsKey(player.getUniqueId())) {
                        if (warpCoolDownTimer.get(player.getUniqueId()) > System.currentTimeMillis()) {
                            long timeLeft = (warpCoolDownTimer.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;

                            player.sendMessage(MineDown.parse(plugin.getMessages().getHomeCoolDownTimerWait()
                                    .replaceAll(TIME_LEFT, Long.toString(timeLeft))));
                        } else {
                            warpCoolDownTimer.put(player.getUniqueId(), System.currentTimeMillis() + (plugin.getSettings().getTeamHomeCooldownValue() * 1000L));
                            tpWarp(player, team, name);
                        }
                    } else {
                        tpWarp(player, team, name);
                        warpCoolDownTimer.put(player.getUniqueId(), System.currentTimeMillis() + (plugin.getSettings().getTeamHomeCooldownValue() * 1000L));
                    }


                    //player.sendMessage(MineDown.parse(plugin.getMessages().getTeamWarpTeleportedSuccessful()));
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()))
        );
    }

    private void tpWarp(Player player, Team team, String name) {

        team.getTeamWarp(name).ifPresentOrElse(
                warp -> {
                    if (!(new TeamTeleportEvent(player, team, warp.getLocation()).callEvent())) return;

                    plugin.getUtils().teleportPlayer(player, warp.getLocation(), warp.getServer(), Utils.TeleportType.WARP, name);
                    MineDown.parse(plugin.getMessages().getTeamWarpTeleportedSuccessful().replaceAll("%WARP_NAME%", warp.getName()));
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getTeamWarpNotFound()))
        );
    }


    public void showWarpsMenu(@NotNull CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        if (!plugin.getSettings().teamWarpEnabled()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getFunctionDisabled()));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
          team -> {
              if (team.getWarps().isEmpty()) {
                  player.sendMessage(MineDown.parse(plugin.getMessages().getTeamWarpEmpty()));

                  return;
              }

              player.sendMessage(MineDown.parse(String.join("\n", plugin.getMessages().getTeamWarpMenuHeader())
                      .replaceAll("%TEAM%", team.getName())
              ));

              for (TeamWarp warp : team.getWarps().values()) {
                  String warpInfo = String.join("\n", plugin.getMessages().getTeamWarpMenuItemInfo())
                          .replaceAll("%WARP_NAME%", warp.getName())
                          .replaceAll("%X%", String.valueOf((int) warp.getWarpX()))
                          .replaceAll("%Y%", String.valueOf((int) warp.getWarpY()))
                          .replaceAll("%Z%", String.valueOf((int) warp.getWarpZ()))
                          .replaceAll("%WORLD_NAME%", warp.getWarpWorld())
                          .replaceAll("%SERVER_NAME%", Objects.requireNonNullElse(warp.getServer(), plugin.getSettings().getServerName()));

                  player.sendMessage(MineDown.parse(plugin.getMessages().getTeamWarpMenuItem()
                          .replaceAll("%WARP_NAME%", warp.getName())
                          .replaceAll("%WARP_INFO%", warpInfo)
                  ));
              }

          },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getNotInTeam()))
        );
    }


}
