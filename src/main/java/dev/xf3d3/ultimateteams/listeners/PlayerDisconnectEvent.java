package dev.xf3d3.ultimateteams.listeners;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.User;
import dev.xf3d3.ultimateteams.network.Broker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerDisconnectEvent implements Listener {
    private final UltimateTeams plugin;
    public PlayerDisconnectEvent(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        plugin.getUsersStorageUtil().removePlayer(player.getUniqueId());
        plugin.getUsersStorageUtil().getOnlineUserMap().remove(player.getUniqueId());

        if (plugin.getSettings().isEnableCrossServer()) {
            final List<User> localPlayerList = plugin.getUsersStorageUtil().getOnlineUserMap().values().stream()
                    .filter(u -> !u.equals(player)).map(u -> User.of(u.getUniqueId(), u.getName())).toList();

            // Update global user list if needed
            if (plugin.getSettings().getBrokerType() == Broker.Type.REDIS) {
                plugin.getUsersStorageUtil().syncGlobalUserList(player, localPlayerList);
            } else {
                plugin.getUsersStorageUtil().getOnlineUserMap().values().stream()
                        .filter(u -> !u.equals(player))
                        .findAny()
                        .ifPresent(user -> plugin.getUsersStorageUtil().syncGlobalUserList(user, localPlayerList));
            }
        }
    }
}