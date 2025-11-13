package dev.xf3d3.ultimateteams.api;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.config.Messages;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamPlayer;
import dev.xf3d3.ultimateteams.models.User;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class UltimateTeamsAPI {

    private final UltimateTeams plugin;
    private static UltimateTeamsAPI instance;

    private UltimateTeamsAPI(UltimateTeams plugin) {
        this.plugin = plugin;
    }

    /**
     * Check if the plugin has finished the startup process
     *
     * @return true if the plugin has loaded, false otherwise
     */

    public boolean isLoaded() {
        return plugin.isLoaded();
    }


    /**
     * Internal use only. register the API instance
     *
     * @param plugin the plugin's instance
     */
    @ApiStatus.Internal
    public static void register(@NotNull UltimateTeams plugin) {
        instance = new UltimateTeamsAPI(plugin);
    }

    /**
     * Get an instance of the API.
     *
     * @return the instance of the API
     * @throws NotRegisteredException if the API has not yet been registered.
     * @since 3.0
     */
    @NotNull
    public static UltimateTeamsAPI getInstance() throws NotRegisteredException {
        if (instance == null) {
            throw new UltimateTeamsAPI.NotRegisteredException();
        }

        return instance;
    }

    /**
     *
     * @return a Set with all the teams
     */
    public Set<Team> getAllTeams() {
        return plugin.getTeamStorageUtil().getTeams();
    }

    /**
     *
     * @return all the plugin raw messages (not formatted).
     */
    public Messages getMessages() {
        return plugin.getMessages();
    }

    /**
     * Find a team by id
     *
     * @param id the team id
     * @return The {@link Team}, if it exists
     */
    public Optional<Team> findTeamById(int id) {
        return plugin.getTeamStorageUtil().findTeam(id);
    }

    /**
     * Find a team by name
     *
     * @param name the team name
     * @return The {@link Team}, if it exists
     */
    public Optional<Team> findTeamByName(@NotNull String name) {
        return plugin.getTeamStorageUtil().findTeamByName(name);
    }

    /**
     * Find a team by owner
     *
     * @param uuid the team owner UUID
     * @return The {@link Team}, if it exists
     */
    public Optional<Team> findTeamByOwner(@NotNull UUID uuid) {
        return plugin.getTeamStorageUtil().findTeamByOwner(uuid);
    }

    /**
     * Find a team by member
     *
     * @param uuid the team member UUID
     * @return The {@link Team}, if it exists
     */
    public Optional<Team> findTeamByMember(@NotNull UUID uuid) {
        return plugin.getTeamStorageUtil().findTeamByMember(uuid);
    }

    /**
     * Adds a member to a {@link Team} and updates the team locally, into database, and to other servers
     *
     * @param team the team
     * @param newMember the new member
     */
    public void addMember(@NotNull Team team, @NotNull Player newMember) {
        plugin.getTeamStorageUtil().addTeamMember(team, newMember);
    }

    /**
     * Kicks a member from a {@link Team} and updates the team locally, into database, and to other servers
     *
     * @param team the team
     * @param member the member to kick
     * @param actor any online player used to propagate the message into other servers
     */
    public void kickMember(@NotNull Team team, OfflinePlayer member, @NotNull Player actor) {
        plugin.getTeamStorageUtil().kickPlayer(actor, team, member);
    }

    /**
     * Transfers the {@link Team} to a new owner, and updates the team locally, into database and to other servers.
     *
     * @param team the team
     * @param newOwner the new owner
     */
    public void transferTeam(@NotNull Team team, @NotNull UUID newOwner) {
        plugin.getTeamStorageUtil().transferTeamOwner(team, newOwner);
    }

    /**
     * Set a {@link Team} as enemy for another {@link Team}, and update data locally, into database and to other servers.
     *
     * @param team the team to add the enemy to
     * @param enemyTeam the enemy team
     * @param actor a player used to propagate the update to other servers. can be any online player
     */
    public void addTeamEnemy(@NotNull Team team, @NotNull Team enemyTeam, @NotNull Player actor) {
        plugin.getTeamStorageUtil().addTeamEnemy(team, enemyTeam, actor);
    }

    /**
     * Remove a {@link Team} as enemy for another {@link Team}, and update data locally, into database and to other servers.
     *
     * @param team the team to remove the enemy to
     * @param enemyTeam the enemy team
     * @param actor a player used to propagate the update to other servers. can be any online player
     */
    public void removeTeamEnemy(@NotNull Team team, @NotNull Team enemyTeam, @NotNull Player actor) {
        plugin.getTeamStorageUtil().removeTeamEnemy(team, enemyTeam, actor);
    }

    /**
     * Set a {@link Team} as ally for another {@link Team}, and update data locally, into database and to other servers.
     *
     * @param team the team to add the ally to
     * @param allyTeam the allied team
     * @param actor a player used to propagate the update to other servers. can be any online player
     */
    public void addTeamAlly(@NotNull Team team, @NotNull Team allyTeam, @NotNull Player actor) {
        plugin.getTeamStorageUtil().addTeamAlly(team, allyTeam, actor);
    }

    /**
     *
     * Remove a {@link Team} as ally for another {@link Team}, and update data locally, into database and to other servers.
     *
     * @param team the team to remove the ally to
     * @param allyTeam the allied team
     * @param actor a player used to propagate the update to other servers. can be any online player
     */
    public void removeTeamAlly(@NotNull Team team, @NotNull Team allyTeam, @NotNull Player actor) {
        plugin.getTeamStorageUtil().removeTeamAlly(team, allyTeam, actor);
    }



    /**
     * Update a team locally, in the database, to other servers if cross-server is enabled
     *
     * @param actor a player needed to propagate the event to other servers. can be any online player
     * @param team the team to update
     */
    public void updateTeam(@NotNull Player actor, @NotNull Team team) {
        plugin.runAsync(task -> plugin.getTeamStorageUtil().updateTeamData(actor, team));
    }

    /**
     * Create a team
     *
     * @param player the team owner
     * @param teamName the team name
     */

    public void createTeam(@NotNull Player player, @NotNull String teamName) {
        plugin.getTeamStorageUtil().createTeam(player, teamName);
    }

    /**
     * Check if a player is in a team
     *
     * @param player the player to check
     * @return true if a player is in a team, false if not
     */
    public boolean isInTeam(@NotNull Player player) {
        return plugin.getTeamStorageUtil().isInTeam(player);
    }

    /**
     * Check if a player is a team owner
     *
     * @param player the player to check
     * @return true if a player is a team owner, false if not
     */
    public boolean isTeamOwner(@NotNull Player player) {
        return plugin.getTeamStorageUtil().isTeamOwner(player);
    }

    /**
     * Check if a player is a team manager
     *
     * @param player the player to check
     * @return true if a player is a team manager, false if not
     */
    public boolean isTeamManager(@NotNull Player player) {
        return plugin.getTeamStorageUtil().isTeamManager(player);
    }

    /**
     *
     * @return a list of {@link User} that contains online users from all servers (when using cross-server)
     */
    public List<User> getOnlineUsers() {
        return plugin.getUsersStorageUtil().getUserList();
    }

    /**
     *
     * @param uuid the player's UUID
     * @return the {@link TeamPlayer}
     */
    public CompletableFuture<TeamPlayer> getTeamPlayer(@NotNull UUID uuid) {
        return plugin.getUsersStorageUtil().getPlayer(uuid);
    }

    /**
     * Update a {@link TeamPlayer} locally and into the database
     *
     * @param teamPlayer the player to update
     */
    public void updatePlayer(@NotNull TeamPlayer teamPlayer) {
        plugin.getUsersStorageUtil().updatePlayer(teamPlayer);
    }

    /**
     * An exception indicating the API has been accessed before it has been registered.
     *
     */
    public static final class NotRegisteredException extends IllegalStateException {

        private static final String MESSAGE = """
                Could not access the UltimateTeams API as it has not yet been initialized. This could be because:
                1) UltimateTeams did not load
                2) Your plugin isn't set to load after UltimateTeams (check the docs)
                3) You are attempting to access UltimateTeams on plugin construction/before your plugin has enabled.""";

        NotRegisteredException() {
            super(MESSAGE);
        }

    }
}
