package dev.xf3d3.celestyteams.api;

import dev.xf3d3.celestyteams.models.TeamPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ClanChatSpyToggledEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final TeamPlayer teamPlayer;
    private final boolean teamChatSpyState;

    public ClanChatSpyToggledEvent(Player createdBy, TeamPlayer teamPlayer, boolean teamChatSpyState) {
        this.createdBy = createdBy;
        this.teamPlayer = teamPlayer;
        this.teamChatSpyState = teamChatSpyState;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getCreatedBy() {
        return createdBy;
    }

    public TeamPlayer getClanPlayer() {
        return teamPlayer;
    }

    public boolean isClanChatSpyState() {
        return teamChatSpyState;
    }
}
