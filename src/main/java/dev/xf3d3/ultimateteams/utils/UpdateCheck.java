package dev.xf3d3.ultimateteams.utils;

import dev.xf3d3.ultimateteams.UltimateTeams;
import net.william278.desertwell.util.UpdateChecker;
import net.william278.desertwell.util.Version;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class UpdateCheck {
    private final UltimateTeams plugin;

    public UpdateCheck(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    private Version getVersion() {
        return Version.fromString(plugin.getDescription().getVersion());
    }

    @NotNull
    private UpdateChecker getUpdateChecker() {
        return UpdateChecker.builder()
                .currentVersion(getVersion())
                .endpoint(UpdateChecker.Endpoint.MODRINTH)
                .resource("O5OQXCl8")
                .build();
    }

    public void checkForUpdates() {
        if (plugin.getSettings().doCheckForUpdates()) {
            getUpdateChecker().check().thenAccept(checked -> {
                if (!checked.isUpToDate()) {
                    plugin.log(Level.WARNING, "A new version of UltimateTeams is available: v"
                            + checked.getLatestVersion() + " (running v" + getVersion() + ")");
                }
            });
        }
    }
}
