package dev.xf3d3.ultimateteams.api;

import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TeamPointsRemovedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final Team playerTeam;
    private final TeamPlayer teamPlayer;
    private final int previousClanPlayerPointBalance;
    private final int newClanPlayerPointBalance;
    private final int withdrawPointValue;
    private final int previousClanPointBalance;
    private final int newClanPointBalance;

    public TeamPointsRemovedEvent(Player createdBy, Team playerTeam, TeamPlayer teamPlayer,
                                  int previousClanPlayerPointBalance, int newClanPlayerPointBalance,
                                  int withdrawPointValue, int previousClanPointBalance, int newClanPointBalance) {
        this.createdBy = createdBy;
        this.playerTeam = playerTeam;
        this.teamPlayer = teamPlayer;
        this.previousClanPlayerPointBalance = previousClanPlayerPointBalance;
        this.newClanPlayerPointBalance = newClanPlayerPointBalance;
        this.withdrawPointValue = withdrawPointValue;
        this.previousClanPointBalance = previousClanPointBalance;
        this.newClanPointBalance = newClanPointBalance;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getCreatedBy() {
        return createdBy;
    }

    public Team getPlayerClan() {
        return playerTeam;
    }

    public TeamPlayer getClanPlayer() {
        return teamPlayer;
    }

    public int getPreviousClanPlayerPointBalance() {
        return previousClanPlayerPointBalance;
    }

    public int getNewClanPlayerPointBalance() {
        return newClanPlayerPointBalance;
    }

    public int getWithdrawPointValue() {
        return withdrawPointValue;
    }

    public int getPreviousClanPointBalance() {
        return previousClanPointBalance;
    }

    public int getNewClanPointBalance() {
        return newClanPointBalance;
    }
}
