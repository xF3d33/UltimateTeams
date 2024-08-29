package dev.xf3d3.ultimateteams.hooks;

import dev.xf3d3.ultimateteams.UltimateTeams;
import net.william278.huskhomes.api.HuskHomesAPI;
import net.william278.huskhomes.position.Position;
import net.william278.huskhomes.position.World;
import net.william278.huskhomes.teleport.TeleportationException;
import net.william278.huskhomes.user.OnlineUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class HuskHomesAPIHook {

    private final HuskHomesAPI huskHomesAPI;
    private final UltimateTeams plugin;

    public HuskHomesAPIHook(@NotNull UltimateTeams plugin) {
        this.huskHomesAPI = HuskHomesAPI.getInstance();
        this.plugin = plugin;
        sendMessages();
    }

    public void teleportPlayer(@NotNull Player player, @NotNull Location location) {
        OnlineUser onlineUser = huskHomesAPI.adaptUser(player);

        Position position = Position.at(
                location.getX(), location.getY(), location.getZ(),
                World.from(location.getWorld().getName(), location.getWorld().getUID()), "server"
        );

        try {
            huskHomesAPI.teleportBuilder()
                    .teleporter(onlineUser)
                    .target(position)
                    .toTimedTeleport()
                    .execute();
        } catch (TeleportationException e) {
            plugin.log(Level.SEVERE, "Error while trying to teleport a player using HuskHomes", e);
            e.displayMessage(onlineUser);
        }
    }

    private void sendMessages() {
        plugin.sendConsole("-------------------------------------------");
        plugin.sendConsole("&6UltimateTeams: &3Hooked into HuskHomes");
        plugin.sendConsole("&6UltimateTeams: &3Now using HuskHomes as teleportation handler!");
        plugin.sendConsole("-------------------------------------------");
    }

}