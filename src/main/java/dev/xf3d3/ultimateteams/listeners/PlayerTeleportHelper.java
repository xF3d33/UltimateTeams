package dev.xf3d3.ultimateteams.listeners;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;


public class PlayerTeleportHelper implements Listener {
    private final UltimateTeams plugin;

    public PlayerTeleportHelper(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!plugin.getUtils().getPendingTeleport().containsKey(event.getPlayer().getUniqueId())) return;

        // Check if they actually moved a block (ignore head rotation)
        if (event.getFrom().getBlockX() != event.getTo().getBlockX()
                || event.getFrom().getBlockY() != event.getTo().getBlockY()
                || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {


            event.getPlayer().sendMessage(MineDown.parse(plugin.getMessages().getTeleportCancelledMoved()));
            plugin.getUtils().getPendingTeleport().get(event.getPlayer().getUniqueId()).cancel();
            plugin.getUtils().getPendingTeleport().remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player
                && plugin.getUtils().getPendingTeleport().containsKey(player.getUniqueId())) {

            event.getEntity().sendMessage(MineDown.parse(plugin.getMessages().getTeleportCancelledMoved()));
            plugin.getUtils().getPendingTeleport().get(player.getUniqueId()).cancel();
            plugin.getUtils().getPendingTeleport().remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!plugin.getUtils().getPendingTeleport().containsKey(event.getPlayer().getUniqueId())) return;

        plugin.getUtils().getPendingTeleport().get(event.getPlayer().getUniqueId()).cancel();
        plugin.getUtils().getPendingTeleport().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (!plugin.getUtils().getPendingTeleport().containsKey(event.getPlayer().getUniqueId())) return;

        plugin.getUtils().getPendingTeleport().get(event.getPlayer().getUniqueId()).cancel();
        plugin.getUtils().getPendingTeleport().remove(event.getPlayer().getUniqueId());
    }
}
