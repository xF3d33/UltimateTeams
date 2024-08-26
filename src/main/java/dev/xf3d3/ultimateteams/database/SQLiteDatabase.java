package dev.xf3d3.ultimateteams.database;

import com.google.gson.JsonSyntaxException;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamPlayer;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteConfig;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class SQLiteDatabase extends Database {

    /**
     * The name of the database file
     */
    private static final String DATABASE_FILE_NAME  = "UltimateTeamsData.db";

    /**
     * Path to the SQLite HuskTownsData.db file
     */
    private final File databaseFile;

    /**
     * The persistent SQLite database connection
     */
    private Connection connection;

    public SQLiteDatabase(@NotNull UltimateTeams plugin) {
        super(plugin);
        this.databaseFile = new File(plugin.getDataFolder(), DATABASE_FILE_NAME);
    }

    /**
     *
     * @return The {@link Connection} to the database
     * @throws SQLException if the connection fails for some reason
     */
    private Connection getConnection() throws SQLException {
        if (connection == null) {
            setConnection();
        } else if (connection.isClosed()) {
            setConnection();
        }
        return connection;
    }


    /**
     * Used to set up a connection from the provided data
     */
    private void setConnection() {
        try {
            plugin.log(Level.INFO, "Attempting to connect to database");

            // Ensure that the database file exists
            if (databaseFile.createNewFile()) {
                plugin.log(Level.INFO, "Created the SQLite database file");
            }

            // Specify use of the JDBC SQLite driver
            Class.forName("org.sqlite.JDBC");

            // Set SQLite database properties
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            config.setEncoding(SQLiteConfig.Encoding.UTF8);
            config.setJournalMode(SQLiteConfig.JournalMode.WAL);
            config.setSynchronous(SQLiteConfig.SynchronousMode.NORMAL);

            // Establish the connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath(), config.toProperties());

            plugin.log(Level.INFO, "SQLite Database Connected!");
        } catch (IOException e) {
            plugin.log(Level.SEVERE, "An exception occurred creating the database file", e);

        } catch (SQLException e) {
            plugin.log(Level.SEVERE, "An SQL exception occurred initializing the SQLite database", e);

        } catch (ClassNotFoundException e) {
            plugin.log(Level.SEVERE, "Failed to load the necessary SQLite driver", e);
        }
    }



    public void initialize() {
        // Establish connection
        this.setConnection();

        // Create tables
        try (Statement statement = getConnection().createStatement()) {
            for (String tableCreationStatement : getSchema("database/sqlite_schema.sql")) {
                statement.execute(tableCreationStatement);
            }

            setLoaded(true);
        } catch (SQLException | IOException e) {
            setLoaded(false);

            throw new IllegalStateException("Failed to create SQLite database tables.", e);
        }

        plugin.getLogger().info("Database tables created");
    }

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

        return teams;
    }

    public void createPlayer(@NotNull TeamPlayer teamplayer) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(format("""
                    INSERT INTO `%user_table%` (`uuid`, `username`, `isBedrock`, `bedrockUUID`, `canChatSpy`)
                    VALUES (?, ?, ?, ?, ?)
                    """))) {

                statement.setString(1, String.valueOf(teamplayer.getJavaUUID()));
                statement.setString(2, teamplayer.getLastPlayerName());
                statement.setBoolean(3, teamplayer.isBedrockPlayer());
                statement.setString(4, teamplayer.getBedrockUUID());
                statement.setBoolean(5, teamplayer.getCanChatSpy());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            plugin.log(Level.SEVERE, "Failed to create team in table", e);
        }
    }

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

    public void createTeam(@NotNull Team team, @NotNull UUID uuid) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(format("""
                    INSERT INTO `%team_table%` (`uuid`, `name`, `data`)
                    VALUES (?, ?, ?)
                    """))) {

                statement.setString(1, String.valueOf(uuid));
                statement.setString(2, team.getTeamFinalName());
                statement.setBytes(3, plugin.getGson().toJson(team).getBytes(StandardCharsets.UTF_8));

                statement.executeUpdate();
            }
        } catch (SQLException | JsonSyntaxException e) {
            plugin.log(Level.SEVERE, "Failed to create team in table", e);
        }
    }

    public void updateTeam(@NotNull Team team) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(format("""
                    UPDATE `%team_table%`
                    SET `name` = ?, `data` = ?
                    WHERE `name` = ?
                    """))) {

                statement.setString(1, team.getTeamFinalName());
                statement.setBytes(2, plugin.getGson().toJson(team).getBytes(StandardCharsets.UTF_8));
                statement.setString(3, team.getTeamFinalName());

                statement.executeUpdate();
                statement.clearParameters();
            }
        } catch (SQLException | JsonSyntaxException e) {
            plugin.log(Level.SEVERE, "Failed to update team in table", e);
        }
    }

    public void deleteTeam(@NotNull UUID uuid) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(format("""
                    DELETE FROM `%team_table%`
                    WHERE `uuid` = ?
                    """))) {

                statement.setString(1, String.valueOf(uuid));

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            plugin.log(Level.SEVERE, "Failed to delete team in table", e);
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.log(Level.SEVERE, "Failed to close connection", e);
        }
    }
}