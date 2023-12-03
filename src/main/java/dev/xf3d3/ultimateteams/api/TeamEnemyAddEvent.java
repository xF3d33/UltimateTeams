package dev.xf3d3.ultimateteams.api;

import dev.xf3d3.ultimateteams.team.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TeamEnemyAddEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final Team team;
    private final Player enemyClanCreatedBy;
    private final Team enemyTeam;



    public TeamEnemyAddEvent(Player createdBy, Team team, Team enemyTeam, Player enemyClanCreatedBy) {
        this.createdBy = createdBy;
        this.team = team;
        this.enemyClanCreatedBy = enemyClanCreatedBy;
        this.enemyTeam = enemyTeam;
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

    public Player getEnemyClanCreatedBy() {
        return enemyClanCreatedBy;
    }

    public Team getEnemyClan() {
        return enemyTeam;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }


}
