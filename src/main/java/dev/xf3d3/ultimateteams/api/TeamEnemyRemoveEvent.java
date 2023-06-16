package dev.xf3d3.ultimateteams.api;

import dev.xf3d3.ultimateteams.models.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TeamEnemyRemoveEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final Team team;
    private final Player exEnemyClanCreatedBy;
    private final Team exEnemyTeam;



    public TeamEnemyRemoveEvent(Player createdBy, Team team, Team exEnemyTeam, Player exEnemyClanCreatedBy) {
        this.createdBy = createdBy;
        this.team = team;
        this.exEnemyClanCreatedBy = exEnemyClanCreatedBy;
        this.exEnemyTeam = exEnemyTeam;
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

    public Player getExEnemyClanCreatedBy() {
        return exEnemyClanCreatedBy;
    }

    public Team getExEnemyClan() {
        return exEnemyTeam;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
