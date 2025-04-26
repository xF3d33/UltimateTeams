package dev.xf3d3.ultimateteams.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class Preferences {
    @Setter
    @Getter
    @Expose
    @SerializedName("team_chat_talking")
    private boolean teamChatTalking;

    @Setter
    @Getter
    @Expose
    @SerializedName("team_chat_spying")
    private boolean teamChatSpying;

    @Expose
    @SerializedName("teleport_target")
    @Nullable
    private Position teleportTarget;

    /**
     * Get the default user {@link Preferences}
     *
     * @return The default {@link Preferences}
     * @since 2.0
     */
    @NotNull
    public static Preferences getDefaults() {
        return new Preferences(
                false,
                false
        );
    }

    private Preferences(boolean teamChatTalking, boolean teamChatSpying) {
        this.teamChatTalking = teamChatTalking;
        this.teamChatSpying = teamChatSpying;
    }

    @SuppressWarnings("unused")
    private Preferences() {
    }

    /**
     * <b>Internal use only</b> - Get the user's current teleport target. This is used for cross-server teleportation.
     *
     * @return The current teleport target, if the user has one.
     * @since 2.0
     */
    public Optional<Position> getTeleportTarget() {
        return Optional.ofNullable(teleportTarget);
    }

    /**
     * <b>Internal use only</b> - Set the user's current teleport target. This is used for cross-server teleportation.
     *
     * @param target The teleport target
     * @since 2.0
     */
    public void setTeleportTarget(@NotNull Position target) {
        this.teleportTarget = target;
    }


    /**
     * <b>Internal use only</b> - Clear the user's current teleport target. This is used for cross-server teleportation.
     *
     * @since 2.0
     */
    public void clearTeleportTarget() {
        this.teleportTarget = null;
    }
}
