package dev.xf3d3.celestyteams.api;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ClanOfflineDisbandEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final OfflinePlayer createdBy;
    private final String teamName;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public ClanOfflineDisbandEvent(OfflinePlayer createdBy, String teamName) {
        this.createdBy = createdBy;
        this.teamName = teamName;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public OfflinePlayer getCreatedBy() {
        return createdBy;
    }

    public String getClan() {
        return teamName;
    }
}
