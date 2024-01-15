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

        plugin.getConnectedPlayers().put(player, player.getName());
        plugin.getManager().users().getPlayer(player);
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onBedrockPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!plugin.getSettings().FloodGateHook() || plugin.getFloodgateApi() == null) {
                return;
        }

        if (plugin.getFloodgateApi().isFloodgatePlayer(uuid)) {
            plugin.getManager().users().getBedrockPlayer(player);

            if (plugin.getManager().users().hasPlayerNameChanged(player)) {
                plugin.getManager().users().updatePlayerName(player);
            }

            if (plugin.getManager().users().hasBedrockPlayerJavaUUIDChanged(player)) {
                plugin.getManager().users().updateBedrockPlayerJavaUUID(player);
            }

            plugin.getBedrockPlayers().put(player, plugin.getFloodgateApi().getPlayer(uuid).getJavaUniqueId().toString());
        }
    }
}
