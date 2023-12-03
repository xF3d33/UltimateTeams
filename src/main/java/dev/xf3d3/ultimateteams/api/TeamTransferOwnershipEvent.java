package dev.xf3d3.ultimateteams.api;

import dev.xf3d3.ultimateteams.team.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TeamTransferOwnershipEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final Player originalClanOwner;
    private final Player newClanOwner;
    private final Team newTeam;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public TeamTransferOwnershipEvent(Player createdBy, Player originalClanOwner, Player newClanOwner, Team newTeam) {
        this.createdBy = createdBy;
        this.originalClanOwner = originalClanOwner;
        this.newClanOwner = newClanOwner;
        this.newTeam = newTeam;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getCreatedBy() {
        return createdBy;
    }

    public Player getOriginalClanOwner() {
        return originalClanOwner;
    }

    public Player getNewClanOwner() {
        return newClanOwner;
    }

    public Team getNewClan() {
        return newTeam;
    }
}
