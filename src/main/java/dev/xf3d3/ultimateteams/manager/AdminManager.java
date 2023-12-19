package dev.xf3d3.ultimateteams.manager;

import dev.xf3d3.ultimateteams.UltimateTeams;
import org.jetbrains.annotations.NotNull;

public class AdminManager extends Manager {
    private final UltimateTeams plugin;

    protected AdminManager(@NotNull UltimateTeams plugin) {
        super(plugin);
        this.plugin = plugin;
    }
}
