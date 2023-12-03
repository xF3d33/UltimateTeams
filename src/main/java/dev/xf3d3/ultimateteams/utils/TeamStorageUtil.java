package dev.xf3d3.ultimateteams.utils;

import dev.xf3d3.ultimateteams.UltimateTeams;
import dev.xf3d3.ultimateteams.api.TeamDisbandEvent;
import dev.xf3d3.ultimateteams.api.TeamOfflineDisbandEvent;
import dev.xf3d3.ultimateteams.api.TeamTransferOwnershipEvent;
import dev.xf3d3.ultimateteams.team.Team;
import dev.xf3d3.ultimateteams.team.TeamWarp;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class TeamStorageUtil {
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '&' + "[0-9A-FK-OR]");
    //private static final Map<UUID, Team> teamsList = new HashMap<>();

    private final FileConfiguration messagesConfig = UltimateTeams.getPlugin().msgFileManager.getMessagesConfig();
    private final UltimateTeams plugin;

    public TeamStorageUtil(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    public void loadTeams() {
        final List<Team> teams = plugin.getDatabase().getAllTeams();

        plugin.log(Level.INFO, "Loading teams from the database...");
        LocalTime startTime = LocalTime.now();
        plugin.setTeams(teams);

        final int teamCount = plugin.getTeams().size();
        final int memberCount = plugin.getTeams().stream().mapToInt(town -> town.getTeamMembers().size()).sum();
        plugin.log(Level.INFO, "Loaded " + teamCount + " team(s) with " + memberCount + " member(s) in " +
                (ChronoUnit.MILLIS.between(startTime, LocalTime.now()) / 1000d) + " seconds");
    }

    public Optional<Team> getTeamByName(@NotNull String townName) {
        return plugin.getTeams().stream()
                .filter(town -> town.getName().equalsIgnoreCase(townName))
                .findFirst();
    }


    public Team createTeam(Player player, String teamName) {
        UUID ownerUUID = player.getUniqueId();
        //Team newTeam = new Team(ownerUUIDString, teamName);

        final Team team = plugin.getDatabase().createTeam(teamName, player);
        plugin.getTeams().add(team);

        return team;
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
                            plugin.runAsync(() -> plugin.getDatabase().updateTeam(alliedTeam));
                        }
                    }


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

    public void updateTeam(@NotNull Team team) {
        plugin.getTeams().removeIf(t -> t.getID() == team.getID());
        plugin.getTeams().add(team);
    }

    public void deleteTeam(Team team) {
        plugin.getDatabase().deleteTeam(team.getID());
        plugin.getTeams().removeIf(t -> t.getID() == team.getID());
    }

    public boolean isTeamOwner(Player player) {
        UUID uuid = player.getUniqueId();
        String ownerUUID = uuid.toString();

        for (Team team : plugin.getTeams()) {
            if (Objects.equals(team.getTeamOwner(), ownerUUID))
                return true;
        }
        return false;
    }

    //TODO: removed findTeamByOfflineOwner because this can be used
    public Optional<Team> findTeamByOwner(Player player) {
        UUID uuid = player.getUniqueId();

        for (Team team : plugin.getTeams()) {
            if (Objects.equals(team.getTeamOwner(), String.valueOf(uuid)))
                return Optional.of(team);
        }
        return Optional.empty();
    }

    public Team findTeamByPlayer(Player player) {
        for (Team team : plugin.getTeams()) {
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
        for (Team team : plugin.getTeams()) {
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

    public void updatePrefix(Team team, Player player, String prefix) {
        UUID uuid = player.getUniqueId();
        if (!isTeamOwner(player)) {
            player.sendMessage(Utils.Color(messagesConfig.getString("team-must-be-owner")));
            return;
        }

        team.setTeamPrefix(prefix);

        updateTeam(team);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));
    }

    public boolean addTeamMember(Team team, Player player) {
        UUID uuid = player.getUniqueId();
        String memberUUID = uuid.toString();
        team.addTeamMember(memberUUID);

        updateTeam(team);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));

        return true;
    }

    public void addTeamEnemy(Player teamOwner, Player enemyTeamOwner) {
        // team uuid
        UUID ownerUUID = teamOwner.getUniqueId();

        // allied team uuid
        UUID uuid = enemyTeamOwner.getUniqueId();

        String allyUUIDString = uuid.toString();
        UUID allyUUID = UUID.fromString(allyUUIDString);

        // team update
        Team team = teamsList.get(ownerUUID);
        team.addTeamEnemy(allyUUIDString);

        // allied team update
        Team alliedTeam = teamsList.get(allyUUID);
        alliedTeam.addTeamEnemy(String.valueOf(ownerUUID));

        // Update the team
        teamsList.replace(ownerUUID, team);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));

        // Update the allied team
        teamsList.replace(allyUUID, alliedTeam);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));
    }

    public void removeTeamEnemy(Player teamOwner, Player enemyTeamOwner) {
        // team uuid
        UUID ownerUUID = teamOwner.getUniqueId();

        // allied team uuid
        UUID uuid = enemyTeamOwner.getUniqueId();

        String allyUUIDString = uuid.toString();
        UUID allyUUID = UUID.fromString(allyUUIDString);

        // team update
        Team team = teamsList.get(ownerUUID);
        team.removeTeamEnemy(allyUUIDString);

        // allied team update
        Team alliedTeam = teamsList.get(allyUUID);
        alliedTeam.removeTeamEnemy(String.valueOf(ownerUUID));

        // Update the team
        teamsList.replace(ownerUUID, team);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));

        // Update the allied team
        teamsList.replace(allyUUID, alliedTeam);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));
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
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));

        // Update the allied team
        teamsList.replace(allyUUID, alliedTeam);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(alliedTeam));
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
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));

        // Update the allied team
        teamsList.replace(allyUUID, alliedTeam);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(alliedTeam));
    }

    public boolean isHomeSet(Team team){
        return team.getTeamHomeWorld() != null;
    }

    public void deleteHome(Team team) {
        team.setTeamHomeWorld(null);

        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));
    }

    public boolean kickPlayer(Team team, OfflinePlayer player) {
        UUID uuid = UUID.fromString(team.getTeamOwner());

        boolean removed = team.removeTeamMember(player.getUniqueId().toString());
        teamsList.replace(uuid, team);

        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));

        return removed;
    }

    public String stripTeamNameColorCodes(Team team) {
        String teamFinalName = team.getName();

        return teamFinalName == null ? null : STRIP_COLOR_PATTERN.matcher(teamFinalName).replaceAll("");
    }

    // TODO: make it works again
    public Team transferTeamOwner(Team originalTeam, Player originalTeamOwner, Player newTeamOwner) throws IOException {
        if (findTeamByOwner(originalTeamOwner) != null) {
            if (isTeamOwner(originalTeamOwner)) {
                if (!isTeamOwner(newTeamOwner) && findTeamByPlayer(newTeamOwner) == null){
                    String originalOwnerKey = originalTeamOwner.getUniqueId().toString();
                    UUID originalOwnerUUID = originalTeamOwner.getUniqueId();
                    UUID newOwnerUUID = newTeamOwner.getUniqueId();

                    Team newTeam = new Team(newOwnerUUID.toString(), originalTeam.getName());

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
                    plugin.runAsync(() -> plugin.getDatabase().deleteTeam(originalOwnerUUID));

                    // create new team
                    teamsList.put(newOwnerUUID, newTeam);
                    //plugin.runAsync(() -> plugin.getDatabase().createTeam(newTeam, newOwnerUUID));

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
            teams.add(team.getName());

        return teams;
    }

    private void fireTeamDisbandEvent(Player player) {
        Team teamByOwner = findTeamByOwner(player);
        TeamDisbandEvent teamDisbandEvent = new TeamDisbandEvent(player, teamByOwner.getName());
        Bukkit.getPluginManager().callEvent(teamDisbandEvent);
    }

    private void fireOfflineTeamDisbandEvent(OfflinePlayer offlinePlayer){
        Team teamByOfflineOwner = findTeamByOfflineOwner(offlinePlayer);
        TeamOfflineDisbandEvent teamOfflineDisbandEvent = new TeamOfflineDisbandEvent(offlinePlayer, teamByOfflineOwner.getName());
        Bukkit.getPluginManager().callEvent(teamOfflineDisbandEvent);
    }

    private void fireTeamTransferOwnershipEvent(Player originalTeamOwner, Player newTeamOwner, Team newTeam){
        TeamTransferOwnershipEvent teamTransferOwnershipEvent = new TeamTransferOwnershipEvent(originalTeamOwner, originalTeamOwner, newTeamOwner, newTeam);
        Bukkit.getPluginManager().callEvent(teamTransferOwnershipEvent);
    }
}
