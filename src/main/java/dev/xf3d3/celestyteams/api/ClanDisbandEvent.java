package dev.xf3d3.celestyteams.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ClanDisbandEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final String teamName;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public ClanDisbandEvent(Player createdBy, String teamName) {
        this.createdBy = createdBy;
        this.teamName = teamName;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getCreatedBy() {
        return createdBy;
    }

    public String getClan() {
        return teamName;
    }
}
