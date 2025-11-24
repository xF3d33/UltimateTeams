package dev.xf3d3.ultimateteams.api.events;

import dev.xf3d3.ultimateteams.models.Team;
import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TeamMemberLeaveEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled = false;

    @Getter
    private final UUID oldMember;
    @Getter
    private final Team team;
    @Getter
    private final LeaveReason leaveReason;


    public TeamMemberLeaveEvent(UUID oldMember, Team team, LeaveReason leaveReason) {
        this.oldMember = oldMember;
        this.team = team;
        this.leaveReason = leaveReason;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }

    public enum LeaveReason {
        MEMBER_LEFT,
        EVICTED
    }
}
