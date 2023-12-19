package dev.xf3d3.ultimateteams.utils;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.team.Team;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private final UltimateTeams plugin;

    public Utils(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void teleportPlayer(@NotNull Player player, @NotNull Location location) {
        if (Bukkit.getPluginManager().getPlugin("HuskHomes") != null && plugin.getSettings().HuskHomesHook()) {
            plugin.getHuskHomesHook().teleportPlayer(player, location);
            return;
        }

        // Run on the appropriate thread scheduler for this platform
        plugin.getScheduler().entitySpecificScheduler(player).run(
                () -> PaperLib.teleportAsync(player, location, PlayerTeleportEvent.TeleportCause.PLUGIN),
                () -> plugin.log(Level.WARNING, "User offline when teleporting: " + player.getName())
        );
    }

    public void sendMessageToTeamPlayers(@NotNull Team team, @NotNull String messagePath, @Nullable Player player) {
        //String joinMessage = Utils.Color(plugin.msgFileManager.getMessagesConfig().getString(messagePath)).replace("%TEAM%", team.getName());
        //sender.sendMessage(joinMessage);

        // Send message to team owner
        Player owner = Bukkit.getPlayer(UUID.fromString(team.getTeamOwner()));

        if (owner != null) {
            owner.sendMessage(Utils.Color(plugin.msgFileManager.getMessagesConfig().getString(messagePath)
                    .replace("%PLAYER%", player.getName())
                    .replace("%TEAM%", Utils.Color(team.getName()))));
        }

        // Send message to team players
        if (plugin.getSettings().teamJoinAnnounce()) {
            for (String playerUUID : team.getTeamMembers()) {
                final Player teamPlayer = Bukkit.getPlayer(UUID.fromString(playerUUID));

                if (teamPlayer != null) {
                    teamPlayer.sendMessage(Utils.Color(plugin.msgFileManager.getMessagesConfig().getString(messagePath)
                            .replace("%PLAYER%", player.getName())
                            .replace("%TEAM%", Utils.Color(team.getName()))));
                }
            }
        }
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
