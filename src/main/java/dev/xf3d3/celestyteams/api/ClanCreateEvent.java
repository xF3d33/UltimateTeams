package dev.xf3d3.celestyteams.api;

import dev.xf3d3.celestyteams.models.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ClanCreateEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final Team team;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public ClanCreateEvent(Player createdBy, Team teamName) {
        this.createdBy = createdBy;
        this.team = teamName;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getCreatedBy() {
        return createdBy;
    }

    public Team getClan() {
        return team;
    }

}
