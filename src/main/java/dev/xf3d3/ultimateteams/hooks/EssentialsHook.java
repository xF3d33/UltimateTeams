package dev.xf3d3.ultimateteams.hooks;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.logging.Level;

/**
 * Hook for EssentialsX integration
 * Provides team information through Bukkit's scoreboard system
 * which EssentialsX can read via {TEAMNAME}, {TEAMPREFIX}, and {TEAMSUFFIX} placeholders
 */
public class EssentialsHook {
    private final UltimateTeams plugin;
    private final Scoreboard scoreboard;
    
    public EssentialsHook(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        
        plugin.log(Level.INFO, "EssentialsX hook initialized - Team data will be available via Bukkit scoreboard");
    }
    
    /**
     * Update a player's scoreboard team based on their UltimateTeams team
     * This allows EssentialsX to read {TEAMNAME}, {TEAMPREFIX}, and {TEAMSUFFIX}
     * 
     * @param player The player to update
     */
    public void updatePlayerScoreboardTeam(@NotNull Player player) {
        Optional<Team> teamOpt = plugin.getTeamStorageUtil().findTeamByMember(player.getUniqueId());
        
        if (teamOpt.isPresent()) {
            Team team = teamOpt.get();
            
            // Get or create scoreboard team
            String teamName = sanitizeTeamName(team.getName());
            org.bukkit.scoreboard.Team scoreboardTeam = scoreboard.getTeam(teamName);
            
            if (scoreboardTeam == null) {
                scoreboardTeam = scoreboard.registerNewTeam(teamName);
            }
            
            // Update team display name (this becomes {TEAMNAME})
            String displayName = Utils.Color(team.getName());
            if (displayName.length() > 128) {
                displayName = displayName.substring(0, 128);
            }
            scoreboardTeam.setDisplayName(displayName);
            
            // Update prefix (this becomes {TEAMPREFIX})
            String prefix = team.getPrefix();
            if (prefix != null && !prefix.isEmpty()) {
                String coloredPrefix = Utils.Color(prefix);
                // Bukkit scoreboard prefix has a 64 character limit
                if (coloredPrefix.length() > 64) {
                    coloredPrefix = coloredPrefix.substring(0, 64);
                }
                scoreboardTeam.setPrefix(coloredPrefix);
            } else {
                scoreboardTeam.setPrefix("");
            }
            
            // Suffix can be used for additional team info (this becomes {TEAMSUFFIX})
            // You can customize this to show team role, member count, etc.
            String suffix = "";
            if (plugin.getSettings().isEssentialsSuffixEnabled()) {
                // Example: Show member count as suffix
                suffix = Utils.Color(" &7[" + team.getMembers().size() + "]");
                if (suffix.length() > 64) {
                    suffix = suffix.substring(0, 64);
                }
            }
            scoreboardTeam.setSuffix(suffix);
            
            // Add player to the scoreboard team
            if (!scoreboardTeam.hasEntry(player.getName())) {
                scoreboardTeam.addEntry(player.getName());
            }
            
            if (plugin.getSettings().debugModeEnabled()) {
                plugin.log(Level.INFO, 
                    "Updated scoreboard team for " + player.getName() + 
                    " - Team: " + teamName + 
                    ", Prefix: " + prefix);
            }
        } else {
            // Player is not in a team, remove from any scoreboard team
            removePlayerFromScoreboardTeam(player);
        }
    }
    
    /**
     * Remove a player from their scoreboard team
     * 
     * @param player The player to remove
     */
    public void removePlayerFromScoreboardTeam(@NotNull Player player) {
        // Find and remove player from any team they might be in
        for (org.bukkit.scoreboard.Team team : scoreboard.getTeams()) {
            if (team.hasEntry(player.getName())) {
                team.removeEntry(player.getName());
                
                if (plugin.getSettings().debugModeEnabled()) {
                    plugin.log(Level.INFO, 
                        "Removed " + player.getName() + " from scoreboard team " + team.getName());
                }
            }
        }
    }
    
    /**
     * Update all online players' scoreboard teams
     * Useful for when teams are modified or plugin is reloaded
     */
    public void updateAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerScoreboardTeam(player);
        }
        
        plugin.log(Level.INFO, "Updated scoreboard teams for all online players");
    }
    
    /**
     * Update all members of a specific team
     * 
     * @param team The team whose members should be updated
     */
    public void updateTeamMembers(@NotNull Team team) {
        for (java.util.UUID uuid : team.getMembers().keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                updatePlayerScoreboardTeam(player);
            }
        }
        
        if (plugin.getSettings().debugModeEnabled()) {
            plugin.log(Level.INFO, "Updated scoreboard teams for all members of team " + team.getName());
        }
    }
    
    /**
     * Clean up scoreboard team when an UltimateTeams team is deleted
     * 
     * @param team The team that was deleted
     */
    public void cleanupTeam(@NotNull Team team) {
        String teamName = sanitizeTeamName(team.getName());
        org.bukkit.scoreboard.Team scoreboardTeam = scoreboard.getTeam(teamName);
        
        if (scoreboardTeam != null) {
            scoreboardTeam.unregister();
            
            if (plugin.getSettings().debugModeEnabled()) {
                plugin.log(Level.INFO, "Unregistered scoreboard team " + teamName);
            }
        }
    }
    
    /**
     * Sanitize team name to be compatible with Bukkit scoreboard team names
     * Scoreboard team names must be 16 characters or less and contain only valid characters
     * 
     * @param name The original team name
     * @return Sanitized team name
     */
    private String sanitizeTeamName(@NotNull String name) {
        // Remove color codes for team name
        String sanitized = Utils.removeColors(name);
        
        // Remove invalid characters (only alphanumeric, underscore, and hyphen allowed)
        sanitized = sanitized.replaceAll("[^a-zA-Z0-9_-]", "");
        
        // Ensure it's not empty
        if (sanitized.isEmpty()) {
            sanitized = "team";
        }
        
        // Limit to 16 characters (Bukkit scoreboard team name limit)
        if (sanitized.length() > 16) {
            sanitized = sanitized.substring(0, 16);
        }
        
        return sanitized;
    }
}
