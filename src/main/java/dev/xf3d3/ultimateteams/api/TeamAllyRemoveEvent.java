package dev.xf3d3.ultimateteams.api;

import dev.xf3d3.ultimateteams.team.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TeamAllyRemoveEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final Team team;
    private final Player exAllyClanCreatedBy;
    private final Team exAllyTeam;



    public TeamAllyRemoveEvent(Player createdBy, Team team, Team exAllyTeam, Player exAllyClanCreatedBy) {
        this.createdBy = createdBy;
        this.team = team;
        this.exAllyClanCreatedBy = exAllyClanCreatedBy;
        this.exAllyTeam = exAllyTeam;
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

    public Player getExAllyClanCreatedBy() {
        return exAllyClanCreatedBy;
    }

    public Team getExAllyClan() {
        return exAllyTeam;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
