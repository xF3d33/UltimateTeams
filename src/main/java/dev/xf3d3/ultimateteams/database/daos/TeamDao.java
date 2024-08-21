package dev.xf3d3.ultimateteams.database.daos;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.database.Database;
import dev.xf3d3.ultimateteams.database.tables.TeamTable;
import dev.xf3d3.ultimateteams.models.Team;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public final class TeamDao {
    private static Dao<TeamTable, String> teamTable; 

    public static void init() {
        try {
            TableUtils.createTableIfNotExists(Database.connectionSource, TeamTable.class);
            teamTable = DaoManager.createDao(Database.connectionSource, TeamTable.class);
        } catch (Exception e) {
            UltimateTeams.getPlugin().getLogger().log(Level.SEVERE, "Error while trying to create team table", e);
        }
    }

    public static TeamTable queryForEq(String key, Object value) {
        try {
            List<TeamTable> result = teamTable.queryForEq(key, value);
            if (result.isEmpty())
                return null;

            return result.get(0);
        } catch (Exception e) {
            UltimateTeams.getPlugin().getLogger().log(Level.SEVERE, "Error while trying to fetch data from teams table", e);
        }
        return null;
    }

    public static List<Team> getAllTeams() {
        final List<Team> teams = new ArrayList<>();

        try {
            List<TeamTable> teamTableList = teamTable.queryForAll(); 
            for (TeamTable teamData: teamTableList) {
                final String data = new String(teamData.getData(), StandardCharsets.UTF_8);
                final Team team = UltimateTeams.getPlugin().getGson().fromJson(data, Team.class);

                if (team != null) teams.add(team);
            }
        } catch (Exception e) {
            UltimateTeams.getPlugin().getLogger().log(Level.SEVERE, "Error while trying to fetch data from teams table", e);
        }

        return teams;
    }

    public static void createTeam(@NotNull Team team, @NotNull UUID uuid) {
        try {
            TeamTable teamData = new TeamTable();
            teamData.setUUID(String.valueOf(uuid));
            teamData.setName(team.getTeamFinalName());
            teamData.setData(UltimateTeams.getPlugin().getGson().toJson(team).getBytes(StandardCharsets.UTF_8));
            teamTable.create(teamData);
        } catch (Exception e) {
            UltimateTeams.getPlugin().getLogger().log(Level.SEVERE, "Error while trying to create team into teams table", e);
        }
    }

    public static void updateTeam(@NotNull Team team) {
        try {
            TeamTable teamData = queryForEq("name", team.getTeamFinalName());

            if (teamData == null)
                return;

            teamData.setName(team.getTeamFinalName());
            teamData.setData(UltimateTeams.getPlugin().getGson().toJson(team).getBytes(StandardCharsets.UTF_8));
            teamTable.update(teamData);
        } catch (Exception e) {
            UltimateTeams.getPlugin().getLogger().log(Level.SEVERE, "Error while trying to update team into teams table", e);
        }
    }
    
    public static void deleteTeam(@NotNull UUID uuid) {
        try {
            TeamTable teamData = queryForEq("uuid", String.valueOf(uuid));

            if (teamData == null)
                return;

            teamTable.delete(teamData);
        } catch (Exception e) {
            UltimateTeams.getPlugin().getLogger().log(Level.SEVERE, "Error while trying to delete team from teams table", e);
        }
    }
}