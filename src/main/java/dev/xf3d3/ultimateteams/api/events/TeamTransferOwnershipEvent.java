package dev.xf3d3.ultimateteams.api.events;

import dev.xf3d3.ultimateteams.models.Team;
import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TeamTransferOwnershipEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled = false;

    @Getter
    private final UUID oldOwner;
    @Getter
    private final UUID newOwner;
    @Getter
    private final Team Team;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public TeamTransferOwnershipEvent(UUID oldOwner, UUID newOwner, Team Team) {
        this.oldOwner = oldOwner;
        this.newOwner = newOwner;
        this.Team = Team;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
