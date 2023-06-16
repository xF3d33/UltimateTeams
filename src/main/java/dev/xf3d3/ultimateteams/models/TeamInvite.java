package dev.xf3d3.ultimateteams.models;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class TeamInvite {

    private final String inviter;
    private final String invitee;
    final Date inviteTime;

    public TeamInvite(@NotNull String inviter, @NotNull String invitee) {
        this.inviter = inviter;
        this.invitee = invitee;
        this.inviteTime = new Date();
    }

    public String getInviter() {
        return inviter;
    }

    public String getInvitee() {
        return invitee;
    }

    public Date getInviteTime() {
        return inviteTime;
    }
}
