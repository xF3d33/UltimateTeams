package dev.xf3d3.ultimateteams.models;

import dev.xf3d3.ultimateteams.UltimateTeams;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

@SuppressWarnings("unused")
public class TeamPlayer {

    private final UltimateTeams plugin = UltimateTeams.getPlugin();

    @Getter @Setter private String javaUUID;
    @Getter @Setter private String lastPlayerName;
    @Getter @Setter private boolean isBedrockPlayer;
    @Getter @Setter private String bedrockUUID;
    @Getter @Setter private boolean canChatSpy;

    public TeamPlayer(@NotNull String UUID, @NotNull String playerName, @Nullable Boolean isBedrock, @Nullable String bedrockUuid, @Nullable Boolean canSpy) {
        javaUUID = UUID;
        lastPlayerName = playerName;
        isBedrockPlayer = isBedrock != null ? isBedrock : false;
        bedrockUUID = bedrockUuid;
        canChatSpy = canSpy != null ? canSpy : false;
    }

    public final int getMaxWarps(final Player player, final int defaultMaxWarps, final boolean stackWarps) {
        final List<Integer> warps = plugin.getUtils().getNumberPermission(player, "ultimateteams.max_warps.");

        if (warps.isEmpty()) {
            if (plugin.getSettings().debugModeEnabled())
                plugin.log(Level.INFO, "Max Warps (default - permission empty)" + defaultMaxWarps);

            return defaultMaxWarps;
        }
        if (stackWarps) {
            if (plugin.getSettings().debugModeEnabled())
                plugin.log(Level.INFO, "Max Warps (stacked)" + defaultMaxWarps + warps.stream().reduce(0, Integer::sum));

            return defaultMaxWarps + warps.stream().reduce(0, Integer::sum);
        } else {
            if (plugin.getSettings().debugModeEnabled())
                plugin.log(Level.INFO, "Max Warps (permission)" + warps.get(0));

            return warps.get(0);
        }
    }

    public final int getMaxMembers(final Player player, final int defaultMaxMembers, final boolean stackMembers) {
        final List<Integer> membersPerm = plugin.getUtils().getNumberPermission(player, "ultimateteams.max_members.");

        if (membersPerm.isEmpty()) {
            if (plugin.getSettings().debugModeEnabled())
                plugin.log(Level.INFO, "Max Members (default - permission empty)" + defaultMaxMembers);

            return defaultMaxMembers;
        }
        if (stackMembers) {
            if (plugin.getSettings().debugModeEnabled())
                plugin.log(Level.INFO, "Max Members (stacked)" + defaultMaxMembers + membersPerm.stream().reduce(0, Integer::sum));

            return defaultMaxMembers + membersPerm.stream().reduce(0, Integer::sum);
        } else {
            if (plugin.getSettings().debugModeEnabled())
                plugin.log(Level.INFO, "Max Members (permission)" + membersPerm.get(0));

            return membersPerm.get(0);
        }
    }

}
