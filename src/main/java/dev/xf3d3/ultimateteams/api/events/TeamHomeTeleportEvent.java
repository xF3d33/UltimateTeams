package dev.xf3d3.ultimateteams.api.events;

import dev.xf3d3.ultimateteams.models.Team;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TeamHomeTeleportEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final Team team;
    private final Location homeLocation;
    private final Location tpFromLocation;

    public TeamHomeTeleportEvent(Player createdBy, Team team, Location homeLocation, Location tpFromLocation) {
        this.createdBy = createdBy;
        this.team = team;
        this.homeLocation = homeLocation;
        this.tpFromLocation = tpFromLocation;
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

    public Location getHomeLocation() {
        return homeLocation;
    }

    public Location getTpFromLocation() {
        return tpFromLocation;
    }
}
