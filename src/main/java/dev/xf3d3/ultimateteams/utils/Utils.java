package dev.xf3d3.ultimateteams.utils;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Position;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
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
    private final FileConfiguration messagesConfig;

    public Utils(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
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
            player.sendMessage(Utils.Color(messagesConfig.getString("team-home-cooldown-start").replaceAll("%SECONDS%", String.valueOf(plugin.getSettings().getTeamHomeTpDelay()))));

            plugin.runLater(() -> {
                // Run on the appropriate thread scheduler for this platform
                plugin.getScheduler().teleportAsync(player, location, PlayerTeleportEvent.TeleportCause.PLUGIN);

                player.sendMessage(Utils.Color(messagesConfig.getString("successfully-teleported-to-home")));
            }, plugin.getSettings().getTeamHomeTpDelay());

            return;
        }

        // if tp is Warp and cooldown is enabled handle teleport
        if ((plugin.getSettings().getTeamWarpTpDelay() > 0) && teleportType.equals(TeleportType.WARP)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-warp-cooldown-start").replaceAll("%SECONDS%", String.valueOf(plugin.getSettings().getTeamWarpTpDelay()))));

            plugin.runLater(() -> {
                // Run on the appropriate thread scheduler for this platform
                plugin.getScheduler().teleportAsync(player, location, PlayerTeleportEvent.TeleportCause.PLUGIN);
                player.sendMessage(Utils.Color(messagesConfig.getString("team-warp-teleported-successful").replaceAll("%WARP_NAME%", String.valueOf(warpName))));

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
        Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6})");
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();

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


    public enum TeleportType {
        WARP,
        HOME,
        SERVER
    }
}
