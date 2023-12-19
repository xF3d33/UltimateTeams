package dev.xf3d3.ultimateteams.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Message {

    public static final String TARGET_ALL = "ALL";
    @NotNull
    @Expose
    private Type type;

    @NotNull
    @Expose
    @SerializedName("target_type")
    private TargetType targetType;
    @NotNull
    @Expose
    private String target;
    @NotNull
    @Expose
    private Payload payload;
    @NotNull
    @Expose
    private String sender;

    @NotNull
    @Expose
    private String sourceServer;

    private Message(@NotNull Type type, @NotNull String target, @NotNull TargetType targetType, @NotNull Payload payload) {
        this.type = type;
        this.target = target;
        this.payload = payload;
        this.targetType = targetType;
    }

    @SuppressWarnings("unused")
    private Message() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public void send(@NotNull Broker broker, @NotNull Player sender) {
        this.sender = sender.getName();
        this.sourceServer = broker.getServer();
        broker.send(this, sender);
    }

    @NotNull
    public Type getType() {
        return type;
    }

    @NotNull
    public String getTarget() {
        return target;
    }

    @NotNull
    public TargetType getTargetType() {
        return targetType;
    }

    @NotNull
    public Payload getPayload() {
        return payload;
    }

    @NotNull
    public String getSender() {
        return sender;
    }

    @NotNull
    public String getSourceServer() {
        return sourceServer;
    }

    /**
     * Builder for {@link Message}s
     */
    public static class Builder {
        private Type type;
        private Payload payload = Payload.empty();
        private TargetType targetType = TargetType.PLAYER;
        private String target;

        private Builder() {
        }

        @NotNull
        public Builder type(@NotNull Type type) {
            this.type = type;
            return this;
        }

        @NotNull
        public Builder payload(@NotNull Payload payload) {
            this.payload = payload;
            return this;
        }

        @NotNull
        public Builder target(@NotNull String target, @NotNull TargetType targetType) {
            this.target = target;
            this.targetType = targetType;
            return this;
        }

        @NotNull
        public Message build() {
            return new Message(type, target, targetType, payload);
        }

    }

    /**
     * Different types of cross-server messages
     */
    public enum Type {
        /**
         * Indicates the target server should pull and cache new town data from the database for a town by ID.
         */
        TEAM_UPDATE,
        /**
         * Indicates the target server should remove all claims for a town by ID and remove it from the cache.
         */
        TOWN_DELETE,
        /**
         * A message sent to handle a cross-server invite
         */
        TOWN_INVITE_REQUEST,
        /**
         * A message send to reply to a cross-server invite
         */
        TOWN_INVITE_REPLY,
        /**
         * A notification message that a town has transferred ownership
         */
        TOWN_TRANSFERRED,
        /**
         * A notification message that a town has evicted a member
         */
        TOWN_EVICTED,
        /**
         * A notification message that a town has renamed
         */
        TOWN_RENAMED,
        /**
         * A message for dispatching a cross-server town chat message
         */
        TOWN_CHAT_MESSAGE,
        /**
         * Request other servers for a list of online users
         */
        REQUEST_USER_LIST,
        /**
         * A message containing a list of users on a server
         */
        USER_LIST
    }

    public enum TargetType {
        /**
         * The target is a server name, or "all" to indicate all servers.
         */
        SERVER("Forward"),
        /**
         * The target is a player name, or "all" to indicate all players.
         */
        PLAYER("ForwardToPlayer");

        private final String pluginMessageChannel;

        TargetType(@NotNull String pluginMessageChannel) {
            this.pluginMessageChannel = pluginMessageChannel;
        }

        @NotNull
        public String getPluginMessageChannel() {
            return pluginMessageChannel;
        }
    }

}
