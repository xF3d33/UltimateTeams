package dev.xf3d3.ultimateteams.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@AllArgsConstructor
public class TeamInvite {

    @Expose
    @SerializedName("team_id")
    @Getter
    private int teamId;

    @Getter
    @Expose
    private UUID inviter;

    @Getter
    @Expose
    private UUID invitee;

    @Getter
    @Expose
    private long invitedAt;

    @Getter @Setter
    @Nullable
    @Expose
    private Boolean accepted;


    @SuppressWarnings("unused")
    private TeamInvite() {
    }


}
