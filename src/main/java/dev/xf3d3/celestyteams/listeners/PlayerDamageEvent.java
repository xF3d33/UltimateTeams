package dev.xf3d3.celestyteams.listeners;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.api.ClanFriendlyFireAttackEvent;
import dev.xf3d3.celestyteams.models.Team;
import dev.xf3d3.celestyteams.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.logging.Logger;

public class PlayerDamageEvent implements Listener {

    Logger logger = CelestyTeams.getPlugin().getLogger();
    FileConfiguration teamsConfig = CelestyTeams.getPlugin().getConfig();
    FileConfiguration messagesConfig = CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig();

    private CelestyTeams plugin;

    public PlayerDamageEvent(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onPlayerHit(EntityDamageByEntityEvent e){
        if (e.getEntity() instanceof Player) {
            Player hurtPlayer = (Player) e.getEntity();
            String hurtUUID = hurtPlayer.getUniqueId().toString();
            
            if (e.getDamager() instanceof Player) {
                Player attackingPlayer = (Player) e.getDamager();
                attackingPlayer.setInvulnerable(false);
                
                Team attackingTeam = plugin.getTeamStorageUtil().findTeamByOwner(attackingPlayer);
                Team victimTeam = plugin.getTeamStorageUtil().findTeamByOwner(hurtPlayer);


                if (attackingTeam != null) {
                    ArrayList<String> attackingClanMembers = attackingTeam.getClanMembers();
                    if (attackingClanMembers.contains(hurtUUID) || attackingTeam.getTeamOwner().equals(hurtUUID)){
                        if (teamsConfig.getBoolean("protections.pvp.pvp-command-enabled")){
                            if (!attackingTeam.isFriendlyFireAllowed()){
                                if (teamsConfig.getBoolean("protections.pvp.enable-bypass-permission")){
                                    if (attackingPlayer.hasPermission("celestyteams.bypass.pvp")){
                                        return;
                                    }
                                }
                                e.setCancelled(true);
                                fireClanFriendlyFireAttackEvent(hurtPlayer, attackingPlayer, hurtPlayer, attackingTeam, victimTeam);
                                
                                if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                                    logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanFriendlyFireAttackEvent"));
                                }
                                attackingPlayer.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("friendly-fire-is-disabled")));
                            }
                        } else {
                            e.setCancelled(false);
                        }
                    }
                }


                else {
                    Team attackingTeamByPlayer = plugin.getTeamStorageUtil().findClanByPlayer(attackingPlayer);
                    Team victimTeamByPlayer = plugin.getTeamStorageUtil().findClanByPlayer(hurtPlayer);
                    if (attackingTeamByPlayer != null){
                        ArrayList<String> attackingMembers = attackingTeamByPlayer.getClanMembers();
                        if (attackingMembers.contains(hurtUUID) || attackingTeamByPlayer.getTeamOwner().equals(hurtUUID)){
                            if (teamsConfig.getBoolean("protections.pvp.pvp-command-enabled")){
                                if (!attackingTeamByPlayer.isFriendlyFireAllowed()){
                                    if (teamsConfig.getBoolean("protections.pvp.enable-bypass-permission")){
                                        if (attackingPlayer.hasPermission("celestyteams.bypass.pvp")
                                                ||attackingPlayer.hasPermission("celestyteams.bypass.*")
                                                ||attackingPlayer.hasPermission("celestyteams.bypass")
                                                ||attackingPlayer.hasPermission("celestyteams.*")
                                                ||attackingPlayer.isOp()){
                                            return;
                                        }
                                    }
                                    e.setCancelled(true);
                                    fireClanFriendlyFireAttackEvent(hurtPlayer, attackingPlayer, hurtPlayer, attackingTeamByPlayer, victimTeamByPlayer);
                                    if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                                        logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanFriendlyFireAttackEvent"));
                                    }
                                    attackingPlayer.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("friendly-fire-is-disabled")));
                                }
                            } else {
                                e.setCancelled(false);
                            }
                        }
                    }
                }
            }
        }
    }

    private void fireClanFriendlyFireAttackEvent(Player createdBy, Player attackingPlayer, Player victimPlayer, Team attackingTeam, Team victimTeam){
        ClanFriendlyFireAttackEvent teamFriendlyFireAttackEvent = new ClanFriendlyFireAttackEvent(createdBy, attackingPlayer, victimPlayer, attackingTeam, victimTeam);
        Bukkit.getPluginManager().callEvent(teamFriendlyFireAttackEvent);
    }
}