package dev.xf3d3.ultimateteams.utils;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.events.TeamDisbandEvent;
import dev.xf3d3.ultimateteams.api.events.TeamOfflineDisbandEvent;
import dev.xf3d3.ultimateteams.api.events.TeamTransferOwnershipEvent;
import dev.xf3d3.ultimateteams.models.Team;
import dev.xf3d3.ultimateteams.models.TeamWarp;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class TeamsStorage {
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '&' + "[0-9A-FK-OR]");
    private static final Map<UUID, Team> teamsList = new ConcurrentHashMap<>();

    private final FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();
    private final UltimateTeams plugin;

    public TeamsStorage(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void loadTeams() {
        final List<Team> teams = plugin.getDatabase().getAllTeams();

        teams.forEach(team -> teamsList.put(UUID.fromString(team.getTeamOwner()), team)
        );

        plugin.sendConsole(Utils.Color("&eLoaded " + teams.size() + " teams!"));
    }

    public Team createTeam(Player player, String teamName) {
        UUID ownerUUID = player.getUniqueId();
        String ownerUUIDString = player.getUniqueId().toString();
        Team newTeam = new Team(ownerUUIDString, teamName);

        teamsList.put(ownerUUID, newTeam);
        plugin.runAsync(task -> plugin.getDatabase().createTeam(newTeam, ownerUUID));

        return newTeam;
    }

    public boolean isTeamExisting(Player player) {
        UUID uuid = player.getUniqueId();
        return teamsList.containsKey(uuid);
    }

    public void updateTeamLocal(Team team) {
        UUID uuid = UUID.fromString(team.getTeamOwner());

        teamsList.replace(uuid, team);
    }

    public void updateTeam(Team team) {
        UUID uuid = UUID.fromString(team.getTeamOwner());

        teamsList.replace(uuid, team);
        plugin.runAsync(task -> plugin.getDatabase().updateTeam(team));
    }

    public boolean deleteTeam(Player player) {
        UUID uuid = player.getUniqueId();

        if (findTeamByOwner(player) != null) {
            if (isTeamOwner(player)) {
                if (teamsList.containsKey(uuid)) {
                    fireTeamDisbandEvent(player);

                    // remove from allies team the deleted team
                    final Team disbandedTeam = findTeamByOwner(player);
                    for (String teamUUIDString : disbandedTeam.getTeamAllies()) {
                        OfflinePlayer alliedTeamOwner = Bukkit.getOfflinePlayer(UUID.fromString(teamUUIDString));
                        Team alliedTeam = findTeamByOfflinePlayer(alliedTeamOwner);

                        if (alliedTeam != null) {
                            alliedTeam.removeTeamAlly(uuid.toString());

                            teamsList.replace(UUID.fromString(teamUUIDString), alliedTeam);
                            plugin.runAsync(task -> plugin.getDatabase().updateTeam(alliedTeam));
                        }
                    }


                    teamsList.remove(uuid);
                    plugin.runAsync(task -> plugin.getDatabase().deleteTeam(uuid));

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
            plugin.runAsync(task -> plugin.getDatabase().deleteTeam(uuid));

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
        plugin.runAsync(task -> plugin.getDatabase().updateTeam(team));
    }

    public boolean addTeamMember(Team team, Player player) {
        UUID uuid = player.getUniqueId();
        String memberUUID = uuid.toString();
        team.addTeamMember(memberUUID);

        teamsList.replace(uuid, team);
        plugin.runAsync(task -> plugin.getDatabase().updateTeam(team));

        return true;
    }

    public void addTeamEnemy(Player teamOwner, Player enemyTeamOwner) {
        // team uuid
        UUID ownerUUID = teamOwner.getUniqueId();

        // allied team uuid
        UUID enemyUUID = enemyTeamOwner.getUniqueId();

        String enemyUUIDString = enemyUUID.toString();

        // team update
        Team team = teamsList.get(ownerUUID);
        team.addTeamEnemy(enemyUUIDString);

        // allied team update
        Team enemyTeam = teamsList.get(enemyUUID);
        enemyTeam.addTeamEnemy(String.valueOf(ownerUUID));

        // Update the team
        teamsList.replace(ownerUUID, team);
        plugin.runAsync(task -> plugin.getDatabase().updateTeam(team));

        // Update the allied team
        teamsList.replace(enemyUUID, enemyTeam);
        plugin.runAsync(task -> plugin.getDatabase().updateTeam(enemyTeam));
    }

    public void removeTeamEnemy(Player teamOwner, Player enemyTeamOwner) {
        // team uuid
        UUID ownerUUID = teamOwner.getUniqueId();

        // enemy team uuid
        UUID enemyUUID = enemyTeamOwner.getUniqueId();

        String enemyUUIDString = enemyUUID.toString();

        // team update
        Team team = teamsList.get(ownerUUID);
        team.removeTeamEnemy(enemyUUIDString);

        // allied team update
        Team enemyTeam = teamsList.get(enemyUUID);
        enemyTeam.removeTeamEnemy(String.valueOf(ownerUUID));

        // Update the team
        teamsList.replace(ownerUUID, team);
        plugin.runAsync(task -> plugin.getDatabase().updateTeam(team));

        // Update the allied team
        teamsList.replace(enemyUUID, enemyTeam);
        plugin.runAsync(task -> plugin.getDatabase().updateTeam(enemyTeam));
    }

    public void addTeamAlly(Player teamOwner, Player allyTeamOwner) {
        // team uuid
        UUID ownerUUID = teamOwner.getUniqueId();

        // allied team uuid
        UUID allyUUID = allyTeamOwner.getUniqueId();

        String allyUUIDString = allyUUID.toString();

        // team update
        Team team = teamsList.get(ownerUUID);
        team.addTeamAlly(allyUUIDString);

        // allied team update
        Team alliedTeam = teamsList.get(allyUUID);
        alliedTeam.addTeamAlly(String.valueOf(ownerUUID));

        // Update the team
        teamsList.replace(ownerUUID, team);
        plugin.runAsync(task -> plugin.getDatabase().updateTeam(team));

        // Update the allied team
        teamsList.replace(allyUUID, alliedTeam);
        plugin.runAsync(task -> plugin.getDatabase().updateTeam(alliedTeam));
    }

    public void removeTeamAlly(Player teamOwner, Player allyTeamOwner) {
        // team uuid
        UUID ownerUUID = teamOwner.getUniqueId();

        // allied team uuid
        UUID uuid = allyTeamOwner.getUniqueId();

        String allyUUIDString = uuid.toString();
        UUID allyUUID = UUID.fromString(allyUUIDString);

        // team update
        Team team = teamsList.get(ownerUUID);
        team.removeTeamAlly(allyUUIDString);

        // allied team update
        Team alliedTeam = teamsList.get(allyUUID);
        alliedTeam.removeTeamAlly(String.valueOf(ownerUUID));

        // Update the team
        teamsList.replace(ownerUUID, team);
        plugin.runAsync(task -> plugin.getDatabase().updateTeam(team));

        // Update the allied team
        teamsList.replace(allyUUID, alliedTeam);
        plugin.runAsync(task -> plugin.getDatabase().updateTeam(alliedTeam));
    }

    public boolean isHomeSet(Team team){
        return team.getTeamHomeWorld() != null;
    }

    public void deleteHome(Team team) {
        team.setTeamHomeWorld(null);

        plugin.runAsync(task -> plugin.getDatabase().updateTeam(team));
    }

    public void kickPlayer(Team team, OfflinePlayer player) {
        UUID uuid = UUID.fromString(team.getTeamOwner());

        team.removeTeamMember(player.getUniqueId().toString());
        teamsList.replace(uuid, team);

        plugin.runAsync(task -> plugin.getDatabase().updateTeam(team));
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

                    Team newTeam = new Team(newOwnerUUID.toString(), originalTeam.getTeamFinalName());

                    if (originalTeam.getTeamHomeWorld() != null) {
                        newTeam.setTeamHomeWorld(originalTeam.getTeamHomeWorld());
                    }

                    try {
                        newTeam.setTeamHomeX(originalTeam.getTeamHomeX());
                        newTeam.setTeamHomeY(originalTeam.getTeamHomeY());
                        newTeam.setTeamHomeZ(originalTeam.getTeamHomeZ());
                        newTeam.setTeamHomeYaw(originalTeam.getTeamHomeYaw());
                        newTeam.setTeamHomePitch(originalTeam.getTeamHomePitch());
                    } catch (NullPointerException ignored) {}

                    if (originalTeam.getTeamMembers() != null) {
                        newTeam.setTeamMembers(originalTeam.getTeamMembers());
                    }

                    if (originalTeam.getTeamAllies() != null) {
                        newTeam.setTeamAllies(originalTeam.getTeamAllies());
                    }

                    if (originalTeam.getTeamEnemies() != null) {
                        newTeam.setTeamEnemies(originalTeam.getTeamEnemies());
                    }

                    if (originalTeam.getTeamWarps() != null) {
                        for (TeamWarp warp : originalTeam.getTeamWarps()) {
                            newTeam.addTeamWarp(warp);
                        }
                    }

                    newTeam.setTeamPrefix(originalTeam.getTeamPrefix());
                    newTeam.setFriendlyFireAllowed(originalTeam.isFriendlyFireAllowed());


                    // delete old team
                    teamsList.remove(originalOwnerUUID);
                    plugin.runAsync(task -> plugin.getDatabase().deleteTeam(originalOwnerUUID));

                    // create new team
                    teamsList.put(newOwnerUUID, newTeam);
                    plugin.runAsync(task -> plugin.getDatabase().createTeam(newTeam, newOwnerUUID));

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
