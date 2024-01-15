package dev.xf3d3.ultimateteams.database;

import com.google.gson.JsonSyntaxException;
import com.zaxxer.hikari.HikariDataSource;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.team.Team;
import dev.xf3d3.ultimateteams.team.TeamPlayer;
import dev.xf3d3.ultimateteams.user.Preferences;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class MySqlDatabase extends Database {
    private static final String DATA_POOL_NAME = "UltimateTeamsHikariPool";
    private HikariDataSource dataSource;

    public MySqlDatabase(@NotNull UltimateTeams plugin) {
        super(plugin, "mysql_schema.sql");
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
        plugin.log(Level.INFO, "Attempting to connect to database");

        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://" +
                plugin.getSettings().getMySqlHost() +
                ":" +
                plugin.getSettings().getMySqlPort() +
                "/" +
                plugin.getSettings().getMySqlDatabase() +
                plugin.getSettings().getMySqlConnectionParameters()
        );

        // Authenticate
        dataSource.setUsername(plugin.getSettings().getMySqlUsername());
        dataSource.setPassword(plugin.getSettings().getMySqlPassword());

        // Set connection pool options
        dataSource.setMaximumPoolSize(plugin.getSettings().getMySqlConnectionPoolSize());
        dataSource.setMinimumIdle(plugin.getSettings().getMySqlConnectionPoolIdle());
        dataSource.setMaxLifetime(plugin.getSettings().getMySqlConnectionPoolLifetime());
        dataSource.setKeepaliveTime(plugin.getSettings().getMySqlConnectionPoolKeepAlive());
        dataSource.setConnectionTimeout(plugin.getSettings().getMySqlConnectionPoolTimeout());
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
    }

    public void initialize() {
        // Establish connection
        this.setConnection();

        // Create tables
        try (Connection connection = getConnection()) {
            plugin.log(Level.INFO, "MySQL Database Connected!");

            try (Statement statement = connection.createStatement()) {
                for (String tableCreationStatement : getSchema()) {
                    statement.execute(tableCreationStatement);
                }
            } catch (SQLException e) {
                setLoaded(false);

                throw new IllegalStateException("Failed to create database tables. Please ensure you are running MySQL v8.0+ " +
                        "and that your connecting user account has privileges to create tables.", e);
            }

            setLoaded(true);

        } catch (SQLException e) {
            setLoaded(false);

            throw new IllegalStateException("Failed to establish a connection to the MySQL database. " +
                    "Please check the supplied database credentials in the config file", e);
        }

        plugin.getLogger().info("Database tables created");
    }

    @Override
    public List<Team> getAllTeams() {
        final List<Team> teams = new ArrayList<>();
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(format("""
                    SELECT `id`, `data`
                    FROM `%team_table%`
                    """))) {
                final ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    final String data = new String(resultSet.getBytes("data"), StandardCharsets.UTF_8);
                    final Team team = plugin.getTeamFromJson(data);

                    if (team != null) {
                        team.setID(resultSet.getInt("id"));
                        teams.add(team);
                    }
                }
            }
        } catch (SQLException | JsonSyntaxException e) {
            plugin.log(Level.SEVERE, "Failed to fetch list of teams from table", e);
        }

        return teams;
    }

    /*@Override
    public void createPlayer(@NotNull TeamPlayer teamplayer, @NotNull Preferences preferences) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(format("""
                    INSERT INTO `%user_table%` (`uuid`, `username`, `isBedrock`, `bedrockUUID`, `canChatSpy`, `data`)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """))) {

                statement.setString(1, String.valueOf(teamplayer.getJavaUUID()));
                statement.setString(2, teamplayer.getLastPlayerName());
                statement.setBoolean(3, teamplayer.isBedrockPlayer());
                statement.setString(4, teamplayer.getBedrockUUID());
                statement.setBoolean(5, teamplayer.getCanChatSpy());
                statement.setBytes(6, plugin.getGson().toJson(preferences).getBytes(StandardCharsets.UTF_8));

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            plugin.log(Level.SEVERE, "Failed to create team in table", e);
        }
    }*/

    @Override
    public void updatePlayer(@NotNull TeamPlayer teamplayer) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(format("""
                    UPDATE `%user_table%`
                    SET `uuid` = ?, `username` = ?, `isBedrock` = ?, `bedrockUUID` = ?, `canChatSpy` = ?
                    WHERE `uuid` = ?
                    """))) {

                statement.setString(1, String.valueOf(teamplayer.getJavaUUID()));
                statement.setString(2, teamplayer.getLastPlayerName());
                statement.setBoolean(3, teamplayer.isBedrockPlayer());
                statement.setString(4, teamplayer.getBedrockUUID());
                statement.setBoolean(5, teamplayer.getCanChatSpy());

                statement.setString(6, String.valueOf(teamplayer.getJavaUUID()));

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            plugin.log(Level.SEVERE, "Failed to create team in table", e);
        }
    }

    @Override
    public Optional<TeamPlayer> getPlayer(@NotNull UUID uuid) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(format("""
                    SELECT *
                    FROM `%user_table%`
                    WHERE `uuid` = ?
                    """))) {
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

                    return Optional.of(teamPlayer);
                }
            }
        } catch (SQLException e) {
            plugin.log(Level.SEVERE, "Failed to get player from table", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<TeamPlayer> getPlayer(@NotNull String name) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(format("""
                    SELECT *
                    FROM `%user_table%`
                    WHERE `username` = ?
                    """))) {
                statement.setString(1, name);

                final ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    final TeamPlayer teamPlayer = new TeamPlayer(
                            resultSet.getString("uuid"),
                            resultSet.getString("username"),
                            resultSet.getBoolean("isBedrock"),
                            resultSet.getString("bedrockUUID"),
                            resultSet.getBoolean("canChatSpy")
                    );

                    return Optional.of(teamPlayer);
                }
            }
        } catch (SQLException e) {
            plugin.log(Level.SEVERE, "Failed to get player from table", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Team> getTeam(int teamID) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(format("""
                    SELECT `id`, `data`
                    FROM `%town_data%`
                    WHERE `id` = ?""")
            )) {
                statement.setInt(1, teamID);
                final ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    final String data = new String(resultSet.getBytes("data"), StandardCharsets.UTF_8);
                    final Team town = plugin.getTeamFromJson(data);
                    town.setID(resultSet.getInt("id"));

                    return Optional.of(town);
                }
            }
        } catch (SQLException | JsonSyntaxException e) {
            plugin.log(Level.SEVERE, "Failed to fetch team data from table by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Team createTeam(@NotNull String name, @NotNull Player player) {
        Team team = new Team(player.getUniqueId().toString(), name);

        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(format("""
                    INSERT INTO `%team_table%` (`uuid`, `name`, `data`)
                    VALUES (?, ?, ?)
                    """), Statement.RETURN_GENERATED_KEYS)) {

                statement.setString(1, String.valueOf(player.getUniqueId()));
                statement.setString(2, team.getName());
                statement.setBytes(3, plugin.getGson().toJson(team).getBytes(StandardCharsets.UTF_8));

                statement.executeUpdate();

                final ResultSet insertedRow = statement.getGeneratedKeys();
                if (insertedRow.next()) {
                    team.setID(insertedRow.getInt(1));
                }
            }
        } catch (SQLException | JsonSyntaxException e) {
            plugin.log(Level.SEVERE, "Failed to create team in table", e);
        }
        return team;
    }

    @Override
    public void updateTeam(@NotNull Team team) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(format("""
                    UPDATE `%team_table%`
                    SET `name` = ?, `data` = ?
                    WHERE `id` = ?
                    """))) {

                statement.setString(1, team.getName());
                statement.setBytes(2, plugin.getGson().toJson(team).getBytes(StandardCharsets.UTF_8));
                statement.setInt(3, team.getID());

                statement.executeUpdate();
            }
        } catch (SQLException | JsonSyntaxException e) {
            plugin.log(Level.SEVERE, "Failed to update team in table", e);
        }
    }

    @Override
    public void deleteTeam(int id) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(format("""
                    DELETE FROM `%team_table%`
                    WHERE `id` = ?
                    """))) {

                statement.setInt(1, id);

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            plugin.log(Level.SEVERE, "Failed to delete team in table", e);
        }
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
