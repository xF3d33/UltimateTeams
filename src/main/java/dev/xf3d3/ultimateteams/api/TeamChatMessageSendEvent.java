package dev.xf3d3.ultimateteams.api;

import dev.xf3d3.ultimateteams.team.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TeamChatMessageSendEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final Team team;
    private final String prefix;
    private final String message;
    private final ArrayList<String> recipients;

    public TeamChatMessageSendEvent(Player createdBy, Team team, String prefix, String message, ArrayList<String> recipients){
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

    public Player getCreatedBy() {
        return createdBy;
    }

    public Team getClan() {
        return team;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<String> getRecipients() {
        return recipients;
    }
}
