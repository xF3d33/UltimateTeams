package dev.xf3d3.ultimateteams.api;

import dev.xf3d3.ultimateteams.config.Messages;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamPlayer;
import dev.xf3d3.ultimateteams.models.User;
import dev.xf3d3.ultimateteams.utils.TeamsStorage;
import dev.xf3d3.ultimateteams.utils.UsersStorage;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public interface UltimateTeamsAPI {

    boolean isLoaded();

    Collection<Team> getAllTeams();

    Messages getMessages();

    Optional<Team> findTeamById(int id);

    Optional<Team> findTeamByName(@NotNull String name);

    Optional<Team> findTeamByOwner(@NotNull UUID uuid);

    Optional<Team> findTeamByMember(@NotNull UUID uuid);

    void addMember(@NotNull Team team, @NotNull Player newMember);

    void kickMember(@NotNull Team team, OfflinePlayer member, @NotNull Player actor);

    void transferTeam(@NotNull Team team, @NotNull UUID newOwner);

    void addTeamEnemy(@NotNull Team team, @NotNull Team enemyTeam, @NotNull Player actor);

    void removeTeamEnemy(@NotNull Team team, @NotNull Team enemyTeam, @NotNull Player actor);

    void addTeamAlly(@NotNull Team team, @NotNull Team allyTeam, @NotNull Player actor);

    void removeTeamAlly(@NotNull Team team, @NotNull Team allyTeam, @NotNull Player actor);

    void updateTeam(@NotNull Player actor, @NotNull Team team);

    void createTeam(@NotNull Player player, @NotNull String teamName);

    boolean isInTeam(@NotNull Player player);

    boolean isTeamOwner(@NotNull Player player);

    boolean isTeamManager(@NotNull Player player);

    List<User> getOnlineUsers();

    CompletableFuture<TeamPlayer> getTeamPlayer(@NotNull UUID uuid);

    void updatePlayer(@NotNull TeamPlayer teamPlayer);


}
