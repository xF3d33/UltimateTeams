package dev.xf3d3.ultimateteams.manager;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.network.objects.Message;
import dev.xf3d3.ultimateteams.network.objects.Payload;
import dev.xf3d3.ultimateteams.team.Team;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class Manager {
    private final UltimateTeams plugin;
    private final TeamsManager teams;
    private final AdminManager admin;
    private final InviteManager invite;
    private final UsersManager users;

    public Manager(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.teams = new TeamsManager(plugin);
        this.admin = new AdminManager(plugin);
        this.invite = new InviteManager(plugin);
        this.users = new UsersManager(plugin);

    }

    @NotNull
    public TeamsManager teams() {
        return teams;
    }

    @NotNull
    public AdminManager admin() {
        return admin;
    }

    @NotNull
    public InviteManager invite() {
        return invite;
    }

    @NotNull
    public UsersManager users() {
        return users;
    }

    @NotNull
    public List<String> getTeamNames() {
        return plugin.getTeams().stream().map(Team::getName).toList();
    }

    public Optional<Team> getTeamByName(@NotNull String townName) {
        return plugin.getTeams().stream()
                .filter(town -> town.getName().equalsIgnoreCase(townName))
                .findFirst();
    }

    /**
     * Update a town's data to the database and propagate cross-server
     *
     * @param player The user who is updating the town's data
     * @param team  The town to update
     */
    public void updateTeamData(@NotNull Player player, @NotNull Team team) {
        // Update in the cache
        plugin.updateTeam(team);

        // Update in the database
        plugin.getDatabase().updateTeam(team);

        // Propagate to other servers
        plugin.getMessageBroker().ifPresent(broker -> Message.builder()
                .type(Message.Type.TEAM_UPDATE)
                .payload(Payload.integer(team.getID()))
                .target(Message.TARGET_ALL, Message.TargetType.SERVER)
                .build()
                .send(broker, player));
    }

    /**
     * Send a message to all online users in a {@link Team}
     *
     * @param team    The town to send the notification for
     * @param message The message to send
     */
    public void sendTeamMessage(@NotNull Team team, @NotNull String message) {
        plugin.getOnlineUsers().stream()
                .filter(user -> team.getTeamMembers().contains(String.valueOf(user.getUniqueId())))
                .filter(user -> team.getTeamOwner().equals(String.valueOf(user.getUniqueId())))
                .forEach(user -> user.sendMessage(message));
    }
}
