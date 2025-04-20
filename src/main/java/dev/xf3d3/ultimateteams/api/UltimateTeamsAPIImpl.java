package dev.xf3d3.ultimateteams.api;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;

import java.util.Collection;

public class UltimateTeamsAPIImpl implements UltimateTeamsAPI {

    private final UltimateTeams plugin;

    public UltimateTeamsAPIImpl(UltimateTeams plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isLoaded() {
        return plugin.isLoaded();
    }

    @Override
    public Collection<Team> getAllTeams() {
        return plugin.getTeamStorageUtil().getTeamsList();
    }
}
