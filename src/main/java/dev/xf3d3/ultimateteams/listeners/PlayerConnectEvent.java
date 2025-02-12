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
    public void onBedrockPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!plugin.getSettings().FloodGateHook()) {
            return;
        }

        if (plugin.getFloodgateApi() == null) {
            return;
        }

        if (plugin.getFloodgateApi().isFloodgatePlayer(uuid)) {

            plugin.getBedrockPlayers().put(plugin.getFloodgateApi().getPlayer(uuid).getJavaUniqueId().toString(), player);
        }
    }
}
