package dev.xf3d3.ultimateteams.listeners;

import dev.xf3d3.ultimateteams.UltimateTeams;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerConnectEvent implements Listener {

    private final UltimateTeams plugin;
    public PlayerConnectEvent(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }



    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        // check if floodgate hook is enabled and available
        if (plugin.getSettings().FloodGateHook() && (plugin.getFloodgateApi() != null)) {

            if (plugin.getFloodgateApi().isFloodgatePlayer(uuid)) {

                plugin.getUsersStorageUtil().getBedrockPlayer(player);

                if (plugin.getUsersStorageUtil().hasPlayerNameChanged(player)) {
                    plugin.getUsersStorageUtil().updatePlayerName(player);
                }

                if (plugin.getUsersStorageUtil().hasBedrockPlayerJavaUUIDChanged(player)) {
                    plugin.getUsersStorageUtil().updateBedrockPlayerJavaUUID(player);
                }

                plugin.getBedrockPlayers().put(plugin.getFloodgateApi().getPlayer(uuid).getJavaUniqueId().toString(), player);

            } else {
                handleJavaPlayer(player);
            }
            return;
        }

        handleJavaPlayer(player);
    }


    private void handleJavaPlayer(Player player) {
        plugin.getUsersStorageUtil().getPlayer(player);
    }
}
