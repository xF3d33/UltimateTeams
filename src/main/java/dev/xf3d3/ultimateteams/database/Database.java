package dev.xf3d3.ultimateteams.database;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


public abstract class Database {
	protected final UltimateTeams plugin;
	private boolean loaded;

	protected Database(@NotNull UltimateTeams plugin) {
		this.plugin = plugin;
	}

	@NotNull
	protected final String[] getSchema(@NotNull String schemaFileName) throws IOException {
		return format(
				new String(Objects.requireNonNull(plugin.getResource(schemaFileName)).readAllBytes(),
						StandardCharsets.UTF_8))
				.split(";");
	}



	@NotNull
	protected final String format(@NotNull String statement) {

		return statement
				.replaceAll("%team_table%", plugin.getSettings().getTableName(Table.TEAM_DATA))
				.replaceAll("%user_table%", plugin.getSettings().getTableName(Table.USER_DATA));
	}

	public abstract void initialize();

	public abstract List<Team> getAllTeams();

	public abstract void createPlayer(@NotNull TeamPlayer teamplayer);

	public abstract void updatePlayer(@NotNull TeamPlayer teamplayer);

	public abstract Optional<TeamPlayer> getPlayer(@NotNull UUID uuid);

	public abstract Optional<TeamPlayer> getPlayer(@NotNull String name);

	public abstract void createTeam(@NotNull Team team, @NotNull UUID uuid);

	public abstract void updateTeam(@NotNull Team team);

	public abstract void deleteTeam(@NotNull UUID uuid);

	public abstract void close();

	/**
	 * Check if the database has been loaded
	 *
	 * @return {@code true} if the database has loaded successfully; {@code false} if it failed to initialize
	 */
	public boolean hasLoaded() {
		return loaded;
	}

	/**
	 * Set if the database has loaded
	 *
	 * @param loaded whether the database has loaded successfully
	 */
	protected void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	@Getter
	@AllArgsConstructor
	public enum Type {
		MYSQL("MySQL", "mysql"),
		MARIADB("MariaDB", "mariadb"),
		SQLITE("SQLite", "sqlite"),
		H2("H2", "h2"),
		POSTGRESQL("PostgreSQL", "postgresql");

		private final String displayName;
		private final String protocol;
	}

	/**
	 * Represents the names of tables in the database
	 */
	public enum Table {
		USER_DATA("ultimateteams_users"),
		TEAM_DATA("ultimateteams_teams");

		@NotNull
		private final String defaultName;

		Table(@NotNull String defaultName) {
			this.defaultName = defaultName;
		}

		@NotNull
		public static Database.Table match(@NotNull String placeholder) throws IllegalArgumentException {
			return Table.valueOf(placeholder.toUpperCase());
		}

		@NotNull
		public String getDefaultName() {
			return defaultName;
		}
	}
}