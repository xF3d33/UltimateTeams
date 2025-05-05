package dev.xf3d3.ultimateteams.network;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamInvite;
import dev.xf3d3.ultimateteams.models.User;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static dev.xf3d3.ultimateteams.commands.subCommands.members.TeamInviteSubCommand.PLAYER_PLACEHOLDER;
import static dev.xf3d3.ultimateteams.commands.subCommands.members.TeamInviteSubCommand.TEAM_PLACEHOLDER;

public interface MessageHandler {

    // Handle inbound user list requests
    default void handleRequestUserList(@NotNull Message message, @Nullable Player receiver) {
        if (receiver == null) {
            return;
        }

        Message.builder()
                .type(Message.Type.UPDATE_USER_LIST)
                .payload(Payload.userList(Bukkit.getOnlinePlayers().stream().map(online -> User.of(online.getUniqueId(), online.getName())).toList()))
                .target(message.getSourceServer(), Message.TargetType.SERVER).build()
                .send(getBroker(), receiver);
    }

    // Handle inbound user list updates (returned from requests)
    default void handleUpdateUserList(@NotNull Message message) {
        message.getPayload().getUserList().ifPresent(
                (players) -> getPlugin().getUsersStorageUtil().setUserList(message.getSourceServer(), players)
        );
    }

    default void handleTeamDelete(@NotNull Message message) {
        message.getPayload().getInteger()
                .flatMap(teamID -> getPlugin().getTeamStorageUtil().getTeams().stream().filter(team -> team.getId() == teamID).findFirst())
                .ifPresent(team -> getPlugin().getTeamStorageUtil().getTeams().remove(team));
    }

    default void handleTeamUpdate(@NotNull Message message) {
        message.getPayload().getInteger()
                .ifPresent(id -> getPlugin().runAsync(task -> getPlugin().getDatabase().getTeam(id)
                        .ifPresentOrElse(
                                team -> getPlugin().getTeamStorageUtil().updateTeamLocal(team, team.getId()),
                                () -> getPlugin().log(Level.WARNING, "Failed to update team: Team not found")
                        )));
    }

    default void handleTeamInviteRequest(@NotNull Message message, @Nullable Player receiver) {
        if (receiver == null) {
            return;
        }
        message.getPayload().getInvite().ifPresentOrElse(
                invite -> getPlugin().getTeamInviteUtil().handleInboundInvite(receiver, invite),
                () -> getPlugin().log(Level.WARNING, "Invalid team invite request payload!")
        );
    }

    default void handleTeamInviteReply(@NotNull Message message, @Nullable Player receiver) {
        final Optional<TeamInvite> optionalTeamInvite = message.getPayload().getInvite();
        if (optionalTeamInvite.isEmpty()) {
            return;
        }

        final TeamInvite invite = optionalTeamInvite.get();
        final Optional<Team> team = getPlugin().getTeamStorageUtil().findTeamByMember(invite.getInviter());
        if (team.isEmpty()) {
            return;
        }

        getPlugin().getTeamInviteUtil().removeInvitee(invite.getInvitee());

        if (!getPlugin().getSettings().teamJoinAnnounce())
            return;

        if (Boolean.FALSE.equals(invite.getAccepted())) {
            final Player inviter = Bukkit.getPlayer(invite.getInviter());

            if (inviter != null)  {
                inviter.sendMessage(Utils.Color(getPlugin().msgFileManager.getMessagesConfig().getString("team-invite-denied-inviter")
                        .replace("%PLAYER%", Objects.requireNonNullElse(Bukkit.getOfflinePlayer(invite.getInvitee()).getName(), "?"))));
            }

            return;
        }

        // Send message to team members
        team.get().sendTeamMessage(Utils.Color(getPlugin().msgFileManager.getMessagesConfig().getString("team-join-broadcast-chat")
                .replace(PLAYER_PLACEHOLDER, Objects.requireNonNullElse(Bukkit.getOfflinePlayer(invite.getInvitee()).getName(), "?"))
                .replace(TEAM_PLACEHOLDER, Utils.Color(team.get().getName()))));
    }

    default void handleTeamChatMessage(@NotNull Message message) {
        message.getPayload().getString().ifPresent(text -> getPlugin().getTeamStorageUtil().findTeamByMember(Bukkit.getOfflinePlayer(message.getSender()).getUniqueId())
                        .ifPresent(team -> {
                            team.sendTeamMessage(Utils.Color(text));

                            // Send spy message
                            if (getPlugin().getSettings().teamChatSpyEnabled()) {
                                Bukkit.broadcast(Utils.Color(getPlugin().getSettings().getTeamChatSpyPrefix() + " " + text), "ultimateteams.chat.spy");
                            }
                        }));
    }

    default void handleTeamAllyChatMessage(@NotNull Message message) {
        message.getPayload().getString().ifPresent(text -> getPlugin().getTeamStorageUtil().findTeamByMember(Bukkit.getOfflinePlayer(message.getSender()).getUniqueId())
                .ifPresent(team -> {
                    final Set<Team> allies = team.getRelations(getPlugin())
                            .entrySet().stream()
                            .filter(entry -> entry.getValue() == Team.Relation.ALLY)
                            .map(Map.Entry::getKey)
                            .filter(otherTeam -> team.areRelationsBilateral(otherTeam, Team.Relation.ALLY))
                            .collect(Collectors.toSet());

                    // Send message to team members
                    team.sendTeamMessage(Utils.Color(text));

                    // Send message to allied team Members
                    allies.forEach(
                            alliedTeam -> alliedTeam.sendTeamMessage(Utils.Color(text))
                    );

                    // Send spy message
                    if (getPlugin().getSettings().teamChatSpyEnabled()) {
                        Bukkit.broadcast(Utils.Color(getPlugin().getSettings().getTeamChatSpyPrefix() + " " + text), "ultimateteams.chat.spy");
                    }
                }));
    }


    default void handleTeamAction(@NotNull Message message) {
        message.getPayload().getInteger().flatMap(id -> getPlugin().getTeamStorageUtil().getTeams().stream()
                .filter(team -> team.getId() == id).findFirst()).ifPresent(team -> {
                    if (message.getType() == Message.Type.TEAM_TRANSFERRED) {
                        final Player owner = Bukkit.getPlayer(team.getOwner());

                        if (owner != null) {
                            owner.sendMessage(Utils.Color(getPlugin().msgFileManager.getMessagesConfig().getString("team-ownership-transfer-new-owner")
                                    .replace("%TEAM%", team.getName())));
                        }
                    }
            //team.sendTeamMessage(Utils.Color(msg));
        });
    }

    default void handleTeamUserAction(@NotNull Message message, @Nullable Player receiver) {
        if (receiver == null) {
            return;
        }

        receiver.sendMessage(Utils.Color(getPlugin().msgFileManager.getMessagesConfig().getString("team-kicked-player-message"))
                .replace("%TEAM%", getPlugin().getTeamStorageUtil().findTeamByMember(receiver.getUniqueId()).map(Team::getName).orElse("?")));

        getPlugin().getUsersStorageUtil().getPlayer(receiver.getUniqueId()).thenAccept(teamPlayer -> {
            teamPlayer.getPreferences().setTeamChatTalking(false);
            getPlugin().getUsersStorageUtil().updatePlayer(teamPlayer);
        });
    }

    @NotNull
    Broker getBroker();

    @NotNull
    UltimateTeams getPlugin();

}
