package dev.xf3d3.ultimateteams.network;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.network.objects.Message;
import dev.xf3d3.ultimateteams.network.objects.Payload;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public abstract class Broker {

    protected final UltimateTeams plugin;

    /**
     * Create a new broker
     *
     * @param plugin the HuskTowns plugin instance
     */
    protected Broker(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    /**
     * Handle an inbound {@link Message}
     *
     * @param receiver The user who received the message, if a receiver exists
     * @param message  The message
     */
    protected void handle(@Nullable Player receiver, @NotNull Message message) {
        if (message.getSourceServer().equals(getServer())) {
            return;
        }
        switch (message.getType()) {
            case TEAM_UPDATE -> plugin.runAsync(() -> message.getPayload().getInteger()
                    .flatMap(id -> plugin.getDatabase().getTeam(id))
                    .ifPresentOrElse(
                            plugin::updateTeam,
                            () -> plugin.log(Level.WARNING, "Failed to update team: Town not found")
                    ));
            case TOWN_DELETE -> message.getPayload().getInteger()
                    .flatMap(teamID -> plugin.getTeams().stream().filter(team -> team.getID() == teamID).findFirst())
                    .ifPresent(team -> plugin.runAsync(() -> {
                        plugin.getManager().sendTeamMessage(team,"team deleted");
                        plugin.removeTeam(team);
                    }));
            case TOWN_INVITE_REQUEST -> {
                if (receiver == null) {
                    return;
                }
                message.getPayload().getInvite().ifPresentOrElse(
                        invite -> plugin.getManager().teams().handleInboundInvite(receiver, invite),
                        () -> plugin.log(Level.WARNING, "Invalid team invite request payload!")
                );
            }
            case TOWN_INVITE_REPLY -> message.getPayload().getBool().ifPresent(accepted -> {
                if (receiver == null) {
                    return;
                }
                final Optional<Member> member = plugin.getUserTown(receiver);
                if (member.isEmpty()) {
                    return;
                }
                final Member townMember = member.get();
                if (!accepted) {
                    plugin.getLocales().getLocale("invite_declined_by", message.getSender())
                            .ifPresent(receiver::sendMessage);
                    return;
                }
                plugin.getLocales().getLocale("user_joined_town", message.getSender(),
                                townMember.town().getName()).map(MineDown::toComponent)
                        .ifPresent(locale -> plugin.getManager().sendTownMessage(townMember.town(), locale));
            });
            case TOWN_CHAT_MESSAGE -> message.getPayload().getString()
                    .ifPresent(text -> plugin.getDatabase().getUser(message.getSender())
                            .flatMap(sender -> plugin.getManager().teams().findTeamByPlayer(sender.user()))
                            .ifPresent(member -> plugin.getManager().teams().sendLocalChatMessage(text, member, plugin)));
            case REQUEST_USER_LIST -> {
                if (receiver == null) {
                    return;
                }
                Message.builder()
                        .type(Message.Type.USER_LIST)
                        .target(message.getSourceServer(), Message.TargetType.SERVER)
                        .payload(Payload.userList(plugin.getOnlineUsers().stream().map(online -> (User) online).toList()))
                        .build().send(this, receiver);
            }
            case USER_LIST -> message.getPayload()
                    .getUserList()
                    .ifPresent(players -> plugin.setUserList(message.getSourceServer(), players));
            case TOWN_EVICTED -> {
                if (receiver == null) {
                    return;
                }
                plugin.getLocales().getLocale("evicted_you",
                        plugin.getUserTown(receiver).map(Member::town).map(Town::getName).orElse("?"),
                        message.getSender()).ifPresent(receiver::sendMessage);
                plugin.editUserPreferences(receiver, preferences -> preferences.setTownChatTalking(false));
            }


            default -> plugin.log(Level.SEVERE, "Received unknown message type: " + message.getType());
        }
    }

    /**
     * Initialize the message broker
     *
     * @throws RuntimeException if the broker fails to initialize
     */
    public abstract void initialize() throws RuntimeException;

    /**
     * Send a message to the broker
     *
     * @param message the message to send
     * @param sender  the sender of the message
     */
    protected abstract void send(@NotNull Message message, @NotNull Player sender);

    /**
     * Move an {@link Player} to a new server on the proxy network
     *
     * @param user   the user to move
     * @param server the server to move the user to
     */
    public abstract void changeServer(@NotNull Player user, @NotNull String server);

    /**
     * Terminate the broker
     */
    public abstract void close();

    @NotNull
    protected String getSubChannelId() {
        final String version = plugin.getVersion().getMajor() + "." + plugin.getVersion().getMinor();
        return plugin.getKey(plugin.getSettings().getClusterId(), version).asString();
    }

    @NotNull
    protected String getServer() {
        return plugin.getServerName();
    }

    /**
     * Identifies types of message brokers
     */
    public enum Type {
        PLUGIN_MESSAGE("Plugin Messages"),
        REDIS("Redis");
        @NotNull
        private final String displayName;

        Type(@NotNull String displayName) {
            this.displayName = displayName;
        }

        @NotNull
        public String getDisplayName() {
            return displayName;
        }
    }

}