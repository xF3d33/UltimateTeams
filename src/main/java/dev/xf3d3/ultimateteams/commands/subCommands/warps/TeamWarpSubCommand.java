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
            sender.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getPlayerOnlyCommand()));
            return;
        }

        if (!plugin.getSettings().getTeam().getWarp().isEnable()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getFunctionDisabled()));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    if (!plugin.getSettings().getTeam().getWarp().getCoolDown().isEnabled()) {
                        tpWarp(player, team, name);

                        return;
                    }

                    if (!player.hasPermission("ultimateteams.bypass.warpcooldown") && warpCoolDownTimer.containsKey(player.getUniqueId())) {
                        if (warpCoolDownTimer.get(player.getUniqueId()) > System.currentTimeMillis()) {
                            long timeLeft = (warpCoolDownTimer.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;

                            player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getHome().getCooldownWait()
                                    .replaceAll(TIME_LEFT, Long.toString(timeLeft))));
                        } else {
                            warpCoolDownTimer.put(player.getUniqueId(), System.currentTimeMillis() + (plugin.getSettings().getTeam().getWarp().getCoolDown().getTime() * 1000L));
                            tpWarp(player, team, name);
                        }
                    } else {
                        tpWarp(player, team, name);
                        warpCoolDownTimer.put(player.getUniqueId(), System.currentTimeMillis() + (plugin.getSettings().getTeam().getWarp().getCoolDown().getTime() * 1000L));
                    }


                    //player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getWarp().getTeleportedSuccessful()));
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getInfo().getNotInTeam()))
        );
    }

    private void tpWarp(Player player, Team team, String name) {

        team.getTeamWarp(name).ifPresentOrElse(
                warp -> {
                    if (!(new TeamTeleportEvent(player, team, warp.getLocation()).callEvent())) return;

                    plugin.getUtils().teleportPlayer(player, warp.getLocation(), warp.getServer(), Utils.TeleportType.WARP, name);
                    MineDown.parse(plugin.getMessages().getTeam().getWarp().getTeleportedSuccessful().replaceAll("%WARP_NAME%", warp.getName()));
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getWarp().getNotFound()))
        );
    }


    public void showWarpsMenu(@NotNull CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getPlayerOnlyCommand()));
            return;
        }

        if (!plugin.getSettings().getTeam().getWarp().isEnable()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getGeneral().getFunctionDisabled()));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
          team -> {
              if (team.getWarps().isEmpty()) {
                  player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getWarp().getNoWarps()));

                  return;
              }

              player.sendMessage(MineDown.parse(String.join("\n", plugin.getMessages().getTeam().getWarp().getMenu().getHeader())
                      .replaceAll("%TEAM%", team.getName())
              ));

              for (TeamWarp warp : team.getWarps().values()) {
                  String warpInfo = String.join("\n", plugin.getMessages().getTeam().getWarp().getMenu().getWarpInfo())
                          .replaceAll("%WARP_NAME%", warp.getName())
                          .replaceAll("%X%", String.valueOf((int) warp.getWarpX()))
                          .replaceAll("%Y%", String.valueOf((int) warp.getWarpY()))
                          .replaceAll("%Z%", String.valueOf((int) warp.getWarpZ()))
                          .replaceAll("%WORLD_NAME%", warp.getWarpWorld())
                          .replaceAll("%SERVER_NAME%", Objects.requireNonNullElse(warp.getServer(), plugin.getSettings().getCrossServer().getServerName()));

                  player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getWarp().getMenu().getWarp()
                          .replaceAll("%WARP_NAME%", warp.getName())
                          .replaceAll("%WARP_INFO%", warpInfo)
                  ));
              }

          },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getTeam().getInfo().getNotInTeam()))
        );
    }


}
