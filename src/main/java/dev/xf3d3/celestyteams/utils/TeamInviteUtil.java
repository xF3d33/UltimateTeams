package dev.xf3d3.celestyteams.utils;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.models.TeamInvite;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.logging.Logger;

public class TeamInviteUtil {

    private static final FileConfiguration teamsConfig = CelestyTeams.getPlugin().getConfig();
    private static final Logger logger = CelestyTeams.getPlugin().getLogger();

    private static Map<UUID, TeamInvite> invitesList = new HashMap<>();
    
    public static TeamInvite createInvite(String inviterUUID, String inviteeUUID){
        UUID uuid = UUID.fromString(inviterUUID);
        clearExpiredInvites();
        if (!invitesList.containsKey(uuid)){
            invitesList.put(uuid, new TeamInvite(inviterUUID, inviteeUUID));
            if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aClan invite created"));
            }
            return invitesList.get(uuid);
        }else {
            return null;
        }
    }

    public static boolean searchInvitee(String inviteeUUID){
        for (TeamInvite invite : invitesList.values()){
            if (invite.getInvitee().equals(inviteeUUID)){
                return true;
            }
        }
        return false;
    }

    public static void clearExpiredInvites(){
        int expiryTime = 25 * 1000;
        Date currentTime = new Date();
        for (TeamInvite teamInvite : invitesList.values()){
            if (currentTime.getTime() - teamInvite.getInviteTime().getTime() > expiryTime){
                invitesList.remove(teamInvite);
                if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                    logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aExpired team invites removed"));
                }
            }
        }
    }

    public static void emptyInviteList() throws UnsupportedOperationException{
        invitesList.clear();
    }

    public static void removeInvite(String inviterUUID){
        UUID uuid = UUID.fromString(inviterUUID);
        invitesList.remove(uuid);
    }

    public static Set<Map.Entry<UUID, TeamInvite>> getInvites(){
        return invitesList.entrySet();
    }

    @Deprecated
    public static Player getInviteOwner(String inviterUUID){
        if (inviterUUID.length() > 36){
            UUID uuid = UUID.fromString(inviterUUID);
            if (invitesList.containsKey(uuid)){
                if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                    logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aInvite owner uuid: &d" + inviterUUID));
                }
                return Bukkit.getPlayer(uuid);
            }
        }else {
            logger.warning(ColorUtils.translateColorCodes("&6CelestyTeams: &4An error occurred whilst getting an Invite Owner."));
            logger.warning(ColorUtils.translateColorCodes("&6CelestyTeams: &4Error: &3The provided UUID is too long."));
        }
        return null;
    }
}
