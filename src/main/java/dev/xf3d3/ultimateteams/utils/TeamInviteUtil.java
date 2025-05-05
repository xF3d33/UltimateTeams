package dev.xf3d3.ultimateteams.utils;

import com.google.common.collect.Maps;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.TeamInvite;
import dev.xf3d3.ultimateteams.network.Message;
import dev.xf3d3.ultimateteams.network.Payload;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

public class TeamInviteUtil {

    private static final Map<UUID, TeamInvite> invites = Maps.newConcurrentMap();

    private final UltimateTeams plugin;

    public TeamInviteUtil(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }
    
    public boolean createInvite(Integer id, Player inviter, UUID inviteeUUID) {
        clearExpiredInvites();

        final Player player = Bukkit.getPlayer(inviteeUUID);
        final TeamInvite invite = new TeamInvite(id, inviter.getUniqueId(), inviteeUUID, new Date().getTime(), null);
        if (player == null && plugin.getSettings().isEnableCrossServer()) {
            final String senderName = Objects.requireNonNullElse(Bukkit.getOfflinePlayer(invite.getInvitee()).getName(), "");

            invites.put(inviter.getUniqueId(), invite);
            plugin.getMessageBroker().ifPresent(broker -> Message.builder()
                    .type(Message.Type.TEAM_INVITE_REQUEST)
                    .payload(Payload.invite(invite))
                    .target(senderName, Message.TargetType.PLAYER)
                    .build()
                    .send(broker, inviter));

            return true;
        } else if (player != null) {

            invites.put(inviter.getUniqueId(), invite);
            return true;
        }

        return false;
    }

    public void handleInboundInvite(Player invitee, TeamInvite invite) {
        invitee.sendMessage(Utils.Color(plugin.msgFileManager.getMessagesConfig().getString("team-invited-player-invite-pending"))
                .replace("%TEAMOWNER%", Objects.requireNonNullElse(Bukkit.getOfflinePlayer(invite.getInviter()).getName(), ""))
        );
        invites.put(invite.getInviter(), invite);
    }

    public boolean hasInvitee(UUID inviteeUUID) {
        return invites.values().stream().anyMatch(invite -> invite.getInvitee().equals(inviteeUUID));
    }

    public Optional<TeamInvite> getInvite(UUID inviteeUUID) {
        return invites.values().stream().filter(invite -> invite.getInvitee().equals(inviteeUUID)).findFirst();
    }

    public void acceptInvite(TeamInvite invite, Player player) {
        final String senderName = Objects.requireNonNullElse(Bukkit.getOfflinePlayer(invite.getInvitee()).getName(), "");
        removeInvite(invite.getInviter());

        invite.setAccepted(Boolean.TRUE);

        plugin.getMessageBroker().ifPresent(broker -> Message.builder()
                .type(Message.Type.TEAM_INVITE_REPLY)
                .payload(Payload.invite(invite))
                .target(Message.TARGET_ALL, Message.TargetType.SERVER)
                .build()
                .send(broker, player));
    }

    public boolean declineInvite(Player player) {
        Optional<TeamInvite> optionalTeamInvite = getInvite(player.getUniqueId());

        if (optionalTeamInvite.isEmpty())
            return false;

        TeamInvite invite = optionalTeamInvite.get();
        invite.setAccepted(Boolean.FALSE);

        final Player inviter = Bukkit.getPlayer(invite.getInviter());
        if (inviter != null)  {
            inviter.sendMessage(Utils.Color(plugin.msgFileManager.getMessagesConfig().getString("team-invite-denied-inviter")
                    .replace("%PLAYER%", player.getName())));
        }

        final String senderName = Objects.requireNonNullElse(Bukkit.getOfflinePlayer(invite.getInvitee()).getName(), "");

        plugin.getMessageBroker().ifPresent(broker -> Message.builder()
                .type(Message.Type.TEAM_INVITE_REPLY)
                .payload(Payload.invite(invite))
                .target(Message.TARGET_ALL, Message.TargetType.SERVER)
                .build()
                .send(broker, player));

        return removeInvitee(player.getUniqueId());
    }

    public void clearExpiredInvites() {
        int expiryTime = 25 * 1000;
        Date currentTime = new Date();

        invites.values().removeIf(invite -> currentTime.getTime() - invite.getInvitedAt() > expiryTime);

        if (plugin.getSettings().debugModeEnabled()) {
            plugin.log(Level.INFO, Utils.Color("&6UltimateTeams-Debug: &aExpired team invites removed"));
        }
    }

    public void emptyInviteList() {
        invites.clear();
    }

    public void removeInvite(UUID inviterUUID) {
        invites.remove(inviterUUID);
    }

    public boolean removeInvitee(UUID uuid) {
        return invites.values().removeIf(invite -> invite.getInvitee().equals(uuid));
    }

    public Set<Map.Entry<UUID, TeamInvite>> getInvites(){
        return invites.entrySet();
    }
}
