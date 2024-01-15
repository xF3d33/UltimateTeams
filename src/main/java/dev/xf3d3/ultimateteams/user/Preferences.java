package dev.xf3d3.ultimateteams.user;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import dev.xf3d3.ultimateteams.team.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class Preferences {

    @Expose
    @SerializedName("team_chat_talking")
    private boolean teamChatTalking;

    @Expose
    @SerializedName("teleport_target")
    @Nullable
    private Position teleportTarget;


    @NotNull
    public static Preferences getDefaults() {
        return new Preferences(
                false
        );
    }

    private Preferences(boolean teamChatTalking) {
        this.teamChatTalking = teamChatTalking;
    }

    @SuppressWarnings("unused")
    private Preferences() {
    }

    public boolean isTeamChatTalking() {
        return teamChatTalking;
    }

    public void setTeamChatTalking(boolean teamChatTalking) {
        this.teamChatTalking = teamChatTalking;
    }

    public Optional<Position> getTeleportTarget() {
        return Optional.ofNullable(teleportTarget);
    }

    public void setTeleportTarget(@NotNull Position target) {
        this.teleportTarget = target;
    }

}
