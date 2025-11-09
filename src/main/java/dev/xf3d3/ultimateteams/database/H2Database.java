package dev.xf3d3.ultimateteams.database;

import com.google.gson.JsonSyntaxException;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamPlayer;
import org.bukkit.entity.Player;
import org.h2.jdbcx.JdbcConnectionPool;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

@SuppressWarnings("DuplicatedCode")
public class H2Database extends Database {

    /**
     * Path to the H2 HuskHomesData.h2 file.
     */
    private final File databaseFile;

    /**
     * The name of the database file.
     */
    private static final String DATABASE_FILE_NAME = "UltimateTeamsData.h2";

    private JdbcConnectionPool connectionPool;

    public H2Database(@NotNull UltimateTeams plugin) {
        super(plugin);
        this.databaseFile = new File(plugin.getDataFolder(), DATABASE_FILE_NAME);
    }

    /**
     * Fetch the auto-closeable connection from the H2 Connection Pool.
     *
     * @return The {@link Connection} to the H2 database
     * @throws SQLException if the connection fails for some reason
     */
    private Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }

    @Override
    public void initialize() throws IllegalStateException {
        // Prepare the database flat file
        final String url = String.format("jdbc:h2:%s", databaseFile.getAbsolutePath());
        this.connectionPool = JdbcConnectionPool.create(url, "sa", "sa");

        // Prepare database schema; make tables if they don't exist
        try (Connection connection = getConnection()) {
            final String[] databaseSchema = getSchema("database/h2_schema.sql");
            try (Statement statement = connection.createStatement()) {
                for (String tableCreationStatement : databaseSchema) {
                    statement.execute(tableCreationStatement);
                }
            }
        } catch (SQLException | IOException e) {
            throw new IllegalStateException("Failed to initialize the H2 database", e);
        }
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
                        team.setId(resultSet.getInt("id"));
                        teams.add(team);
                    }
                }
            }
        } catch (SQLException | JsonSyntaxException e) {
            plugin.log(Level.SEVERE, "Failed to fetch list of teams from table", e);
        }

        return teams;
    }

    public Optional<Team> getTeam(@NotNull Integer id) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(format("""
                    SELECT *
                    FROM `%team_table%`
                    WHERE `id` = ?
                    """))) {

                statement.setInt(1, id);

                final ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    final String data = new String(resultSet.getBytes("data"), StandardCharsets.UTF_8);
                    final Team team = plugin.getGson().fromJson(data, Team.class);

                    if (team != null) {
                        team.setId(resultSet.getInt("id"));
                        return Optional.of(team);
                    }
                }
            }
        } catch (SQLException | JsonSyntaxException e) {
            plugin.log(Level.SEVERE, "Failed to update team in table", e);
        }
        return Optional.empty();
    }

    public void createPlayer(@NotNull TeamPlayer teamplayer) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(format("""
                    INSERT INTO `%user_table%` (`uuid`, `username`, `isBedrock`, `bedrockUUID`, `preferences`)
                    VALUES (?, ?, ?, ?, ?)
                    """))) {

                statement.setString(1, String.valueOf(teamplayer.getJavaUUID()));
                statement.setString(2, teamplayer.getLastPlayerName());
                statement.setBoolean(3, teamplayer.isBedrockPlayer());
                statement.setString(4, teamplayer.getBedrockUUID());
                statement.setBytes(5, plugin.getGson().toJson(teamplayer.getPreferences()).getBytes(StandardCharsets.UTF_8));

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            plugin.log(Level.SEVERE, "Failed to create player in table", e);
        }
    }

    public void updatePlayer(@NotNull TeamPlayer teamplayer) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(format("""
                    UPDATE `%user_table%`
                    SET `username` = ?, `isBedrock` = ?, `bedrockUUID` = ?, `preferences` = ?
                    WHERE `uuid` = ?
                    """))) {

                statement.setString(1, teamplayer.getLastPlayerName());
                statement.setBoolean(2, teamplayer.isBedrockPlayer());
                statement.setString(3, teamplayer.getBedrockUUID());
                statement.setBytes(4, plugin.getGson().toJson(teamplayer.getPreferences()).getBytes(StandardCharsets.UTF_8));

                statement.setString(5, String.valueOf(teamplayer.getJavaUUID()));

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
                            UUID.fromString(resultSet.getString("uuid")),
                            resultSet.getString("username"),
                            resultSet.getBoolean("isBedrock"),
                            resultSet.getString("bedrockUUID"),
                            plugin.getPreferencesFromJson(new String(resultSet.getBytes("preferences"), StandardCharsets.UTF_8))
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
    public void deleteAllUsers() {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(format("""
                DELETE FROM `%user_table%`"""))) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            plugin.log(Level.SEVERE, "Failed to delete all users from table", e);
        }
    }

    public Team createTeam(@NotNull String name, @NotNull Player creator) {
        final Team team = Team.create(name, creator, plugin.getSettings().isPvpDefaultAllow(), plugin.getSettings().getTeamEnderChestRows());

        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(format("""
                    INSERT INTO `%team_table%` (`name`, `data`)
                    VALUES (?, ?)
                    """), Statement.RETURN_GENERATED_KEYS)) {

                statement.setString(1, team.getName());
                statement.setBytes(2, plugin.getGson().toJson(team).getBytes(StandardCharsets.UTF_8));

                statement.executeUpdate();

                final ResultSet insertedRow = statement.getGeneratedKeys();
                if (insertedRow.next()) {
                    team.setId(insertedRow.getInt(1));
                }
            }
        } catch (SQLException | JsonSyntaxException e) {
            plugin.log(Level.SEVERE, "Failed to create team in table", e);
        }
        return team;
    }

    public Team createTeam(@NotNull Team team) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(format("""
                    INSERT INTO `%team_table%` (`name`, `data`)
                    VALUES (?, ?)
                    """), Statement.RETURN_GENERATED_KEYS)) {

                statement.setString(1, team.getName());
                statement.setBytes(2, plugin.getGson().toJson(team).getBytes(StandardCharsets.UTF_8));

                statement.executeUpdate();

                final ResultSet insertedRow = statement.getGeneratedKeys();
                if (insertedRow.next()) {
                    team.setId(insertedRow.getInt(1));
                }
            }
        } catch (SQLException | JsonSyntaxException e) {
            plugin.log(Level.SEVERE, "Failed to create migrated team in table", e);
        }
        return team;
    }
    public void updateTeam(@NotNull Team team) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(format("""
                    UPDATE `%team_table%`
                    SET `name` = ?, `data` = ?
                    WHERE `id` = ?
                    """))) {

                statement.setString(1, team.getName());
                statement.setBytes(2, plugin.getGson().toJson(team).getBytes(StandardCharsets.UTF_8));
                statement.setInt(3, team.getId());

                statement.executeUpdate();
            }
        } catch (SQLException | JsonSyntaxException e) {
            plugin.log(Level.SEVERE, "Failed to update team in table", e);
        }
    }

    public void deleteTeam(@NotNull Integer id) {
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
    public void deleteAllTeams() {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(format("""
                DELETE FROM `%team_table%`"""))) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            plugin.log(Level.SEVERE, "Failed to delete all teams from table", e);
        }
    }

    public void close() {
        if (connectionPool != null) {
            connectionPool.dispose();
        }
    }



}

