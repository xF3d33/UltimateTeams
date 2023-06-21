package dev.xf3d3.ultimateteams.listeners;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.TeamFriendlyFireAttackEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerDamageEvent implements Listener {
    FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();

    private final UltimateTeams plugin;

    public PlayerDamageEvent(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onPlayerHit(EntityDamageByEntityEvent e){
        if (e.getEntity() instanceof final Player hurtPlayer) {
            String hurtUUID = hurtPlayer.getUniqueId().toString();

            if (e.getDamager() instanceof final Player attackingPlayer) {
                attackingPlayer.setInvulnerable(false);

                Team attackingTeam;
                if (plugin.getTeamStorageUtil().findTeamByOwner(attackingPlayer) != null) {
                    attackingTeam = plugin.getTeamStorageUtil().findTeamByOwner(attackingPlayer);
                } else {
                    attackingTeam = plugin.getTeamStorageUtil().findTeamByPlayer(attackingPlayer);
                }

                Team victimTeam;
                if (plugin.getTeamStorageUtil().findTeamByOwner(hurtPlayer) != null) {
                    victimTeam = plugin.getTeamStorageUtil().findTeamByOwner(hurtPlayer);
                } else {
                    victimTeam = plugin.getTeamStorageUtil().findTeamByPlayer(hurtPlayer);
                }


                if (attackingTeam != null) {
                    ArrayList<String> attackingClanMembers = attackingTeam.getTeamMembers();
                    if (attackingClanMembers.contains(hurtUUID) || attackingTeam.getTeamOwner().equals(hurtUUID)){
                        if (plugin.getSettings().isPvpCommandEnabled()){
                            if (!attackingTeam.isFriendlyFireAllowed()){
                                if (plugin.getSettings().enablePvPBypassPermission()){
                                    if (attackingPlayer.hasPermission("ultimateteams.bypass.pvp")){
                                        return;
                                    }
                                }
                                e.setCancelled(true);
                                fireClanFriendlyFireAttackEvent(hurtPlayer, attackingPlayer, hurtPlayer, attackingTeam, victimTeam);
                                
                                if (plugin.getSettings().debugModeEnabled()){
                                    plugin.log(Level.INFO, Utils.Color("&6UltimateTeams-Debug: &aFired ClanFriendlyFireAttackEvent"));
                                }
                                attackingPlayer.sendMessage(Utils.Color(messagesConfig.getString("friendly-fire-is-disabled")));
                            }
                        } else {
                            e.setCancelled(false);
                        }
                    }
                }
            }
        }
    }

    private void fireClanFriendlyFireAttackEvent(Player createdBy, Player attackingPlayer, Player victimPlayer, Team attackingTeam, Team victimTeam){
        TeamFriendlyFireAttackEvent teamFriendlyFireAttackEvent = new TeamFriendlyFireAttackEvent(createdBy, attackingPlayer, victimPlayer, attackingTeam, victimTeam);
        Bukkit.getPluginManager().callEvent(teamFriendlyFireAttackEvent);
    }
}