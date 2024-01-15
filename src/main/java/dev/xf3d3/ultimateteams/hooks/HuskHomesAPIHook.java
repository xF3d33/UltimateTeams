package dev.xf3d3.ultimateteams.hooks;

import dev.xf3d3.ultimateteams.UltimateTeams;
import net.william278.huskhomes.api.HuskHomesAPI;
import net.william278.huskhomes.position.Position;
import net.william278.huskhomes.position.World;
import net.william278.huskhomes.teleport.TeleportBuilder;
import net.william278.huskhomes.teleport.TeleportationException;
import net.william278.huskhomes.user.OnlineUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class HuskHomesAPIHook {

    @Nullable
    private final HuskHomesAPI api;

    private final UltimateTeams plugin;

    public HuskHomesAPIHook(@NotNull UltimateTeams plugin) {
        this.api = HuskHomesAPI.getInstance();
        this.plugin = plugin;
        sendMessages();
    }


    public void teleport(@NotNull Player player, @NotNull dev.xf3d3.ultimateteams.team.Position position, @NotNull String server,
                         boolean instant) {
        try {
            final HuskHomesAPI api = getHuskHomes()
                    .orElseThrow(() -> new IllegalStateException("HuskHomes API not present"));
            final TeleportBuilder builder = api
                    .teleportBuilder(api.adaptUser(player))
                    .target(net.william278.huskhomes.position.Position.at(
                            position.getX(),
                            position.getY(),
                            position.getZ(),
                            position.getYaw(),
                            position.getPitch(),
                            net.william278.huskhomes.position.World.from(position.getWorld().getName(), position.getWorld().getUID()),
                            server
                    ));
            if (instant) {
                builder.toTeleport().execute();
            } else {
                builder.toTimedTeleport().execute();
            }
        } catch (IllegalStateException e) {
            // TODO: send message
        }
    }

    private Optional<HuskHomesAPI> getHuskHomes() {
        return Optional.ofNullable(api);
    }

    private void sendMessages() {
        plugin.sendConsole("-------------------------------------------");
        plugin.sendConsole("&6UltimateTeams: &3Hooked into HuskHomes");
        plugin.sendConsole("&6UltimateTeams: &3Now using HuskHomes as teleportation handler!");
        plugin.sendConsole("-------------------------------------------");
    }

}