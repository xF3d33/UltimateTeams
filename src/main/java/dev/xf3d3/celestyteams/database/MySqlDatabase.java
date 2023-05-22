package dev.xf3d3.celestyteams.database;

import com.google.gson.JsonSyntaxException;
import com.zaxxer.hikari.HikariDataSource;
import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.models.Team;
import dev.xf3d3.celestyteams.models.TeamPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class MySqlDatabase {
    private static final String DATA_POOL_NAME = "CelestyTeamsHikariPool";
    private HikariDataSource dataSource;
    private final CelestyTeams plugin;

    public MySqlDatabase(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
    }

    /**
     * Fetch the auto-closeable connection from the hikariDataSource
     *
     * @return The {@link Connection} to the MySQL database
     * @throws SQLException if the connection fails for some reason
     */
    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }


    /**
     * Used to set up a connection from the provided data
     */
    private void setConnection() {
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("database");
        assert config != null;

        Bukkit.getLogger().info("[DeluxeTeams] Attempting to connect to database");

        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://" +
                config.getString("host", "localhost") +
                ":" +
                config.getInt("port", 3306) +
                "/" +
                config.getString("database", "celestyteams") +
                config.getString("parameters", "?autoReconnect=true&useSSL=false&useUnicode=true&characterEncoding=UTF-8")
        );

        // Authenticate
        dataSource.setUsername(config.getString("user", "root"));
        dataSource.setPassword(config.getString("password", ""));

        // Set connection pool options
        dataSource.setMaximumPoolSize(config.getInt("connection_pool.size", 12));
        dataSource.setMinimumIdle(config.getInt("connection_pool.idle", 12));
        dataSource.setMaxLifetime(config.getInt("connection_pool.lifetime", 1800000));
        dataSource.setKeepaliveTime(config.getInt("connection_pool.keepalive", 30000));
        dataSource.setConnectionTimeout(config.getInt("connection_pool.timeout", 20000));
        dataSource.setPoolName(DATA_POOL_NAME);

        // Set additional connection pool properties
        dataSource.setDataSourceProperties(new Properties() {{
            put("cachePrepStmts", "true");
            put("prepStmtCacheSize", "250");
            put("prepStmtCacheSqlLimit", "2048");
            put("useServerPrepStmts", "true");
            put("useLocalSessionState", "true");
            put("useLocalTransactionState", "true");
            put("rewriteBatchedStatements", "true");
            put("cacheResultSetMetadata", "true");
            put("cacheServerConfiguration", "true");
            put("elideSetAutoCommits", "true");
            put("maintainTimeStats", "false");
        }});

        plugin.getLogger().info("connected");

    }

    private String[] getSchema() {
        try (InputStream schemaStream = Objects.requireNonNull(plugin.getResource("mysql_schema.sql"))) {
            final String schema = new String(schemaStream.readAllBytes(), StandardCharsets.UTF_8);
            return schema.split(";");
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load database schema", e);
        }
        return new String[0];
    }

    public void initialize() throws RuntimeException {
        // Establish connection
        this.setConnection();

        // Create tables
        try (Connection connection = getConnection()) {
            try (Statement statement = connection.createStatement()) {
                for (String tableCreationStatement : getSchema()) {
                    statement.execute(tableCreationStatement);
                }
            }

        } catch (SQLException e) {
            plugin.log(Level.SEVERE, "Failed to create MySQL database tables", e);
        }

        plugin.getLogger().info("tables created");
    }

    public List<Team> getAllTeams() {
        final List<Team> teams = new ArrayList<>();
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                    SELECT `id`, `data`
                    FROM `celestyteams_teams`
                    """)) {
                final ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    final String data = new String(resultSet.getBytes("data"), StandardCharsets.UTF_8);
                    final Team team = plugin.getGson().fromJson(data, Team.class);
                    if (team != null) {
                        //team.setId(resultSet.getInt("id"));
                        teams.add(team);
                    }
                }
            }
        } catch (SQLException | JsonSyntaxException e) {
            plugin.log(Level.SEVERE, "Failed to fetch list of teams from table", e);
        }

        System.out.println(teams);
        return teams;
    }

    public List<TeamPlayer> getAllPlayers() {
        final List<TeamPlayer> players = new ArrayList<>();

        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                    SELECT *
                    FROM `celestyteams_users`
                    """)) {
                final ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    final TeamPlayer teamPlayer = new TeamPlayer(
                            resultSet.getString("uuid"),
                            resultSet.getString("username"),
                            resultSet.getBoolean("isBedrock"),
                            resultSet.getString("bedrockUUID"),
                            resultSet.getBoolean("canChatSpy")
                    );

                    players.add(teamPlayer);
                }
            }
        } catch (SQLException e) {
            plugin.log(Level.SEVERE, "Failed to get all players from table", e);
        }

        System.out.println(players);
        return players;
    }

    public void createPlayer(@NotNull TeamPlayer teamplayer) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO `celestyteams_users` (`uuid`, `username`, `isBedrock`, `bedrockUUID`, `canChatSpy`)
                    VALUES (?, ?, ?, ?, ?)
                    """)) {

                statement.setString(1, String.valueOf(teamplayer.getJavaUUID()));
                statement.setString(2, teamplayer.getLastPlayerName());
                statement.setBoolean(3, teamplayer.isBedrockPlayer());
                statement.setString(4, teamplayer.getBedrockUUID());
                statement.setBoolean(5, teamplayer.getCanChatSpy());

                statement.executeUpdate();

                System.out.println("saved team player " + teamplayer.getLastPlayerName());
            }
        } catch (SQLException e) {
            plugin.log(Level.SEVERE, "Failed to create team in table", e);
        }
    }

    public void updatePlayer(@NotNull TeamPlayer teamplayer) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                    UPDATE `celestyteams_users`
                    SET `uuid` = ?, `username` = ?, `isBedrock` = ?, `bedrockUUID` = ?, `canChatSpy` = ?
                    WHERE `uuid` = ?
                    """)) {

                statement.setString(1, String.valueOf(teamplayer.getJavaUUID()));
                statement.setString(2, teamplayer.getLastPlayerName());
                statement.setBoolean(3, teamplayer.isBedrockPlayer());
                statement.setString(4, teamplayer.getBedrockUUID());
                statement.setBoolean(5, teamplayer.getCanChatSpy());

                statement.setString(6, String.valueOf(teamplayer.getJavaUUID()));

                statement.executeUpdate();

                System.out.println("updated team player " + teamplayer.getLastPlayerName());
            }
        } catch (SQLException e) {
            plugin.log(Level.SEVERE, "Failed to create team in table", e);
        }
    }

    public Optional<TeamPlayer> getPlayer(UUID uuid) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                    SELECT *
                    FROM `celestyteams_users`
                    WHERE `uuid` = ?
                    """)) {
                statement.setString(1, String.valueOf(uuid));

                final ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    final TeamPlayer teamPlayer = new TeamPlayer(
                           resultSet.getString("uuid"),
                           resultSet.getString("username"),
                           resultSet.getBoolean("isBedrock"),
                           resultSet.getString("bedrockUUID"),
                           resultSet.getBoolean("canChatSpy")
                    );

                    System.out.println("got team player " + teamPlayer.getLastPlayerName());

                    return Optional.of(teamPlayer);
                }
            }
        } catch (SQLException e) {
            plugin.log(Level.SEVERE, "Failed to get player from table", e);
        }
        return Optional.empty();
    }

    public void createTeam(@NotNull Team team, @NotNull UUID uuid) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO `celestyteams_teams` (`uuid`, `name`, `data`)
                    VALUES (?, ?, ?)
                    """)) {

                statement.setString(1, String.valueOf(uuid));
                statement.setString(2, team.getTeamFinalName());
                statement.setBytes(3, plugin.getGson().toJson(team).getBytes(StandardCharsets.UTF_8));

                System.out.println(uuid);
                System.out.println(plugin.getGson().toJson(team));

                statement.executeUpdate();
            }
        } catch (SQLException | JsonSyntaxException e) {
            plugin.log(Level.SEVERE, "Failed to create team in table", e);
        }
    }

    public void updateTeam(@NotNull Team team) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                    UPDATE `celestyteams_teams`
                    SET `name` = ?, `data` = ?
                    WHERE `name` = ?
                    """)) {

                statement.setString(1, team.getTeamFinalName());
                statement.setBytes(2, plugin.getGson().toJson(team).getBytes(StandardCharsets.UTF_8));
                statement.setString(3, team.getTeamFinalName());

                statement.executeUpdate();

                System.out.println("updated team " + team.getTeamFinalName());
            }
        } catch (SQLException | JsonSyntaxException e) {
            plugin.log(Level.SEVERE, "Failed to update team in table", e);
        }
    }

    public void deleteTeam(@NotNull UUID uuid) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                    DELETE FROM `celestyteams_teams`
                    WHERE `uuid` = ?
                    """)) {

                statement.setString(1, String.valueOf(uuid));

                statement.executeUpdate();

                System.out.println("deleted team " + uuid);
            }
        } catch (SQLException e) {
            plugin.log(Level.SEVERE, "Failed to delete team in table", e);
        }
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
