package dev.xf3d3.ultimateteams.api.events;

import dev.xf3d3.ultimateteams.models.Team;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TeamFriendlyFireEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    @Getter
    private final Player attackingPlayer;
    @Getter
    private final Player victimPlayer;
    @Getter
    private final Team attackingTeam;
    @Getter
    private final Team victimTeam;

    public TeamFriendlyFireEvent(@NotNull Player attackingPlayer, @NotNull Player victimPlayer, @NotNull Team attackingTeam, @NotNull Team victimTeam) {
        this.attackingPlayer = attackingPlayer;
        this.victimPlayer = victimPlayer;
        this.attackingTeam = attackingTeam;
        this.victimTeam = victimTeam;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() { return HANDLERS; }
}
