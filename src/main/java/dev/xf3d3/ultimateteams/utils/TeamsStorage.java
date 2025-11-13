package dev.xf3d3.ultimateteams.utils;

import com.google.common.collect.Sets;
import de.themoep.minedown.adventure.MineDown;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamCreateEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamEnderChest;
import dev.xf3d3.ultimateteams.network.Message;
import dev.xf3d3.ultimateteams.network.Payload;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

public class TeamsStorage {
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '&' + "[0-9A-FK-OR]");

    @Getter
    private final Set<Team> teams = Sets.newConcurrentHashSet();

    private final UltimateTeams plugin;

    public TeamsStorage(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void loadTeams() {
        teams.addAll(plugin.getDatabase().getAllTeams());

        if (plugin.getSettings().isTeamEnderChestMigrate()) {
            teams.forEach(team -> {
               if (team.getEnderChestCount() < 1) {
                   TeamEnderChest defaultChest = TeamEnderChest.builder()
                           .chestNumber(1)
                           .rows(plugin.getSettings().getTeamEnderChestRows())
                           .serializedContents("")
                           .build();

                   team.setEnderChest(defaultChest);
                   plugin.getTeamStorageUtil().updateTeamData(null, team);
               }

            });
        }

        plugin.sendConsole(Utils.Color("&eLoaded " + teams.size() + " teams!"));
        plugin.setLoaded(true);
    }

    public Collection<String> getTeamsName() {
        return teams.stream().map(Team::getName).sorted().toList();
    }

    public void removeTeam(@NotNull Team team) {
        teams.removeIf(t -> t.getId() == team.getId());
    }

    public void updateTeamLocal(@NotNull Team team) {
        removeTeam(team);
        teams.add(team);
    }

    public void updateTeamLocal(Team team, Integer id) {
        teams.removeIf(team1 -> team1.getId() == id);
        teams.add(team);
    }

    public void createTeam(Player player, String teamName) {
        plugin.runAsync(task -> {
            final Team team = plugin.getDatabase().createTeam(teamName, player);

            teams.add(team);
            plugin.getMessageBroker().ifPresent(broker -> Message.builder()
                    .type(Message.Type.TEAM_UPDATE)
                    .payload(Payload.integer(team.getId()))
                    .target(Message.TARGET_ALL, Message.TargetType.SERVER)
                    .build()
                    .send(broker, player));

            plugin.runSync(task2 -> new TeamCreateEvent(player, team).callEvent());
        });
    }

    public void updateTeamData(@Nullable Player actor, @NotNull Team team) {
        // update team in the cache
        updateTeamLocal(team);

        // Update in the database
        plugin.getDatabase().updateTeam(team);

        // Propagate to other servers
        plugin.getMessageBroker().ifPresent(broker -> Message.builder()
                .type(Message.Type.TEAM_UPDATE)
                .payload(Payload.integer(team.getId()))
                .target(Message.TARGET_ALL, Message.TargetType.SERVER)
                .build()
                .send(broker, actor));
    }

    public void deleteTeamData(@Nullable Player player, @NotNull Team team) {
        plugin.getDatabase().deleteTeam(team.getId());
        removeTeam(team);

        // Propagate the town deletion to all servers
        if (player != null) {
            plugin.getMessageBroker().ifPresent(broker -> Message.builder()
                    .type(Message.Type.TEAM_DELETE)
                    .payload(Payload.integer(team.getId()))
                    .target(Message.TARGET_ALL, Message.TargetType.SERVER)
                    .build()
                    .send(broker, player));
        }
    }

    public boolean isInTeam(Player player) {
        return teams.stream().anyMatch(team -> team.getMembers().containsKey(player.getUniqueId()));
    }

    public boolean isTeamOwner(Player player) {
        return teams.stream().anyMatch(team -> team.getOwner().equals(player.getUniqueId()));
    }

    public boolean isTeamManager(Player player) {
        return teams.stream().anyMatch(team -> team.getMembers().getOrDefault(player.getUniqueId(), 0) == 2);
    }

    public Optional<Team> findTeam(int id) {
        return teams.stream().filter(team -> team.getId() == id).findFirst();
    }

    public Optional<Team> findTeamByName(String name) {
        return teams.stream().filter(team -> Utils.removeColors(team.getName()).equalsIgnoreCase(Utils.removeColors(name))).findFirst();

    }

    public Optional<Team> findTeamByOwner(UUID ownerUUID) {
        return teams.stream().filter(team -> team.getOwner().equals(ownerUUID)).findFirst();
    }

    public Optional<Team> findTeamByMember(UUID memberUUID) {
        return teams.stream().filter(team -> team.getMembers().containsKey(memberUUID)).findFirst();
    }

    public void addTeamMember(Team team, Player player) {
        team.addMember(player.getUniqueId(), null);
        plugin.runAsync(task -> updateTeamData(player, team));
    }

    public void addTeamEnemy(Team team, Team otherTeam, Player player) {
        // team uuid
        team.setRelationWith(otherTeam, Team.Relation.ENEMY);

        // Update the team
        plugin.runAsync(task -> updateTeamData(player, team));

    }

    public void removeTeamEnemy(Team team, Team otherTeam, Player player) {
        // team uuid
        team.removeRelationWith(otherTeam);

        // Update the team
        plugin.runAsync(task -> updateTeamData(player, team));
    }

    public void addTeamAlly(Team team, Team otherTeam, Player player) {
        // team uuid
        team.setRelationWith(otherTeam, Team.Relation.ALLY);

        // Update the team
        plugin.runAsync(task -> updateTeamData(player, team));
    }

    public void removeTeamAlly(Team team, Team otherTeam, Player player) {
        // team uuid
        team.removeRelationWith(otherTeam);

        // Update the team
        plugin.runAsync(task -> updateTeamData(player, team));
    }

    public boolean isHomeSet(Team team) {
        return team.getHome() != null;
    }

    public void deleteHome(Player player, Team team) {
        team.setHome(null);

        plugin.runAsync(task -> updateTeamData(player, team));
    }

    public void kickPlayer(Player teamOwner, Team team, OfflinePlayer player) {
        final String name = Objects.requireNonNullElse(player.getName(), "");
        team.removeMember(player.getUniqueId());

        plugin.getUsersStorageUtil().getOnlineUsers().stream()
                .filter(online -> online.getUniqueId().equals(player.getUniqueId()))
                .findFirst()
                .ifPresentOrElse(onlineUser -> {
                            onlineUser.sendMessage(MineDown.parse(plugin.getMessages().getTeamKickedPlayerMessage().replace("%TEAM%", team.getName())));

                            plugin.getUsersStorageUtil().getPlayer(player.getUniqueId()).thenAcceptAsync(teamPlayer -> {
                                if (teamPlayer.getPreferences().isTeamChatTalking() || teamPlayer.getPreferences().isAllyChatTalking()) {
                                    teamPlayer.getPreferences().setTeamChatTalking(false);
                                    teamPlayer.getPreferences().setAllyChatTalking(false);

                                    plugin.getDatabase().updatePlayer(teamPlayer);
                                }
                            });
                        },
                        () -> plugin.getMessageBroker().ifPresent(broker -> Message.builder()
                                .type(Message.Type.TEAM_EVICTED)
                                .target(name, Message.TargetType.PLAYER)
                                .build()
                                .send(broker, teamOwner)));

        plugin.runAsync(task -> updateTeamData(teamOwner, team));
    }

    public void transferTeamOwner(Team team, UUID newOwnerUUID) {
        team.getMembers().put(team.getOwner(), 1);
        team.getMembers().put(newOwnerUUID, 3);

        Player randomPlayer = Bukkit.getOnlinePlayers().stream().findAny().orElse(null);
        plugin.runAsync(task1 -> plugin.getTeamStorageUtil().updateTeamData(randomPlayer, team));

        plugin.runAsync(task -> {
            updateTeamData(randomPlayer, team);
            plugin.getMessageBroker().ifPresent(broker -> Message.builder()
                    .type(Message.Type.TEAM_TRANSFERRED)
                    .payload(Payload.integer(team.getId()))
                    .target(Message.TARGET_ALL, Message.TargetType.SERVER)
                    .build()
                    .send(broker, randomPlayer));
        });
    }
}