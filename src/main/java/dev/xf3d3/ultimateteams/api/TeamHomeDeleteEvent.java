package dev.xf3d3.ultimateteams.api;

import dev.xf3d3.ultimateteams.models.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TeamHomeDeleteEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final Team team;

    public TeamHomeDeleteEvent(Player createdBy, Team team) {
        this.createdBy = createdBy;
        this.team = team;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getCreatedBy() {
        return createdBy;
    }

    public Team getTeam() {
        return team;
    }
}
