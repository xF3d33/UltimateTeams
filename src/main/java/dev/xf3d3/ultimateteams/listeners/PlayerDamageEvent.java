package dev.xf3d3.ultimateteams.listeners;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamFriendlyFireAttackEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerDamageEvent implements Listener {
    FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();

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

        if (attackingPlayer == null) return;

        final Team attackerTeam = plugin.getTeamStorageUtil().findTeamByMember(attackingPlayer.getUniqueId()).orElse(null);
        final Team victimTeam = plugin.getTeamStorageUtil().findTeamByMember(victim.getUniqueId()).orElse(null);


        if (attackerTeam == null || victimTeam == null || (attackingPlayer.hasPermission("ultimateteams.bypass.pvp") && plugin.getSettings().enablePvPBypassPermission())) {
            return;
        }

        if (attackerTeam.equals(victimTeam) && attackerTeam.isFriendlyFire()) {
            return;
        }

        if (attackerTeam.areRelationsBilateral(victimTeam, Team.Relation.ALLY)) {
            e.setCancelled(true);
            attackingPlayer.sendMessage(Utils.Color(messagesConfig.getString("friendly-fire-is-disabled-for-allies")));
        }
    }

    private void fireClanFriendlyFireAttackEvent(Player createdBy, Player attackingPlayer, Player victimPlayer, Team attackingTeam, Team victimTeam){
        TeamFriendlyFireAttackEvent teamFriendlyFireAttackEvent = new TeamFriendlyFireAttackEvent(createdBy, attackingPlayer, victimPlayer, attackingTeam, victimTeam);
        Bukkit.getPluginManager().callEvent(teamFriendlyFireAttackEvent);
    }
}