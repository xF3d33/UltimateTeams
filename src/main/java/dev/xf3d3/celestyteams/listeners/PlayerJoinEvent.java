package dev.xf3d3.celestyteams.listeners;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.logging.Logger;

public class PlayerJoinEvent implements Listener {

    private final CelestyTeams plugin;
    public PlayerJoinEvent(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        CelestyTeams.connectedPlayers.put(player, player.getName());
        plugin.getUsersStorageUtil().getPlayer(player);
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onBedrockPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        if (CelestyTeams.getFloodgateApi() != null){
            if (CelestyTeams.getFloodgateApi().isFloodgatePlayer(uuid)){
                plugin.getUsersStorageUtil().getBedrockPlayer(player);

                if (plugin.getUsersStorageUtil().hasPlayerNameChanged(player)){
                    plugin.getUsersStorageUtil().updatePlayerName(player);
                }

                if (plugin.getUsersStorageUtil().hasBedrockPlayerJavaUUIDChanged(player)){
                    plugin.getUsersStorageUtil().updateBedrockPlayerJavaUUID(player);
                }
                CelestyTeams.bedrockPlayers.put(player, CelestyTeams.getFloodgateApi().getPlayer(uuid).getJavaUniqueId().toString());
            }
        }
    }
}
