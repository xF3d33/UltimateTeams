package dev.xf3d3.ultimateteams.utils.LuckPermsContexts;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TeamContext implements ContextCalculator<Player> {
    private final UltimateTeams plugin;
    private static final String KEY = "team-name";

    public TeamContext(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    @Override
    public void calculate(@NotNull Player target, @NotNull ContextConsumer consumer) {
        String teamName = "null";

        Optional<Team> optionalTeam = plugin.getTeamStorageUtil().findTeamByMember(target.getUniqueId());

        if (optionalTeam.isPresent()) {
            teamName = optionalTeam.get().getName();
        }

        consumer.accept(KEY, String.valueOf(teamName));
    }

    @NotNull
    @Override
    public ContextSet estimatePotentialContexts() {
        ImmutableContextSet.Builder teams = ImmutableContextSet.builder();

        for (String teamName : plugin.getTeamStorageUtil().getTeamsName()) {
            teams.add(KEY, teamName);
        }

        
        return teams.build();
    }
}
