package dev.xf3d3.ultimateteams.api.events;

import dev.xf3d3.ultimateteams.models.Team;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TeamCreateEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    @Getter
    private final Player user;
    @Getter
    private final Team team;


    public TeamCreateEvent(@NotNull Player user, @NotNull Team teamName) {
        this.user = user;
        this.team = teamName;
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
