package dev.xf3d3.ultimateteams.api;

import dev.xf3d3.ultimateteams.models.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TeamAllyAddEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final Team team;
    private final Player allyClanCreatedBy;
    private final Team allyTeam;



    public TeamAllyAddEvent(Player createdBy, Team team, Team allyTeam, Player allyClanCreatedBy) {
        this.createdBy = createdBy;
        this.team = team;
        this.allyClanCreatedBy = allyClanCreatedBy;
        this.allyTeam = allyTeam;
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

    public Player getAllyClanCreatedBy() {
        return allyClanCreatedBy;
    }

    public Team getAllyClan() {
        return allyTeam;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
