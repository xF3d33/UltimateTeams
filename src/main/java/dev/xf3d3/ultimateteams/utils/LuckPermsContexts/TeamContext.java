package dev.xf3d3.ultimateteams.utils.LuckPermsContexts;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import net.luckperms.api.context.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamContext implements ContextCalculator<Player> {
    private final UltimateTeams plugin;
    private static final String KEY = "team-name";

    public TeamContext(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    @Override
    public void calculate(@NotNull Player target, @NotNull ContextConsumer consumer) {
        String teamName = "Null";

        Team team;
        if (plugin.getTeamStorageUtil().findTeamByOwner(target) != null) {
            team = plugin.getTeamStorageUtil().findTeamByOwner(target);
        } else {
            team = plugin.getTeamStorageUtil().findTeamByPlayer(target);
        }

        if (team != null) teamName = team.getTeamFinalName();

        consumer.accept(KEY, String.valueOf(teamName));
    }

    @NotNull
    @Override
    public ContextSet estimatePotentialContexts() {
        ImmutableContextSet.Builder teams = ImmutableContextSet.builder();

        for (String teamName : plugin.getTeamStorageUtil().getTeamsListNames()) {
            teams.add(KEY, teamName);
        }

        
        return teams.build();
    }
}
