package dev.xf3d3.ultimateteams.utils;

import com.google.common.collect.Maps;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Preferences;
import dev.xf3d3.ultimateteams.models.TeamPlayer;
import dev.xf3d3.ultimateteams.models.User;
import dev.xf3d3.ultimateteams.network.Broker;
import dev.xf3d3.ultimateteams.network.Message;
import dev.xf3d3.ultimateteams.network.Payload;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

public class UsersStorage {

    private final Map<UUID, TeamPlayer> usermap = new ConcurrentHashMap<>();
    // Track in-flight player loading operations to prevent duplicate creation
    private final ConcurrentHashMap<UUID, CompletableFuture<TeamPlayer>> loadingPlayers = new ConcurrentHashMap<>();
    private final UltimateTeams plugin;

    @Getter
    private final Map<String, List<User>> globalUserList = Maps.newConcurrentMap();
    @Getter
    private final ConcurrentMap<UUID, Player> onlineUserMap = Maps.newConcurrentMap();
    @Getter
    private final ConcurrentMap<UUID, ChatType> chatPlayers = Maps.newConcurrentMap();


    public UsersStorage(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public List<User> getUserList() {
        return Stream.concat(
                globalUserList.values().stream().flatMap(Collection::stream),
                onlineUserMap.values().stream().map(player -> User.of(player.getUniqueId(), player.getName()))
        ).distinct().sorted().toList();
    }

    public Collection<Player> getOnlineUsers() {
        return getOnlineUserMap().values();
    }

    public Optional<Player> findOnlinePlayer(@NotNull String username) {
        return onlineUserMap.values().stream()
                .filter(online -> online.getName().equalsIgnoreCase(username))
                .findFirst();
    }

    public void setUserList(@NotNull String server, @NotNull List<User> players) {
        globalUserList.values().forEach(list -> {
            list.removeAll(players);
            list.removeAll(onlineUserMap.values().stream().map(player -> User.of(player.getUniqueId(), player.getName())).toList());
        });
        globalUserList.put(server, players);
    }

    // Synchronize the global player list
    public void syncGlobalUserList(@NotNull Player user, @NotNull List<User> onlineUsers) {
        final Optional<Broker> optionalBroker = plugin.getMessageBroker();
        if (optionalBroker.isEmpty()) {
            return;
        }
        final Broker broker = optionalBroker.get();

        // Send this server's player list to all servers
        Message.builder()
                .type(Message.Type.UPDATE_USER_LIST)
                .target(Message.TARGET_ALL, Message.TargetType.SERVER)
                .payload(Payload.userList(onlineUsers))
                .build().send(broker, user);

        // Clear cached global player lists and request updated lists from all servers
        if (this.onlineUserMap.size() == 1) {
            this.globalUserList.clear();
            Message.builder()
                    .type(Message.Type.REQUEST_USER_LIST)
                    .target(Message.TARGET_ALL, Message.TargetType.SERVER)
                    .build().send(broker, user);
        }
    }

    public void removePlayer(UUID uuid) {
        usermap.remove(uuid);
    }

    public CompletableFuture<TeamPlayer> getPlayer(UUID uuid) {
        // Fast path: player already loaded
        TeamPlayer existing = usermap.get(uuid);
        if (existing != null) {
            return CompletableFuture.completedFuture(existing);
        }

        // Check if player is already being loaded by another thread
        CompletableFuture<TeamPlayer> inFlight = loadingPlayers.get(uuid);
        if (inFlight != null) {
            return inFlight;
        }

        // Create new loading operation atomically
        final String name = Bukkit.getOfflinePlayer(uuid).getName();
        CompletableFuture<TeamPlayer> future = plugin.supplyAsync(() -> {
            try {
                return plugin.getDatabase().getPlayer(uuid).map(teamPlayer -> {
                    usermap.put(teamPlayer.getJavaUUID(), teamPlayer);
                    return teamPlayer;
                }).orElseGet(() -> {
                    if (name == null) {
                        throw new IllegalArgumentException("Player " + uuid + " not found");
                    }

                    TeamPlayer teamPlayer = new TeamPlayer(uuid, name, false, null, Preferences.getDefaults());
                    plugin.getDatabase().createPlayer(teamPlayer);
                    usermap.put(uuid, teamPlayer);
                    return teamPlayer;
                });
            } finally {
                // Remove from loading map when done (success or failure)
                loadingPlayers.remove(uuid);
            }
        });

        // Store the future atomically - if another thread already started loading, use that instead
        CompletableFuture<TeamPlayer> existingFuture = loadingPlayers.putIfAbsent(uuid, future);
        if (existingFuture != null) {
            // Another thread started loading first, use that future instead
            return existingFuture;
        }

        return future;
    }

    public CompletableFuture<TeamPlayer> getBedrockPlayer(Player player){
        FloodgatePlayer floodgatePlayer = plugin.getFloodgateApi().getPlayer(player.getUniqueId());

        return plugin.supplyAsync(() -> plugin.getDatabase().getPlayer(floodgatePlayer.getJavaUniqueId()).map(
            teamPlayer -> usermap.put(teamPlayer.getJavaUUID(), teamPlayer))

            .orElseGet(() -> {
                UUID bedrockPlayerUUID = floodgatePlayer.getJavaUniqueId();
                String lastPlayerName = floodgatePlayer.getUsername();
                TeamPlayer teamPlayer = new TeamPlayer(floodgatePlayer.getJavaUniqueId(), lastPlayerName, true, floodgatePlayer.getCorrectUniqueId().toString(), null);

                plugin.getDatabase().createPlayer(teamPlayer);
                return usermap.put(bedrockPlayerUUID, teamPlayer);
        }));

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

    public void updatePlayerName(Player player) {
        UUID uuid = player.getUniqueId();
        String newPlayerName = player.getName();
        TeamPlayer teamPlayer = usermap.get(uuid);
        teamPlayer.setLastPlayerName(newPlayerName);

        plugin.runAsync(task -> plugin.getDatabase().updatePlayer(teamPlayer));
        usermap.replace(uuid, teamPlayer);
    }

    public void updatePlayer(TeamPlayer teamPlayer) {
        plugin.runAsync(task -> plugin.getDatabase().updatePlayer(teamPlayer));
        usermap.replace(teamPlayer.getJavaUUID(), teamPlayer);
    }

    public void updateBedrockPlayerJavaUUID(Player player){
        UUID uuid = player.getUniqueId();
        TeamPlayer teamPlayer = usermap.get(uuid);
        if (plugin.getSettings().FloodGateHook()) {
            if (plugin.getFloodgateApi() != null) {
                FloodgatePlayer floodgatePlayer = plugin.getFloodgateApi().getPlayer(uuid);
                teamPlayer.setJavaUUID(floodgatePlayer.getJavaUniqueId());

                plugin.runAsync(task -> plugin.getDatabase().updatePlayer(teamPlayer));
                usermap.replace(uuid, teamPlayer);
            }
        }

    }

    public boolean toggleChatSpy(Player player) {
        UUID uuid = player.getUniqueId();
        TeamPlayer teamPlayer = usermap.get(uuid);

        if (teamPlayer.getPreferences().isTeamChatSpying()) {
            teamPlayer.getPreferences().setTeamChatSpying(false);

            usermap.replace(uuid, teamPlayer);
            plugin.runAsync(task -> plugin.getDatabase().updatePlayer(teamPlayer));

            return false;
        } else {
            teamPlayer.getPreferences().setTeamChatSpying(true);

            usermap.replace(uuid, teamPlayer);
            plugin.runAsync(task -> plugin.getDatabase().updatePlayer(teamPlayer));

            return true;
        }
    }

    public Set<UUID> getRawUsermapList(){
        return usermap.keySet();
    }

    public Map<UUID, TeamPlayer> getUsermap() {
        return usermap;
    }

    public enum ChatType {
        TEAM_CHAT,
        ALLY_CHAT;
    }

}
