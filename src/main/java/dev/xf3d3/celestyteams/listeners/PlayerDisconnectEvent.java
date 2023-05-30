package dev.xf3d3.celestyteams.listeners;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Logger;

public class PlayerDisconnectEvent implements Listener {

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        CelestyTeams.connectedPlayers.remove(player);
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onBedrockPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if (CelestyTeams.getFloodgateApi() != null){
            if (CelestyTeams.bedrockPlayers.containsKey(player)){
                CelestyTeams.bedrockPlayers.remove(player);
            }
        }
    }
}
