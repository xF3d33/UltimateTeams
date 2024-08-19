package dev.xf3d3.ultimateteams.database.daos;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import dev.xf3d3.ultimateteams.models.TeamPlayer;
import dev.xf3d3.ultimateteams.database.tables.UserTable;
import dev.xf3d3.ultimateteams.database.Database;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.List;

import java.util.UUID;

public final class UserDao {
    private static Dao<UserTable, String> userTable; 

    public static void init() {
        try {
            TableUtils.createTableIfNotExists(Database.connectionSource, UserTable.class);
            userTable = DaoManager.createDao(Database.connectionSource, UserTable.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static UserTable queryForEq(String key, Object value) {
        try {
            List<UserTable> result = userTable.queryForEq(key, value);
            if (result.size() <= 0)
                return null;

            return result.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void createPlayer(@NotNull TeamPlayer teamplayer) {
        try {
            UserTable user = new UserTable();
            user.setUUID(String.valueOf(teamplayer.getJavaUUID()));
            user.setUsername(teamplayer.getLastPlayerName());
            user.setBedrock(teamplayer.isBedrockPlayer());
            user.setBedrockUUID(teamplayer.getBedrockUUID());
            user.setChatSpy(teamplayer.getCanChatSpy());
            userTable.create(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updatePlayer(@NotNull TeamPlayer teamplayer) {
        try {
            UserTable user = queryForEq("uuid", String.valueOf(teamplayer.getJavaUUID()));

            if (user == null)
                return;

            user.setUUID(String.valueOf(teamplayer.getJavaUUID()));
            user.setUsername(teamplayer.getLastPlayerName());
            user.setBedrock(teamplayer.isBedrockPlayer());
            user.setBedrockUUID(teamplayer.getBedrockUUID());
            user.setChatSpy(teamplayer.getCanChatSpy());
            userTable.update(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Optional<TeamPlayer> getPlayer(@NotNull UUID uuid) {
        try {
            UserTable user = queryForEq("uuid", String.valueOf(uuid));

            if (user == null)
                return Optional.empty();

            return Optional.of(new TeamPlayer(
                user.getUUID(), user.getUsername(), user.isBedrock(), user.getBedrockUUID(), user.canChatSpy()
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static Optional<TeamPlayer> getPlayer(@NotNull String name) {
        try {
            UserTable user = queryForEq("name", name);

            if (user == null)
                return Optional.empty();

            return Optional.of(new TeamPlayer(
                user.getUUID(), user.getUsername(), user.isBedrock(), user.getBedrockUUID(), user.canChatSpy()
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

}