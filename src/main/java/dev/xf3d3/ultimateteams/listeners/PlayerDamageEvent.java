package dev.xf3d3.ultimateteams.listeners;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamFriendlyFireEvent;
import dev.xf3d3.ultimateteams.models.Team;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDamageEvent implements Listener {

    private final Map<Block, UUID> explodingAnchors = new ConcurrentHashMap<>();

    private final UltimateTeams plugin;

    public PlayerDamageEvent(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onPlayerHit(EntityDamageByEntityEvent e){
        if (!(e.getEntity() instanceof Player victim)) return;

        Player attackingPlayer = null;

        // Melee attack
        if (e.getDamager() instanceof Player) {
            attackingPlayer = (Player) e.getDamager();
        }

        // Projectiles (Arrows, SpectralArrows, Tridents, Snowballs, Eggs, Potions, etc.)
        else if (e.getDamager() instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player shooter) {
                attackingPlayer = shooter;
            }
        }

        // Fireworks shot from a crossbow
        else if (e.getDamager() instanceof Firework firework) {
            if (firework.getShooter() instanceof Player shooter) {
                attackingPlayer = shooter;
            }
        }

        // Primed TNT from another player
        else if (e.getDamager() instanceof TNTPrimed tntPrimed) {
            if (tntPrimed.getSource() instanceof Player shooter) {
                attackingPlayer = shooter;
            }
        }

        // --- Ender Crystal explosion ---
        else if (e.getDamager() instanceof EnderCrystal crystal) {
            if (crystal.hasMetadata("exploder")) {
                UUID uuid = UUID.fromString(crystal.getMetadata("exploder").get(0).asString());
                attackingPlayer = Bukkit.getPlayer(uuid);

                System.out.println("b");

                crystal.removeMetadata("exploder", plugin);
            }
        }

        if (attackingPlayer == null) return;

        // ignore if attacker and victim are the same player
        if (attackingPlayer.getUniqueId() == victim.getUniqueId()) return;

        final Team attackerTeam = plugin.getTeamStorageUtil().findTeamByMember(attackingPlayer.getUniqueId()).orElse(null);
        final Team victimTeam = plugin.getTeamStorageUtil().findTeamByMember(victim.getUniqueId()).orElse(null);


        if (attackerTeam == null || victimTeam == null || (attackingPlayer.hasPermission("ultimateteams.bypass.pvp") && plugin.getSettings().enablePvPBypassPermission())) {
            return;
        }

        if (attackerTeam.equals(victimTeam) && !attackerTeam.isFriendlyFire()) {
            e.setCancelled(true);
            attackingPlayer.sendMessage(MineDown.parse(plugin.getMessages().getFriendlyFireIsDisabled()));

            new TeamFriendlyFireEvent(attackingPlayer, victim, attackerTeam, victimTeam).callEvent();
            return;
        }

        if (attackerTeam.areRelationsBilateral(victimTeam, Team.Relation.ALLY)) {
            e.setCancelled(true);
            attackingPlayer.sendMessage(MineDown.parse(plugin.getMessages().getFriendlyFireIsDisabledForAllies()));
        }
    }


    // track crystal hits
    @EventHandler
    public void onCrystalHit(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof EnderCrystal crystal)) return;

        Player player = null;

        if (e.getDamager() instanceof Player p) player = p;
        else if (e.getDamager() instanceof Projectile proj && proj.getShooter() instanceof Player p2) player = p2;

        if (player != null) {
            // Tag the crystal with the player who caused damage
            crystal.setMetadata("exploder", new FixedMetadataValue(plugin, player.getUniqueId().toString()));

            System.out.println("a");
        }
    }

/*
    // track respawn anchors
    @EventHandler
    public void onAnchorInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        Block block = e.getClickedBlock();

        if (block.getType() != Material.RESPAWN_ANCHOR) return;
        Action action = e.getAction();

        if (action == Action.RIGHT_CLICK_BLOCK) {
            block.setMetadata("exploder", new FixedMetadataValue(plugin, e.getPlayer().getUniqueId().toString()));

            Bukkit.broadcast("set: " + block.getLocation() + " uuid " + e.getPlayer().getName(), "suca");
        }
    }

    // handle respawn anchors
    @EventHandler
    public void onAnchorExplode(EntityExplodeEvent e) {
        for (Block block : e.blockList()) {
            if (block.getType() != Material.RESPAWN_ANCHOR) continue;
            if (!block.hasMetadata("exploder")) continue;

            UUID uuid = UUID.fromString(block.getMetadata("exploder").get(0).asString());
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                // Store temporarily so EntityDamageByEntityEvent can read it
                explodingAnchors.put(block, uuid);

                Bukkit.broadcast("exploded: " + block.getLocation() + " uuid " + uuid, "suca");
            }

            // Clean up metadata
            block.removeMetadata("exploder", plugin);
        }
    }

    @EventHandler
    public void onPlayerBlockDamage(EntityDamageByBlockEvent e) {
        System.out.println(e.getDamager() + " aaaa " + e.getDamager().hasMetadata("exploder") + " aaa " + (e.getEntity() instanceof Player));

        if (e.getDamager() == null) return;
        if (!e.getDamager().hasMetadata("exploder")) return;
        if (!(e.getEntity() instanceof final Player victim)) return;

        Bukkit.broadcast("trigger", "suca");


        UUID uuid = UUID.fromString(e.getDamager().getMetadata("exploder").get(0).asString());
        Player attackingPlayer = Bukkit.getPlayer(uuid);

        Bukkit.broadcast("tracked: " + e.getDamager().getLocation() + " uuid " + attackingPlayer.getName(), "suca");

    }*/
}