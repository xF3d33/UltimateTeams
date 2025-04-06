package dev.xf3d3.ultimateteams.utils.LuckPermsContexts;

import dev.xf3d3.ultimateteams.UltimateTeams;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class IsInTeamContext implements ContextCalculator<Player> {
    private final UltimateTeams plugin;
    private static final String KEY = "is-in-team";

    public IsInTeamContext(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    @Override
    public void calculate(@NotNull Player target, @NotNull ContextConsumer consumer) {
        boolean isInTeam = false;

        if (plugin.getTeamStorageUtil().isTeamOwner(target)) {
            isInTeam = true;
        } else if (plugin.getTeamStorageUtil().findTeamByPlayer(target) != null) {
            isInTeam = true;
        }

        consumer.accept(KEY, String.valueOf(isInTeam));
    }

    @NotNull
    @Override
    public ContextSet estimatePotentialContexts() {
        return ImmutableContextSet.builder()
                .add(KEY, "true")
                .add(KEY, "false")
                .build();
    }
}
