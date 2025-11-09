package dev.xf3d3.ultimateteams.migrator;

import com.google.common.collect.Maps;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.commands.TeamCommand;
import dev.xf3d3.ultimateteams.database.Database;
import dev.xf3d3.ultimateteams.migrator.legacyModels.LegacyTeam;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamHome;
import dev.xf3d3.ultimateteams.models.TeamPlayer;
import dev.xf3d3.ultimateteams.models.TeamWarp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class Migrator {
    private final UltimateTeams plugin;
    private final TreeMap<String, String> parameters;

    public Migrator(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
        this.parameters = new TreeMap<>();
    }

    public void startMigration() {
        plugin.setLoaded(false);

        plugin.runAsync(task -> {
            try {
                // Migrate teams
                plugin.log(Level.INFO, "Migration started...");

                    plugin.getTeamStorageUtil().getTeams().clear();
                    plugin.getDatabase().deleteAllTeams();

                    plugin.log(Level.INFO, "Migrating teams...");
                    getConvertedTeams().forEach(team -> {
                        try {
                            team.setId(plugin.getDatabase().createTeam(team).getId());
                            plugin.getTeamStorageUtil().getTeams().add(team);
                        } catch (IllegalStateException e) {
                            plugin.getLogger().log(Level.SEVERE, "Failed to migrate team " + team.getName(), e.getMessage());
                        }
                    });

                    // Migrate users
                    plugin.log(Level.INFO, "Migrating users (this may take some time)...");
                    plugin.getDatabase().deleteAllUsers();
                    getConvertedUsers().forEach(teamPlayer -> {
                        try {
                            plugin.getDatabase().createPlayer(teamPlayer);
                        } catch (IllegalStateException e) {
                            plugin.log(Level.SEVERE,"Failed to migrate user " + teamPlayer.getLastPlayerName(), e.getCause());
                        }
                    });


            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to migrate", e.getMessage());

            } finally {
                plugin.log(Level.INFO, "Migration ended successfully.");

                plugin.loadConfigs();
                TeamCommand.updateBannedTagsList();
                plugin.getTeamStorageUtil().loadTeams();

                plugin.runSync(t -> plugin.setLoaded(true));
            }
        });
    }

    @NotNull
    private List<Team> getConvertedTeams() {
        final List<Team> teams = new ArrayList<>();

        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    formatStatement("SELECT * FROM %teams%"))) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        final String data = new String(resultSet.getBytes("data"), StandardCharsets.UTF_8);
                        final LegacyTeam team = plugin.getGson().fromJson(data, LegacyTeam.class);

                        // Team Members
                        Map<UUID, Integer> members = Maps.newHashMap();
                        members.put(UUID.fromString(team.getTeamOwner()), 3);
                        team.getTeamMembers().forEach(member -> members.put(UUID.fromString(member), 1));

                        // Team Warps
                        Map<String, TeamWarp> warps = Maps.newHashMap();
                        team.getTeamWarps().forEach(legacyWarp -> warps.put(legacyWarp.getName(), TeamWarp.of(legacyWarp.getName(), legacyWarp.getLocation(), plugin.getSettings().isEnableCrossServer() ? plugin.getSettings().getServerName() : null)));

                        // Team Home
                        TeamHome teamHome = null;
                        if (team.getTeamHomeWorld() != null) {
                            final Location homeLocation = new Location(Bukkit.getWorld(team.getTeamHomeWorld()), team.getTeamHomeX(), team.getTeamHomeY(), team.getTeamHomeZ(), team.getTeamHomeYaw(), team.getTeamHomePitch());
                            teamHome = TeamHome.of(homeLocation, plugin.getSettings().getServerName());
                        }

                        teams.add(Team.builder()
                                .name(team.getTeamFinalName())
                                .friendlyFire(team.isFriendlyFireAllowed())
                                .prefix(team.getTeamPrefix())
                                .members(members)
                                .home(teamHome)
                                .warps(warps)
                                .build());
                    }
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return teams;
    }

    private List<TeamPlayer> getConvertedUsers() {
        final List<TeamPlayer> teamPlayers = new ArrayList<>();
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    formatStatement("SELECT * FROM %players%"))) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        teamPlayers.add(
                                new TeamPlayer(
                                        UUID.fromString(resultSet.getString("uuid")),
                                        resultSet.getString("username"),
                                        resultSet.getBoolean("isBedrock"),
                                        resultSet.getString("bedrockUUID"),
                                        null
                                )
                        );
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return teamPlayers;
    }

    @NotNull
    private Connection getConnection() throws SQLException {
        final Database.Type type = getParameter(Parameters.DATABASE_TYPE.name())
                .map(String::toUpperCase).map(Database.Type::valueOf).orElse(Database.Type.MYSQL);
        final String host = getParameter(Parameters.DATABASE_HOST.name()).orElse("localhost");
        final int port = getParameter(Parameters.DATABASE_PORT.name()).map(Integer::parseInt).orElse(3306);
        final String database = getParameter(Parameters.DATABASE_NAME.name()).orElse("ultimateteams");
        final String username = getParameter(Parameters.DATABASE_USERNAME.name()).orElse("root");
        final String password = getParameter(Parameters.DATABASE_PASSWORD.name()).orElse("");

        return switch (type) {
            case MYSQL ->
                    DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            case SQLITE ->
                    DriverManager.getConnection("jdbc:sqlite:" + new File(plugin.getDataFolder(), "LegacyUltimateTeamsData.db").getAbsolutePath());
            case H2 ->
                    DriverManager.getConnection("jdbc:h2:" + new File(plugin.getDataFolder(), "LegacyUltimateTeamsData").getAbsolutePath());
            case MARIADB ->
                    DriverManager.getConnection("jdbc:mariadb://" + host + ":" + port + "/" + database, username, password);
            case POSTGRESQL ->
                    DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + database, username, password);
        };
    }

    @NotNull
    public TreeMap<String, String> getParameters() {
        return parameters;
    }

    public void setParameter(@NotNull String key, @NotNull String value) {
        parameters.put(key.toUpperCase(), value);
    }

    public Optional<String> getParameter(@NotNull String key) {
        return Optional.ofNullable(getParameters().get(key));
    }

    @NotNull
    private String formatStatement(@NotNull String statement) {
        return statement
                .replaceAll("%players%", getParameter(Parameters.PLAYERS_TABLE.name()).orElse("ultimateteams_users"))
                .replaceAll("%teams%", getParameter(Parameters.TEAMS_TABLE.name()).orElse("ultimateteams_teams"));
    }

    private enum Parameters {
        DATABASE_TYPE("MySQL"),
        DATABASE_HOST("localhost"),
        DATABASE_PORT("3306"),
        DATABASE_NAME("ultimateteams"),
        DATABASE_USERNAME("root"),
        DATABASE_PASSWORD("pa55w0rd"),
        PLAYERS_TABLE("ultimateteams_users"),
        TEAMS_TABLE("ultimateteams_teams");

        private final String defaultValue;

        Parameters(@NotNull String defaultValue) {
            this.defaultValue = defaultValue;
        }

        @NotNull
        private String getDefault() {
            return defaultValue;
        }

    }
}