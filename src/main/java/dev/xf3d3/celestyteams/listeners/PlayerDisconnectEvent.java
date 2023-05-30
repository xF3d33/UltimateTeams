package dev.xf3d3.celestyteams.listeners;

import dev.xf3d3.celestyteams.CelestyTeams;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerDisconnectEvent implements Listener {
    private final CelestyTeams plugin;
    public PlayerDisconnectEvent(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        plugin.getConnectedPlayers().remove(player);
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onBedrockPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if (plugin.getFloodgateApi() != null){
            if (plugin.getBedrockPlayers().containsKey(player)){
                plugin.getBedrockPlayers().remove(player);
            }
        }
    }
}
