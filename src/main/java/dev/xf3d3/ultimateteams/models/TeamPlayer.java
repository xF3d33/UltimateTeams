package dev.xf3d3.ultimateteams.models;

import dev.xf3d3.ultimateteams.UltimateTeams;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class TeamPlayer {

    private final UltimateTeams plugin = UltimateTeams.getPlugin();

    @Getter @Setter private UUID javaUUID;
    @Getter @Setter private String lastPlayerName;
    @Getter @Setter private boolean isBedrockPlayer;
    @Getter @Setter private String bedrockUUID;
    @Getter @Setter private Preferences preferences;

    public TeamPlayer(@NotNull UUID UUID, @NotNull String playerName, @Nullable Boolean isBedrock, @Nullable String bedrockUuid, @Nullable Preferences preferences) {
        this.javaUUID = UUID;
        this.lastPlayerName = playerName;
        this.isBedrockPlayer = isBedrock != null ? isBedrock : false;
        this.bedrockUUID = bedrockUuid;
        this.preferences = preferences != null ? preferences : Preferences.getDefaults();
    }

    public final int getMaxWarps(final Player player, final int defaultMaxWarps, final boolean stackWarps) {
        final List<Integer> warps = plugin.getUtils().getNumberPermission(player, "ultimateteams.max_warps.");

        if (warps.isEmpty()) {
            return defaultMaxWarps;

        } else if (stackWarps) {
            return defaultMaxWarps + warps.stream().reduce(0, Integer::sum);

        } else {
            return warps.getFirst();
        }
    }

    public final int getMaxMembers(final Player player, final int defaultMaxMembers, final boolean stackMembers) {
        final List<Integer> membersPerm = plugin.getUtils().getNumberPermission(player, "ultimateteams.max_members.");

        if (membersPerm.isEmpty()) {
            return defaultMaxMembers;

        } else if (stackMembers) {
            return defaultMaxMembers + membersPerm.stream().reduce(0, Integer::sum);

        } else {
            return membersPerm.getFirst();
        }
    }

}
