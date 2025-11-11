package dev.xf3d3.ultimateteams.api.events;

import dev.xf3d3.ultimateteams.models.Team;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TeamAllyRemoveEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled = false;

    @Getter
    private final Player user;
    @Getter
    private final Team team;
    @Getter
    private final UUID exAllyTeamOwner;
    @Getter
    private final Team exAllyTeam;



    public TeamAllyRemoveEvent(@NotNull Player teamOwner, @NotNull Team team, @NotNull Team exAllyTeam, @NotNull UUID exAllyTeamOwner) {
        this.user = teamOwner;
        this.team = team;
        this.exAllyTeamOwner = exAllyTeamOwner;
        this.exAllyTeam = exAllyTeam;
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

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
