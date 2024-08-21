package dev.xf3d3.ultimateteams.database;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.db.MysqlDatabaseType;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.database.daos.TeamDao;
import dev.xf3d3.ultimateteams.database.daos.UserDao;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Level;

public final class Database {
	public static JdbcConnectionSource connectionSource;

	public static void connectMysql(String host, String port, String username, String password, String database, String connectedParam) {
		try {
			connectionSource = new JdbcConnectionSource("jdbc:mysql://" + host + ":" + port + "/" + database + connectedParam, username, password, new MysqlDatabaseType());
			setupData();
		} catch (Exception e) {
			UltimateTeams.getPlugin().log(Level.SEVERE, "Error connecting to MySQL", e);
			connectionSource = null;
		}
	}

	public static void connectSqlite() {
		try {
			final File databaseFile = new File(UltimateTeams.getPlugin().getDataFolder(), "UltimateTeamsData.db");

			if (databaseFile.createNewFile())
	            UltimateTeams.getPlugin().log(Level.INFO, "Created the SQLite database file");

			connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + databaseFile.getAbsolutePath());
			setupData();
		} catch (Exception e) {
			UltimateTeams.getPlugin().log(Level.SEVERE, "Error connecting to SQLite", e);
			connectionSource = null;
		}
	}

	private static void setupData() {
		UserDao.init();
		TeamDao.init();
	}

	public static void close() {
		if (connectionSource != null) {
			try {
				connectionSource.close();
				UltimateTeams.getPlugin().log(Level.INFO, "Database connection closed");

			} catch (Exception e) {
				UltimateTeams.getPlugin().log(Level.SEVERE, "Error while closing database connection", e);
			}
		}
	}

	public enum Type {
        MYSQL("MySQL"),
        SQLITE("SQLite");
        @NotNull
        private final String displayName;

        Type(@NotNull String displayName) {
            this.displayName = displayName;
        }

        @NotNull
        public String getDisplayName() {
            return displayName;
        }
    }

}