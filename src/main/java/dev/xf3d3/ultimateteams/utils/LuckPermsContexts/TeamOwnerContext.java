package dev.xf3d3.ultimateteams.utils.LuckPermsContexts;

import dev.xf3d3.ultimateteams.UltimateTeams;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamOwnerContext implements ContextCalculator<Player> {
    private final UltimateTeams plugin;
    private static final String KEY = "is-team-owner";

    public TeamOwnerContext(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    @Override
    public void calculate(@NotNull Player target, @NotNull ContextConsumer consumer) {

        consumer.accept(KEY, String.valueOf(plugin.getTeamStorageUtil().isTeamOwner(target)));
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
