package dev.xf3d3.ultimateteams.utils;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.expansions.HuskHomesAPIHook;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private final UltimateTeams plugin;

    public Utils(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void teleportPlayer(@NotNull Player player, @NotNull Location location) {
        if (Bukkit.getPluginManager().getPlugin("HuskHomes") != null && plugin.getConfig().getBoolean("use-huskhomes")) {
            plugin.getHuskHomesHook().teleportPlayer(player, location);
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
}
