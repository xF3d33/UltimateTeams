package dev.xf3d3.ultimateteams.manager;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.TeamChatSpyToggledEvent;
import dev.xf3d3.ultimateteams.team.TeamPlayer;
import dev.xf3d3.ultimateteams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsersManager {

    private final Logger logger = UltimateTeams.getPlugin().getLogger();
    private final Map<UUID, TeamPlayer> usermap = new ConcurrentHashMap<>();
    private final FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();

    private static final String PLAYER_PLACEHOLDER = "%PLAYER%";
    private final UltimateTeams plugin;

    public UsersManager(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }


    public void getPlayer(Player player){
        UUID uuid = player.getUniqueId();
        String javaUUID = uuid.toString();
        String lastPlayerName = player.getName();

        if (!usermap.containsKey(uuid)) {
            plugin.runAsync(() -> plugin.getDatabase().getPlayer(uuid).ifPresentOrElse(
                    teamPlayer -> usermap.put(UUID.fromString(teamPlayer.getJavaUUID()), teamPlayer),
                    () -> {
                        TeamPlayer teamPlayer = new TeamPlayer(javaUUID, lastPlayerName, false, null, null);

                        plugin.getDatabase().createPlayer(teamPlayer);
                        usermap.put(uuid, teamPlayer);
                    }
            ));
        }
    }

    public void getBedrockPlayer(Player player){
        UUID uuid = player.getUniqueId();

        plugin.runAsync(() -> plugin.getDatabase().getPlayer(uuid).ifPresentOrElse(
                teamPlayer -> usermap.put(UUID.fromString(teamPlayer.getJavaUUID()), teamPlayer),
                () -> {
                    FloodgatePlayer floodgatePlayer = plugin.getFloodgateApi().getPlayer(uuid);
                    UUID bedrockPlayerUUID = floodgatePlayer.getJavaUniqueId();
                    String javaUUID = floodgatePlayer.getJavaUniqueId().toString();
                    String lastPlayerName = floodgatePlayer.getUsername();
                    TeamPlayer teamPlayer = new TeamPlayer(javaUUID, lastPlayerName, true, floodgatePlayer.getCorrectUniqueId().toString(), null);

                    plugin.getDatabase().createPlayer(teamPlayer);
                    usermap.put(bedrockPlayerUUID, teamPlayer);
                }
        ));

    }

    public TeamPlayer getTeamPlayerByBukkitOfflinePlayer(OfflinePlayer offlinePlayer){
        UUID uuid = offlinePlayer.getUniqueId();
        if (usermap.containsKey(uuid)){
            TeamPlayer teamPlayer = usermap.get(uuid);
            return teamPlayer;
        }else {
            logger.warning(Utils.Color(messagesConfig.getString("team-player-not-found-1")
                    .replace(PLAYER_PLACEHOLDER, offlinePlayer.getName())));
            logger.warning(Utils.Color(messagesConfig.getString("team-player-not-found-2")
                    .replace(PLAYER_PLACEHOLDER, offlinePlayer.getName())));
        }
        return null;
    }

    public Player getBukkitPlayerByName(String name){
        for (TeamPlayer teamPlayer : usermap.values()){
            if (teamPlayer.getLastPlayerName().equalsIgnoreCase(name)){
                return Bukkit.getPlayer(teamPlayer.getLastPlayerName());
            } else {
                logger.warning(Utils.Color(messagesConfig.getString("team-player-not-found-1")
                        .replace(PLAYER_PLACEHOLDER, name)));
                logger.warning(Utils.Color(messagesConfig.getString("team-player-not-found-2")
                        .replace(PLAYER_PLACEHOLDER, name)));
            }
        }
        return null;
    }

    public OfflinePlayer getBukkitOfflinePlayerByName(String name){
        for (TeamPlayer teamPlayer : usermap.values()){
            if (teamPlayer.getLastPlayerName().equalsIgnoreCase(name)){
                return Bukkit.getOfflinePlayer(UUID.fromString(teamPlayer.getJavaUUID()));
            } else {
                plugin.getDatabase().getPlayer(name).isPresent();

                logger.warning(Utils.Color(messagesConfig.getString("team-player-not-found-1")
                        .replace(PLAYER_PLACEHOLDER, name)));
                logger.warning(Utils.Color(messagesConfig.getString("team-player-not-found-2")
                        .replace(PLAYER_PLACEHOLDER, name)));
            }
        }
        return null;
    }

    public boolean hasPlayerNameChanged(Player player){
        for (TeamPlayer teamPlayer : usermap.values()){
            if (!player.getName().equals(teamPlayer.getLastPlayerName())){
                return true;
            }
        }
        return false;
    }

    public boolean hasBedrockPlayerJavaUUIDChanged(Player player) {
        final UUID uuid = player.getUniqueId();

        for (TeamPlayer teamPlayer : usermap.values()) {
            if (plugin.getSettings().FloodGateHook()) {
                if (plugin.getFloodgateApi() != null) {
                    FloodgatePlayer floodgatePlayer = plugin.getFloodgateApi().getPlayer(uuid);
                    if (!(floodgatePlayer.getJavaUniqueId().toString().equals(teamPlayer.getBedrockUUID()))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void updatePlayerName(Player player){
        UUID uuid = player.getUniqueId();
        String newPlayerName = player.getName();
        TeamPlayer teamPlayer = usermap.get(uuid);
        teamPlayer.setLastPlayerName(newPlayerName);

        plugin.runAsync(() -> plugin.getDatabase().updatePlayer(teamPlayer));
        usermap.replace(uuid, teamPlayer);
    }

    public void updateBedrockPlayerJavaUUID(Player player){
        UUID uuid = player.getUniqueId();
        TeamPlayer teamPlayer = usermap.get(uuid);
        if (plugin.getSettings().FloodGateHook()) {
            if (plugin.getFloodgateApi() != null) {
                FloodgatePlayer floodgatePlayer = plugin.getFloodgateApi().getPlayer(uuid);
                String newJavaUUID = floodgatePlayer.getJavaUniqueId().toString();
                teamPlayer.setJavaUUID(newJavaUUID);

                plugin.runAsync(() -> plugin.getDatabase().updatePlayer(teamPlayer));
                usermap.replace(uuid, teamPlayer);
            }
        }

    }

    public boolean toggleChatSpy(Player player){
        UUID uuid = player.getUniqueId();
        TeamPlayer teamPlayer = usermap.get(uuid);
        if (!teamPlayer.getCanChatSpy()){
            teamPlayer.setCanChatSpy(true);
            plugin.runAsync(() -> plugin.getDatabase().updatePlayer(teamPlayer));

            fireClanChatSpyToggledEvent(player, teamPlayer ,true);
            if (plugin.getSettings().debugModeEnabled()){
                plugin.log(Level.INFO, Utils.Color("&6UltimateTeams-Debug: &aFired ClanChatSpyToggledEvent"));
            }
            return true;
        } else {
            teamPlayer.setCanChatSpy(false);
            plugin.runAsync(() -> plugin.getDatabase().updatePlayer(teamPlayer));

            fireClanChatSpyToggledEvent(player, teamPlayer ,false);
            if (plugin.getSettings().debugModeEnabled()){
                plugin.log(Level.INFO, Utils.Color("&6UltimateTeams-Debug: &aFired ClanChatSpyToggledEvent"));
            }
            return false;
        }
    }

    public Set<UUID> getRawUsermapList(){
        return usermap.keySet();
    }

    public Map<UUID, TeamPlayer> getUsermap() {
        return usermap;
    }

    private void fireClanChatSpyToggledEvent(Player player, TeamPlayer teamPlayer, boolean chatSpyToggledState) {
        TeamChatSpyToggledEvent teamChatSpyToggledEvent = new TeamChatSpyToggledEvent(player, teamPlayer, chatSpyToggledState);
        Bukkit.getPluginManager().callEvent(teamChatSpyToggledEvent);
    }
}
