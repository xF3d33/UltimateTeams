package dev.xf3d3.ultimateteams.api;

import dev.xf3d3.ultimateteams.models.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TeamFriendlyFireAttackEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final Player attackingPlayer;
    private final Player victimPlayer;
    private final Team attackingTeam;
    private final Team victimTeam;

    public TeamFriendlyFireAttackEvent(Player createdBy, Player attackingPlayer, Player victimPlayer, Team attackingTeam, Team victimTeam) {
        this.createdBy = createdBy;
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

    public Player getCreatedBy() {
        return createdBy;
    }

    public Player getAttackingPlayer() {
        return attackingPlayer;
    }

    public Player getVictimPlayer() {
        return victimPlayer;
    }

    public Team getAttackingClan() {
        return attackingTeam;
    }

    public Team getVictimClan() {
        return victimTeam;
    }
}
