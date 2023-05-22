package dev.xf3d3.celestyteams.api;

import dev.xf3d3.celestyteams.models.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ClanEnemyAddEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final Team team;
    private final Player enemyClanCreatedBy;
    private final Team enemyTeam;



    public ClanEnemyAddEvent(Player createdBy, Team team, Team enemyTeam, Player enemyClanCreatedBy) {
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
