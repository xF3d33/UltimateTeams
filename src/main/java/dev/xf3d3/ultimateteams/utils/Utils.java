package dev.xf3d3.ultimateteams.utils;

import dev.xf3d3.ultimateteams.UltimateTeams;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private final UltimateTeams plugin;
    private final FileConfiguration messagesConfig;

    public Utils(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.messagesConfig = plugin.msgFileManager.getMessagesConfig();
    }

    public void teleportPlayer(@NotNull Player player, @NotNull Location location, @NotNull TeleportType teleportType, @Nullable String warpName) {
        // Execute tp immediately if HuskHome is in use
        if (Bukkit.getPluginManager().getPlugin("HuskHomes") != null && plugin.getSettings().HuskHomesHook()) {
            plugin.getHuskHomesHook().teleportPlayer(player, location);
            return;
        }

        // if tp is Home and cooldown is enabled handle teleport
        if ((plugin.getSettings().getTeamHomeTpDelay() > 0) && teleportType.equals(TeleportType.HOME)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-home-cooldown-start").replaceAll("%SECONDS%", String.valueOf(plugin.getSettings().getTeamHomeTpDelay()))));

            plugin.runLater(() -> {
                // Run on the appropriate thread scheduler for this platform
                plugin.getScheduler().entitySpecificScheduler(player).run(
                        () -> PaperLib.teleportAsync(player, location, PlayerTeleportEvent.TeleportCause.PLUGIN),
                        () -> plugin.log(Level.WARNING, "User offline when teleporting: " + player.getName())
                );

                player.sendMessage(Utils.Color(messagesConfig.getString("successfully-teleported-to-home")));
            }, plugin.getSettings().getTeamHomeTpDelay());

            return;
        }

        // if tp is Warp and cooldown is enabled handle teleport
        if ((plugin.getSettings().getTeamWarpTpDelay() > 0) && teleportType.equals(TeleportType.WARP)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-warp-cooldown-start").replaceAll("%SECONDS%", String.valueOf(plugin.getSettings().getTeamWarpTpDelay()))));

            plugin.runLater(() -> {
                // Run on the appropriate thread scheduler for this platform
                plugin.getScheduler().entitySpecificScheduler(player).run(
                        () -> PaperLib.teleportAsync(player, location, PlayerTeleportEvent.TeleportCause.PLUGIN),
                        () -> plugin.log(Level.WARNING, "User offline when teleporting: " + player.getName())
                );
                player.sendMessage(Utils.Color(messagesConfig.getString("team-warp-teleported-successful").replaceAll("%WARP_NAME%", String.valueOf(warpName))));

            }, plugin.getSettings().getTeamWarpTpDelay());

            return;
        }

        // Run on the appropriate thread scheduler for this platform
        plugin.getScheduler().entitySpecificScheduler(player).run(
                () -> PaperLib.teleportAsync(player, location, PlayerTeleportEvent.TeleportCause.PLUGIN),
                () -> plugin.log(Level.WARNING, "User offline when teleporting: " + player.getName())
        );
    }

    /**
     * @param text The string of text to apply color/effects to
     * @return Returns a string of text with color/effects applied
     */
    public static String Color(String text) {

        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher match = pattern.matcher(text);

        while(match.find()) {
            String color = text.substring(match.start(),match.end());
            text = text.replace(color, net.md_5.bungee.api.ChatColor.of(color)+"");

            match = pattern.matcher(text);
        }

        text = ChatColor.translateAlternateColorCodes('&', text);
        return text;
    }

    public enum TeleportType {
        WARP,
        HOME
    }
}
