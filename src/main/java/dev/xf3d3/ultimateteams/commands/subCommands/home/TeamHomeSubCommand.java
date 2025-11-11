package dev.xf3d3.ultimateteams.commands.subCommands.home;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamTeleportEvent;
import dev.xf3d3.ultimateteams.models.TeamHome;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeamHomeSubCommand {
    private static final String TIME_LEFT = "%TIMELEFT%";
    private static TeamTeleportEvent homePreTeleportEvent = null;
    private static final ConcurrentHashMap<UUID, Long> homeCoolDownTimer = new ConcurrentHashMap<>();
    private final UltimateTeams plugin;

    public TeamHomeSubCommand(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void tpTeamHomeSubCommand(CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(MineDown.parse(plugin.getMessages().getPlayerOnlyCommand()));
            return;
        }

        // check if function is enabled
        if (!plugin.getSettings().teamHomeEnabled()) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getFunctionDisabled()));
            return;
        }

        plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId()).ifPresentOrElse(
                team -> {
                    final TeamHome home = team.getHome();

                    // check if home exists
                    if (home == null) {
                        player.sendMessage(MineDown.parse(plugin.getMessages().getFailedNoHomeSet()));
                        return;
                    }

                    if (plugin.getSettings().teamHomeCooldownEnabled()) {
                        if (!player.hasPermission("ultimateteams.bypass.homecooldown") && homeCoolDownTimer.containsKey(player.getUniqueId())) {
                            if (homeCoolDownTimer.get(player.getUniqueId()) > System.currentTimeMillis()) {
                                long timeLeft = (homeCoolDownTimer.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;

                                player.sendMessage(MineDown.parse(plugin.getMessages().getHomeCoolDownTimerWait()
                                        .replaceAll(TIME_LEFT, Long.toString(timeLeft))));
                            } else {
                                homeCoolDownTimer.put(player.getUniqueId(), System.currentTimeMillis() + (plugin.getSettings().getTeamHomeCooldownValue() * 1000L));

                                if (new TeamTeleportEvent(player, team, home.getLocation()).callEvent()) return;
                                tpHome(player, home);
                            }
                        } else {
                            if (new TeamTeleportEvent(player, team, home.getLocation()).callEvent()) return;
                            tpHome(player, home);
                            homeCoolDownTimer.put(player.getUniqueId(), System.currentTimeMillis() + (plugin.getSettings().getTeamHomeCooldownValue() * 1000L));
                        }
                        return;
                    }

                    if (new TeamTeleportEvent(player, team, home.getLocation()).callEvent()) return;
                    tpHome(player, home);
                },
                () -> player.sendMessage(MineDown.parse(plugin.getMessages().getFailedTpNotInTeam()))
        );


    }

    private void tpHome(@NotNull Player player, @NotNull TeamHome home) {
        plugin.getUtils().teleportPlayer(player, home.getLocation(), home.getServer(), Utils.TeleportType.HOME, null);
        //player.sendMessage(Utils.Color(messagesConfig.getString("successfully-teleported-to-home")));
    }
}
