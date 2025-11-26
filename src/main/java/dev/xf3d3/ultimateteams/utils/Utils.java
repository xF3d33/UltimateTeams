package dev.xf3d3.ultimateteams.utils;

import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Position;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {
    private final UltimateTeams plugin;
    private static final Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6})");

    public Utils(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void teleportPlayer(@NotNull Player player, @NotNull Location location, @Nullable String server, @NotNull TeleportType teleportType, @Nullable String warpName) {
        final String targetServer = server != null ? server : plugin.getSettings().getServerName();

        // Execute tp immediately if HuskHome is in use
        if (Bukkit.getPluginManager().getPlugin("HuskHomes") != null && plugin.getSettings().HuskHomesHook()) {
            plugin.getHuskHomesHook().teleportPlayer(player, location, targetServer);

            return;
        }

        if (plugin.getSettings().isEnableCrossServer() && !targetServer.equals(plugin.getSettings().getServerName())) {
            plugin.getUsersStorageUtil().getPlayer(player.getUniqueId()).thenAccept(teamPlayer -> {
                teamPlayer.getPreferences().setTeleportTarget(
                        Position.at(location.getX(), location.getY(), location.getZ(), location.getWorld().getName(), location.getYaw(), location.getPitch())
                );

                plugin.getUsersStorageUtil().updatePlayer(teamPlayer);
                plugin.getMessageBroker().ifPresent(broker -> broker.changeServer(player, targetServer));
            });

            return;
        }

        // if tp is Home and cooldown is enabled handle teleport
        if ((plugin.getSettings().getTeamHomeTpDelay() > 0) && teleportType.equals(TeleportType.HOME)) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamHomeCooldownStart().replaceAll("%SECONDS%", String.valueOf(plugin.getSettings().getTeamHomeTpDelay()))));

            plugin.runLater(() -> {
                // Run on the appropriate thread scheduler for this platform
                plugin.getScheduler().teleportAsync(player, location, PlayerTeleportEvent.TeleportCause.PLUGIN);

                player.sendMessage(MineDown.parse(plugin.getMessages().getSuccessfullyTeleportedToHome()));
            }, plugin.getSettings().getTeamHomeTpDelay());

            return;
        }

        // if tp is Warp and cooldown is enabled handle teleport
        if ((plugin.getSettings().getTeamWarpTpDelay() > 0) && teleportType.equals(TeleportType.WARP)) {
            player.sendMessage(MineDown.parse(plugin.getMessages().getTeamWarpCooldownStart().replaceAll("%SECONDS%", String.valueOf(plugin.getSettings().getTeamWarpTpDelay()))));

            plugin.runLater(() -> {
                // Run on the appropriate thread scheduler for this platform
                plugin.getScheduler().teleportAsync(player, location, PlayerTeleportEvent.TeleportCause.PLUGIN);
                player.sendMessage(MineDown.parse(plugin.getMessages().getTeamWarpTeleportedSuccessful().replaceAll("%WARP_NAME%", String.valueOf(warpName))));

            }, plugin.getSettings().getTeamWarpTpDelay());

            return;
        }

        // Run on the appropriate thread scheduler for this platform
        plugin.getScheduler().teleportAsync(player, location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public List<Integer> getNumberPermission(@NotNull Player player, @NotNull String permissionPrefix) {
        final Map<String, Boolean> playerPermissions = player.getEffectivePermissions()
                .stream()
                .collect(Collectors.toMap(
                        PermissionAttachmentInfo::getPermission,
                        PermissionAttachmentInfo::getValue, (a, b) -> b
                ));

        return playerPermissions.entrySet().stream()
                .filter(Map.Entry::getValue)
                .filter(permission -> permission.getKey().startsWith(permissionPrefix))
                .filter(permission -> {
                    try {
                        Integer.parseInt(permission.getKey().substring(permissionPrefix.length()));

                    } catch (final NumberFormatException e) {
                        return false;
                    }

                    return true;
                })
                .map(permission -> Integer.parseInt(permission.getKey().substring(permissionPrefix.length())))
                .sorted(Collections.reverseOrder())
                .toList();
    }

    /**
     * @param message The string of text to apply color/effects to
     * @return Returns a string of text with color/effects applied
     */
    public static String Color(String message) {
        if (message == null) return null;


        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            String hex = matcher.group(1); // Now safe — group(1) is defined

            // Build §x§R§R§G§G§B§B color code
            StringBuilder colorCode = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                colorCode.append('§').append(c);
            }

            matcher.appendReplacement(buffer, colorCode.toString());
        }
        matcher.appendTail(buffer);

        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    /**
     * @param message The string of text that may contain color/effects
     * @return Returns a string of text with all color/effects removed
     */
    public static String removeColors(String message) {
        if (message == null) return null;

        // Remove Minecraft-style color codes using § (e.g., §a, §l, §x§R§R§G§G§B§B)
        message = message.replaceAll("§[0-9A-FK-ORa-fk-or]", "");

        // Remove full hex color sequences like §x§R§R§G§G§B§B (14 characters)
        message = message.replaceAll("§x(§[0-9A-Fa-f]){6}", "");

        // Remove alternate color codes (e.g., &a, &l)
        message = message.replaceAll("&[0-9A-FK-ORa-fk-or]", "");

        // literal hex strings like #A1B2C3
        message = message.replaceAll("#[A-Fa-f0-9]{6}", "");

        return message;
    }



    public enum TeleportType {
        WARP,
        HOME,
        SERVER
    }
}
