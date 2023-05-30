package dev.xf3d3.celestyteams.utils;

import dev.xf3d3.celestyteams.CelestyTeams;
import dev.xf3d3.celestyteams.api.ClanDisbandEvent;
import dev.xf3d3.celestyteams.api.ClanOfflineDisbandEvent;
import dev.xf3d3.celestyteams.api.ClanTransferOwnershipEvent;
import dev.xf3d3.celestyteams.models.Team;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class TeamStorageUtil {

    private static final Logger logger = CelestyTeams.getPlugin().getLogger();

    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf('&') + "[0-9A-FK-OR]");

    private static Map<UUID, Team> teamsList = new HashMap<>();

    private static final FileConfiguration messagesConfig = CelestyTeams.getPlugin().messagesFileManager.getMessagesConfig();
    private static final FileConfiguration teamsConfig = CelestyTeams.getPlugin().getConfig();
    private final CelestyTeams plugin;

    public TeamStorageUtil(@NotNull CelestyTeams plugin) {
        this.plugin = plugin;
    }

    public void loadTeams() {
        teamsList.clear();

        plugin.getDatabase().getAllTeams().forEach(rawTeam -> {

            final String finalName = rawTeam.getTeamFinalName();

            Team team = new Team(rawTeam.getTeamOwner(), finalName);

            if (finalName.contains("&") || finalName.contains("#")){
                team.setTeamFinalName(stripClanNameColorCodes(team));
            }

            team.setTeamPrefix(rawTeam.getTeamPrefix());
            team.setTeamMembers(rawTeam.getClanMembers());
            team.setClanAllies(rawTeam.getClanAllies());
            team.setClanEnemies(rawTeam.getClanEnemies());
            team.setFriendlyFireAllowed(rawTeam.isFriendlyFireAllowed());

            if (rawTeam.getClanHomeWorld() != null){
                team.setClanHomeWorld(rawTeam.getClanHomeWorld());
                team.setClanHomeX(rawTeam.getClanHomeX());
                team.setClanHomeY(rawTeam.getClanHomeY());
                team.setClanHomeZ(rawTeam.getClanHomeZ());
                team.setClanHomeYaw(rawTeam.getClanHomeYaw());
                team.setClanHomePitch(rawTeam.getClanHomePitch());
            }

            teamsList.put(UUID.fromString(rawTeam.getTeamOwner()), team);
        });

    }

    public Team createTeam(Player player, String teamName){
        UUID ownerUUID = player.getUniqueId();
        String ownerUUIDString = player.getUniqueId().toString();
        Team newTeam = new Team(ownerUUIDString, teamName);
        teamsList.put(ownerUUID, newTeam);

        plugin.runAsync(() -> plugin.getDatabase().createTeam(newTeam, ownerUUID));

        return newTeam;
    }

    public boolean isTeamExisting(Player player){
        UUID uuid = player.getUniqueId();
        return teamsList.containsKey(uuid);
    }

    public boolean deleteTeam(Player player) throws IOException{
        UUID uuid = player.getUniqueId();
        if (findTeamByOwner(player) != null) {
            if (isClanOwner(player)) {
                if (teamsList.containsKey(uuid)) {
                    fireClanDisbandEvent(player);

                    teamsList.remove(uuid);
                    plugin.runAsync(() -> plugin.getDatabase().deleteTeam(uuid));

                    return true;
                } else {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("teams-update-error-1")));
                    return false;
                }
            }
        }
        return false;
    }

    public boolean deleteOfflineClan(OfflinePlayer offlinePlayer) throws IOException{
        UUID uuid = offlinePlayer.getUniqueId();

        if (findTeamByOfflineOwner(offlinePlayer) != null) {
            if (teamsList.containsKey(uuid)) {
                fireOfflineClanDisbandEvent(offlinePlayer);

                teamsList.remove(uuid);
                plugin.runAsync(() -> plugin.getDatabase().deleteTeam(uuid));

                return true;
            }else {
                logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("teams-update-error-1")));
                return false;
            }
        }
        return false;
    }

    public boolean isClanOwner(Player player) {
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

    public Team findClanByPlayer(Player player) {
        for (Team team : teamsList.values()) {
            if (findTeamByOwner(player) != null) {
                return team;
            }
            if (team.getClanMembers() != null) {
                for (String member : team.getClanMembers()) {
                    if (member.equals(player.getUniqueId().toString())) {
                        return team;
                    }
                }
            }
        }
        return null;
    }

    public Team findClanByOfflinePlayer(OfflinePlayer player) {
        for (Team team : teamsList.values()) {
            if (findTeamByOfflineOwner(player) != null) {
                return team;
            }
            if (team.getClanMembers() != null) {
                for (String member : team.getClanMembers()) {
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
        if (!isClanOwner(player)) {
            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-must-be-owner")));
            return;
        }

        Team team = teamsList.get(uuid);
        team.setTeamPrefix(prefix);

        //teamsList.replace(uuid, team);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));
    }

    public boolean addClanMember(Team team, Player player) {
        UUID uuid = player.getUniqueId();
        String memberUUID = uuid.toString();
        team.addTeamMember(memberUUID);

        //teamsList.replace(uuid, team);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));

        return true;
    }

    public void addClanEnemy(Player teamOwner, Player enemyClanOwner) {
        UUID ownerUUID = teamOwner.getUniqueId();
        UUID enemyUUID = enemyClanOwner.getUniqueId();
        String enemyOwnerUUID = enemyUUID.toString();
        Team team = teamsList.get(ownerUUID);

        team.addClanEnemy(enemyOwnerUUID);

        //teamsList.replace(ownerUUID, team);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));
    }

    public void removeClanEnemy(Player teamOwner, Player enemyClanOwner) {
        UUID ownerUUID = teamOwner.getUniqueId();
        UUID enemyUUID = enemyClanOwner.getUniqueId();
        String enemyOwnerUUID = enemyUUID.toString();
        Team team = teamsList.get(ownerUUID);

        team.removeClanEnemy(enemyOwnerUUID);

        //teamsList.replace(ownerUUID, team);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));
    }

    public void addClanAlly(Player teamOwner, Player allyClanOwner) {
        UUID ownerUUID = teamOwner.getUniqueId();
        UUID uuid = allyClanOwner.getUniqueId();
        String allyUUID = uuid.toString();
        Team team = teamsList.get(ownerUUID);

        team.addClanAlly(allyUUID);

        //teamsList.replace(ownerUUID, team);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));
    }

    public void removeClanAlly(Player teamOwner, Player allyClanOwner) {
        UUID ownerUUID = teamOwner.getUniqueId();
        UUID uuid = allyClanOwner.getUniqueId();
        String allyUUID = uuid.toString();
        Team team = teamsList.get(ownerUUID);

        team.removeClanAlly(allyUUID);

        //teamsList.replace(ownerUUID, team);
        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));
    }

    public boolean isHomeSet(Team team){
        return team.getClanHomeWorld() != null;
    }

    public void deleteHome(Team team) {
        team.setClanHomeWorld(null);

        plugin.runAsync(() -> plugin.getDatabase().updateTeam(team));
    }

    public String stripClanNameColorCodes(Team team) {
        String teamFinalName = team.getTeamFinalName();

        return teamFinalName == null ? null : STRIP_COLOR_PATTERN.matcher(teamFinalName).replaceAll("");
    }

    public Team transferClanOwner(Team originalTeam, Player originalClanOwner, Player newClanOwner) throws IOException {
        if (findTeamByOwner(originalClanOwner) != null) {
            if (isClanOwner(originalClanOwner)) {
                if (!isClanOwner(newClanOwner) && findClanByPlayer(newClanOwner) == null){
                    String originalOwnerKey = originalClanOwner.getUniqueId().toString();
                    UUID originalOwnerUUID = originalClanOwner.getUniqueId();
                    UUID newOwnerUUID = newClanOwner.getUniqueId();

                    String teamFinalName = originalTeam.getTeamFinalName();
                    String teamPrefix = originalTeam.getTeamPrefix();
                    ArrayList<String> teamMembers = new ArrayList<>(originalTeam.getClanMembers());
                    ArrayList<String> teamAllies = new ArrayList<>(originalTeam.getClanAllies());
                    ArrayList<String> teamEnemies = new ArrayList<>(originalTeam.getClanEnemies());
                    boolean friendlyFire = originalTeam.isFriendlyFireAllowed();
                    String teamHomeWorld = originalTeam.getClanHomeWorld();
                    double teamHomeX = originalTeam.getClanHomeX();
                    double teamHomeY = originalTeam.getClanHomeY();
                    double teamHomeZ = originalTeam.getClanHomeZ();
                    float teamHomeYaw = originalTeam.getClanHomeYaw();
                    float teamHomePitch = originalTeam.getClanHomePitch();

                    Team newTeam = new Team(newOwnerUUID.toString(), teamFinalName);
                    newTeam.setTeamPrefix(teamPrefix);
                    newTeam.setTeamMembers(teamMembers);
                    newTeam.setClanAllies(teamAllies);
                    newTeam.setClanEnemies(teamEnemies);
                    newTeam.setFriendlyFireAllowed(friendlyFire);
                    newTeam.setClanHomeWorld(teamHomeWorld);
                    newTeam.setClanHomeX(teamHomeX);
                    newTeam.setClanHomeY(teamHomeY);
                    newTeam.setClanHomeZ(teamHomeZ);
                    newTeam.setClanHomeYaw(teamHomeYaw);
                    newTeam.setClanHomePitch(teamHomePitch);

                    // delete old team
                    teamsList.remove(originalOwnerUUID);
                    plugin.runAsync(() -> plugin.getDatabase().deleteTeam(originalOwnerUUID));

                    // create new team
                    teamsList.put(newOwnerUUID, newTeam);
                    plugin.runAsync(() -> plugin.getDatabase().createTeam(newTeam, newOwnerUUID));

                    return newTeam;

                } else {
                    originalClanOwner.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-ownership-transfer-failed-target-in-team")));
                }
            } else {
                originalClanOwner.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("team-must-be-owner")));
            }
        }
        return null;
    }

    public Set<Map.Entry<UUID, Team>> getClans(){
        return teamsList.entrySet();
    }

    public Set<UUID> getRawClansList(){
        return teamsList.keySet();
    }

    public Collection<Team> getClanList(){
        return teamsList.values();
    }

    public Collection<String> getTeamsListNames(){
        List<String> teams = new ArrayList<>();

        for (Team team : teamsList.values())
            teams.add(team.getTeamFinalName());

        return teams;
    }

    private void fireClanDisbandEvent(Player player) {
        Team teamByOwner = findTeamByOwner(player);
        ClanDisbandEvent teamDisbandEvent = new ClanDisbandEvent(player, teamByOwner.getTeamFinalName());
        Bukkit.getPluginManager().callEvent(teamDisbandEvent);
    }

    private void fireOfflineClanDisbandEvent(OfflinePlayer offlinePlayer){
        Team teamByOfflineOwner = findTeamByOfflineOwner(offlinePlayer);
        ClanOfflineDisbandEvent teamOfflineDisbandEvent = new ClanOfflineDisbandEvent(offlinePlayer, teamByOfflineOwner.getTeamFinalName());
        Bukkit.getPluginManager().callEvent(teamOfflineDisbandEvent);
    }

    private void fireClanTransferOwnershipEvent(Player originalClanOwner, Player newClanOwner, Team newTeam){
        ClanTransferOwnershipEvent teamTransferOwnershipEvent = new ClanTransferOwnershipEvent(originalClanOwner, originalClanOwner, newClanOwner, newTeam);
        Bukkit.getPluginManager().callEvent(teamTransferOwnershipEvent);
    }
}
