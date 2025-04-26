package dev.xf3d3.ultimateteams.api.events;

import dev.xf3d3.ultimateteams.models.Team;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class TeamChatMessageSendEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    @Getter
    private final Player createdBy;
    private final Team team;
    @Getter
    private final String prefix;
    @Getter
    private final String message;
    @Getter
    private final List<UUID> recipients;

    public TeamChatMessageSendEvent(Player createdBy, Team team, String prefix, String message, List<UUID> recipients){
        this.createdBy = createdBy;
        this.team = team;
        this.prefix = prefix;
        this.message = message;
        this.recipients = recipients;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Team getTeam() {
        return team;
    }

}
