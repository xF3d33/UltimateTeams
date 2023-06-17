package dev.xf3d3.ultimateteams.utils;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.TeamDisbandEvent;
import dev.xf3d3.ultimateteams.api.TeamOfflineDisbandEvent;
import dev.xf3d3.ultimateteams.api.TeamTransferOwnershipEvent;
import dev.xf3d3.ultimateteams.models.Team;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class TeamStorageUtil {
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '&' + "[0-9A-FK-OR]");
    private static final Map<UUID, Team> teamsList = new HashMap<>();

    private final FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();
    private final UltimateTeams plugin;

    public TeamStorageUtil(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void loadTeams() {
        teamsList.clear();

        plugin.getDatabase().getAllTeams().forEach(rawTeam -> {

            final String finalName = rawTeam.getTeamFinalName();

            Team team = new Team(rawTeam.getTeamOwner(), finalName);

            if (finalName.contains("&") || finalName.contains("#")) {
                team.setTeamFinalName(stripTeamNameColorCodes(team));
            }

            team.setTeamPrefix(rawTeam.getTeamPrefix());
            team.setTeamMembers(rawTeam.getTeamMembers());
            team.setTeamAllies(rawTeam.getTeamAllies());
            team.setTeamEnemies(rawTeam.getTeamEnemies());
            team.setFriendlyFireAllowed(rawTeam.isFriendlyFireAllowed());

            if (rawTeam.getTeamHomeWorld() != null){
                team.setTeamHomeWorld(rawTeam.getTeamHomeWorld());
                team.setTeamHomeX(rawTeam.getTeamHomeX());
                team.setTeamHomeY(rawTeam.getTeamHomeY());
                team.setTeamHomeZ(rawTeam.getTeamHomeZ());
                team.setTeamHomeYaw(rawTeam.getTeamHomeYaw());
                team.setTeamHomePitch(rawTeam.getTeamHomePitch());
            }

            teamsList.put(UUID.fromString(rawTeam.getTeamOwner()), team);
        });

    }

    public Team createTeam(Player player, String teamName) {
        UUID ownerUUID = player.getUniqueId();
        String ownerUUIDString = player.getUniqueId().toString();
        Team newTeam = new Team(ownerUUIDString, teamName);

        teamsList.put(ownerUUID, newTeam);
        plugin.runAsync(() -> plugin.getDatabase().createTeam(newTeam, ownerUUID));

        return newTeam;
    }

    public boolean isTeamExisting(Player player) {
        UUID uuid = player.getUniqueId();
        return teamsList.containsKey(uuid);
    }

    public boolean deleteTeam(Player player) {
        UUID uuid = player.getUniqueId();
        if (findTeamByOwner(player) != null) {
            if (isTeamOwner(player)) {
                if (teamsList.containsKey(uuid)) {
                    fireTeamDisbandEvent(player);

                    teamsList.remove(uuid);
                    plugin.runAsync(() -> plugin.getDatabase().deleteTeam(uuid));

                    return true;
                } else {
                    player.sendMessage(Utils.Color(messagesConfig.getString("teams-update-error-1")));
                    return false;
                }
            }
        }
        return false;
    }

    public boolean deleteTeam(String name) {

        if (findTeamByName(name) != null) {
            final Team team = findTeamByName(name);
            final UUID uuid = UUID.fromString(team.getTeamOwner());

            //fireTeamDisbandEvent(player);
            teamsList.remove(uuid);
            plugin.runAsync(() -> plugin.getDatabase().deleteTeam(uuid));

            return true;
        }

        return false;
    }

    public boolean isTeamOwner(Player player) {
        UUID uuid = player.getUniqueId();
        String ownerUUID = uuid.toString();
        Team team = teamsList.get(uuid);

        if (team != null) {
            if (team.getTeamOwner() == null) {
                return false;
            } else {
                return team.getTeamOwner().equals(ownerUUID);
            }
        }
        return false;
    }

    public Team findTeamByName(String name) {
        Map<String, Team> teamsMap = new HashMap<>();

        for (Team team : teamsList.values())
            teamsMap.put(team.getTeamFinalName(), team);

        return teamsMap.get(name);
    }

    public Team findTeamByOwner(Player player) {
        UUID uuid = player.getUniqueId();
        return teamsList.get(uuid);
    }

    public Team findTeamByOfflineOwner(OfflinePlayer offlinePlayer) {
        UUID uuid = offlinePlayer.getUniqueId();
        return teamsList.get(uuid);
    }

    public Team findTeamByPlayer(Player player) {
        for (Team team : teamsList.values()) {
            if (findTeamByOwner(player) != null) {
                return team;
            }

            if (team.getTeamMembers() != null) {
                for (String member : team.getTeamMembers()) {
                    if (member.equals(player.getUniqueId().toString())) {
                        return team;
                    }
                }
            }
        }
        return null;
    }

    public Team findTeamByOfflinePlayer(OfflinePlayer player) {
        for (Team team : teamsList.values()) {
            if (findTeamByOfflineOwner(player) != null) {
                return team;
            }

            if (team.getTeamMembers() != null) {
                for (String member : team.getTeamMembers()) {
                    if (member.equals(player.getUniqueId().toString())) {
                        return team;
                    }
                }
            }
        }
        return null;
    }

    public void updatePrefix(Player player, String prefix) {
        UUID uuid = player.getUniqueId();
        if (!isTeamOwner(player)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            return;
        }

        Team team = teamsList.get(uuid);
        team.setTeamPrefix(prefix);

        teamsList.replace(uuid, team);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));
    }

    public boolean addTeamMember(Team team, Player player) {
        UUID uuid = player.getUniqueId();
        String memberUUID = uuid.toString();
        team.addTeamMember(memberUUID);

        teamsList.replace(uuid, team);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));

        return true;
    }

    public void addTeamEnemy(Player teamOwner, Player enemyTeamOwner) {
        UUID ownerUUID = teamOwner.getUniqueId();
        UUID enemyUUID = enemyTeamOwner.getUniqueId();
        String enemyOwnerUUID = enemyUUID.toString();
        Team team = teamsList.get(ownerUUID);

        team.addTeamEnemy(enemyOwnerUUID);

        teamsList.replace(ownerUUID, team);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));
    }

    public void removeTeamEnemy(Player teamOwner, Player enemyTeamOwner) {
        UUID ownerUUID = teamOwner.getUniqueId();
        UUID enemyUUID = enemyTeamOwner.getUniqueId();
        String enemyOwnerUUID = enemyUUID.toString();
        Team team = teamsList.get(ownerUUID);

        team.removeTeamEnemy(enemyOwnerUUID);

        teamsList.replace(ownerUUID, team);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));
    }

    public void addTeamAlly(Player teamOwner, Player allyTeamOwner) {
        UUID ownerUUID = teamOwner.getUniqueId();
        UUID uuid = allyTeamOwner.getUniqueId();
        String allyUUID = uuid.toString();
        Team team = teamsList.get(ownerUUID);

        team.addTeamAlly(allyUUID);

        teamsList.replace(ownerUUID, team);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));
    }

    public void removeTeamAlly(Player teamOwner, Player allyTeamOwner) {
        UUID ownerUUID = teamOwner.getUniqueId();
        UUID uuid = allyTeamOwner.getUniqueId();
        String allyUUID = uuid.toString();
        Team team = teamsList.get(ownerUUID);

        team.removeTeamAlly(allyUUID);

        teamsList.replace(ownerUUID, team);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));
    }

    public boolean isHomeSet(Team team){
        return team.getTeamHomeWorld() != null;
    }

    public void deleteHome(Team team) {
        team.setTeamHomeWorld(null);

        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));
    }

    public String stripTeamNameColorCodes(Team team) {
        String teamFinalName = team.getTeamFinalName();

        return teamFinalName == null ? null : STRIP_COLOR_PATTERN.matcher(teamFinalName).replaceAll("");
    }

    public Team transferTeamOwner(Team originalTeam, Player originalTeamOwner, Player newTeamOwner) throws IOException {
        if (findTeamByOwner(originalTeamOwner) != null) {
            if (isTeamOwner(originalTeamOwner)) {
                if (!isTeamOwner(newTeamOwner) && findTeamByPlayer(newTeamOwner) == null){
                    String originalOwnerKey = originalTeamOwner.getUniqueId().toString();
                    UUID originalOwnerUUID = originalTeamOwner.getUniqueId();
                    UUID newOwnerUUID = newTeamOwner.getUniqueId();

                    String teamFinalName = originalTeam.getTeamFinalName();
                    String teamPrefix = originalTeam.getTeamPrefix();
                    ArrayList<String> teamMembers = new ArrayList<>(originalTeam.getTeamMembers());
                    ArrayList<String> teamAllies = new ArrayList<>(originalTeam.getTeamAllies());
                    ArrayList<String> teamEnemies = new ArrayList<>(originalTeam.getTeamEnemies());
                    boolean friendlyFire = originalTeam.isFriendlyFireAllowed();
                    String teamHomeWorld = originalTeam.getTeamHomeWorld();
                    double teamHomeX = originalTeam.getTeamHomeX();
                    double teamHomeY = originalTeam.getTeamHomeY();
                    double teamHomeZ = originalTeam.getTeamHomeZ();
                    float teamHomeYaw = originalTeam.getTeamHomeYaw();
                    float teamHomePitch = originalTeam.getTeamHomePitch();

                    Team newTeam = new Team(newOwnerUUID.toString(), teamFinalName);
                    newTeam.setTeamPrefix(teamPrefix);
                    newTeam.setTeamMembers(teamMembers);
                    newTeam.setTeamAllies(teamAllies);
                    newTeam.setTeamEnemies(teamEnemies);
                    newTeam.setFriendlyFireAllowed(friendlyFire);
                    newTeam.setTeamHomeWorld(teamHomeWorld);
                    newTeam.setTeamHomeX(teamHomeX);
                    newTeam.setTeamHomeY(teamHomeY);
                    newTeam.setTeamHomeZ(teamHomeZ);
                    newTeam.setTeamHomeYaw(teamHomeYaw);
                    newTeam.setTeamHomePitch(teamHomePitch);

                    // delete old team
                    teamsList.remove(originalOwnerUUID);
                    plugin.runAsync(() -> plugin.getDatabase().deleteTeam(originalOwnerUUID));

                    // create new team
                    teamsList.put(newOwnerUUID, newTeam);
                    plugin.runAsync(() -> plugin.getDatabase().createTeam(newTeam, newOwnerUUID));

                    return newTeam;

                } else {
                    originalTeamOwner.sendMessage(Utils.Color(messagesConfig.getString("team-ownership-transfer-failed-target-in-team")));
                }
            } else {
                originalTeamOwner.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            }
        }
        return null;
    }

    public Set<Map.Entry<UUID, Team>> getTeams(){
        return teamsList.entrySet();
    }

    public Set<UUID> getRawTeamsList(){
        return teamsList.keySet();
    }

    public Collection<Team> getTeamsList(){
        return teamsList.values();
    }

    public Collection<String> getTeamsListNames(){
        List<String> teams = new ArrayList<>();

        for (Team team : teamsList.values())
            teams.add(team.getTeamFinalName());

        return teams;
    }

    private void fireTeamDisbandEvent(Player player) {
        Team teamByOwner = findTeamByOwner(player);
        TeamDisbandEvent teamDisbandEvent = new TeamDisbandEvent(player, teamByOwner.getTeamFinalName());
        Bukkit.getPluginManager().callEvent(teamDisbandEvent);
    }

    private void fireOfflineTeamDisbandEvent(OfflinePlayer offlinePlayer){
        Team teamByOfflineOwner = findTeamByOfflineOwner(offlinePlayer);
        TeamOfflineDisbandEvent teamOfflineDisbandEvent = new TeamOfflineDisbandEvent(offlinePlayer, teamByOfflineOwner.getTeamFinalName());
        Bukkit.getPluginManager().callEvent(teamOfflineDisbandEvent);
    }

    private void fireTeamTransferOwnershipEvent(Player originalTeamOwner, Player newTeamOwner, Team newTeam){
        TeamTransferOwnershipEvent teamTransferOwnershipEvent = new TeamTransferOwnershipEvent(originalTeamOwner, originalTeamOwner, newTeamOwner, newTeam);
        Bukkit.getPluginManager().callEvent(teamTransferOwnershipEvent);
    }
}
