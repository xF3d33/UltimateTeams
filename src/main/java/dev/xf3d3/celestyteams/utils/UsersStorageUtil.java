package dev.xf3d3.celestyteams.utils;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.api.ClanChatSpyToggledEvent;
import dev.xf3d3.celestyteams.models.TeamPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class UsersStorageUtil {

    private final Logger logger = CelestyTeams.getPlugin().getLogger();

    private final Map<UUID, TeamPlayer> usermap = new HashMap<>();

    private final FileConfiguration teamsConfig = CelestyTeams.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String PLAYER_PLACEHOLDER = "%PLAYER%";
    private final CelestyTeams plugin;

    public UsersStorageUtil(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
    }

    public void loadUsers() {
        usermap.clear();

        /*plugin.getDatabase().getAllPlayers().forEach(teamPlayer -> {
            UUID uuid = UUID.fromString(teamPlayer.getJavaUUID());

            usermap.put(uuid, teamPlayer);
        });*/
    }

    public void getPlayer(Player player){
        UUID uuid = player.getUniqueId();
        String javaUUID = uuid.toString();
        String lastPlayerName = player.getName();

        plugin.runAsync(() -> plugin.getDatabase().getPlayer(uuid).ifPresentOrElse(
                teamPlayer -> usermap.put(UUID.fromString(teamPlayer.getJavaUUID()), teamPlayer),
                () -> {
                    TeamPlayer teamPlayer = new TeamPlayer(javaUUID, lastPlayerName, false, null, null);

                    plugin.getDatabase().createPlayer(teamPlayer);
                    usermap.put(uuid, teamPlayer);
                }
        ));
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

    public TeamPlayer getTeamPlayerByBukkitPlayer(Player player){
        UUID uuid = player.getUniqueId();
        if (usermap.containsKey(uuid)){
            TeamPlayer teamPlayer = usermap.get(uuid);
            return teamPlayer;
        }else {
            logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("team-player-not-found-1")
                    .replace(PLAYER_PLACEHOLDER, player.getName())));
            logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("team-player-not-found-2")
                    .replace(PLAYER_PLACEHOLDER, player.getName())));
        }
        return null;
    }

    public TeamPlayer getTeamPlayerByBukkitOfflinePlayer(OfflinePlayer offlinePlayer){
        UUID uuid = offlinePlayer.getUniqueId();
        if (usermap.containsKey(uuid)){
            TeamPlayer teamPlayer = usermap.get(uuid);
            return teamPlayer;
        }else {
            logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("team-player-not-found-1")
                    .replace(PLAYER_PLACEHOLDER, offlinePlayer.getName())));
            logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("team-player-not-found-2")
                    .replace(PLAYER_PLACEHOLDER, offlinePlayer.getName())));
        }
        return null;
    }

    public Player getBukkitPlayerByName(String name){
        for (TeamPlayer teamPlayer : usermap.values()){
            if (teamPlayer.getLastPlayerName().equalsIgnoreCase(name)){
                return Bukkit.getPlayer(teamPlayer.getLastPlayerName());
            } else {
                logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("team-player-not-found-1")
                        .replace(PLAYER_PLACEHOLDER, name)));
                logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("team-player-not-found-2")
                        .replace(PLAYER_PLACEHOLDER, name)));
            }
        }
        return null;
    }

    public OfflinePlayer getBukkitOfflinePlayerByName(String name){
        for (TeamPlayer teamPlayer : usermap.values()){
            if (teamPlayer.getLastPlayerName().equalsIgnoreCase(name)){
                return Bukkit.getOfflinePlayer(UUID.fromString(teamPlayer.getJavaUUID()));
            }else {
                logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("team-player-not-found-1")
                        .replace(PLAYER_PLACEHOLDER, name)));
                logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("team-player-not-found-2")
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

    public boolean hasBedrockPlayerJavaUUIDChanged(Player player){
        UUID uuid = player.getUniqueId();
        for (TeamPlayer teamPlayer : usermap.values()){
            if (plugin.getFloodgateApi() != null){
                FloodgatePlayer floodgatePlayer = plugin.getFloodgateApi().getPlayer(uuid);
                if (!(floodgatePlayer.getJavaUniqueId().toString().equals(teamPlayer.getBedrockUUID()))){
                    return true;
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
        if (plugin.getFloodgateApi() != null){
            FloodgatePlayer floodgatePlayer = plugin.getFloodgateApi().getPlayer(uuid);
            String newJavaUUID = floodgatePlayer.getJavaUniqueId().toString();
            teamPlayer.setJavaUUID(newJavaUUID);

            plugin.runAsync(() -> plugin.getDatabase().updatePlayer(teamPlayer));
            usermap.replace(uuid, teamPlayer);
        }

    }

    public boolean toggleChatSpy(Player player){
        UUID uuid = player.getUniqueId();
        TeamPlayer teamPlayer = usermap.get(uuid);
        if (!teamPlayer.getCanChatSpy()){
            teamPlayer.setCanChatSpy(true);
            plugin.runAsync(() -> plugin.getDatabase().updatePlayer(teamPlayer));

            fireClanChatSpyToggledEvent(player, teamPlayer ,true);
            if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanChatSpyToggledEvent"));
            }
            return true;
        } else {
            teamPlayer.setCanChatSpy(false);
            plugin.runAsync(() -> plugin.getDatabase().updatePlayer(teamPlayer));

            fireClanChatSpyToggledEvent(player, teamPlayer ,false);
            if (teamsConfig.getBoolean("general.developer-debug-mode.enabled")){
                logger.info(ColorUtils.translateColorCodes("&6CelestyTeams-Debug: &aFired ClanChatSpyToggledEvent"));
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
        ClanChatSpyToggledEvent teamChatSpyToggledEvent = new ClanChatSpyToggledEvent(player, teamPlayer, chatSpyToggledState);
        Bukkit.getPluginManager().callEvent(teamChatSpyToggledEvent);
    }
}
