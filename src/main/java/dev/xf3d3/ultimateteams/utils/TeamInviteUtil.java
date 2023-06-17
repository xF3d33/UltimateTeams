package dev.xf3d3.ultimateteams.utils;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.TeamInvite;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TeamInviteUtil {

    private final FileConfiguration teamsConfig;
    private final Logger logger;

    private static final Map<UUID, TeamInvite> invitesList = new HashMap<>();

    private final UltimateTeams plugin;

    public TeamInviteUtil(@NotNull UltimateTeams plugin) {
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

    public boolean hasInvitee(String inviteeUUID) {
        for (TeamInvite invite : invitesList.values()) {
            if (invite.getInvitee().equals(inviteeUUID)) {
                return true;
            }
        }
        return false;
    }

    public TeamInvite getInvitee(String inviteeUUID) {
        for (TeamInvite invite : invitesList.values()) {
            if (invite.getInvitee().equals(inviteeUUID)) {
                return invite;
            }
        }
        return null;
    }

    public void clearExpiredInvites() {
        int expiryTime = 25 * 1000;
        Date currentTime = new Date();

        for (TeamInvite teamInvite : invitesList.values()) {
            if (currentTime.getTime() - teamInvite.getInviteTime().getTime() > expiryTime) {
                invitesList.remove(teamInvite);

                if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")) {
                    plugin.log(Level.INFO, Utils.Color("&6UltimateTeams-Debug: &aExpired team invites removed"));
                }
            }
        }
    }

    public void emptyInviteList() throws UnsupportedOperationException {
        invitesList.clear();
    }

    public void removeInvite(String inviterUUID) {
        UUID uuid = UUID.fromString(inviterUUID);
        invitesList.remove(uuid);
    }

    public Set<Map.Entry<UUID, TeamInvite>> getInvites(){
        return invitesList.entrySet();
    }
}