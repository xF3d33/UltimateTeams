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

public class PlayerConnectionEvent implements Listener {

    FileConfiguration teamsConfig = CelestyTeams.getPlugin().getConfig();
    Logger logger = CelestyTeams.getPlugin().getLogger();

    private final CelestyTeams plugin;
    public PlayerConnectionEvent(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        CelestyTeams.connectedPlayers.put(player, player.getName());
        if (!(plugin.getUsersStorageUtil().isUserExisting(player))){
            plugin.getUsersStorageUtil().addToUsermap(player);
            return;
        }
        if (plugin.getUsersStorageUtil().hasPlayerNameChanged(player)){
            plugin.getUsersStorageUtil().updatePlayerName(player);
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onBedrockPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (CelestyTeams.getFloodgateApi() != null){
            if (CelestyTeams.getFloodgateApi().isFloodgatePlayer(uuid)){
                if (!(plugin.getUsersStorageUtil().isUserExisting(player))){
                    plugin.getUsersStorageUtil().addBedrockPlayerToUsermap(player);
                    return;
                }
                if (plugin.getUsersStorageUtil().hasPlayerNameChanged(player)){
                    plugin.getUsersStorageUtil().updatePlayerName(player);
                    if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                        logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aUpdated bedrock player name"));
                    }
                }
                if (plugin.getUsersStorageUtil().hasBedrockPlayerJavaUUIDChanged(player)){
                    plugin.getUsersStorageUtil().updateBedrockPlayerJavaUUID(player);
                    if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                        logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aUpdated bedrock player Java UUID"));
                    }
                }
                CelestyTeams.bedrockPlayers.put(player, CelestyTeams.getFloodgateApi().getPlayer(uuid).getJavaUniqueId().toString());
                if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                    logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aAdded bedrock player to connected bedrock players hashmap"));
                }
            }
        }
    }
}
