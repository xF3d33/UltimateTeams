package dev.xf3d3.celestyteams.utils;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.models.TeamInvite;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Logger;

public class TeamInviteUtil {

    private final FileConfiguration teamsConfig;
    private final Logger logger;

    private static final Map<UUID, TeamInvite> invitesList = new HashMap<>();

    private final CelestyTeams plugin;

    public TeamInviteUtil(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.teamsConfig = plugin.getConfig();
    }
    
    public TeamInvite createInvite(String inviterUUID, String inviteeUUID) {
        UUID uuid = UUID.fromString(inviterUUID);
        clearExpiredInvites();

        if (!invitesList.containsKey(uuid)) {
            invitesList.put(uuid, new TeamInvite(inviterUUID, inviteeUUID));
            return invitesList.get(uuid);
        } else {
            return null;
        }
    }

    public boolean searchInvitee(String inviteeUUID) {
        for (TeamInvite invite : invitesList.values()) {
            if (invite.getInvitee().equals(inviteeUUID)) {
                return true;
            }
        }
        return false;
    }

    public void clearExpiredInvites() {
        int expiryTime = 25 * 1000;
        Date currentTime = new Date();

        for (TeamInvite teamInvite : invitesList.values()) {
            if (currentTime.getTime() - teamInvite.getInviteTime().getTime() > expiryTime) {
                invitesList.remove(teamInvite);

                if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")) {
                    logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aExpired team invites removed"));
                }
            }
        }
    }

    public void emptyInviteList() throws UnsupportedOperationException {
        invitesList.clear();
    }

    public static void removeInvite(String inviterUUID) {
        UUID uuid = UUID.fromString(inviterUUID);
        invitesList.remove(uuid);
    }

    public static Set<Map.Entry<UUID, TeamInvite>> getInvites(){
        return invitesList.entrySet();
    }

    @Deprecated
    public Player getInviteOwner(String inviterUUID) {
        if (inviterUUID.length() > 36) {
            UUID uuid = UUID.fromString(inviterUUID);

            if (invitesList.containsKey(uuid)) {
                return Bukkit.getPlayer(uuid);
            }
        } else {
            logger.warning(ColorUtils.translateColorCodes("&6CelestyTeams: &4An error occurred whilst getting an Invite Owner."));
            logger.warning(ColorUtils.translateColorCodes("&6CelestyTeams: &4Error: &3The provided UUID is too long."));
        }
        return null;
    }
}
